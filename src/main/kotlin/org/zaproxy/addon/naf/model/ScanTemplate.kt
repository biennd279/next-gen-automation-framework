package org.zaproxy.addon.naf.model


data class ScanTemplate(
    val url: String,
    val excludesRegex: List<String> = emptyList(),
    val includesRegex: List<String> = emptyList(),
    val crawlOptions: CrawlOptions = CrawlOptions(),
    val scanOptions: ActiveScanOptions
)

data class CrawlOptions(
    val crawl: Boolean = true,
    val ajaxCrawl: Boolean = true,
)

data class ActiveScanOptions(
    val activeScan: Boolean = true,
    val plugins: List<NafPlugin>,
)
