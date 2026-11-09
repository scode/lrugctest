plugins {
    application
}

dependencies {
    implementation(libs.argparse4j)
}

java {
    // Compiles with, and emits bytecode for, JDK 25. The `lrugctest` launcher runs the result on whatever JDK the user
    // picks, which therefore has to be 25 or newer; keep MIN_JAVA_MAJOR there in sync with this.
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "org.scode.lrugctest.cmdline.Main"
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:all")
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter(libs.versions.junit)
        }
    }
}
