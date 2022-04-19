package org.zaproxy.addon.naf.pipeline

import org.zaproxy.addon.naf.NafContext

abstract class NafCrawlPipeline: NafPipeline<org.zaproxy.zap.model.Target, Set<String>>(phase = NafPhase.CRAWL) {
}
