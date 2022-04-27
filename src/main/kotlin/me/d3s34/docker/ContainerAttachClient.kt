package me.d3s34.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.*

class ContainerAttachClient(
    val containerId: String,
    val dockerClient: DockerClient,
    val coroutineScope: CoroutineScope
) {
    enum class Status { NOT_RUNNING, NOT_ATTACH, ATTACHING, DETACH, ERROR }

    private val _status = MutableStateFlow(Status.NOT_ATTACH)
    val status = _status.asStateFlow()

    private val _stdoutChannel = Channel<ByteArray>(10)
    val stdoutChannel: ReceiveChannel<ByteArray> = _stdoutChannel

    private lateinit var resultCallback: ResultCallback.Adapter<Frame>
    private lateinit var out: OutputStream
    private lateinit var `in`: InputStream

    fun attach(): ResultCallback.Adapter<Frame>? {
        resultCallback = object : ResultCallback.Adapter<Frame>() {
            override fun onStart(stream: Closeable?) {
                super.onStart(stream)
                _status.update { Status.ATTACHING }
            }

            override fun onNext(frame: Frame?) {
                frame?.let { _stdoutChannel.trySend(it.payload) }
                super.onNext(frame)
            }

            override fun onError(throwable: Throwable?) {
                _stdoutChannel.close()
                _status.update { Status.ERROR }
                super.onError(throwable)
            }

            override fun onComplete() {
                super.onComplete()
                _stdoutChannel.close()
                _status.update { Status.DETACH }
            }
        }

        coroutineScope.launch(Dispatchers.IO) {
            out = PipedOutputStream()
            `in` = PipedInputStream(out as PipedOutputStream)

            dockerClient
                .attachContainerCmd(containerId)
                .withStdIn(`in`)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .exec(resultCallback)
        }

        return resultCallback.awaitStarted()
    }

    fun start() {
        if (!this::resultCallback.isInitialized) {
            attach()
        }

        kotlin.runCatching {
            dockerClient
            .startContainerCmd(containerId)
            .exec()
        }
    }

    fun send(message: ByteArray) {
        out.write(message)
        out.flush()
    }

    fun close() {
        resultCallback.close()
        _stdoutChannel.close()
        _status.update { Status.DETACH }

        kotlin.runCatching {
            stop()
            remove()
        }
    }

    fun stop() {
        dockerClient
            .stopContainerCmd(containerId)
            .exec()
    }

    fun remove() {
        dockerClient
            .removeContainerCmd(containerId)
            .exec()
    }
}