package org.zaproxy.addon.naf.component

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.zaproxy.addon.naf.database.NafDatabase
import org.zaproxy.addon.naf.model.NafIssue

class IssueComponent(
    val nafDatabase: NafDatabase,
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    val issues = MutableStateFlow(nafDatabase.getAllIssue())
    fun saveIssue(issue: NafIssue) {
        if (issue.id == null) {
            val newIssue = issue.copy(id = nafDatabase.saveNewIssue(issue))
            issues.update {
                it + newIssue
            }
        } else {
            nafDatabase.updateIssue(issue)
            val updatedIssue = nafDatabase.findIssue(issue.id)!!
            issues.update { currentIssues ->
                currentIssues.map { if (it.id == issue.id) updatedIssue else it }
            }
        }
    }
}