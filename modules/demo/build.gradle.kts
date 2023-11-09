import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.run.BootRun
import org.warrenroad.HoneycombPlugin.HoneycombExtension

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
    id("honeycomb")
    id("opentel")
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
    implementation(libs.spring.boot.starter.actuator)
//    implementation(libs.springdoc.openapi.starter.webmvc.ui)
//    implementation(libs.springdoc.openapi.starter.webmvc.api)
    implementation(libs.h2)
    implementation(platform(libs.micrometer.tracing.bom))
    implementation(libs.opentelemetry.annotations)
    implementation(libs.micrometer.tracing)
    implementation(libs.micrometer.tracing.bridge.otel)
    implementation(libs.micrometer.registry.prometheus)
//    implementation(libs.logback.classic)
    implementation(libs.logstash.logback.encoder)
}


springBoot {
    buildInfo()
}

configure<HoneycombExtension> {
    appName = "WarrenRoad::Demo"
}

tasks.getByName<BootRun>("bootRun") {
    setEnvironment(mapOf(
            "HONEYCOMB_API_KEY" to System.getenv("HONEYCOMB_API_KEY"),
            "OTEL_SERVICE_NAME" to System.getenv("Demo"),
            "OTEL_EXPORTER_OTLP_ENDPOINT" to "https://api.honeycomb.io",
            "OTEL_EXPORTER_OTLP_HEADERS" to "x-honeycomb-team=" + System.getenv("HONEYCOMB_API_KEY"),
            "OTEL_TRACES_EXPORTER" to "logging",
            "OTEL_METRICS_EXPORTER" to "logging",
            "OTEL_LOGS_EXPORTER" to "logging"
    ))
    jvmArgs = listOf(
            "-javaagent:build/opentel/opentel.jar"
    )
}

tasks.named<BootBuildImage>("bootBuildImage") {
    buildpacks.set(listOf(
            "urn:cnb:builder:paketo-buildpacks/java",
            "gcr.io/paketo-buildpacks/opentelemetry"
    ))
    environment = mapOf(
            "BP_OPENTELEMETRY_ENABLED" to "true"
    )
}


//tasks.bootRun {
//    jvmArgs = [
//        '-javaagent:/newrelic.jar'
//    ]
//}

//honeycomb {
//    appName = "WarrenRoad::Demo"
//}

//bootRun {
//
//}
//tasks.getByName<BootRun>("bootRun") {
////    main = "com.example.ExampleApplication"
//    setEnvironment(mapOf(
//        "HONEYCOMB_API_KEY" to System.getenv("HONEYCOMB_API_KEY"),
//        "OTEL_SERVICE_NAME" to System.getenv("Demo"),
//        "OTEL_EXPORTER_OTLP_ENDPOINT" to "https://api.honeycomb.io",
////        "OTEL_EXPORTER_OTLP_TRACES_ENDPOINT" to "https://api.honeycomb.io/v1/traces",
////        "OTEL_EXPORTER_OTLP_METRICS_ENDPOINT" to "https://api.honeycomb.io/v1/metrics",
//        "OTEL_EXPORTER_OTLP_HEADERS" to "x-honeycomb-team=" + System.getenv("HONEYCOMB_API_KEY")
//    ))
//    jvmArgs = listOf(
//        "-javaagent:build/opentel/opentel.jar"
//    )
//}


//bootRun {
//    environment = ["HONEYCOMB_API_KEY": System.getenv("HONEYCOMB_API_KEY"),
//    "OTEL_SERVICE_NAME": System.getenv("OTEL_SERVICE_NAME")]
//    jvmArgs = ["-javaagent:honeycomb-opentelemetry-javaagent-1.5.2.jar"]
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

//tasks.named<BootBuildImage>("bootBuildImage") {
////    builder.set("mine/java-cnb-builder")
////    runImage.set("mine/java-cnb-run")
//    buildpacks.set(listOf(
//            "urn:cnb:builder:paketo-buildpacks/java",
//            "gcr.io/paketo-buildpacks/opentelemetry"
////            "gcr.io/paketo-buildpacks/opentelemetry"
////            "gcr.io/paketo-buildpacks/new-relic"
//    ))
//    environment = mapOf(
//            "BP_OPENTELEMETRY_ENABLED" to "true"
//    )
//
//}


