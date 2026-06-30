package com.example.parser

import com.example.models.*
import org.yaml.snakeyaml.Yaml

class YamlParser {

  fun parse(content: String): Config {

    val rawMap =
        Yaml().load(content) as? Map<String, Any>
            ?: throw IllegalArgumentException("Invalid YAML format")

    val stages = parseStages(rawMap)

    val jobs = parseJobs(rawMap)

    return Config(stages, jobs)
  }

  private fun parseStages(rawMap: Map<String, Any>): Stages {
    val stagesList =
        rawMap["stages"] as? List<String>
            ?: throw IllegalArgumentException("Missing required field 'stages'")
    return Stages(stagesList)
  }

  private fun parseJobs(rawMap: Map<String, Any>): Map<String, Job> {
    return rawMap
        .filterKeys { it != "stages" }
        .mapValues { (jobName, jobData) ->
          val jobMap =
              jobData as? Map<String, Any>
                  ?: throw IllegalArgumentException("Job '$jobName' must be an object")

          val imageRaw =
              jobMap["image"] ?: throw IllegalArgumentException("Job '$jobName' must have 'image'")

          val image =
              when (imageRaw) {
                is String -> Image.AsString(imageRaw)
                is Map<*, *> -> {
                  val name =
                      imageRaw["name"] as? String
                          ?: throw IllegalArgumentException(
                              "Image in job '$jobName' must have 'name' field"
                          )
                  Image.AsObject(name)
                }
                else -> throw IllegalArgumentException("Invalid image type in job '$jobName'")
              }

          Job(image)
        }
  }
}
