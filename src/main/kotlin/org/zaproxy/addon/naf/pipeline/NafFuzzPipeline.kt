package org.zaproxy.addon.naf.pipeline

abstract class NafFuzzPipeline : NafPipeline<List<String>>(NafPhase.FUZZ)