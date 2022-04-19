package org.zaproxy.addon.naf.pipeline

enum class NafPhase(
    val priority: Int
) {
    INIT(-1),
    FUZZ(0),
    CRAWL(10),
    SCAN(20),
    ATTACK(30),
    REPORT(40),
    UNKNOWN(-3)
}
