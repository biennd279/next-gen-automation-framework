package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.zaproxy.zap.extension.bruteforce.ExtensionBruteForce
import org.zaproxy.zap.model.Target
import java.io.File
import kotlin.coroutines.CoroutineContext

class BruteForcePipeline(
    val files: List<File>,
    override val coroutineContext: CoroutineContext
): NafFuzzPipeline() {

    val refreshTime = 1000L

    private val extensionBruteForce: ExtensionBruteForce by lazy {
        extensionLoader
            .getExtension(ExtensionBruteForce::class.java)
    }

    override suspend fun start(input: Target): List<String> {

        val startNode = input.startNode

        val listFuzzId = files.map {
            extensionBruteForce
                .bruteForceSite(startNode, it)
        }

        do {
            delay(refreshTime)
        } while (isActive && isRunning(listFuzzId))

        if (!isActive) {
            extensionBruteForce.stopAllScans()
        }

        return emptyList()
    }

    private fun isRunning(listFuzzId: List<Int>): Boolean {
        return listFuzzId.map { extensionBruteForce.isRunning(it) }.all { it }
    }
}