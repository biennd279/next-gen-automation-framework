package org.zaproxy.addon.naf.pipeline

import org.zaproxy.zap.model.Target

abstract class NafFuzzPipeline : NafPipeline<Target, List<String>>(NafPhase.FUZZ)