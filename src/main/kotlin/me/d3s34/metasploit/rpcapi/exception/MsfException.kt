package me.d3s34.metasploit.rpcapi.exception

class MsfException(
    override val message: String?,
    override val cause: Throwable?
): Throwable()
