# lrugctest

A garbage collector stress tester for the JVM.

## How to use

### Prerequisites

The only prerequisite is a JDK 25 or newer, for example `brew install openjdk`. Nothing else needs installing: the
Gradle wrapper (`./gradlew`) downloads the Gradle version pinned in `gradle/wrapper/gradle-wrapper.properties`, and
Gradle downloads the JDK 25 it builds with. That makes the first run slow (about a minute on my machine, mostly
downloads); later runs spend a few seconds checking that the build is up to date before the test starts. The JDK you
point the script at only decides what the test runs on, not how it is built.

To build and run the unit tests without running the workload, use `./gradlew build`. Run that way, the wrapper needs
`JAVA_HOME` set or a `java` on your `PATH` to start on; `./lrugctest` takes care of that itself.

### Run it

The script builds the project and runs it on the java found via `JAVABIN`, then `JAVA_HOME`, then `PATH`, so set one
of those to pick the JDK under test. It refuses to run on anything older than JDK 25.

On Linux, Homebrew's JDK is on your `PATH` after `brew install openjdk`, so this is enough:

```bash
LRUGCTEST_COLLECTOR=z ./lrugctest
```

NOTE: On macOS, Homebrew's `openjdk` is keg-only, so it is not on your `PATH` and `/usr/bin/java` is a stub that
fails unless some other JDK is installed. Point `JAVA_HOME` at it instead:

```bash
JAVA_HOME=$(brew --prefix openjdk)/libexec/openjdk.jdk/Contents/Home LRUGCTEST_COLLECTOR=z ./lrugctest
```

`LRUGCTEST_COLLECTOR` selects one of:

- `g1` (the default)
- `z` (ZGC, which is generational-only as of JDK 24)
- `shenandoah` (in generational mode)
- `throughput` (Parallel GC)

The heap is fixed at 2 GB. Use `_JAVA_OPTIONS` to override that or to add other JVM flags, since the JVM applies it
after the flags the script passes. To change the heap, set both `-Xms` and `-Xmx` (as in the example at the end);
an `-Xmx` alone below 2 GB conflicts with the script's `-Xms2G` and the JVM refuses to start.

For command line options:

```bash
./lrugctest --help
```

### Interpreting output

Other than a mountain of JVM output, the built-in "hiccup detector" will print lines like:

```
HICCUP: 3449414ns (3ms)
```

This occurs whenever a hiccup is detected longer than the specified
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

That should have built `target/jHiccup.jar`. You can run this test
with jHiccup as such (substitute your actual path):

```bash
_JAVA_OPTIONS='-javaagent:/path/to/jHiccup/target/jHiccup.jar="-d 20000 -i 1000"' LRUGCTEST_COLLECTOR=g1 ./lrugctest
```

The JVM runs in the directory you invoked `./lrugctest` from, so that
is where the jHiccup log files end up. You can then use
`/path/to/jHiccupLogProcessor -i PATH_TO_LOG` et al (see
[jHiccup](https://github.com/giltene/jHiccup)).

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
