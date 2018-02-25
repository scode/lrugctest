java_library(
    name = "tests",
    srcs = glob(["*.java"]),
    deps = [
        "//:junit_junit",
        "//src/java/org/scode/lrugctest:lrucache",
    ],
)

java_test(
    name = "LruCacheTest",
    size = "small",
    runtime_deps = [
        ":tests",
    ],
)
