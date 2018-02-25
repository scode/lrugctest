java_binary(
    name = "lrugctest",
    srcs = ["LruGcTest.java"],
    main_class = "org.scode.lrugctest.LruGcTest",
    deps = [
        ":cachechurner",
        "//:argparse4j",
    ],
)

java_library(
    name = "lrucache",
    srcs = ["LruCache.java"],
)

java_library(
    name = "lrucache-test",
    srcs = ["LruCacheTest.java"],
    deps = [
        ":lrucache",
        "//:junit_junit",
    ],
)

java_test(
    name = "LruCacheTest",
    size = "small",
    runtime_deps = [
        ":lrucache-test",
    ],
)

java_library(
    name = "cachechurner",
    srcs = ["CacheChurner.java"],
)

java_library(
    name = "cachechurner-test",
    srcs = ["CacheChurnerTest.java"],
    deps = [
        ":cachechurner",
        "//:junit_junit",
    ],
)

java_test(
    name = "CacheChurnerTest",
    size = "small",
    runtime_deps = [
        ":cachechurner-test",
    ],
)
