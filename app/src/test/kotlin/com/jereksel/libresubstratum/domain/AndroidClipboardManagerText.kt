package com.jereksel.libresubstratum.domain

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.MockedApp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Suppress("IllegalIdentifier")
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class AndroidClipboardManagerText {

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

