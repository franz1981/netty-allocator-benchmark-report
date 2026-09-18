import io.netty.buffer.*;
import java.nio.file.*;
import java.util.*;

/** Cost of getting memory back after a drop: minor page faults and time of a second identical load.
 *  args: ADAPTIVE|MIMALLOC N size */
public class ReloadCost {
    static long minflt() throws Exception { String s = new String(Files.readAllBytes(Paths.get("/proc/self/stat"))); return Long.parseLong(s.substring(s.lastIndexOf(')') + 2).split(" ")[7]); }
    static long rssMb() throws Exception { for (String l : Files.readAllLines(Paths.get("/proc/self/status"))) if (l.startsWith("VmRSS:")) return Long.parseLong(l.replaceAll("[^0-9]", "")) / 1024; return -1; }
    static void cycle(ByteBufAllocator a, int n, int size, String label) throws Exception {
        long f0 = minflt(), t0 = System.nanoTime();
        List<ByteBuf> bufs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) { ByteBuf b = a.directBuffer(size); b.writeByte(1); bufs.add(b); }
        long t1 = System.nanoTime(), f1 = minflt();
        System.out.printf("%-14s load: %5d ms, %7d minor faults, rss=%5d MB%n", label, (t1 - t0) / 1_000_000, f1 - f0, rssMb());
        for (ByteBuf b : bufs) b.release();
        for (int i = 0; i < 100_000; i++) a.directBuffer(size).release();
        System.out.printf("%-14s after release+trickle: rss=%5d MB%n", label, rssMb());
    }
    public static void main(String[] args) throws Exception {
        ByteBufAllocator a;
        if (args[0].equals("ADAPTIVE")) a = new AdaptiveByteBufAllocator(true, false);
        else { Object b = Class.forName("io.github.neoionet.netty.mimalloc.MiByteBufAllocator").getMethod("builder").invoke(null); a = (ByteBufAllocator) b.getClass().getMethod("build").invoke(b); }
        int n = Integer.parseInt(args[1]), size = Integer.parseInt(args[2]);
        java.io.File trig = args.length > 3 ? new java.io.File(args[3]) : null;
        for (int c = 1; c <= 3; c++) {
            cycle(a, n, size, args[0] + " cycle " + c);
            if (trig != null) { System.out.println("PAUSE " + c + " pid " + ProcessHandle.current().pid()); System.out.flush(); while (!trig.exists()) Thread.sleep(50); trig.delete(); }
        }
    }
}
