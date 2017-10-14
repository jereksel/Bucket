package com.jereksel.libresubstratumlib

data class ThemeToCompile(
        val targetOverlayId: String,
        val targetThemeId: String,
        val originalTargetApp: String,
        // com.android.systemui.* -> com.android.systemui. Should only be used when looking for overlay data in theme
        val fixedTargetApp: String,
        val type1: List<Type1DataToCompile> = listOf(),
        val type2: Type2Extension? = null,
        val type3: Type3Extension? = null,
        val versionCode: Int = 0,
        val versionName: String = ""
)