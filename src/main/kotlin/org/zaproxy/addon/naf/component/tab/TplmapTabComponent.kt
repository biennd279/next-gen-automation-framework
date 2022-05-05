package org.zaproxy.addon.naf.component.tab

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.d3s34.docker.ContainerAttachClient
import me.d3s34.tplmap.TplmapDockerEngine
import me.d3s34.tplmap.TplmapRequest

class TplmapTabComponent(
    val tplmapDockerEngine: TplmapDockerEngine,
    override val coroutineScope: CoroutineScope
): ExploitTabComponent(title = "tplmap"), ShellTabComponent {
    val tplmapRequest = mutableStateOf(TplmapRequest("", osShell = true))
    override val shellContent = MutableStateFlow<List<String>>(emptyList())
    override val hasNewLine = mutableStateOf(false)
    lateinit var client: ContainerAttachClient

    override val status: StateFlow<ContainerAttachClient.Status>
        get() = if (this::client.isInitialized) {
            client.status
        } else {
            notRunningState
        }

    override fun sendCommand(command: String) {
        client.send(command.toByteArray())
    }

    override suspend fun exploit() {
        client = tplmapDockerEngine.tryGetShell(tplmapRequest.value)
        attachJob(client)
    }

    override suspend fun onClose() {}
}