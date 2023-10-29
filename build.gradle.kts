plugins {
    id("java")
////    id("org.springframework.boot") version "2.7.0-SNAPSHOT" apply false
////    id("org.springframework.boot") version "2.5.4" apply false
////    id("org.springframework.boot") version "3.0.5" apply false
////    id("org.springframework.boot") version "3.1.2" apply false
//    alias(libs.plugins.spring.boot) apply false
////    id("io.spring.dependency-management") version "1.0.15.RELEASE" apply false
//    alias(libs.plugins.spring.dependency.management) apply false
//
//    id("com.github.johnrengelman.processes") version "0.5.0" apply false
//    id("org.springdoc.openapi-gradle-plugin") version "1.3.2" apply false
//    id("com.gorylenko.gradle-git-properties") version "2.4.1" apply false
//
//    id("com.github.ben-manes.versions") version "0.41.0"
//
////	id("com.palantir.git-version") version "0.12.3"
//    id("fr.brouillard.oss.gradle.jgitver") version "0.10.0-rc03"
//
//    kotlin("jvm") version "1.8.10" apply false
//    kotlin("kapt") version "1.8.10" apply false
//    kotlin("plugin.spring") version "1.8.10" apply false
//
    id("org.openrewrite.rewrite") version "5.38.0"
//    id("github") apply false
}

//rewrite {
//    activeRecipe("org.openrewrite.java.migrate.jakarta.JavaxAnnotationMigrationToJakartaAnnotation")
//}

//jgitver {
//    strategy = fr.brouillard.oss.jgitver.Strategies.MAVEN
//}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    rewrite("org.openrewrite.recipe:rewrite-migrate-java:1.18.0")
}

