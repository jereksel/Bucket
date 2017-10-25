package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratum.data.KeyPair

interface IKeyFinder {
    /**
     * When key is not needed KeyPair([], []) is returned
     * Null is returned when key can't be found - theme cannot be decrypted
     */
    fun getKey(appId: String): KeyPair?
}