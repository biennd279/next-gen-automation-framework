package org.zaproxy.addon.naf.pipeline

abstract class NafCrawlPipeline: NafPipeline<org.zaproxy.zap.model.Target, Set<String>>(phase = NafPhase.CRAWL) {
}
