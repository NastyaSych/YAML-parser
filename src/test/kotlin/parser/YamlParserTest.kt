package com.example.parser

import com.example.models.*
import kotlin.test.*
import org.junit.jupiter.api.Assertions.*

class YamlParserTest {

  private val parser = YamlParser()

  @Test
  fun `GIVEN YAML with valid structure WHEN parse THEN returns Config with stages and jobs`() {
    val yaml =
        """
        stages:
          - build
          - test

        compile:
          image: ubuntu:22.04

        test:
          image:
            name: maven:3.8

        security:
          image:
            name: alpine
            entrypoint: ["sh"]

        deploy:
          image: debian:bullseye
        """
            .trimIndent()

    val config = parser.parse(yaml)

    assertEquals(listOf("build", "test"), config.stages.names)

    assertEquals(4, config.jobs.size)

    val compile = config.jobs["compile"]
    assertNotNull(compile)
    assertTrue(compile?.image is Image.AsString)
    assertEquals("ubuntu:22.04", ((compile?.image) as Image.AsString).value)

    val test = config.jobs["test"]
    assertNotNull(test)
    assertTrue(test?.image is Image.AsObject)
    assertEquals("maven:3.8", ((test?.image) as Image.AsObject).name)

    val security = config.jobs["security"]
    assertNotNull(security)
    assertTrue(security?.image is Image.AsObject)
    assertEquals("alpine", ((security?.image) as Image.AsObject).name)
  }

  @Test
  fun `GIVEN YAML with invalid structure WHEN parse THEN throws Exception`() {
    val yaml =
        """
        stages:
        - build
          - test

        compile
          image: ubuntu
        """
            .trimIndent()

    assertFailsWith<Exception> {
      parser.parse(yaml)
    }
  }

  @Test
  fun `GIVEN YAML with image as string WHEN parse THEN returns Image AsString`() {
    val yaml =
        """
        stages:
          - build

        compile:
          image: ubuntu:22.04
        """
            .trimIndent()

    val config = parser.parse(yaml)

    val compile = config.jobs["compile"]
    assertNotNull(compile)
    assertTrue(compile?.image is Image.AsString)
    assertEquals("ubuntu:22.04", ((compile?.image) as Image.AsString).value)
  }

  @Test
  fun `GIVEN YAML with image as object WHEN parse THEN returns Image AsObject`() {
    val yaml =
        """
        stages:
          - test

        test:
          image:
            name: maven:3.8
        """
            .trimIndent()

    val config = parser.parse(yaml)

    val test = config.jobs["test"]
    assertNotNull(test)
    assertTrue(test?.image is Image.AsObject)
    assertEquals("maven:3.8", ((test?.image) as Image.AsObject).name)
  }

  @Test
  fun `GIVEN YAML with image object and extra fields WHEN parse THEN ignores extra fields`() {
    val yaml =
        """
        stages:
          - security

        security:
          image:
            name: alpine
            entrypoint: ["sh"]
        """
            .trimIndent()

    val config = parser.parse(yaml)

    val security = config.jobs["security"]
    assertNotNull(security)
    assertTrue(security?.image is Image.AsObject)
    assertEquals("alpine", ((security?.image) as Image.AsObject).name)
  }

  @Test
  fun `GIVEN YAML without jobs WHEN parse THEN returns Config with empty jobs`() {
    val yaml =
        """
        stages:
          - build
          - test
        """
            .trimIndent()

    val config = parser.parse(yaml)

    assertEquals(listOf("build", "test"), config.stages.names)
    assertTrue(config.jobs.isEmpty())
  }

  @Test
  fun `GIVEN YAML with one job WHEN parse THEN returns Config with single job`() {
    val yaml =
        """
        stages:
          - build

        compile:
          image: ubuntu
        """
            .trimIndent()

    val config = parser.parse(yaml)

    assertEquals(listOf("build"), config.stages.names)
    assertEquals(1, config.jobs.size)
    assertNotNull(config.jobs["compile"])
  }

  @Test
  fun `GIVEN YAML without stages WHEN parse THEN throws IllegalArgumentException`() {
    val yaml =
        """
        compile:
          image: ubuntu
        """
            .trimIndent()

    assertFailsWith<IllegalArgumentException> { parser.parse(yaml) }
  }

  @Test
  fun `GIVEN YAML with job missing image WHEN parse THEN throws IllegalArgumentException`() {
    val yaml =
        """
        stages:
          - build

        compile:
          script: echo hello
        """
            .trimIndent()

    assertFailsWith<IllegalArgumentException> { parser.parse(yaml) }
  }

  @Test
  fun `GIVEN YAML with image object without name WHEN parse THEN throws IllegalArgumentException`() {
    val yaml =
        """
        stages:
          - build

        compile:
          image:
            entrypoint: ["sh"]
        """
            .trimIndent()

    val exception =
        assertFailsWith<IllegalArgumentException> {
          parser.parse(yaml)
        }
    assertTrue(exception.message?.contains("name") == true)
  }
}
