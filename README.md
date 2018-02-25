# lrugctest

## How to use

## Get Bazel

If you don't have [bazel](https://bazel.build/) installed, install it:

```bash
nix-env -i bazel   # using nix (linux/macOS)
brew install bazel # using homebrew (macOS)
```

Ubuntu users, see
[instructions](https://docs.bazel.build/versions/master/install-ubuntu.html)
(or just use [Nix](https://nixos.org/nix/)).

## Run it

```bash
bazel run //src/java/org/scode/lrugctest:lrugctest
```
