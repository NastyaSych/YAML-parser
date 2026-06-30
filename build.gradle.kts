plugins {
  kotlin("jvm") version "2.2.21"
  application
  id("com.ncorti.ktfmt.gradle") version "0.26.0"
}

group = "org.example"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
  implementation("org.yaml:snakeyaml:2.3")
  implementation("ch.qos.logback:logback-classic:1.5.20")
  implementation("io.github.microutils:kotlin-logging:3.0.5")
  testImplementation(kotlin("test"))
}

kotlin { jvmToolchain(21) }

application { mainClass.set("com.example.MainKt") }

tasks.test { useJUnitPlatform() }
