package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratumlib.ThemePack
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

interface ICompileThemeUseCase {

    fun execute(
            themePack: ThemePack,
            themeId: String,
            destAppId: String,
            type1aName: String?,
            type1bName: String?,
            type1cName: String?,
            type2Name: String?,
            type3Name: String?
    ): Single<File>
}
