package com.jereksel.libresubstratum.domain

import android.content.ClipData
import android.content.Context

typealias RealClipboardManager = android.content.ClipboardManager

class AndroidClipboardManager(private val context: Context): ClipboardManager {
    override fun addToClipboard(message: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as RealClipboardManager
        val clip = ClipData.newPlainText("BucketThemeManager", message)
        clipboard.primaryClip = clip
    }
}