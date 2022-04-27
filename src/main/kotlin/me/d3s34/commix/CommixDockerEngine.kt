package me.d3s34.commix

import kotlinx.coroutines.CoroutineScope
import me.d3s34.docker.ContainerAttachClient
import me.d3s34.docker.DockerClientManager
import kotlin.coroutines.CoroutineContext

class CommixDockerEngine(
    override val coroutineContext: CoroutineContext
): CoroutineScope {

    private val dockerClientManager = DockerClientManager()

    private val dockerClient = dockerClientManager.dockerClient

    fun tryGetShell(commixRequest: CommixRequest): ContainerAttachClient {
        val containerId = dockerClientManager
            .createCommixContainer(commixRequest)!!

        return ContainerAttachClient(containerId, dockerClient, this)
    }
}
