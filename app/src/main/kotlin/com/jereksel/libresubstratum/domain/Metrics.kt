package com.jereksel.libresubstratum.domain

interface Metrics {
    //MainView
    fun userEnteredTheme(themeId: String)

    //DetailedView
    fun userCompiledOverlay(themeId: String, targetApp: String)

    //InstalledView
    fun userEnabledOverlay(overlayId: String)
    fun userDisabledOverlay(overlayId: String)

    fun logOverlayServiceType(overlayService: OverlayService)
}