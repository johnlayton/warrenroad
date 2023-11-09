pluginManagement {
    repositories {
        maven("https://repo.spring.io/release")
        maven("https://packages.confluent.io/maven/")
        mavenCentral()
//        maven("https://repo.spring.io/release")
//        maven("https://repo.spring.io/snapshot")
//        maven("https://repo.spring.io/milestone")
//        mavenCentral()
        maven("https://plugins.gradle.org/m2")
        gradlePluginPortal()
    }
}


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "warrenroad"

include(
    "modules:demo"
)
