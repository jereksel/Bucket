package com.jereksel.libresubstratum.extensions

import android.os.Bundle

fun Bundle.has(key: String) = this.get(key) != null
