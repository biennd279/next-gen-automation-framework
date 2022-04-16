package me.d3s34.metasploit.rpcapi.request.core


@kotlinx.serialization.Serializable
data class SetOptionGlobalRequest(
    val token: String,
    val optionName: String,
    val optionValue: String
): CoreModuleRequest("setg")
