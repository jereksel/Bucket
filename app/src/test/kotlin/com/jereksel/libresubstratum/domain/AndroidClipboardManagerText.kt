package com.jereksel.libresubstratum.domain

import android.content.ClipboardManager
import android.content.Context
import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.infrastructure.AndroidClipboardManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.robolectric.RuntimeEnvironment

@Suppress("IllegalIdentifier")
class AndroidClipboardManagerText: BaseRobolectricTest() {

    lateinit var manager: AndroidClipboardManager

    @Before
    fun setup() {
        manager = AndroidClipboardManager(RuntimeEnvironment.application)
    }

    @Test
    fun `Basic clipboard test`() {
        manager.addToClipboard("Test string")
        val text = (RuntimeEnvironment.application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).text
        assertEquals("Test string", text)
    }

}

