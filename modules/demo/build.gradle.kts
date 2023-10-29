//plugins {
//    id("java")
//    alias(libs.plugins.spring.boot)
//    alias(libs.plugins.spring.dependency.management)
//
////    kotlin("jvm") version "1.8.10" apply false
////    kotlin("kapt") version "1.8.10" apply false
////    kotlin("plugin.spring") version "1.8.10" apply false
//
//    kotlin("jvm") version "1.8.10"
//    kotlin("plugin.spring") version "1.8.10"
//    kotlin("plugin.jpa") version "1.8.10"
//    kotlin("plugin.serialization") version "1.8.10"
//}

@Suppress(
        "DSL_SCOPE_VIOLATION",
        "MISSING_DEPENDENCY_CLASS",
        "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
        "FUNCTION_CALL_EXPECTED"
)
plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
    maven("https://repo.spring.io/release")
    maven("https://repo.spring.io/snapshot")
    maven("https://repo.spring.io/milestone")
    maven("https://packages.confluent.io/maven/")
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.rest)
//    implementation(libs.springdoc.openapi.starter.webmvc.ui)
//    implementation(libs.springdoc.openapi.starter.webmvc.api)
    implementation(libs.h2)
}


//springBoot {
//    buildInfo()
//}
//
//bootJar {
//    archiveFileName = 'timetable-service.jar'
//}
//
//bootBuildImage {
//    dependsOn(["copyNewRelicConfiguration", "copyNewRelicArtifacts"])
//    buildpacks = [
//        "urn:cnb:builder:paketo-buildpacks/java",
//        "gcr.io/paketo-buildpacks/new-relic"
//    ]
//    bindings = [
//        "build/new-relic:/platform/bindings/new-relic"
//    ]
//}
//
//newRelic {
//    appName = "Demo"
//}
