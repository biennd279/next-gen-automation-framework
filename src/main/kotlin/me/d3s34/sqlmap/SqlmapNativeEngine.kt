package me.d3s34.sqlmap

import kotlin.coroutines.CoroutineContext

class SqlmapNativeEngine(
    override val coroutineContext: CoroutineContext
): SqlmapEngine() {
}