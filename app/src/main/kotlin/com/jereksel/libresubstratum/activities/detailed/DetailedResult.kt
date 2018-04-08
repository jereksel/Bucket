package com.jereksel.libresubstratum.activities.detailed

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension

sealed class DetailedResult {
    data class ListLoaded(
            val themeAppId: String,
            val themeName: String,
            val themes: List<Theme>,
            val type3: Type3?
    ): DetailedResult() {

        data class Theme(
                val appId: String,
                val name: String,
                val type1a: Type1?,
                val type1b: Type1?,
                val type1c: Type1?,
                val type2: Type2?
        )

        data class Type1(
                val data: List<Type1Extension>
        )

        data class Type2(
                val data: List<Type2Extension>
        )

        data class Type3(
                val data: List<Type3Extension>
        )

    }

    sealed class ChangeSpinnerSelection: DetailedResult() {
        abstract val listPosition: Int
        abstract val position: Int

        data class ChangeType1aSpinnerSelection(override val listPosition: Int, override val position: Int): ChangeSpinnerSelection()
        data class ChangeType1bSpinnerSelection(override val listPosition: Int, override val position: Int): ChangeSpinnerSelection()
        data class ChangeType1cSpinnerSelection(override val listPosition: Int, override val position: Int): ChangeSpinnerSelection()
        data class ChangeType2SpinnerSelection(override val listPosition: Int, override val position: Int): ChangeSpinnerSelection()
    }

    class ChangeType3SpinnerSelection(val position: Int): DetailedResult()

    sealed class InstalledStateResult: DetailedResult() {

        data class Result(
                val targetApp: String,
                val targetOverlayId: String,
                val installedResult: DetailedViewState.InstalledState,
                val enabledState: DetailedViewState.EnabledState
        ): InstalledStateResult()

        data class PositionResult(
                var position: Int
        ): InstalledStateResult()

        data class AppIdResult(
                val appId: String
        ): InstalledStateResult()
    }

    sealed class CompilationStatusResult: DetailedResult() {
        abstract val appId: String

        data class StartFlow(override val appId: String): CompilationStatusResult()
        data class StartCompilation(override val appId: String): CompilationStatusResult()
        data class StartInstallation(override val appId: String): CompilationStatusResult()
        data class FailedCompilation(override val appId: String, val error: Throwable): CompilationStatusResult()
        data class CleanError(override val appId: String): CompilationStatusResult()
        data class EndFlow(override val appId: String): CompilationStatusResult()
    }

    class SelectAllResult: DetailedResult()

    class DeselectAllResult: DetailedResult()

    data class ToggleCheckbox(val position: Int, val state: Boolean): DetailedResult()

    data class LongClickBasicResult(val position: Int, val compileMode: DetailedAction.CompileMode) : DetailedResult()

    data class CompileSelectedResult(val compileMode: DetailedAction.CompileMode): DetailedResult()

    data class ShowErrorResult(val message: String): DetailedResult()

}