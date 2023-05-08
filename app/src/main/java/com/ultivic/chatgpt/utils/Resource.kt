package com.ultivic.chatgpt.utils


sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val isLoading: Boolean? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(isLoading: Boolean, data: T? = null) : Resource<T>(isLoading = isLoading, data = data)
}