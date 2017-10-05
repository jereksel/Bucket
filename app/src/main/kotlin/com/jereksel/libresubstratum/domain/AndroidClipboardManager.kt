package com.jereksel.libresubstratum.domain

import android.content.ClipData
import android.content.Context

typealias ReadClipboardManager = android.content.ClipboardManager

class AndroidClipboardManager(private val context: Context): ClipboardManager {
    override fun addToClipboard(message: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ReadClipboardManager
        val clip = ClipData.newPlainText("LibreSubstratum", message)
        clipboard.primaryClip = clip
    }
}