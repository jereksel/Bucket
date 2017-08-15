package com.jereksel.libresubstratumlib

data class ThemeToCompile(
        val appId: String,
        val type1: List<Type1DataToCompile> = listOf(),
        val type2: Type2Extension? = null,
        val type3: Type3Extension? = null
)