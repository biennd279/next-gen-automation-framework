package me.d3s34.docker

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import kotlinx.coroutines.*
import org.parosproxy.paros.Constant
import java.io.File
import java.nio.charset.Charset
import java.time.Duration


class DockerClientManager() {

    var config = DefaultDockerClientConfig
        .createDefaultConfigBuilder()
        .build()

    var httpClient = ApacheDockerHttpClient
        .Builder()
        .dockerHost(config.dockerHost)
        .sslConfig(config.sslConfig)
        .maxConnections(100)
        .connectionTimeout(Duration.ofSeconds(30))
        .responseTimeout(Duration.ofSeconds(45))
        .build()

    var dockerClient = DockerClientImpl.getInstance(config, httpClient)

    fun createSqlmapImage(): String? {
        val image = dockerClient
            .buildImageCmd()
            .withDockerfile(File(Constant.getZapHome(), SQLMAP_API_DOCKER_URI))
            .withPull(true)
            .withTag(SQLMAP_API_IMAGE_TAG)
            .start()

        return image.awaitImageId()
    }

    fun createSqlmapApiContainer(host: String = "127.0.0.1", port: Int = 8775): String? {
        val listContainer = dockerClient
            .listContainersCmd()
            .withStatusFilter(listOf("created", "restarting", "running", "paused", "exited"))
            .withNameFilter(listOf(SQLMAP_API_CONTAINER_NAME))
            .exec()

        if (listContainer.size > 0) {

            listContainer.first().status

            kotlin.runCatching {
                dockerClient
                    .stopContainerCmd(SQLMAP_API_CONTAINER_NAME)
                    .exec()
            }

            kotlin.runCatching {
                dockerClient
                    .removeContainerCmd(SQLMAP_API_CONTAINER_NAME)
                    .withRemoveVolumes(true)
                    .exec()
            }
        }

        val container = dockerClient
            .createContainerCmd(SQLMAP_API_IMAGE_TAG)
            .withName(SQLMAP_API_CONTAINER_NAME)
            .withNetworkMode("host")
            .withCmd("./sqlmapapi.py", "-s", "-H", host, "-p", port.toString())
            .exec()

        return container.id
    }

    fun checkStatusContainer() {

    }

    fun startSqlmapApiContainer() {
        dockerClient
            .startContainerCmd(SQLMAP_API_CONTAINER_NAME)
            .exec()
    }

    fun stopSqlmapApiContainer() {
        dockerClient
            .stopContainerCmd(SQLMAP_API_CONTAINER_NAME)
            .exec()
    }

    companion object {
        const val SQLMAP_API_DOCKER_URI = "me/d3s34/sqlmap/Dockerfile"
        const val SQLMAP_API_CONTAINER_NAME = "naf-sqlmap-api"
        const val SQLMAP_API_IMAGE_TAG = "biennd279/naf-sqlmap-api"

        const val COMMIX_DOCKER_URI = "me/d3s34/commix/Dockerfile"
        const val COMMIX_CONTAINER_NAME = "naf-commix"
        const val COMMIX_IMAGE_TAG = "biennd279/naf-commix"
    }
}

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun main() {
    val client = DockerClientManager()
    val dockerClient = client.dockerClient

    runBlocking {
        val container = dockerClient
            .createContainerCmd("biennd279/naf-commix")
            .withNetworkMode("host")
            .withCmd("commix","-u", "http://localhost:8888/command.php", "-d", "dir=/", "--batch")
            .withAttachStdin(true)
            .withAttachStdout(true)
            .withAttachStderr(true)
//            .withTty(true)
            .withStdinOpen(true)
            .exec()

        val containerAttachClient = ContainerAttachClient(
            containerId = container.id,
            dockerClient = dockerClient,
            coroutineContext = Dispatchers.IO
        )

        GlobalScope.launch {
            containerAttachClient.status
                .collect {
                    println("Update status ${it.name}")
                }
        }

        GlobalScope.launch {
            containerAttachClient.attach()
            dockerClient
                .startContainerCmd(container.id)
                .exec()
            for (output in containerAttachClient.stdoutChannel) {
                println(output.toString(Charset.defaultCharset()))
            }
        }

        while (containerAttachClient.status.value != ContainerAttachClient.Status.DEATTACH) {
            val line = readln()
            if (line == "exit") {
                containerAttachClient.close()
                break
            }

            containerAttachClient.send((line + "\n").toByteArray())
        }
    }
}
