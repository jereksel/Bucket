package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension

sealed class DetailedResult {
    data class ListLoaded(
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
        class ChangeType1aSpinnerSelection(val listPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType1bSpinnerSelection(val listPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType1cSpinnerSelection(val listPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType2SpinnerSelection(val listPosition: Int, val position: Int): ChangeSpinnerSelection()
    }

    class ChangeType3SpinnerSelection(val position: Int): DetailedResult()

    data class InstalledStateBasicResult(
            val appId: String,
            var position: Int
    ): DetailedResult()

    data class InstalledStateResult(
            val targetApp: String,
            val targetOverlayId: String,
            val installedResult: DetailedViewState.InstalledState,
            val enabledState: DetailedViewState.EnabledState
    ): DetailedResult()

    data class ToggleCheckbox(val position: Int, val state: Boolean): DetailedResult()

    data class LongClickBasicResult(val position: Int) : DetailedResult()

}