package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratumlib.Theme
import rx.Observable
import java.io.File

interface ICompileThemeUseCase {

    fun execute(
            theme: Theme,
            themeId: String,
            themeLocation: File,
            destAppId: String,
            //            destOverlayId: String,
            type1aName: String?,
            type1bName: String?,
            type1cName: String?,
            type2Name: String?,
            type3Name: String?
//            versionCode: Int,
//            versionName: String
//            type3Data: Type3Data
    ): Observable<File>

}
