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

### Combine with jHiccup

The excellent [jHiccup](https://github.com/giltene/jHiccup) is great to combine with this test.

```bash
git clone https://github.com/giltene/jHiccup
cd jHiccup
mvn package
```

That should have build `target/jHiccup.jar`. You can run this test
with jHiccup as such (substitute your acutal path):

```bash
_JAVA_OPTIONS='-javaagent:/path/to/jHiccup/target/jHiccup.jar="-d 20000 -i 1000"' LRUGCTEST_COLLECTOR=g1 ./lrugctest
```

You can then use `/path/to/jHiccupLogProcessor -i PATH_TO_LOG` et al
(see [jHiccup](https://github.com/giltene/jHiccup)).

The log is located in a hard-to-find place. To find where, `cat
./lrugctest-bazel` and look at the diredctory it's entering before
executing the tool. It will contain the jhiccup log files.

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
