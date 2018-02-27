genrule(
    name = "hello",
    outs = ["hello_world.txt"],
    cmd = "echo Hello World > $@",
)

java_library(
    name = "junit_junit",
    visibility = ["//visibility:public"],
    exports = [
        "@junit_junit//jar",
    ],
)

java_library(
    name = "argparse4j",
    visibility = ["//visibility:public"],
    exports = [
        "@net_sourcrforge_argparse4j_argparse4j//jar",
    ],
)
