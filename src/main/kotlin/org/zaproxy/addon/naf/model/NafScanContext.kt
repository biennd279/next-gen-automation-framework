package org.zaproxy.addon.naf.model

import org.zaproxy.zap.extension.ascan.ScanPolicy
import org.zaproxy.zap.model.Context
import org.zaproxy.zap.users.User

data class NafScanContext(
    val startUrl: String,
    val target: org.zaproxy.zap.model.Target,
    val context: Context? = null,
    val user: User? = null,
    val policy: ScanPolicy? = null
)
