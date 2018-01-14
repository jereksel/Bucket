package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.functions.BiFunction
import com.jereksel.libresubstratum.utils.ListUtils.replace

object DetailedReducer: BiFunction<DetailedViewState, DetailedResult, DetailedViewState> {

    val log = getLogger()

    override fun apply(t1: DetailedViewState, t2: DetailedResult): DetailedViewState {
        return when(t2) {
//            is DetailedResult.ListLoaded -> t1.copy(type3 = DetailedViewState.Type3(t2.type3, 0))
            is DetailedResult.ListLoaded -> {

                t1.copy(
                        themePack = DetailedViewState.ThemePack(t2.themes.map {

                            DetailedViewState.Theme(
                                    it.appId,
                                    it.name,
                                    "",
                                    it.type1a?.let { DetailedViewState.Type1(it.data, 0) },
                                    it.type1b?.let { DetailedViewState.Type1(it.data, 0) },
                                    it.type1c?.let { DetailedViewState.Type1(it.data, 0) },
                                    it.type2?.let { DetailedViewState.Type2(it.data, 0) },
                                    DetailedViewState.CompilationState.DEFAULT,
                                    DetailedViewState.EnabledState.UNKNOWN,
                                    DetailedViewState.InstalledState.Unknown,
                                    checked = false
                            )

                        },
                                t2.type3?.let { DetailedViewState.Type3(it.data, 0) }
                        )
                )

            }
            is DetailedResult.ChangeSpinnerSelection -> {

                val oldThemes = t1.themePack!!.themes

                when(t2) {
                    is DetailedResult.ChangeSpinnerSelection.ChangeType1aSpinnerSelection -> {

                        val themes = oldThemes.replace(t2.listPosition, {
                            val type1a = it.type1a
                            if (type1a == null) {
                                it
                            } else {
                                it.copy(type1a = type1a.copy(position = t2.position))
                            }

                        })

                        t1.copy(
                                themePack = t1.themePack.copy(themes = themes)
                        )

                    }
                    is DetailedResult.ChangeSpinnerSelection.ChangeType1bSpinnerSelection -> {

                        val themes = oldThemes.replace(t2.listPosition, {
                            val type1b = it.type1b
                            if (type1b == null) {
                                it
                            } else {
                                it.copy(type1b = type1b.copy(position = t2.position))
                            }

                        })

                        t1.copy(
                                themePack = t1.themePack.copy(themes = themes)
                        )

                    }
                    is DetailedResult.ChangeSpinnerSelection.ChangeType1cSpinnerSelection -> {


                        val themes = oldThemes.replace(t2.listPosition, {
                            val type1c = it.type1c
                            if (type1c == null) {
                                it
                            } else {
                                it.copy(type1c = type1c.copy(position = t2.position))
                            }

                        })

                        t1.copy(
                                themePack = t1.themePack.copy(themes = themes)
                        )

                    }
                    is DetailedResult.ChangeSpinnerSelection.ChangeType2SpinnerSelection -> {

                        val themes = oldThemes.replace(t2.listPosition, {
                            val type2 = it.type2
                            if (type2 == null) {
                                it
                            } else {
                                it.copy(type2 = type2.copy(position = t2.position))
                            }

                        })

                        t1.copy(
                                themePack = t1.themePack.copy(themes = themes)
                        )

                    }
                }
            }
            is DetailedResult.InstalledStateResult -> {

                val installedState = t2.installedResult
                val targetApp = t2.targetApp

                val themePack = t1.themePack

                val themes = themePack?.themes

                val newThemes = themes?.replace({it.appId == targetApp}, {
                    it.copy(
                            installedState = installedState,
                            enabledState = t2.enabledState,
                            overlayId = t2.targetOverlayId
                    )
                })

                if (newThemes != null) {
                    t1.copy(
                            themePack = themePack.copy(
                                    themes = newThemes
                            )
                    )
                } else {
                    t1
                }

            }
            is DetailedResult.ChangeType3SpinnerSelection -> {

                val position = t2.position

                t1.copy(
                        themePack = t1.themePack?.copy(
                                type3 = t1.themePack.type3?.copy(
                                        position = position
                                )
                        )
                )

            }
            is DetailedResult.ToggleCheckbox -> {
                t1.copy(
                        themePack = t1.themePack?.copy(
                                themes = t1.themePack.themes.replace(t2.position) { it.copy(checked = t2.state) }
                        )
                )
            }
        }
    }

}