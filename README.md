# lrugctest

A garbage collector stress tester for the JVM.

## How to use

### Get Bazel

If you don't have [bazel](https://bazel.build/) installed, install it:

```bash
nix-env -i bazel   # using nix (linux/macOS)
brew install bazel # using homebrew (macOS)
```

Ubuntu users, see
[instructions](https://docs.bazel.build/versions/master/install-ubuntu.html)
(or just use [Nix](https://nixos.org/nix/)).

### Run it

```bash
LRUGCTEST_COLLECTOR=g1 ./lrugctest
```

The following collectors are supported: throughput, cms, g1, shenandoah

For command line options:

```bash
./lrugctest --help
```

## What it does

We generate an LRU cache of many small items and continuously churn
cache lookups (and puts on misses) to generate garbage.

LRU caches tend to be one of the most difficult workloads for garbage
collectors because of the fact that there's a ton of *old* objects
that become garbage. This defeats the optimizations of generational
garbage collectors.

We generate a huge number of small items in the cache (rather than a
small number of large items) because a large number of small objects
tends to be more difficult for the garbage collector to deal with
(e.g., many inter-regional pointers inflating remembered sets, causing
additional scanning, generating many read/write barriers, additional
cost to tracing, etc).

We randomize the order of lookups in the LRU cache, such that there is
no relationship between the relative timing of object allocation and
it becoming garbage. We avoid, for example, objects sequentially
becoming garbage as a result of initially being allocated
sequentially. This means that garbage objects should be spread out
across all regions (for garbage collectors that have them), rather
than being lumped together in mostly-garbage regions. This should make
the garbage more expensive to collect.
