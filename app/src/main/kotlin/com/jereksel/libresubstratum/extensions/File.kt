package com.jereksel.libresubstratum.extensions

import java.io.File

fun getFile(file: File, vararg sub: String) = sub.fold(file) {a, b -> File(a, b)}
