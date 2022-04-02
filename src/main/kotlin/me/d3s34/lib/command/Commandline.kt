package me.d3s34.lib.command

class Commandline(
    val path: String,
    args: List<String> = listOf(),
    shortFlag: Map<String, String?> = mapOf(),
    longFlag: Map<String, String?> = mapOf(),
) {

    private constructor(builder: Builder) : this(builder.path!!, builder.args, builder.shortFlag, builder.longFlag)

    private var _escapedArgs = mutableListOf<String>()

    val escapedArgs: List<String>
        get() = _escapedArgs

    private fun escapeCommand(command: String): String {
        return command.replace("[`;$|<>]".toRegex(), "")
    }

    private fun escapeArg(arg: String): String {
        //TODO
        return arg
    }

    init {
        longFlag.forEach { (flag, value) ->
            _escapedArgs.add("--${escapeCommand(flag)}")
            value?.let { _escapedArgs.add(it) }
        }

        shortFlag.forEach { (flag, value) ->
            _escapedArgs.add("-${escapeCommand(flag)}")
            value?.let { _escapedArgs.add(it) }
        }

        args.forEach { arg ->
            _escapedArgs.add(escapeArg(arg))
        }
    }

    fun toCommandline(rawArgs: List<String>? = null): String {
        val command = buildString {
            append(escapeCommand(path))

            append(" ")

            append(_escapedArgs.joinToString(" "))

            rawArgs?.let {
                append(rawArgs)
            }
        }

        return command
    }

    override fun toString(): String {
        return this.toCommandline()
    }

    class Builder {
        var path: String? = null
        var args: List<String> = listOf()
        var shortFlag: Map<String, String?> = mapOf()
        var longFlag: Map<String, String?> = mapOf()

        fun build() = Commandline(this)
    }

    companion object {
        inline fun buildCommand(block: Builder.() -> Unit) = Builder().apply(block).build()
    }
}

inline fun buildCommand(block: Commandline.Builder.() -> Unit) = Commandline.buildCommand(block)
