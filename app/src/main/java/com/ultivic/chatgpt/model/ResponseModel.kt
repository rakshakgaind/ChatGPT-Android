package com.ultivic.chatgpt.model

@kotlinx.serialization.Serializable
data class ResponseModel(
    val choices: List<Choice>,
    val created: Int,
    val id: String?,
    val model: String?,
    val `object`: String?,
    val usage: Usage
)

@kotlinx.serialization.Serializable
data class Choice(
    val finish_reason: String?,
    val index: Int,
    val logprobs:String?,
    val text: String?
)

@kotlinx.serialization.Serializable
data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)