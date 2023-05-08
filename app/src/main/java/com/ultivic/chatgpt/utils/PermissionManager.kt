package com.ultivic.chatgpt.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionManager {
    fun isPermission(context: Context,permission:String) = ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED
}