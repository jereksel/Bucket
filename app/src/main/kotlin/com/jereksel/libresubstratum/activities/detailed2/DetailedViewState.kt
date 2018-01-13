package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension

data class DetailedViewState(
        val themePack: ThemePack?
) {

    data class ThemePack(
            val themes: List<Theme>,
            val type3: Type3?
    )

    data class Theme(
            val appId: String,
            val name: String,
            val type1a: Type1?,
            val type1b: Type1?,
            val type1c: Type1?,
            val type2: Type2?,
            val state: State
    )

    data class Type1(
            val data: List<Type1Extension>,
            val position: Int
    )

    data class Type2(
            val data: List<Type2Extension>,
            val position: Int
    )

    data class Type3(
            val data: List<Type3Extension>,
            val position: Int
    )

    companion object {
        val INITIAL = DetailedViewState(null)
    }

    enum class State {
        DEFAULT,
        COMPILING,
        INSTALLING
    }

}