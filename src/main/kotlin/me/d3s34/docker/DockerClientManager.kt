package me.d3s34.docker

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import org.parosproxy.paros.Constant
import java.io.File
import java.time.Duration

class DockerClientManager {

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

    fun createSqlmapApiContainer(host: String = "127.0.0.1", port: Int = 8875): String? {
        val listContainer = dockerClient
            .listContainersCmd()
            .withStatusFilter(listOf("created", "restarting", "running", "paused", "exited"))
            .withNameFilter(listOf(SQLMAP_API_CONTAINER_NAME))
            .exec()

        if (listContainer.size > 0) {
            dockerClient
                .removeContainerCmd(SQLMAP_API_CONTAINER_NAME)
                .withRemoveVolumes(true)
                .exec()
        }

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

    companion object {
        val SQLMAP_API_DOCKER_URI = "me/d3s34/sqlmap/Dockerfile"
        val SQLMAP_API_CONTAINER_NAME = "naf-sqlmap-api"
        val SQLMAP_API_IMAGE_TAG = "biennd279/naf-sqlmap-api"
    }
}
