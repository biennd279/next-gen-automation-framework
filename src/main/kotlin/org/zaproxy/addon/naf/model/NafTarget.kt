package org.zaproxy.addon.naf.model

import org.zaproxy.zap.model.Target

data class NafTarget(
    val startNode: NafNode,
    val contextName: String
)

fun Target.toNafTarget(): NafTarget {
    return NafTarget(
        startNode = this.startNode.toNafNode(),
        contextName = this.context.name
    )
}
