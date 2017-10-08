package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratumlib.ThemePack
import io.reactivex.Observable
import java.io.File

interface ICompileThemeUseCase {

    fun execute(
            themePack: ThemePack,
            themeId: String,
            themeLocation: File,
            destAppId: String,
            type1aName: String?,
            type1bName: String?,
            type1cName: String?,
            type2Name: String?,
            type3Name: String?
    ): Observable<File>
}
