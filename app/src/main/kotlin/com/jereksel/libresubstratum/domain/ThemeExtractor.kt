package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratum.data.KeyPair
import java.io.File
import java.util.concurrent.Future

interface ThemeExtractor {
    fun extract(file: File, dest: File, key: KeyPair?): Future<File>
}