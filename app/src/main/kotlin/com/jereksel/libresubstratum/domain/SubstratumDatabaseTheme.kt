package com.jereksel.libresubstratum.domain

data class SubstratumDatabaseTheme(
        val name: String,
        val author: String,
        //Play store link is basically https://play.google.com/store/apps/details?id= + packageId
        val link: String,
        val packageId: String,
        val pricing: Pricing,
        val support: List<Support>,
        val image: String,
        val backgroundImage: String
)

enum class Pricing {
    FREE,
    PAID
}

enum class Support {
    OVERLAYS,
    FONTS,
    SOUNDS,
    BOOTANIMATIONS
}