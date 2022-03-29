package me.d3s34.feature.nuclei

import eu.jrie.jetbrains.kotlinshell.processes.process.Process
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun Process.throwOnError() {
    if (this.pcb.exitCode != 0) {
        throw Exception("${this.name} failed with status ${this.pcb.exitCode}")
    }
}

