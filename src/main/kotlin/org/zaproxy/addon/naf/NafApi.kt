package org.zaproxy.addon.naf

import net.sf.json.JSONObject
import org.apache.logging.log4j.LogManager
import org.zaproxy.zap.extension.api.ApiImplementor
import org.zaproxy.zap.extension.api.ApiResponse

class NafApi(
    private val _prefix: String
): ApiImplementor() {
    override fun getPrefix(): String = _prefix

    override fun handleApiAction(name: String?, params: JSONObject?): ApiResponse {
        return super.handleApiAction(name, params)
    }

    companion object {
        private val LOGGER = LogManager.getLogger(NafApi::class.java)!!
    }
}