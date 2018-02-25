java_binary(
    name = "lrugctest",
    srcs = ["LruGcTest.java"],
    main_class = "org.scode.lrugctest.LruGcTest",
    deps = [
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
