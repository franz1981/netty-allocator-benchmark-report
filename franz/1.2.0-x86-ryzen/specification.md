---
title: Contents
toc: false
---

Same benchmark, code base and parameters as [lao's 1.2.0 x86 run](https://github.com/netty/netty/discussions/17485#discussioncomment-18478658); only the server differs (section 2).

#### 1. Allocators:
- [PooledByteBufAllocator](https://github.com/franz1981/netty/blob/58005864c969f8ddddd98ef634891f4e30dae039/buffer/src/main/java/io/netty/buffer/PooledByteBufAllocator.java).
- [AdaptivePoolingAllocator](https://github.com/franz1981/netty/blob/58005864c969f8ddddd98ef634891f4e30dae039/buffer/src/main/java/io/netty/buffer/AdaptivePoolingAllocator.java).
- [MiMallocByteBufAllocator](https://github.com/neoionet/netty-allocator/blob/9de9d9187ea75eea9869ce6b253b6a9a988d2bd2/mimalloc/src/main/java/io/github/neoionet/netty/mimalloc/MiMallocByteBufAllocator.java).

#### 2. Server:
Bare metal AMD Ryzen 9 7950X (16 cores / 32 vCPUs, 2 NUMA nodes, 64 GB), restricted to mimic lao's x86 VM (8 cores x 2 HT = 16 vCPUs, 1 NUMA node, 60 GB, 2.3 GHz):
- `numactl --cpunodebind=0 --preferred=0` -> CPUs 0-7,16-23 (8 cores + SMT siblings), `availableProcessors()` = 16.
- `-XX:MaxRAM=60g` -> heap 24 GiB via the benchmark's `MaxRAMPercentage=40`, same as lao's box.
- Frequency boost off, `performance` governor, min = max = 2300 MHz (measured 2280-2291 MHz during the run).
- Swap used: 0 (sampled every 2 s); memory on NUMA node 1: max 0.02 GiB.
<details>
      <summary>lscpu (whole machine, before pinning)</summary>
<pre>
Architecture:                            x86_64
CPU op-mode(s):                          32-bit, 64-bit
Address sizes:                           48 bits physical, 48 bits virtual
Byte Order:                              Little Endian
CPU(s):                                  32
On-line CPU(s) list:                     0-31
Vendor ID:                               AuthenticAMD
Model name:                              AMD Ryzen 9 7950X 16-Core Processor
CPU family:                              25
Model:                                   97
Thread(s) per core:                      2
Core(s) per socket:                      16
Socket(s):                               1
Stepping:                                2
Frequency boost:                         disabled
CPU(s) scaling MHz:                      51%
CPU max MHz:                             4501.0000
CPU min MHz:                             425.2920
BogoMIPS:                                9000.68
Flags:                                   fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ht syscall nx mmxext fxsr_opt pdpe1gb rdtscp lm constant_tsc rep_good amd_lbr_v2 nopl xtopology nonstop_tsc cpuid extd_apicid aperfmperf rapl pni pclmulqdq monitor ssse3 fma cx16 sse4_1 sse4_2 x2apic movbe popcnt aes xsave avx f16c rdrand lahf_lm cmp_legacy svm extapic cr8_legacy abm sse4a misalignsse 3dnowprefetch osvw ibs skinit wdt tce topoext perfctr_core perfctr_nb bpext perfctr_llc mwaitx cpuid_fault cpb cat_l3 cdp_l3 hw_pstate ssbd mba perfmon_v2 ibrs ibpb stibp ibrs_enhanced vmmcall fsgsbase bmi1 avx2 smep bmi2 erms invpcid cqm rdt_a avx512f avx512dq rdseed adx smap avx512ifma clflushopt clwb avx512cd sha_ni avx512bw avx512vl xsaveopt xsavec xgetbv1 xsaves cqm_llc cqm_occup_llc cqm_mbm_total cqm_mbm_local user_shstk avx512_bf16 clzero irperf xsaveerptr rdpru wbnoinvd cppc arat npt lbrv svm_lock nrip_save tsc_scale vmcb_clean flushbyasid decodeassists pausefilter pfthreshold avic vgif x2avic v_spec_ctrl vnmi avx512vbmi umip pku ospke avx512_vbmi2 gfni vaes vpclmulqdq avx512_vnni avx512_bitalg avx512_vpopcntdq rdpid overflow_recov succor smca fsrm flush_l1d amd_lbr_pmc_freeze
Virtualization:                          AMD-V
L1d cache:                               512 KiB (16 instances)
L1i cache:                               512 KiB (16 instances)
L2 cache:                                16 MiB (16 instances)
L3 cache:                                64 MiB (2 instances)
NUMA node(s):                            2
NUMA node0 CPU(s):                       0-7,16-23
NUMA node1 CPU(s):                       8-15,24-31
      </pre>
  </details>
<br>

#### 3. Thread types:
- `Event loop thread`: AKA `FastThreadLocalThread` in Netty.
- `Platform thread`: `-jvmArgsAppend -Djmh.executor=PLATFORM`.

#### 4. Threads count:
- `32` threads on 16 vCPUs, as lao.

#### 5. Java:
- `Oracle Java HotSpot 21.0.11+9-LTS` (lao: OpenJDK 21.0.12.1).

#### 6. JVM args:
- `-XX:InitialRAMPercentage=40.0 -XX:MaxRAMPercentage=40.0 -XX:MaxRAM=60g`
- `-Dio.netty.leakDetection.level=disabled`
- `-dsa -da`
- JMH: `-t 32 -f 1 -wi 10 -i 10 -w 1 -r 1`

#### 7. Data:
- `SOCKET_PROXY`, `API_GATEWAY`: identical to lao's, see [WebSocketProxyPattern.java](https://github.com/neoionet/netty-allocator/blob/9de9d9187ea75eea9869ce6b253b6a9a988d2bd2/benchmark/src/main/java/io/github/neoionet/netty/microbenchmark/data/WebSocketProxyPattern.java) and [ApiGatewayPattern.java](https://github.com/neoionet/netty-allocator/blob/9de9d9187ea75eea9869ce6b253b6a9a988d2bd2/benchmark/src/main/java/io/github/neoionet/netty/microbenchmark/data/ApiGatewayPattern.java).

#### 8. Benchmark code:
- [ByteBufAllocatorAllocPatternBenchmark.java](https://raw.githubusercontent.com/laosijikaichele/garage/refs/heads/main/bench/m/260916/ByteBufAllocatorAllocPatternBenchmark.java), unmodified.

#### 9. Code base:
- Netty-4.2.17.Final-SNAPSHOT built from [58005864c9](https://github.com/franz1981/netty/tree/58005864c969f8ddddd98ef634891f4e30dae039).
- Mimalloc-netty-allocator [9de9d9187e](https://github.com/neoionet/netty-allocator/tree/9de9d9187ea75eea9869ce6b253b6a9a988d2bd2).
- The classes in the benchmark jar were compared byte for byte against fresh builds of both commits.

#### 10. Max live buffers per thread:
- `MAX_LIVE_BUFFERS`: [128, 1024, 4096, 8192, 16384, 32768, 65536].

#### 11. Switch on read-write:
- `enableReadWrite`: if true, enables read and write on each buffer to simulate real-world usage.
