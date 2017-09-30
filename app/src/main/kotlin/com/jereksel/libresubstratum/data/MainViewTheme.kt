package com.jereksel.libresubstratum.data

import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

data class MainViewTheme(val appId: String, val name: String, val author: String, val heroImage: FutureTask<File?>, val isEncrypted: Boolean) {
    constructor(appId: String, name: String, author: String, file: File?, isEncrypted: Boolean) : this(appId, name, author, FutureTask { file }, isEncrypted)

    companion object {
        fun fromInstalledTheme(theme: InstalledTheme, isEncrypted: Boolean): MainViewTheme =
                MainViewTheme(theme.appId, theme.name, theme.author, theme.heroImage, isEncrypted)
    }

}