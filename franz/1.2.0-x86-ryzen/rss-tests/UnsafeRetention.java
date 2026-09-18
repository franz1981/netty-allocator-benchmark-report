import java.lang.reflect.Field;
import java.nio.file.*;

/** glibc behaviour with plain malloc/free (sun.misc.Unsafe), no allocator involved.
 *  args: blockSize count cycles keep [threads]   where keep = none | oldest:K | newest:K
 *  With threads > 1 each thread runs the same cycle on its own blocks (so on its own glibc arena).
 *  Each cycle: malloc `count` blocks of `blockSize` (touching every page), then free them all except `keep`; print RSS.
 *  Kept blocks are freed at the start of the next cycle (so retention is per cycle, like an allocator's cache). */
public class UnsafeRetention {
    static long rssMb() throws Exception { for (String l : Files.readAllLines(Paths.get("/proc/self/status"))) if (l.startsWith("VmRSS:")) return Long.parseLong(l.replaceAll("[^0-9]", "")) / 1024; return -1; }
    public static void main(String[] a) throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); f.setAccessible(true); sun.misc.Unsafe U = (sun.misc.Unsafe) f.get(null);
        long size = Long.parseLong(a[0]); int count = Integer.parseInt(a[1]), cycles = Integer.parseInt(a[2]); String keep = a[3];
        int k = keep.equals("none") ? 0 : Integer.parseInt(keep.split(":")[1]); boolean newest = keep.startsWith("newest");
        int threads = a.length > 4 ? Integer.parseInt(a[4]) : 1; int per = count / threads;
        System.out.printf("block %d KiB x %d = %d MiB, keep=%s, threads=%d%n", size / 1024, count, size * count / 1048576, keep, threads);
        long[][] kept = new long[threads][0];
        for (int c = 1; c <= cycles; c++) {
            final int cc = c; long[] live = new long[1];
            java.util.concurrent.CyclicBarrier freed = new java.util.concurrent.CyclicBarrier(threads, () -> { try { live[0] = rssMb(); } catch (Exception e) { throw new RuntimeException(e); } });
            Thread[] ts = new Thread[threads];
            for (int t = 0; t < threads; t++) { final int tt = t; ts[t] = new Thread(() -> { try {
                for (long p : kept[tt]) U.freeMemory(p);
                long[] blocks = new long[per];
                for (int i = 0; i < per; i++) { blocks[i] = U.allocateMemory(size); for (long o = 0; o < size; o += 4096) U.putByte(blocks[i] + o, (byte) 1); }
                freed.await();
                int from = newest ? per - k : 0, to = newest ? per : k;
                for (int i = 0; i < per; i++) if (i < from || i >= to) U.freeMemory(blocks[i]);
                kept[tt] = java.util.Arrays.copyOfRange(blocks, from, to);
            } catch (Exception e) { throw new RuntimeException(e); } }); ts[t].start(); }
            for (Thread t : ts) t.join();
            System.out.printf("  cycle %d: live rss=%5d MB -> after free rss=%5d MB%n", c, live[0], rssMb());
        }
    }
}
