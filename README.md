# lrugctest

A garbage collector stress tester for the JVM.

## How to use

### Get Bazelisk

`bazelisk` must be in your `PATH`. If you have Go installed usually this can be done with:

```
go install github.com/bazelbuild/bazelisk@latest
```

For more information see [bazelisk README](https://github.com/bazelbuild/bazelisk).

This repository uses Bazel modules (Bzlmod), so Bazel 7+ will manage external dependencies defined in `MODULE.bazel`.
The build targets Java 11, so ensure your `JAVA_HOME` points at JDK 11 or newer when running.

### Run it

```bash
# Adjust JAVA_HOME accordingly.

JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home LRUGCTEST_COLLECTOR=g1 ./lrugctest
```

The following collectors are supported: throughput, g1, shenandoah, z

For command line options:

```bash
./lrugctest --help
```

### Interpreting output

Other than a mountain of JVM output, the built-in "hiccup detector" will print lines like:

```
HICCUP: 3449414ns (3ms)
```

This occurrs whenever a hiccup is detected longer than the specified
threshold (`--hiccup-threshold-nanos`).

For better hiccup detection see the next section. The built-in hiccup
detector is trivial and there for convenience. It will simply sleep
for a millisecond at a time and measure how long it slept. The delta
between actual sleep time and requested sleep time is the hiccup
length.

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

## Examples

Reasonably interesting starting point for a ~25 gb box with 4 cores
and zgc:

```bash
_JAVA_OPTIONS="-Xms16G -Xmx16G" \
JAVABIN=~/jdk/bin/java \
LRUGCTEST_COLLECTOR=z \
./lrugctest -t 4 --hiccup-threshold-nanos=10000000 -s 50000000 -r1000000
```
