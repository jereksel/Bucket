package com.jereksel.libresubstratum.activities.detailed

import com.jereksel.libresubstratum.View
import com.jereksel.libresubstratum.data.ThemePack

interface IDetailedView : View {
    fun addThemes(themePack: ThemePack)
}

