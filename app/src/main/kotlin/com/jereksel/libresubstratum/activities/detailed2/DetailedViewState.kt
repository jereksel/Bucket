package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension

data class DetailedViewState(
        val themeAppId: String?,
        val themePack: ThemePack?
) {

    data class ThemePack(
            val themes: List<Theme>,
            val type3: Type3?
    )

    data class Theme(
            val appId: String,
            val name: String,
            val overlayId: String,
            val type1a: Type1?,
            val type1b: Type1?,
            val type1c: Type1?,
            val type2: Type2?,
            val compilationState: CompilationState,
            val enabledState: EnabledState,
            val installedState: InstalledState,
            val checked: Boolean
    )

    data class Type1(
            val data: List<Type1Extension>,
            val position: Int
    ) {
        fun get() = data[position]
    }

    data class Type2(
            val data: List<Type2Extension>,
            val position: Int
    ) {
        fun get() = data[position]
    }

    data class Type3(
            val data: List<Type3Extension>,
            val position: Int
    ) {
        fun get() = data[position]
    }

    companion object {
        val INITIAL = DetailedViewState(null, null)
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
                val currentVersionName: String,
                val currentVersionCode: Int,
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