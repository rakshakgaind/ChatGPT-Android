package com.ultivic.chatgpt.utils

import java.io.IOException

object MyConstants {
    const val ChatGptResponse = 0
    const val UserInput = 1


    private const val API_TOKEN_KEY = ""

    fun getAPIToken(): String {
        if (API_TOKEN_KEY == "") {
            throw Exception(
                "\nAs an AI language model, ChatGPT does not have an API key or provide one for Android applications. However, if you want to use OpenAI's GPT-3 API in your Android application, you can follow these general steps:\n1. Open this url platform.openai.com\n2. Create an OpenAI account: Go to the OpenAI website and create an account.\n" +

                        "3. Apply for API access: Once you have an account, go to the OpenAI API page and apply for API access. You will need to provide information about your project and how you plan to use the API." + "\n" + "4. Get API key: After your application is approved, you will receive an API key. You will use this key to authenticate your requests to the OpenAI API.\n\n"
            )
        }
        return API_TOKEN_KEY

    }

    //Base Url
    const val BASE_URL = "https://api.openai.com/v1/"

    //POST endpoints

    /*ask question*/
    const val COMPLETIONS = "completions"

    /*generating images*/
    const val IMAGE_GENERATIONS = "images/generations"
    const val GOOGLE_LLC = "com.google.android.googlequicksearchbox"
}
