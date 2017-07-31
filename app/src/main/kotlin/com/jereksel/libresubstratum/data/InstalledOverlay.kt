package com.jereksel.libresubstratum.data

import android.graphics.drawable.Drawable

data class InstalledOverlay (
        val overlayId: String,
//        val appName: String,
        val sourceThemeId: String,
        val sourceThemeName: String,
        val sourceThemeDrawable: Drawable,
        val targetId: String,
        val targetName: String,
        val targetDrawable: Drawable,
        val type1a: String? = null,
        val type1b: String? = null,
        val type1c: String? = null,
        val type2: String? = null,
        val type3: String? = null
)
