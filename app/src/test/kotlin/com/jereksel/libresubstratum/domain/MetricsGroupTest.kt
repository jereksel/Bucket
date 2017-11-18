package com.jereksel.libresubstratum.domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MetricsGroupTest: StringSpec() {

    @Mock
    lateinit var metrics1: Metrics
    @Mock
    lateinit var metrics2: Metrics

    lateinit var metricsGroup: MetricsGroup

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        metricsGroup = MetricsGroup(metrics1, metrics2)
    }

    //Test checks if calls are passed to both metrics
    init {

        "userEnteredTheme" {
            metricsGroup.userEnteredTheme("themeid")
            verify(metrics1).userEnteredTheme("themeid")
            verify(metrics2).userEnteredTheme("themeid")
            verifyNoMoreInteractions(metrics1)
            verifyNoMoreInteractions(metrics2)
        }
        "userCompiledOverlay" {
            metricsGroup.userCompiledOverlay("themeid", "targetapp")
            verify(metrics1).userCompiledOverlay("themeid", "targetapp")
            verify(metrics2).userCompiledOverlay("themeid", "targetapp")
            verifyNoMoreInteractions(metrics1)
            verifyNoMoreInteractions(metrics2)
        }
        "userEnabledOverlay" {
            metricsGroup.userEnabledOverlay("themeid")
            verify(metrics1).userEnabledOverlay("themeid")
            verify(metrics2).userEnabledOverlay("themeid")
            verifyNoMoreInteractions(metrics1)
            verifyNoMoreInteractions(metrics2)
        }
        "userDisabledOverlay" {
            metricsGroup.userDisabledOverlay("themeid")
            verify(metrics1).userDisabledOverlay("themeid")
            verify(metrics2).userDisabledOverlay("themeid")
            verifyNoMoreInteractions(metrics1)
            verifyNoMoreInteractions(metrics2)
        }
        "logOverlayServiceType" {
            val overlayService: OverlayService = mock()
            metricsGroup.logOverlayServiceType(overlayService)
            verify(metrics1).logOverlayServiceType(overlayService)
            verify(metrics2).logOverlayServiceType(overlayService)
            verifyNoMoreInteractions(metrics1)
            verifyNoMoreInteractions(metrics2)
        }
        "getMetrics" {
            whenever(metrics1.getMetrics()).thenReturn(mapOf("1" to "a", "2" to "b"))
            whenever(metrics2.getMetrics()).thenReturn(mapOf("2" to "b", "3" to "c"))
            val map = metricsGroup.getMetrics()

            assertThat(map).containsOnly(entry("1", "a"), entry("2", "b"), entry("3", "c"))
        }
    }
}