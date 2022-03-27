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

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

dependencies {
    api(kotlin("stdlib"))

    //Runtime
    api(kotlin("stdlib"))
    api("org.slf4j:slf4j-nop:1.7.36")

    //Classpath
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")


    testImplementation(kotlin("test"))
}

//tasks.test {
//    useJUnitPlatform()
//}
