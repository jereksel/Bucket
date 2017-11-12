package com.jereksel.libresubstratum.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.graphics.Palette
import android.widget.ListView
import android.widget.TextView
import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratumlib.Type1Extension
import org.assertj.android.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.anko.find
import org.junit.Before
import org.junit.Test
import org.robolectric.RuntimeEnvironment

@Suppress("IllegalIdentifier")
class Type1SpinnerArrayAdapterTest: BaseRobolectricTest() {

    lateinit var adapter: Type1SpinnerArrayAdapter

    @Before
    fun setUp() {
        val objects = listOf(Type1Extension("a", true, "#ABCDEF"), Type1Extension("b", false, "")).map { Type1ExtensionToString(it) }
        adapter = Type1SpinnerArrayAdapter(RuntimeEnvironment.application, objects)
    }

    @Test
    fun `Type1 extension with color should have background and colored text`() {
        val parent = ListView(RuntimeEnvironment.application)
        val view = adapter.getView(0, null, parent)
        val textView = view.find<TextView>(R.id.textView)
        val swatch = Palette.Swatch(Color.parseColor("#ABCDEF"), 1)

        assertThat(textView).hasText("a")
        assertThat(textView).hasCurrentTextColor(swatch.titleTextColor)
        assertThat((view.background as ColorDrawable).color).isEqualTo(Color.parseColor("#ABCDEF"))
    }

    @Test
    fun `Type1 extension without color has white background and black text`() {
        val parent = ListView(RuntimeEnvironment.application)
        val view = adapter.getView(1, null, parent)
        val textView = view.find<TextView>(R.id.textView)

        assertThat(textView).hasText("b")
        assertThat(textView).hasCurrentTextColor(Color.BLACK)
        assertThat((view.background as ColorDrawable).color).isEqualTo(Color.WHITE)
    }

    @Test
    fun `Translucent colors doesn't throw exceptions`() {
        val objects = listOf(Type1Extension("a", true, "#33ABCDEF")).map { Type1ExtensionToString(it) }
        adapter = Type1SpinnerArrayAdapter(RuntimeEnvironment.application, objects)
        val parent = ListView(RuntimeEnvironment.application)
        val view = adapter.getView(0, null, parent)
        assertThat(view).isNotNull
    }

}