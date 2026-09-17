---
title: Contents
toc: false
---

- [1. Allocaotrs](#1-allocaotrs)
- [2. Servers](#2-servers)
- [3. Thread types](#3-thread-types)
- [4. Threads count](#4-threads-count)
- [5. Java](#5-java)
- [6. JVM args](#6-jvm-args)
- [7. Data](#7-data)
- [8. Benchmark code](#8-benchmark-code)
- [9. Code base](#9-code-base)
- [10. Max live buffers per thread](#10-max-live-buffers-per-thread)
- [11. Switch on read-write](#11-switch-on-read-write)

#### 1. Allocaotrs:
- [PooledByteBufAllocator](https://github.com/franz1981/netty/blob/58005864c969f8ddddd98ef634891f4e30dae039/buffer/src/main/java/io/netty/buffer/PooledByteBufAllocator.java).
- [AdaptivePoolingAllocator](https://github.com/franz1981/netty/blob/58005864c969f8ddddd98ef634891f4e30dae039/buffer/src/main/java/io/netty/buffer/AdaptivePoolingAllocator.java).
- [MiMallocByteBufAllocator](https://github.com/neoionet/netty-allocator/blob/9de9d9187ea75eea9869ce6b253b6a9a988d2bd2/mimalloc/src/main/java/io/github/neoionet/netty/mimalloc/MiMallocByteBufAllocator.java).

#### 2. Servers:
<details>
      <summary>ARM server: 16 cores, 60 GB ram.</summary>
<pre>
Architecture:                aarch64
  CPU op-mode(s):            64-bit
  Byte Order:                Little Endian
CPU(s):                      16
  On-line CPU(s) list:       0-15
Vendor ID:                   ARM
  Model name:                Neoverse-V2
    BIOS Model name:         Axion  CPU @ 3.0GHz
    BIOS CPU family:         257
    Model:                   1
    Thread(s) per core:      1
    Core(s) per socket:      16
    Socket(s):               1
    Stepping:                r0p1
    BogoMIPS:                2000.00
    Flags:                   fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid asimdrdm jscvt fcma lrcpc dcpop sha3 sm3 sm4 asimddp sha512 sve asimdfhm dit usca
                             t ilrcpc flagm sb paca pacg dcpodp sve2 sveaes svepmull svebitperm svesha3 svesm4 flagm2 frint svei8mm svebf16 i8mm bf16 dgh rng bti
Caches (sum of all):         
  L1d:                       1 MiB (16 instances)
  L1i:                       1 MiB (16 instances)
  L2:                        32 MiB (16 instances)
  L3:                        80 MiB (1 instance)
NUMA:                        
  NUMA node(s):              1
  NUMA node0 CPU(s):         0-15
Vulnerabilities:             
  Gather data sampling:      Not affected
  Indirect target selection: Not affected
  Itlb multihit:             Not affected
  L1tf:                      Not affected
  Mds:                       Not affected
  Meltdown:                  Not affected
  Mmio stale data:           Not affected
  Reg file data sampling:    Not affected
  Retbleed:                  Not affected
  Spec rstack overflow:      Not affected
  Spec store bypass:         Mitigation; Speculative Store Bypass disabled via prctl
  Spectre v1:                Mitigation; __user pointer sanitization
  Spectre v2:                Mitigation; CSV2, BHB
  Srbds:                     Not affected
  Tsa:                       Not affected
  Tsx async abort:           Not affected
      </pre>
  </details>  
<br>

<details>
      <summary>x86 server: 16 cores, 60 GB ram.</summary>
<pre>
Architecture:                x86_64
  CPU op-mode(s):            32-bit, 64-bit
  Address sizes:             52 bits physical, 57 bits virtual
  Byte Order:                Little Endian
CPU(s):                      16
  On-line CPU(s) list:       0-15
Vendor ID:                   GenuineIntel
  Model name:                INTEL(R) XEON(R) PLATINUM 8581C CPU @ 2.30GHz
    CPU family:              6
    Model:                   207
    Thread(s) per core:      2
    Core(s) per socket:      8
    Socket(s):               1
    Stepping:                2
    BogoMIPS:                4600.00
    Flags:                   fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ss ht syscall nx pdpe1gb rdtscp lm constant_tsc rep_g
                             ood nopl xtopology nonstop_tsc cpuid tsc_known_freq pni pclmulqdq monitor ssse3 fma cx16 pcid sse4_1 sse4_2 x2apic movbe popcnt aes xsave avx f16c rdra
                             nd hypervisor lahf_lm abm 3dnowprefetch ssbd ibrs ibpb stibp ibrs_enhanced fsgsbase tsc_adjust bmi1 hle avx2 smep bmi2 erms invpcid rtm avx512f avx512d
                             q rdseed adx smap avx512ifma clflushopt clwb avx512cd sha_ni avx512bw avx512vl xsaveopt xsavec xgetbv1 xsaves avx_vnni avx512_bf16 wbnoinvd arat avx512
                             vbmi umip avx512_vbmi2 gfni vaes vpclmulqdq avx512_vnni avx512_bitalg avx512_vpopcntdq la57 rdpid cldemote movdiri movdir64b fsrm md_clear serialize ts
                             xldtrk amx_bf16 avx512_fp16 amx_tile amx_int8 arch_capabilities
Virtualization features:     
  Hypervisor vendor:         KVM
  Virtualization type:       full
Caches (sum of all):         
  L1d:                       384 KiB (8 instances)
  L1i:                       256 KiB (8 instances)
  L2:                        16 MiB (8 instances)
  L3:                        260 MiB (1 instance)
NUMA:                        
  NUMA node(s):              1
  NUMA node0 CPU(s):         0-15
Vulnerabilities:             
  Gather data sampling:      Not affected
  Indirect target selection: Not affected
  Itlb multihit:             Not affected
  L1tf:                      Not affected
  Mds:                       Not affected
  Meltdown:                  Not affected
  Mmio stale data:           Not affected
  Reg file data sampling:    Not affected
  Retbleed:                  Not affected
  Spec rstack overflow:      Not affected
  Spec store bypass:         Mitigation; Speculative Store Bypass disabled via prctl
  Spectre v1:                Mitigation; usercopy/swapgs barriers and __user pointer sanitization
  Spectre v2:                Mitigation; Enhanced / Automatic IBRS; IBPB conditional; PBRSB-eIBRS SW sequence; BHI BHI_DIS_S
  Srbds:                     Not affected
  Tsa:                       Not affected
  Tsx async abort:           Not affected
  Vmscape:                   Not affected
      </pre>
  </details>  
<br>

#### 3. Thread types:
- `Event loop thread`: AKA `FastThreadLocalThread` in Netty.
- `Platform thread`.

#### 4. Threads count:
- `32` threads, which equals to 2 × 16 cores, aligned with the default configuration commonly used in production env.

#### 5. Java:
- `OpenJDK-21.0.12.1`.

#### 6. JVM args:
- `-XX:InitialRAMPercentage=40.0 -XX:MaxRAMPercentage=40.0`
- `-Dio.netty.leakDetection.level=disabled`
- `-dsa -da`

#### 7. Data:
- `SOCKET_PROXY`: Generated by [WebSocketProxyPattern.java](https://github.com/neoionet/netty-allocator/blob/9de9d9187ea75eea9869ce6b253b6a9a988d2bd2/benchmark/src/main/java/io/github/neoionet/netty/microbenchmark/data/WebSocketProxyPattern.java), the data was copied from netty's [AllocationPatternSimulator](https://github.com/netty/netty/blob/ec4efdbbeebf024b64e0fb782184989835c9ab92/microbench/src/main/java/io/netty/buffer/AllocationPatternSimulator.java#L53), which derived from a web socket proxy service.
- `API_GATEWAY`: Generated by [ApiGatewayPattern.java](https://github.com/neoionet/netty-allocator/blob/9de9d9187ea75eea9869ce6b253b6a9a988d2bd2/benchmark/src/main/java/io/github/neoionet/netty/microbenchmark/data/ApiGatewayPattern.java), which generates sizes based on a log-normal distribution, which is commonly used to model network traffic patterns, the median size ≈ 2 KiB; the mean size ≈ 3.3 KiB; the P95 ≈ 10KiB; the P99 ≈ 20 KiB, the sizes are constrained to be between 8 bytes to 1 MiB.

#### 8. Benchmark code:
- [ByteBufAllocatorAllocPatternBenchmark.java](https://raw.githubusercontent.com/laosijikaichele/garage/refs/heads/main/bench/m/260916/ByteBufAllocatorAllocPatternBenchmark.java).

#### 9. Code base:
- Netty-4.2.17.Final-SNAPSHOT, which compiled based on https://github.com/franz1981/netty/tree/58005864c969f8ddddd98ef634891f4e30dae039.
- Mimalloc-netty-allocator: [1.2.0.Final](https://github.com/neoionet/netty-allocator/tree/9de9d9187ea75eea9869ce6b253b6a9a988d2bd2).

#### 10. Max live buffers per thread: 
- `MAX_LIVE_BUFFERS`: [128, 1024, 4096, 8192, 16384, 32768, 65536].
- Allocations on platform threads skipped `65536`, as the pooled allocator threw `OOM` when `MAX_LIVE_BUFFERS` reached `65536` with heap allocations.

#### 11. Switch on read-write: 
- `enableReadWrite`: if true, enables read and write on each buffer to simulate real-world usage.
  
