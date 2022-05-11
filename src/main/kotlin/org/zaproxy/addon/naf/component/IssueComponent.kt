package org.zaproxy.addon.naf.component

import androidx.compose.runtime.State
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.zaproxy.addon.naf.database.IssueService
import org.zaproxy.addon.naf.model.AlertEvent
import org.zaproxy.addon.naf.model.NafIssue
import org.zaproxy.addon.naf.model.mapToIssue

class IssueComponent(
    val issueService: IssueService,
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    val currentIssue = MutableStateFlow<NafIssue?>(null)
    val issues = MutableStateFlow(issueService.getAllIssue())

    fun handleAlertEvent(alertEvent: AlertEvent) {
        val newIssue = alertEvent.nafAlert
            .mapToIssue()
        currentIssue.update { newIssue }
    }

    fun saveIssue(issue: NafIssue) {
        if (issue.id == null) {
            val newIssue = issue.copy(id = issueService.saveNewIssue(issue))
            issues.update {
                it + newIssue
            }
        } else {
            issueService.updateIssue(issue)
            val updatedIssue = issueService.findIssue(issue.id)!!
            issues.update { currentIssues ->
                currentIssues.map { if (it.id == issue.id) updatedIssue else it }
            }
        }
    }

    fun removeIssue(issue: NafIssue) {
        issue.id?.let {id ->
            issueService.removeIssue(id)
            issues.update { nafIssues ->
                nafIssues.filter { it.id != id }
            }
        }
    }
}