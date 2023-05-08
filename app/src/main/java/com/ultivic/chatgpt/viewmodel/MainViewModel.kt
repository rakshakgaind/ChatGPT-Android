package com.ultivic.chatgpt.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ultivic.chatgpt.api.ApiInterfaceImpl
import com.ultivic.chatgpt.model.ImageModel
import com.ultivic.chatgpt.model.ImageResponseModel
import com.ultivic.chatgpt.model.QuesAnsData
import com.ultivic.chatgpt.model.QuestionModel
import com.ultivic.chatgpt.model.ResponseModel
import com.ultivic.chatgpt.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val apiInterfaceImpl: ApiInterfaceImpl, val app: Application) : AndroidViewModel(app) {

    private val answerResponseModel = MutableLiveData<Resource<ResponseModel?>?>()
    private val imageResponseModel = MutableLiveData<Resource<ImageResponseModel?>?>()
    val textToSpeech = TextToSpeech(app) {}
    private var list = ArrayList<QuesAnsData>()

    var translateTo = ""

    var isGenerateImage = false

    fun getList() = list

    fun addData(isGenerateImage: Boolean, data: String, isFirst: Boolean, tag: Int) {
        list.add(QuesAnsData(data = data, tag = tag, isFirst = isFirst, isGenerateImage = isGenerateImage))
    }

    fun getAnswerResponse() = answerResponseModel

    fun askQuestion(model: QuestionModel) {
        viewModelScope.launch {
            answerResponseModel.value = Resource.Loading(true)
            val d = apiInterfaceImpl.getResponse(model)
            if (d == null) {
                answerResponseModel.value = Resource.Loading(false)
                answerResponseModel.value = Resource.Error("Something went wrong")
            } else {
                answerResponseModel.postValue(handleResponse(d))
            }
            answerResponseModel.value = Resource.Loading(false)
        }
    }

    private fun handleResponse(response: Response<ResponseModel?>): Resource<ResponseModel?> {
        if (response.isSuccessful) {
            return Resource.Success(response.body())
        }

        return Resource.Error(response.errorBody()?.string() ?: response.message() ?: "something went wrong")
    }

    fun getImageResponse() = imageResponseModel
    fun sendImage(data: ImageModel) {
        viewModelScope.launch {
            imageResponseModel.value = Resource.Loading(true)
            val d = apiInterfaceImpl.getImageResponse(data)
            if (d == null) {
                imageResponseModel.value = Resource.Loading(false)
                imageResponseModel.value = Resource.Error("Something went wrong")
            } else {
                imageResponseModel.postValue(handleImageResponse(d))
            }
            imageResponseModel.value = Resource.Loading(false)
        }
    }

    private fun handleImageResponse(response: Response<ImageResponseModel?>): Resource<ImageResponseModel?> {
        if (response.isSuccessful) {
            return Resource.Success(response.body())
        }
        return Resource.Error(response.errorBody()?.string() ?: response.message() ?: "something went wrong")
    }

    fun setVoice(data: String) {
        textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}