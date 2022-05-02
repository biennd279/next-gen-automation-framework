package org.zaproxy.addon.naf.pipeline

abstract class NafCrawlPipeline: NafPipeline<Set<String>>(phase = NafPhase.CRAWL) {
}
