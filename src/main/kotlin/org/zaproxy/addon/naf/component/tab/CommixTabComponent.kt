package org.zaproxy.addon.naf.component.tab

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.d3s34.commix.CommixDockerEngine
import me.d3s34.commix.CommixRequest
import me.d3s34.docker.ContainerAttachClient

class CommixTabComponent(
    val commixDockerEngine: CommixDockerEngine,
    override val coroutineScope: CoroutineScope
): ExploitTabComponent(title = "Commix"), ShellTabComponent {
    val commixRequest = mutableStateOf(CommixRequest(""))
    override val shellContent = MutableStateFlow<List<String>>(emptyList())
    override val hasNewLine = mutableStateOf(false)

    lateinit var client: ContainerAttachClient

    override val status: StateFlow<ContainerAttachClient.Status>
        get() = if (this::client.isInitialized) {
            client.status
        } else {
            notRunningState
        }

    override suspend fun exploit() {
        client = commixDockerEngine.tryGetShell(commixRequest.value)
        attachJob(client)
    }

    override fun sendCommand(command: String) {
        client.send(command.toByteArray())
    }

    override suspend fun onClose() {}
}