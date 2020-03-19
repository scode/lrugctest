genrule(
    name = "hello",
    outs = ["hello_world.txt"],
    cmd = "echo Hello World > $@",
)

java_library(
    name = "junit_junit",
    visibility = ["//visibility:public"],
    exports = [
        "@maven//:junit_junit",
    ],
)

java_library(
    name = "argparse4j",
    visibility = ["//visibility:public"],
    exports = [
        "@maven//:net_sourceforge_argparse4j_argparse4j",
    ],
)
