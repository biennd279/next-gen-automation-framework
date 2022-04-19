val ktorVersion: String = "2.0.0"
val kotlinVersion: String = "1.6.20"
val kotlinxSerializationVersion = "1.3.2"
val coroutinesVersion = "1.6.0"
val decomposeVersion = "0.6.0"

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
    gradlePluginPortal()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.20"
    id("org.jetbrains.compose") version "1.1.1"
}

dependencies {
    // Runtime
    api("org.slf4j:slf4j-nop:1.7.36")

    // Classpath
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    implementation("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    // Web client dependencies
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Ui dependencies
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
    implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
}

// tasks.test {
//    useJUnitPlatform()
// }
