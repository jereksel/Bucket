package com.jereksel.libresubstratum.domain

data class SubstratumDatabaseTheme(
        val author: String,
        //Play store link is basically https://play.google.com/store/apps/details?id= + packageId
        val packageId: String,
        val princing: Princing,
        val support: List<Support>,
        val image: String,
        val backgroundImage: String
)

enum class Princing {
    FREE,
    PAID
}

enum class Support {
    OVERLAYS,
    FONTS,
    SOUNDS,
    BOOTANIMATIONS
}