package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratumlib.Type3Extension

data class DetailedViewState(
        val number: Int,
        val type3: Type3?
) {

    data class Type3(
            val data: List<Type3Extension>,
            val position: Int
    )
}