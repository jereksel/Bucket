package com.jereksel.libresubstratum.data

import android.graphics.drawable.Drawable

data class InstalledOverlay (
        val appId: String,
        val appName: String,
        val sourceDrawable: Drawable,
        val targetDrawable: Drawable,
        val targetId: String,
        val targetName: String,
        val type1a: String? = null,
        val type1b: String? = null,
        val type1c: String? = null,
        val type2: String? = null,
        val type3: String? = null
)
