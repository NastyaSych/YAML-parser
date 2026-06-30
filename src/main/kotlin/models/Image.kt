package com.example.models

sealed class Image {
  data class AsString(val value: String) : Image()

  data class AsObject(val name: String) : Image()
}
