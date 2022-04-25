package org.zaproxy.addon.naf.model

import org.parosproxy.paros.model.SiteNode

data class NafNode(
    val name: String,
    val nodeName: String,
    val historyId: Int,
)

fun SiteNode.toNafNode(): NafNode {
    return NafNode(
        name = this.name,
        nodeName = this.nodeName,
        historyId = this.historyReference.historyId
    )
}

