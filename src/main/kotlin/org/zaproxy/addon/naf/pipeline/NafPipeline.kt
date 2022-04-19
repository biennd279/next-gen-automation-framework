package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.CoroutineScope
import org.zaproxy.addon.naf.NafContext

abstract class NafPipeline<in T, out U>(
    val phase: NafPhase
): CoroutineScope {
    abstract suspend fun start(input: T): U
}