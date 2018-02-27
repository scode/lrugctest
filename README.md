# lrugctest

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
