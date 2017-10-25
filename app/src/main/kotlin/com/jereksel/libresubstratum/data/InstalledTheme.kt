package com.jereksel.libresubstratum.data

import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

data class InstalledTheme(
        val appId: String,
        val name: String,
        val author: String,
        val encrypted: Boolean,
        val pluginVersion: String,
        val heroImage: FutureTask<File?>
) {
//    constructor(appId: String, name: String, author: String, file: File?) : this(appId, name, author, FutureTask { file })
}