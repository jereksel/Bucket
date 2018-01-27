package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension

sealed class DetailedAction {
    data class InitialAction(val appId: String) : DetailedAction()

    data class GetInfoAction(
            val appId: String,
            val targetAppId: String,
            val type1a: Type1Extension?,
            val type1b: Type1Extension?,
            val type1c: Type1Extension?,
            val type2: Type2Extension?,
            val type3: Type3Extension?
    ): DetailedAction()

    data class GetInfoBasicAction(
            val position: Int
    ): DetailedAction()

    sealed class ChangeSpinnerSelection: DetailedAction() {
        data class ChangeType1aSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        data class ChangeType1bSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        data class ChangeType1cSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        data class ChangeType2SpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
    }

    enum class CompileMode {
        //Long press
        DISABLE_COMPILE_AND_ENABLE,
        //Compile selected
        COMPILE,
        //Compile and enable selected
        COMPILE_AND_ENABLE
    }

    data class CompilationAction(
            val appId: String,
            val targetAppId: String,
            val type1a: Type1Extension?,
            val type1b: Type1Extension?,
            val type1c: Type1Extension?,
            val type2: Type2Extension?,
            val type3: Type3Extension?,
            val compileMode: CompileMode
    ): DetailedAction()

    class SelectAllAction : DetailedAction()

    class DeselectAllAction : DetailedAction()

    class RestartUIAction : DetailedAction()

    data class CompilationLocationAction(val position: Int, val compileMode: CompileMode): DetailedAction()

    data class ChangeType3SpinnerSelection(val position: Int): DetailedAction()

    data class ToggleCheckbox(val position: Int, val state: Boolean): DetailedAction()

    data class CompileSelectedAction(val compileMode: CompileMode): DetailedAction()

}