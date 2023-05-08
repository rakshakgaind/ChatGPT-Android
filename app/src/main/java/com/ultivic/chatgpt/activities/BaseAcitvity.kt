package com.ultivic.chatgpt.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ultivic.chatgpt.R

abstract class BaseAcitvity : AppCompatActivity() {
    private val sharedPreferences by lazy { this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE) }
    private val shareEditor by lazy { sharedPreferences.edit() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}