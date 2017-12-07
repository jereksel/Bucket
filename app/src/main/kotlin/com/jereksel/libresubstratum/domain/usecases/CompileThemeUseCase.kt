/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.jereksel.libresubstratum.domain.ThemeCompiler
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.ThemeNameUtils
import com.jereksel.libresubstratumlib.Theme
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.ThemeToCompile
import com.jereksel.libresubstratumlib.Type1DataToCompile
import io.reactivex.Observable
import java.io.File

class CompileThemeUseCase(
        private val packageManager: IPackageManager,
        private val themeCompiler: ThemeCompiler
): ICompileThemeUseCase {

    val log = getLogger()

    override fun execute(
            themePack: ThemePack,
            themeId: String,
            destAppId: String,
            type1aName: String?,
            type1bName: String?,
            type1cName: String?,
            type2Name: String?,
            type3Name: String?
    ): Observable<File> = Observable.fromCallable {

        val (versionCode, versionName) = packageManager.getAppVersion(themeId)

        val theme = themePack.themes.first { it.application == destAppId }

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

        val originalTargetApp = theme.application
        val fixedTargetApp = if (originalTargetApp.startsWith("com.android.systemui.")) "com.android.systemui" else originalTargetApp

        val themeName = packageManager.getInstalledTheme(themeId).name

        val targetOverlayId = ThemeNameUtils.getTargetOverlayName(destAppId, themeName, type1a, type1b, type1c, type2, type3)

        val themeToCompile = ThemeToCompile(targetOverlayId, themeId, originalTargetApp,
                fixedTargetApp, type1s, type2, type3, versionCode, versionName)

        val file = themeCompiler.compileTheme(themeToCompile)

        file
    }

}