package com.jereksel.libresubstratum.domain

import java.io.File
import java.util.concurrent.Future

interface ThemeExtractor {
    fun extract(file: File, dest: File): Future<File>
}