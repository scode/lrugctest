load("@rules_java//java:java_library.bzl", "java_library")

# Stable local aliases for the Maven artifacts, so packages depend on //:junit_junit rather than on the generated
# repository's label scheme.

java_library(
    name = "junit_junit",
    visibility = ["//visibility:public"],
    exports = [
        "@lrug_maven//:junit_junit",
    ],
)

java_library(
    name = "argparse4j",
    visibility = ["//visibility:public"],
    exports = [
        "@lrug_maven//:net_sourceforge_argparse4j_argparse4j",
    ],
)
