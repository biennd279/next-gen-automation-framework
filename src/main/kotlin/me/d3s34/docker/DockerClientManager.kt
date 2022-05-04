package me.d3s34.docker

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import me.d3s34.commix.CommixRequest
import me.d3s34.commix.toCommand
import me.d3s34.tplmap.TplmapRequest
import me.d3s34.tplmap.toCommand
import org.parosproxy.paros.Constant
import java.io.File
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
            .withTags(setOf(SQLMAP_API_IMAGE_TAG))
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
        @Suppress("Deprecation")
        val container = dockerClient
            .createContainerCmd(SQLMAP_API_IMAGE_TAG)
            .withName(SQLMAP_API_CONTAINER_NAME)
            .withNetworkMode("host")
            .withCmd("./sqlmapapi.py", "-s", "-H", host, "-p", port.toString())
            .exec()

        return container.id
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

    fun createCommixImage(): String? {
        val image = dockerClient
            .buildImageCmd()
            .withDockerfile(File(Constant.getZapHome(), COMMIX_DOCKER_URI))
            .withPull(true)
            .withTags(setOf(COMMIX_IMAGE_TAG))
            .start()

        return image.awaitImageId()
    }

    fun createCommixContainer(commixRequest: CommixRequest): String? {
        val commandline = buildList {
            val command =  commixRequest.toCommand()
            add(command.path)
            addAll(command.escapedArgs)
        }

        @Suppress("Deprecation")
        return dockerClient
            .createContainerCmd(COMMIX_IMAGE_TAG)
            .withNetworkMode("host")
            .withCmd(commandline)
            .withAttachStdin(true)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withTty(true)
            .withStdinOpen(true)
            .exec()
            .id
    }

    fun createTplmapImage(): String? {
        val image = dockerClient
            .buildImageCmd()
            .withDockerfile(File(Constant.getZapHome(), TPLMAP_DOCKER_URI))
            .withPull(true)
            .withTags(setOf(TPLMAP_IMAGE_TAG))
            .start()

        return image.awaitImageId()
    }

    fun createTplmapContainer(tplmapRequest: TplmapRequest): String? {
        val commandline = buildList {
            val command =  tplmapRequest.toCommand()
            addAll(command.escapedArgs)
        }

        @Suppress("Deprecation")
        return dockerClient
            .createContainerCmd(TPLMAP_IMAGE_TAG)
            .withNetworkMode("host")
            .withCmd(commandline)
            .withAttachStdin(true)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withTty(true)
            .withStdinOpen(true)
            .exec()
            .id
    }

    companion object {
        const val SQLMAP_API_DOCKER_URI = "me/d3s34/sqlmap/Dockerfile"
        const val SQLMAP_API_CONTAINER_NAME = "naf-sqlmap-api"
        const val SQLMAP_API_IMAGE_TAG = "biennd279/naf-sqlmap-api"

        const val COMMIX_DOCKER_URI = "me/d3s34/commix/Dockerfile"
        const val COMMIX_IMAGE_TAG = "biennd279/naf-commix"

        const val TPLMAP_DOCKER_URI = "me/d3s34/tplmap/Dockerfile"
        const val TPLMAP_IMAGE_TAG = "biennd279/naf-tplmap"
    }
}
