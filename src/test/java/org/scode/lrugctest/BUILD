java_library(
    name = "lrugctest",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/org/scode/lrugctest",
        "//:junit_junit",
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
