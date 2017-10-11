package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratumlib.ThemePack

interface IGetThemeInfoUseCase {
    fun getThemeInfo(appId: String, keyPair: KeyPair?): ThemePack
}