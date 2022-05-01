package org.zaproxy.addon.naf.model

import me.d3s34.nuclei.NucleiTemplate
import org.zaproxy.zap.model.Tech
import java.io.File


data class ScanTemplate(
    val url: String,
    val excludesRegex: List<String> = emptyList(),
    val includesRegex: List<String> = emptyList(),
    val includeTech: Set<Tech> = setOf(*Tech.getAll().toTypedArray()),
    val excludeTech: Set<Tech> = emptySet(),
    val fuzzOptions: FuzzOptions = FuzzOptions(),
    val crawlOptions: CrawlOptions = CrawlOptions(),
    val systemOptions: SystemOptions = SystemOptions(),
    val scanOptions: ActiveScanOptions,
)

data class CrawlOptions(
    val crawl: Boolean = false,
    val ajaxCrawl: Boolean = false,
)

data class ActiveScanOptions(
    val activeScan: Boolean = false,
    val plugins: List<NafPlugin>,
)

data class SystemOptions(
    val useNuclei: Boolean = false,
    val templates: List<NucleiTemplate> = emptyList()
)

data class FuzzOptions(
    val useBruteForce: Boolean = false,
    val files: List<File> = emptyList()
)
