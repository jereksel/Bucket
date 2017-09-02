package com.jereksel.libresubstratum.extensions

import org.slf4j.LoggerFactory

inline fun <reified T> T.getLogger() = LoggerFactory.getLogger(T::class.java)

