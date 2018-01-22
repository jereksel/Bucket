package com.jereksel.libresubstratum.activities.detailed2

import arrow.optics.modify
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.functions.BiFunction
import com.jereksel.libresubstratum.utils.ListUtils.replace

object DetailedReducer: BiFunction<DetailedViewState, DetailedResult, Pair<DetailedViewState, List<DetailedAction>>> {

    val log = getLogger()

    override fun apply(t1: DetailedViewState, t2: DetailedResult): Pair<DetailedViewState, List<DetailedAction>> {

//        return t1 to emptyList()

        return when(t2) {
            is DetailedResult.ListLoaded -> {

                t1.copy(
//                        themeAppId = t2.themeAppId,
                        themePack = DetailedViewState.ThemePack(
                                appId = t2.themeAppId,
                                themes = t2.themes.map {

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
                                type3 = t2.type3?.let { DetailedViewState.Type3(it.data, 0) }
                        )
                ) to emptyList()

            }
            is DetailedResult.ChangeSpinnerSelection -> {

                val oldThemes = t1.themePack!!.themes

                val state = when(t2) {
                    is DetailedResult.ChangeSpinnerSelection.ChangeType1aSpinnerSelection -> {

                        val optional = detailedViewStateThemePackOptional() +
                                themePackThemes() +
                                listElementOptional(t2.listPosition) +
                                themeType1aOptional() +
                                type1Position()

                        optional.modify(t1, { t2.position })

                    }
                    is DetailedResult.ChangeSpinnerSelection.ChangeType1bSpinnerSelection -> {

                        val optional = detailedViewStateThemePackOptional() +
                                themePackThemes() +
                                listElementOptional(t2.listPosition) +
                                themeType1bOptional() +
                                type1Position()

                        optional.modify(t1, { t2.position })

                    }
                    is DetailedResult.ChangeSpinnerSelection.ChangeType1cSpinnerSelection -> {

                        val optional = detailedViewStateThemePackOptional() +
                                themePackThemes() +
                                listElementOptional(t2.listPosition) +
                                themeType1cOptional() +
                                type1Position()

                        optional.modify(t1, { t2.position })

                    }
                    is DetailedResult.ChangeSpinnerSelection.ChangeType2SpinnerSelection -> {

                        val optional = detailedViewStateThemePackOptional() +
                                themePackThemes() +
                                listElementOptional(t2.listPosition) +
                                themeType2Optional() +
                                type2Position()

                        optional.modify(t1, { t2.position })

                    }
                }


                state to listOf(DetailedAction.GetInfoBasicAction(t2.listPosition))

            }
            is DetailedResult.InstalledStateResult.Result -> {

                val installedState = t2.installedResult
                val targetApp = t2.targetApp

                val themePack = t1.themePack

                val position = themePack?.themes?.indexOfFirst { it.appId == targetApp } ?: -1

                //Technically we don't have to add if here, but it's for readability
                if (position != -1) {

                    val option = detailedViewStateThemePackOptional() +
                            themePackThemes() +
                            listElementOptional(position)

                    option.modify(t1, {
                        it.copy(
                                installedState = installedState,
                                enabledState = t2.enabledState,
                                overlayId = t2.targetOverlayId
                        )
                    })

                } else {
                    t1
                } to emptyList()

            }
            is DetailedResult.ChangeType3SpinnerSelection -> {

                val position = t2.position

                val optional = detailedViewStateThemePackOptional() +
                        themePackType3Optional() +
                        type3Position()

                optional.modify(t1, { position }) to
                        (t1.themePack?.themes ?: listOf()).indices.map { DetailedAction.GetInfoBasicAction(it) }

            }
            is DetailedResult.ToggleCheckbox -> {

                val optional = detailedViewStateThemePackOptional() +
                        themePackThemes() +
                        listElementOptional(t2.position) +
                        themeChecked()

                optional.modify(t1, { t2.state }) to emptyList()

            }
            is DetailedResult.InstalledStateResult.PositionResult -> {

                val theme = t1.themePack?.themes?.get(t2.position) ?: return t1 to emptyList()

                val action = DetailedAction.GetInfoAction(
                        appId = t1.themePack.appId,
                        targetAppId = theme.appId,
                        type1a = theme.type1a?.get(),
                        type1b = theme.type1b?.get(),
                        type1c = theme.type1c?.get(),
                        type2 = theme.type2?.get(),
                        type3 = t1.themePack.type3?.get()
                )

                return t1 to listOf(action)

            }
            is DetailedResult.InstalledStateResult.AppIdResult -> {

                val themeLocation = t1.themePack?.themes?.indexOfFirst { it.appId == t2.appId } ?: -1

                if (themeLocation == -1) {
                    log.error("Cannot find app: {}", t2.appId)
                    t1 to emptyList()
                } else {
                    t1 to listOf(DetailedAction.GetInfoBasicAction(themeLocation))
                }

            }
            is DetailedResult.LongClickBasicResult -> {

                val theme = t1.themePack?.themes?.get(t2.position) ?: return t1 to emptyList()

                val action = DetailedAction.LongClick(
                        appId = t1.themePack.appId,
                        targetAppId = theme.appId,
                        type1a = theme.type1a?.get(),
                        type1b = theme.type1b?.get(),
                        type1c = theme.type1c?.get(),
                        type2 = theme.type2?.get(),
                        type3 = t1.themePack.type3?.get()
                )

                return t1 to listOf(action)

            }
            is DetailedResult.CompilationStatusResult -> {

                val appId = t2.appId
                val themeLocation = t1.themePack?.themes?.indexOfFirst { it.appId == appId } ?: -1

                val compilationState = when(t2) {
                    is DetailedResult.CompilationStatusResult.StartCompilation -> DetailedViewState.CompilationState.COMPILING
                    is DetailedResult.CompilationStatusResult.StartInstallation -> DetailedViewState.CompilationState.INSTALLING
                    is DetailedResult.CompilationStatusResult.EndCompilation -> DetailedViewState.CompilationState.DEFAULT
                }

                val optional = detailedViewStateThemePackOptional() +
                        themePackThemes() +
                        listElementOptional(themeLocation) +
                        themeCompilationState()

                optional.modify(t1, { compilationState }) to listOf()

            }
        }
    }

}