plugins {
    id("java-gradle-plugin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
//    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
//    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
//    implementation("org.postgresql:postgresql:42.2.19")
}

gradlePlugin {
    plugins {
/*
        create("new-relic") {
            id = "new-relic"
            implementationClass = "org.warrenroad.NewRelicPlugin"
        }
*/
        create("honeycomb") {
            id = "honeycomb"
            implementationClass = "org.warrenroad.HoneycombPlugin"
        }
    }
}
