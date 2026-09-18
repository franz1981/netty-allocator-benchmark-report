import io.netty.buffer.*;
import io.github.neoionet.netty.microbenchmark.data.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/** Realistic-ish load cycle: 32 threads, mixed sizes, fill to HIGH live buffers per thread, then drop to LOW and keep
 *  churning at LOW (replace random live buffers), read RSS while churning. Repeat. Trim only at the very end.
 *  args: ADAPTIVE|MIMALLOC pattern high low cycles triggerFile */
public class IdleCycle {
    static long rssMb() throws Exception { for (String l : Files.readAllLines(Paths.get("/proc/self/status"))) if (l.startsWith("VmRSS:")) return Long.parseLong(l.replaceAll("[^0-9]", "")) / 1024; return -1; }
    static long usedMb(ByteBufAllocator a) { return ((ByteBufAllocatorMetricProvider) a).metric().usedDirectMemory() / 1048576; }
    static void report(String phase, ByteBufAllocator a) throws Exception { System.out.printf("%-30s rss=%5d MB used=%5d MB%n", phase, rssMb(), usedMb(a)); System.out.flush(); }
    public static void main(String[] args) throws Exception {
        ByteBufAllocator a;
        if (args[0].equals("ADAPTIVE")) a = new AdaptiveByteBufAllocator(true, false);
        else { Object b = Class.forName("io.github.neoionet.netty.mimalloc.MiByteBufAllocator").getMethod("builder").invoke(null); a = (ByteBufAllocator) b.getClass().getMethod("build").invoke(b); }
        int[] sizes = args[1].equals("SOCKET_PROXY") ? WebSocketProxyPattern.FLATTENED_SIZE_ARRAY : ApiGatewayPattern.FLATTENED_SIZE_ARRAY;
        int threads = 32, high = Integer.parseInt(args[2]), low = Integer.parseInt(args[3]), cycles = Integer.parseInt(args[4]);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<List<ByteBuf>> live = new ArrayList<>(); for (int t = 0; t < threads; t++) live.add(new ArrayList<>(high));
        report("start", a);
        for (int c = 1; c <= cycles; c++) {
            final int cc = c;
            List<Future<?>> fs = new ArrayList<>();
            for (int t = 0; t < threads; t++) { final List<ByteBuf> l = live.get(t); final int seed = t;
                fs.add(pool.submit(() -> { Random r = new Random(seed * 31 + cc); while (l.size() < high) { ByteBuf b = a.directBuffer(sizes[r.nextInt(sizes.length)]); b.writeByte(1); l.add(b); } return null; })); }
            for (Future<?> f : fs) f.get();
            report("cycle " + c + " high (" + threads + "x" + high + ")", a);
            fs.clear();
            for (int t = 0; t < threads; t++) { final List<ByteBuf> l = live.get(t); final int seed = t;
                fs.add(pool.submit(() -> { Random r = new Random(seed * 17 + cc);
                    boolean oldest = System.getProperty("drop.oldest") != null;
                    if (oldest) { int keep = low; List<ByteBuf> old = new ArrayList<>(l.subList(0, l.size() - keep)); for (ByteBuf b : old) b.release(); l.subList(0, l.size() - keep).clear(); }
                    else { while (l.size() > low) { int i = r.nextInt(l.size()); l.get(i).release(); l.set(i, l.get(l.size() - 1)); l.remove(l.size() - 1); } }
                    for (int k = 0; k < 2_000_000; k++) { int i = r.nextInt(l.size()); l.get(i).release(); ByteBuf b = a.directBuffer(sizes[r.nextInt(sizes.length)]); b.writeByte(1); l.set(i, b); }
                    return null; })); }
            for (Future<?> f : fs) f.get();
            report("cycle " + c + " low, churning at " + low, a);
        }
        System.out.println("PID " + ProcessHandle.current().pid()); System.out.flush();
        while (!new File(args[5]).exists()) Thread.sleep(100);
        report("after jcmd trim", a);
        pool.shutdownNow();
    }
}
