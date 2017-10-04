package com.jereksel.libresubstratum.domain.usecases

import android.util.TimeUtils
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.ThemeNameUtils
import com.jereksel.libresubstratumlib.*
import io.reactivex.Observable
import java.io.File
import java.util.concurrent.TimeUnit

class CompileThemeUseCase(
        val packageManager: IPackageManager,
        val themeReader: IThemeReader,
        val overlayService: OverlayService,
        val activityProxy: IActivityProxy,
        val themeCompiler: ThemeCompiler,
        val themeExtractor: ThemeExtractor
): ICompileThemeUseCase {

    val log = getLogger()

    override fun execute(
            theme: Theme,
            themeId: String,
            themeLocation: File,
            destAppId: String,
            type1aName: String?,
            type1bName: String?,
            type1cName: String?,
            type2Name: String?,
            type3Name: String?
    ): Observable<File> = Observable.fromCallable {

        if (!themeLocation.exists()) {
            throw IllegalArgumentException("$themeLocation doesn't exists")
        }

        if (!themeLocation.isDirectory) {
            throw IllegalArgumentException("$themeLocation is not a directory")
        }

        val (versionCode, versionName) = packageManager.getAppVersion(themeId)

        val location = packageManager.getAppLocation(themeId)

        val themePack = themeReader.readThemePack(location)

//        val theme = themePack.themes.find { it.application == destAppId }!!

        val m = mapOf(
                "a" to type1aName,
                "b" to type1bName,
                "c" to type1cName
        )

        val type1a = theme.type1.firstOrNull { it.suffix == "a" }?.extension?.firstOrNull { it.name == type1aName }
        val type1b = theme.type1.firstOrNull { it.suffix == "b" }?.extension?.firstOrNull { it.name == type1bName }
        val type1c = theme.type1.firstOrNull { it.suffix == "c" }?.extension?.firstOrNull { it.name == type1cName }

        val type1s = theme.type1.mapNotNull {
            val id = it.suffix
            val ext = it.extension.find { it.name == m[id] }
            if (ext != null) {
                Type1DataToCompile(ext, id)
            } else {
                null
            }
        }

        val type2 = theme.type2?.extensions?.firstOrNull { it.name == type2Name }
        val type3 = themePack.type3?.extensions?.firstOrNull { it.name == type3Name }

        val fixedTargetApp = if (theme.application.startsWith("com.android.systemui.")) "com.android.systemui" else theme.application

        val themeName = packageManager.getAppName(themeId)

        val targetOverlayId = ThemeNameUtils.getTargetOverlayName(destAppId, themeName, type1a, type1b, type1c, type2, type3)

        val themeToCompile = ThemeToCompile(targetOverlayId, themeId, fixedTargetApp, type1s, type2,
                type3, versionCode, versionName)

        val t1 = System.currentTimeMillis()

        val file = themeCompiler.compileTheme(themeToCompile, themeLocation)

        val t2 = System.currentTimeMillis()

        val seconds = TimeUnit.MILLISECONDS.toSeconds(t2-t1)

//        log.debug("Compilation of {} took {}s", targetOverlayId, seconds)

//        val compilationTimeInSeconds = TimeUnit.MIL(t2-t1)

        file

    }

}