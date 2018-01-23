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

    class GetInfoBasicAction(
            val position: Int
    ): DetailedAction()

    sealed class ChangeSpinnerSelection: DetailedAction() {
        class ChangeType1aSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType1bSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType1cSpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
        class ChangeType2SpinnerSelection(val rvPosition: Int, val position: Int): ChangeSpinnerSelection()
    }

    enum class CompileMode {
        //Long press
        DISABLE_COMPILE_AND_ENABLE,
        //Compile selected
        COMPILE,
        //Compile and enable selected
        COMPILE_AND_ENABLE
    }

    class CompilationAction(
            val appId: String,
            val targetAppId: String,
            val type1a: Type1Extension?,
            val type1b: Type1Extension?,
            val type1c: Type1Extension?,
            val type2: Type2Extension?,
            val type3: Type3Extension?,
            val compileMode: CompileMode
    ): DetailedAction()

    class CompilationLocationAction(val position: Int, val compileMode: CompileMode): DetailedAction()

    class ChangeType3SpinnerSelection(val position: Int): DetailedAction()

    class ToggleCheckbox(val position: Int, val state: Boolean): DetailedAction()

    class CompileSelectedAction(val compileMode: CompileMode): DetailedAction()

}