package com.jereksel.libresubstratum.data

import java.io.File

data class MainViewTheme(val appId: String, val name: String, val author: String, val heroImage: File?, val isEncrypted: Boolean) {

    companion object {
        fun fromInstalledTheme(theme: InstalledTheme, isEncrypted: Boolean): MainViewTheme =
                MainViewTheme(theme.appId, theme.name, theme.author, theme.heroImage, isEncrypted)
    }

}