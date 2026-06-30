package com.example

import com.example.parser.YamlParser
import java.io.File
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
  val filePath = args.firstOrNull() ?: "samples/.gitlab-ci.yml"
  logger.info { "Reading config from: $filePath" }

  try {
    val file = File(filePath)
    if (!file.exists()) {
      logger.error { "File not found: $filePath" }
      return
    }
    val content = file.readText()
    val config = YamlParser().parse(content)

    logger.info { "Config loaded: stages=${config.stages.names}, jobs=${config.jobs.size}" }
  } catch (e: Exception) {
    logger.error(e) { "Failed to load config from $filePath" }
  }
}
