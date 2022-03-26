/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2018 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.addon.simpleexample

import net.sf.json.JSONObject
import org.apache.logging.log4j.LogManager
import org.zaproxy.zap.extension.api.*

class NextgenAutomationApi : ApiImplementor() {
    init {
        addApiAction(ApiAction(ACTION_HELLO_WORLD))
    }

    override fun getPrefix(): String {
        return PREFIX
    }

    @Throws(ApiException::class)
    override fun handleApiAction(name: String, params: JSONObject): ApiResponse {
        when (name) {
            ACTION_HELLO_WORLD -> LOGGER.debug("hello world called")
            else -> throw ApiException(ApiException.Type.BAD_ACTION)
        }
        return ApiResponseElement.OK
    }

    companion object {
        private const val PREFIX = "naf"
        private const val ACTION_HELLO_WORLD = "helloWorld"
        private val LOGGER = LogManager.getLogger(NextgenAutomationApi::class.java)
    }
}