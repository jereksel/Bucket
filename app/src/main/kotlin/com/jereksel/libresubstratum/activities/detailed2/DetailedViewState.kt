package com.jereksel.libresubstratum.activities.detailed2

import arrow.core.Option
import arrow.lenses
import arrow.optionals
import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension

@lenses
@optionals
data class DetailedViewState(
        val themePack: ThemePack?,
        val compilationError: Throwable?,
        val numberOfFinishedCompilations: Int,
        val numberOfAllCompilations: Int,
        val toast: () -> String?,
        val test: Option<String>
) {

    @lenses
    @optionals
    data class ThemePack(
            val appId: String,
            val themeName: String,
            val themes: List<Theme>,
            val type3: Type3?
    )

    @lenses
    @optionals
    data class Theme(
            val appId: String,
            val name: String,
            val overlayId: String,
            val type1a: Type1?,
            val type1b: Type1?,
            val type1c: Type1?,
            val type2: Type2?,
            val compilationError: Throwable?,
            val compilationState: CompilationState,
            val enabledState: EnabledState,
            val installedState: InstalledState,
            val checked: Boolean
    )

    @lenses
    data class Type1(
            val data: List<Type1Extension>,
            val position: Int
    ) {
        fun get() = data[position]
    }

    @lenses
    data class Type2(
            val data: List<Type2Extension>,
            val position: Int
    ) {
        fun get() = data[position]
    }

    @lenses
    data class Type3(
            val data: List<Type3Extension>,
            val position: Int
    ) {
        fun get() = data[position]
    }

    companion object {
        val INITIAL = DetailedViewState(null, null, 0,0, {null}, Option.empty())
    }

    enum class CompilationState {
        DEFAULT,
        COMPILING,
        INSTALLING
    }

    sealed class InstalledState {
        object Unknown: InstalledState()

        data class Installed(
                val versionName: String,
                val versionCode: Int
        ): InstalledState()

        data class Outdated(
                val installedVersionName: String,
                val installedVersionCode: Int,
                val newestVersionName: String,
                val newestVersionCode: Int
        ): InstalledState()

        object Removed: InstalledState()
    }

    enum class EnabledState {
        UNKNOWN,
        ENABLED,
        DISABLED
    }

//    enum class InstalledState {
//        UNKNOWN,
//        INSTALLED,
//        OUTDATED,
//        REMOVED
//    }

}