package com.ultivic.chatgpt.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ultivic.chatgpt.BuildConfig
import com.ultivic.chatgpt.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun TextView.typeWriterEffect(text: String, intervalMs: Long, isFinished: (() -> Unit)) {
    this@typeWriterEffect.text = ""
    CoroutineScope(Dispatchers.Main).launch {
        repeat(text.length) {
            delay(intervalMs)
            this@typeWriterEffect.text = text.take(it + 1)
            if ((it + 1) == text.length) {
                isFinished.invoke()
            }
        }
    }
}

fun Context.copyToClipBoard(text: String) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("copied text", text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(this, getString(R.string.text_copied_to_clipboard), Toast.LENGTH_LONG).show()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.startActivity(clazz: Class<*>, isLastFinished: Boolean = false) {
    startActivity(Intent(this, clazz))
    if (isLastFinished) finish()
}

fun Context.shareMediaFile(uri: Uri): Boolean {
    val shareMessage = "Chat GPT \n\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.setPackage(BuildConfig.APPLICATION_ID)
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareMessage)
    shareIntent.setDataAndType(uri, contentResolver.getType(uri))
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
    try {
        startActivity(Intent.createChooser(shareIntent, "Share"))
    } catch (e: Exception) {
        return false
    }
    return true
}


fun PackageManager.isPackageInstalled(packageName: String): Boolean {
    return try {
        getPackageGids(packageName)
        val appEnabledSetting: Int = getApplicationEnabledSetting(packageName)
        return !(appEnabledSetting == COMPONENT_ENABLED_STATE_DISABLED ||
                appEnabledSetting == COMPONENT_ENABLED_STATE_DISABLED_USER)
    } catch (e: PackageManager.NameNotFoundException) {
        false
    } catch (e: Exception) {
        false
    }
}

fun Context.createAnyDialog(title: String, message: String, installCallBack: (() -> Unit)): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(this ,R.style.AlertDialogTheme)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Install") { dialogInterface, _ ->
            installCallBack.invoke()
            dialogInterface.dismiss()
        }
        .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
}

fun rateIntentForUrl(url: String, packageName: String?): Intent {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(java.lang.String.format("%s?id=%s", url, packageName)))
    var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
    intent.addFlags(flags)
    return intent
}

fun Context.rateApp(packageName: String? = this.packageName) {
    try {
        val rateIntent = rateIntentForUrl("market://details", packageName)
        this.startActivity(rateIntent)
    } catch (e: ActivityNotFoundException) {
        val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details", packageName)
        this.startActivity(rateIntent)
    }
}
