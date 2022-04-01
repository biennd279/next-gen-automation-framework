package me.d3s34.lib.process

import eu.jrie.jetbrains.kotlinshell.processes.execution.ProcessExecutable
import eu.jrie.jetbrains.kotlinshell.shell.Shell
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.d3s34.lib.command.Commandline

@OptIn(ExperimentalCoroutinesApi::class)
fun Shell.buildSystemExecutor(commandline: Commandline): ProcessExecutable {
    return systemProcess {
        commandline.path withArgs commandline.escapedArgs
    }
}
