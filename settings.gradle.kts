plugins {
    // Lets Gradle download the JDK that the Java toolchain in app/build.gradle.kts asks for, so building needs no
    // locally installed JDK of that exact version.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "lrugctest"

dependencyResolutionManagement {
    // Repositories live here rather than in project build files; a project that declares its own is a build error.
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

include("app")
