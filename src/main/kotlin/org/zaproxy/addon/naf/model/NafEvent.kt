package org.zaproxy.addon.naf.model

abstract class NafEvent()

object NopEvent: NafEvent()

sealed class ExploitEvent: NafEvent()

//TODO: Use nafTarget
class SqlInjectionEvent(
    val alert: NafAlert
): ExploitEvent()

class CommandInjectionEvent(
    val alert: NafAlert
): ExploitEvent()

class AlertEvent(
    val nafAlert: NafAlert
): NafEvent()
