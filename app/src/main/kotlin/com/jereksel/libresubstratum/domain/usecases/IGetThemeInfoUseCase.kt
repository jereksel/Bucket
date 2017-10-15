package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratumlib.ThemePack

interface IGetThemeInfoUseCase {
    fun getThemeInfo(appId: String): ThemePack
}