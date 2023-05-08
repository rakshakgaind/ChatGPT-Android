package com.ultivic.chatgpt.api

import com.ultivic.chatgpt.model.ImageModel
import com.ultivic.chatgpt.model.ImageResponseModel
import com.ultivic.chatgpt.model.QuestionModel
import com.ultivic.chatgpt.model.ResponseModel
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

class ApiInterfaceImpl @Inject constructor(private val apiInterface: ApiInterface) : ApiInterface {
    override suspend fun getResponse(userData: QuestionModel, auth: String?): Response<ResponseModel?>? {
        return try {
            apiInterface.getResponse(userData)
        } catch (e: Exception) {
            Response.error<ResponseModel?>(800, e.message?.toResponseBody() ?: return null)
        }
    }

    override suspend fun getImageResponse(userData: ImageModel, auth: String?): Response<ImageResponseModel?>? {
        return try {
            apiInterface.getImageResponse(userData)
        }catch (e: Exception){
            Response.error<ImageResponseModel?>(800,e.message?.toResponseBody() ?: return null)
        }
    }
}




