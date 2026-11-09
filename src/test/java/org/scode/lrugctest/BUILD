load("@rules_java//java:java_library.bzl", "java_library")
load("@rules_java//java:java_test.bzl", "java_test")

java_library(
    name = "lrugctest",
    srcs = glob(["*.java"]),
    deps = [
        "//:junit_junit",
        "//src/main/java/org/scode/lrugctest",
    ],
)

java_test(
    name = "LruCacheTest",
    size = "small",
    runtime_deps = [
        ":lrugctest",
    ],
)

java_test(
    name = "CacheChurnerTest",
    size = "small",
    runtime_deps = [
        ":lrugctest",
    ],
)

java_test(
    name = "RateLimiterTest",
    size = "small",
    runtime_deps = [
        ":lrugctest",
    ],
)

java_test(
    name = "HiccupDetectorTest",
    size = "small",
    runtime_deps = [
        ":lrugctest",
    ],
)
