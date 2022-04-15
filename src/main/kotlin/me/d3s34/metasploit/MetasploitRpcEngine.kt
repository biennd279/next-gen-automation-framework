package me.d3s34.metasploit

import kotlin.coroutines.CoroutineContext

class MetasploitRpcEngine(
    override val coroutineContext: CoroutineContext
) : MetasploitEngine()