package com.jereksel.libresubstratum.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
inline fun <reified T> T.getLogger(): Logger = LoggerFactory.getLogger(T::class.java)

