package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratumlib.ThemeToCompile
import java.io.File

interface ThemeCompiler {
    fun compileTheme(themeDate: ThemeToCompile, dir: File): File
}