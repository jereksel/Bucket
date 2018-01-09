package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratumlib.Type3Extension

sealed class DetailedResult {
    data class ListLoaded(val type3: List<Type3Extension>): DetailedResult()
}