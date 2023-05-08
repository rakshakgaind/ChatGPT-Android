package com.ultivic.chatgpt.model

@kotlinx.serialization.Serializable
data class ImageResponseModel(
    val created: Int,
    val `data`: List<Data>
)
@kotlinx.serialization.Serializable
data class Data(
    val url: String?
)