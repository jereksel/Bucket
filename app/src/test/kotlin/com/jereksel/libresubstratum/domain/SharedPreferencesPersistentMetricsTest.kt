package com.jereksel.libresubstratum.domain

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.infrastructure.SharedPreferencesPersistentMetrics
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.MapEntry.entry
import org.junit.Before
import org.junit.Test
import org.robolectric.RuntimeEnvironment

@Suppress("IllegalIdentifier")
class SharedPreferencesPersistentMetricsTest: BaseRobolectricTest() {

    lateinit var metrics: SharedPreferencesPersistentMetrics
    lateinit var sp: SharedPreferences

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application
        metrics = SharedPreferencesPersistentMetrics(app)
        sp = app.getSharedPreferences(metrics.SP_KEY, MODE_PRIVATE)
    }

    @Test
    fun `UserEnteredTheme is saving id to SharedPreferences`() {
        val themeId = "themeid"
        assertThat(sp.getString(metrics.THEME_KEY, null)).isNull()
        metrics.userEnteredTheme(themeId)
        assertThat(sp.getString(metrics.THEME_KEY, null)).isEqualTo("themeid")
    }

    @Test
    fun `logOverlayServiceType is saving id to SharedPreferences`() {
        val os: OverlayService = mock()
        assertThat(sp.getString(metrics.OVERLAY_SERVICE_TYPE_KEY, null)).isNull()
        metrics.logOverlayServiceType(os)
        assertThat(sp.getString(metrics.OVERLAY_SERVICE_TYPE_KEY, null)).isEqualTo(os.javaClass.toString())
    }

    @Test
    fun `getMetrics returned data saved in SharedPreferences`() {
        sp.edit().putString(metrics.THEME_KEY, "theme").commit()
        sp.edit().putString(metrics.OVERLAY_SERVICE_TYPE_KEY, "overlay").commit()
        assertThat(metrics.getMetrics()).containsExactly(entry("currentTheme", "theme"), entry("overlayService", "overlay"))
    }

    @Test
    fun `getMetrics doesn't return null values`() {
        sp.edit().putString(metrics.THEME_KEY, "theme").commit()
        assertThat(metrics.getMetrics()).containsExactly(entry("currentTheme", "theme"))
    }

}