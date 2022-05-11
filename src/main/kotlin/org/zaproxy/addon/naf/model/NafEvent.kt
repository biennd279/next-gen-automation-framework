package org.zaproxy.addon.naf.model

sealed class NafEvent()

object NopEvent: NafEvent()

sealed class ExploitEvent(
    open val alert: NafAlert
): NafEvent()

class SqlInjectionEvent(
    alert: NafAlert
): ExploitEvent(alert)

class CommandInjectionEvent(
    alert: NafAlert
): ExploitEvent(alert)

class TemplateInjectionEvent(
    alert: NafAlert
): ExploitEvent(alert)

class LFIInjectionEvent(
    alert: NafAlert
): ExploitEvent(alert)

class RFIInjectionEvent(
    alert: NafAlert
): ExploitEvent(alert)

class AlertEvent(
    val nafAlert: NafAlert
): NafEvent()
