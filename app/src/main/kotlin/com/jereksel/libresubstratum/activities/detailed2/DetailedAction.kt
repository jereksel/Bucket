package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension

sealed class DetailedAction {
    class InitialAction(val appId: String) : DetailedAction()

    class GetInfoAction(
            val appId: String,
            val targetAppId: String,
            val type1a: Type1Extension?,
            val type1b: Type1Extension?,
            val type1c: Type1Extension?,
            val type2: Type2Extension?,
            val type3: Type3Extension?
    ): DetailedAction()

    sealed class ChangeSpinnerSelection: DetailedAction() {
        class ChangeType1aSpinnerSelection(val theme: DetailedViewState.Theme, val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType1bSpinnerSelection(val theme: DetailedViewState.Theme, val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType1cSpinnerSelection(val theme: DetailedViewState.Theme, val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType2SpinnerSelection(val theme: DetailedViewState.Theme, val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
    }

    class ChangeType3SpinnerSelection(val position: Int): DetailedAction()
}