package org.zaproxy.addon.naf.model

data class NafIssue(
    val id: Int? = null,
    val name: String,
    val severity: Severity,
    val description: String,
    val reproduce: String,
    val solution: String,
    val note: String
)

fun emptyIssue() = NafIssue(
    null,
    "",
    Severity.UNKNOWN,
    "",
    "",
    "",
    ""
)


fun NafIssue.toMarkdown() = buildString {
    appendLine("### ${name.replace("\n", " ").trimIndent()}")
    appendLine("**Severity**: ${severity.name}")
    appendLine("**Description**")
    appendLine(description.trim('\n', ' ', '\t'))
    appendLine("**Reproduce**")
    appendLine(reproduce.trim('\n', ' ', '\t'))
    appendLine("**Solution**")
    appendLine(solution.trim('\n', ' ', '\t'))
    appendLine("**Note**")
    appendLine(note.trim('\n', ' ', '\t'))
    appendLine("---")
}
