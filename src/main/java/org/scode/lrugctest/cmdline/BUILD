load("@rules_java//java:java_binary.bzl", "java_binary")

java_binary(
    name = "cmdline",
    srcs = ["Main.java"],
    main_class = "org.scode.lrugctest.cmdline.Main",
    deps = [
        "//src/main/java/org/scode/lrugctest",
        "//:argparse4j",
    ],
)
