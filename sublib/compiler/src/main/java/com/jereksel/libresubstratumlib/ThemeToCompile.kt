package com.jereksel.libresubstratumlib

data class ThemeToCompile(
        val targetOverlayId: String,
        val targetThemeId: String,
        val targetAppId: String,
        val type1: List<Type1DataToCompile> = listOf(),
        val type2: Type2Extension? = null,
        val type3: Type3Extension? = null,
        val versionCode: Int = 0,
        val versionName: String = ""
)