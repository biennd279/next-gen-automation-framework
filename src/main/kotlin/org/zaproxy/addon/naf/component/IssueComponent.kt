package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.zaproxy.addon.naf.database.IssueService
import org.zaproxy.addon.naf.model.NafIssue

class IssueComponent(
    val issueService: IssueService,
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    val issues = MutableStateFlow(issueService.getAllIssue())
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
}