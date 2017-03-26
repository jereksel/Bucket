package com.jereksel.libresubstratum

import org.junit.Assert.assertEquals
import kotlin.reflect.KClass

fun assertType(clz: KClass<out Any>, obj: Any) {
    assertEquals(clz.java, obj.javaClass)
}

