package me.d3s34.tplmap

import kotlinx.coroutines.CoroutineScope
import me.d3s34.docker.ContainerAttachClient
import me.d3s34.docker.DockerClientManager
import kotlin.coroutines.CoroutineContext

class TplmapDockerEngine(
    override val coroutineContext: CoroutineContext
): CoroutineScope {

    private val dockerClientManager = DockerClientManager()

    private val dockerClient = dockerClientManager.dockerClient

    fun tryGetShell(tplmapRequest: TplmapRequest): ContainerAttachClient {
        val containerId = dockerClientManager
            .createTplmapContainer(tplmapRequest)!!

        return ContainerAttachClient(containerId, dockerClient, this)
    }

    fun validate(tplmapRequest: TplmapRequest): Boolean {
        val containerId = dockerClientManager.createTplmapValidateContainer(
            tplmapRequest.copy(
                osShell = false,
                osCmd = "echo naftestnaftest"
            )
        )!!

        dockerClient.startContainerCmd(containerId).exec()

        val result = dockerClientManager.getContainerLog(containerId)

        return result.contains("naftestnaftest")
    }
}