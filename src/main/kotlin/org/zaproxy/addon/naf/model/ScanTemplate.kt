package org.zaproxy.addon.naf.model


data class ScanTemplate(
    val url: String,
    val crawlOptions: CrawlOptions = CrawlOptions()
)

data class CrawlOptions(
    val crawl: Boolean = true,
    val ajaxCrawl: Boolean = true,
)

fun emptyTemplate(): ScanTemplate = ScanTemplate("")
