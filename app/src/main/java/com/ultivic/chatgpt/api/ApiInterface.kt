package com.ultivic.chatgpt.api

import com.ultivic.chatgpt.model.ImageModel
import com.ultivic.chatgpt.model.ImageResponseModel
import com.ultivic.chatgpt.model.QuestionModel
import com.ultivic.chatgpt.model.ResponseModel
import com.ultivic.chatgpt.utils.MyConstants.COMPLETIONS
import com.ultivic.chatgpt.utils.MyConstants.IMAGE_GENERATIONS
import com.ultivic.chatgpt.utils.MyConstants.getAPIToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST(value = COMPLETIONS)
    suspend fun getResponse(@Body userData: QuestionModel, @Header("Authorization") auth: String? = "Bearer ${getAPIToken()
    }"): Response<ResponseModel?>?

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST(value = IMAGE_GENERATIONS)
    suspend fun getImageResponse(@Body userData: ImageModel, @Header("Authorization") auth: String? = "Bearer ${getAPIToken()}"): Response<ImageResponseModel?>?

}
