package com.ultivic.chatgpt.model

import com.google.gson.annotations.SerializedName

data class QuestionModel(
    @SerializedName("model") val model: String = "text-davinci-003",
    @SerializedName("prompt") val prompt: String?,
    @SerializedName("max_tokens") val max_tokens: Int? = 500,
    @SerializedName("temperature") val temperature: Int? = 0,
)
