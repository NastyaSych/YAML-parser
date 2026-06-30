package com.example.models

data class Config(
    val stages: Stages,
    val jobs: Map<String, Job> = emptyMap(),
)
