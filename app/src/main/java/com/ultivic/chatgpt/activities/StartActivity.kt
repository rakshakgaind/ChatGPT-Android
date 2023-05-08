package com.ultivic.chatgpt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ultivic.chatgpt.MainActivity
import com.ultivic.chatgpt.R

class StartActivity : BaseAcitvity() {
    private val getSharedValue by lazy { getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isFirst = getSharedValue.getBoolean("isFirst",false)
        Log.d( "onCreate:","$isFirst")
        if (isFirst){
            startActivity(Intent(this@StartActivity,MainActivity :: class.java))
        }else{
            startActivity(Intent(this@StartActivity, OnboardingActivity::class.java))
        }
        finishAffinity()
    }
}