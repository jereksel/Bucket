package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratum.data.KeyPair

interface IKeyFinder {
    fun getKey(appId: String): KeyPair?
}