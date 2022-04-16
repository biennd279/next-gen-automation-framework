
val ktor_version: String = "2.0.0"
val kotlin_version: String = "1.6.20"
val kotlinx_serialization_version = "1.3.2"
description = "Next gen automation framework addon for ZAP"

zapAddOn {
    addOnName.set("Next gen automation framework")
    zapVersion.set("2.11.1")

    manifest {
        author.set("NDBien from UET/VNU")
    }
}

crowdin {
    configuration {
        val resourcesPath = "org/zaproxy/addon/${zapAddOn.addOnId.get()}/resources/"
        tokens.put("%messagesPath%", resourcesPath)
        tokens.put("%helpPath%", resourcesPath)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

plugins {
    kotlin("jvm") version "1.6.20"
    kotlin("plugin.serialization") version "1.6.20"
}

dependencies {
    api(kotlin("stdlib"))

    // Runtime
    api(kotlin("stdlib"))
    api("org.slf4j:slf4j-nop:1.7.36")

    // Classpath
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-auth:$ktor_version")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
}

// tasks.test {
//    useJUnitPlatform()
// }
