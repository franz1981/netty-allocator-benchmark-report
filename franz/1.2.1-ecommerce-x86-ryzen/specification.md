---
title: Contents
toc: false
---

Re-run of [lao's 1.2.1 E_COMMERCE benchmark](https://github.com/netty/netty/discussions/17485#discussioncomment-18505016) on x86, same setup as [my 1.2.0 re-run](../1.2.0-x86-ryzen/specification): AMD Ryzen 9 7950X restricted to one NUMA node (8 cores x 2 HT = 16 vCPUs, `numactl --cpunodebind=0 --preferred=0`), `-XX:MaxRAM=60g` (heap 24 GiB), 2300 MHz with boost off, 32 threads, Oracle JDK 21.0.11, JMH `-t 32 -f 1 -wi 10 -i 10 -w 1 -r 1`, read/write on. Swap used: 0.

#### Series
- `ADAPTIVE #17151`: [franz1981/netty@5800586](https://github.com/franz1981/netty/tree/58005864c969f8ddddd98ef634891f4e30dae039).
- `ADAPTIVE + size classes`: the same plus [one commit](https://github.com/franz1981/netty/commit/4c1621ed58) adding size classes up to 128 KiB (branch [4.2_striped_heap_size_classes](https://github.com/franz1981/netty/tree/4.2_striped_heap_size_classes)).
- `MIMALLOC 1.2.1`: [neoionet/netty-allocator@397e933](https://github.com/neoionet/netty-allocator/tree/397e93304a5f17fe1361c3e56e72526088786f35).

#### Data and benchmark code
- `E_COMMERCE`: sizes from `e-commerce.jfr` (netty/netty-allocator-profiles), through lao's `ECommercePattern.java` and `ByteBufAllocatorAllocPatternBenchmark.java` at 397e933, unmodified.
- The Netty classes in the benchmark jar were compared byte for byte against builds of the two commits above (lao's pom resolves `netty 4.2.17.Final`, so they were replaced).
- RSS in the charts is peak RSS (VmHWM), as in lao's pages.
