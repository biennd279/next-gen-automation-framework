package org.zaproxy.addon.naf.component.tab

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.d3s34.docker.ContainerAttachClient

interface ShellTabComponent {
    val coroutineScope: CoroutineScope
    val hasNewLine: MutableState<Boolean>
    val status: StateFlow<ContainerAttachClient.Status>
    val shellContent: MutableStateFlow<List<String>>
    fun sendCommand(command: String)

    fun attachJob(client: ContainerAttachClient) {
        val writeJob = coroutineScope.launch {
            for (output in client.stdoutChannel) {
                val lines = output
                    .toString(Charsets.UTF_8)
                    .replace("\\u001B\\[[;\\d]*m".toRegex(), "")
                    .split("[\\r\\n]+".toRegex())
                    .filter { it.isNotBlank() }

                shellContent.update {
                    hasNewLine.value = true
                    it + lines
                }
            }
        }

        coroutineScope.launch {
            val callback = client.attach()!!
            client.start()

            callback.awaitCompletion()
            client.close()
            writeJob.cancelAndJoin()
        }
    }


    val notRunningState: StateFlow<ContainerAttachClient.Status>
        get() = MutableStateFlow(ContainerAttachClient.Status.NOT_RUNNING)
}
