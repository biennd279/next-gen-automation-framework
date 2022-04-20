package org.zaproxy.addon.naf

import org.parosproxy.paros.core.proxy.ConnectRequestProxyListener
import org.parosproxy.paros.core.proxy.ProxyListener
import org.parosproxy.paros.network.HttpMessage

// Passive listener
internal object ProxyListenerImpl: ProxyListener, ConnectRequestProxyListener {

    private const val PROXY_LISTENER_ORDER = 250
    override fun getArrangeableListenerOrder(): Int = PROXY_LISTENER_ORDER

    override fun onHttpRequestSend(msg: HttpMessage?): Boolean {
        return true
    }

    override fun onHttpResponseReceive(msg: HttpMessage?): Boolean {
        return true
    }

    override fun receivedConnectRequest(connectMessage: HttpMessage?) {

    }
}
