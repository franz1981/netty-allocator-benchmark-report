import io.netty.buffer.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;

/**
 * Does the memory of released buffers go back to the OS? Single thread, direct buffers, one size.
 * args: ADAPTIVE|MIMALLOC  N  size  triggerFile  [holeSize holeCount]
 * Optional "hole" first: holeCount live buffers of holeSize, released and purged, so that glibc sees chunks of
 * another size freed before the main load. Then: N live buffers of `size`, release all, 100k alloc/release of the
 * same size so the allocator's purge can run, print RSS; then wait for the trigger file
 * (run: jcmd <pid> System.trim_native_heap; touch it) and print RSS again.
 */
public class ChunkReleaseTest {
    static long rssMb() throws Exception {
        for (String l : Files.readAllLines(Paths.get("/proc/self/status"))) if (l.startsWith("VmRSS:")) return Long.parseLong(l.replaceAll("[^0-9]", "")) / 1024;
        return -1;
    }
    static void report(String phase, ByteBufAllocator a) throws Exception {
        long used = ((ByteBufAllocatorMetricProvider) a).metric().usedDirectMemory() / 1048576;
        System.out.printf("%-18s rss=%5d MB  used=%5d MB%n", phase, rssMb(), used);
    }
    public static void main(String[] args) throws Exception {
        ByteBufAllocator a;
        if (args[0].equals("ADAPTIVE")) {
            a = new AdaptiveByteBufAllocator(true, false);
        } else {
            Object builder = Class.forName("io.github.neoionet.netty.mimalloc.MiByteBufAllocator").getMethod("builder").invoke(null);
            a = (ByteBufAllocator) builder.getClass().getMethod("build").invoke(builder);
        }
        int n = Integer.parseInt(args[1]), size = Integer.parseInt(args[2]);
        report("start", a);
        if (args.length > 5) {
            int holeSize = Integer.parseInt(args[4]), holeCount = Integer.parseInt(args[5]);
            List<ByteBuf> hole = new ArrayList<>(holeCount);
            for (int i = 0; i < holeCount; i++) { ByteBuf b = a.directBuffer(holeSize); b.writeByte(1); hole.add(b); }
            for (ByteBuf b : hole) b.release();
            for (int i = 0; i < 100_000; i++) a.directBuffer(holeSize).release();
            report("hole freed", a);
        }
        List<ByteBuf> bufs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) { ByteBuf b = a.directBuffer(size); b.writeByte(1); bufs.add(b); }
        report("live", a);
        boolean reverse = System.getProperty("release.reverse") != null;
        if (reverse) Collections.reverse(bufs);
        for (ByteBuf b : bufs) b.release();
        bufs.clear();
        report(reverse ? "released (newest first)" : "released", a);
        for (int i = 0; i < 100_000; i++) a.directBuffer(size).release();
        report("after trickle", a);
        System.out.println("PID " + ProcessHandle.current().pid());
        while (!new File(args[3]).exists()) Thread.sleep(100);
        report("after jcmd trim", a);
    }
}
