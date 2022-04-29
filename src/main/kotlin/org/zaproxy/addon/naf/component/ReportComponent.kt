package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import com.qkyrie.markdown2pdf.Markdown2PdfConverter
import org.zaproxy.addon.naf.database.NafDatabase
import org.zaproxy.addon.naf.model.NafIssue
import org.zaproxy.addon.naf.model.Severity
import java.io.File


class ReportComponent(
    private val nafDatabase: NafDatabase,
    componentContext: ComponentContext
): ComponentContext by componentContext {


    private fun reportHeader() = buildString {
        appendLine("# Vulnerability Report")
        appendLine()
        appendLine("## Vulnerabilities")
        appendLine("---")
    }

    private fun issueTemplate(issue: NafIssue) = buildString {
        appendLine("### ${issue.name.replace("\n", " ").trimIndent()}")
        appendLine("**Severity**: ${issue.severity.name}")
        appendLine("**Description**")
        appendLine(issue.description.trim('\n', ' ', '\t'))
        appendLine("**Reproduce**")
        appendLine(issue.reproduce.trim('\n', ' ', '\t'))
        appendLine("**Solution**")
        appendLine(issue.solution.trim('\n', ' ', '\t'))
        appendLine("**Note**")
        appendLine(issue.note.trim('\n', ' ', '\t'))
        appendLine("---")
    }

    fun buildReport(): String {
        val issues = nafDatabase
            .issueService
            .getAllIssue()
            .filter { it.severity != Severity.UNKNOWN }
            .sortedBy { it.severity.ordinal }

        return buildString {
            append(reportHeader())

            appendLine()

            issues.forEach {
                append(issueTemplate(it))
                appendLine()
            }
        }
    }

    fun exportToPdf(filePath: String) {
        val file = File(filePath)
        Markdown2PdfConverter.newConverter()
            .readFrom {
                buildReport()
            }
            .writeTo {
                file.writeBytes(it)
            }
            .doIt()
    }
}