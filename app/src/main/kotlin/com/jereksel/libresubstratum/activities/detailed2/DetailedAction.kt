package com.jereksel.libresubstratum.activities.detailed2

sealed class DetailedAction {
    class InitialAction(val appId: String) : DetailedAction()

    sealed class ChangeSpinnerSelection: DetailedAction() {
        class ChangeType1aSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType1bSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType1cSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType2SpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
    }
}