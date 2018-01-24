package com.jereksel.libresubstratum.activities.detailed2

import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.modify
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.functions.BiFunction

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

                val option = detailedViewStateThemePackOptional() +
                        themePackThemes() +
                        listElementOptional(position)

                t1.modify(option, {
                    it.copy(
                            installedState = installedState,
                            enabledState = t2.enabledState,
                            overlayId = t2.targetOverlayId
                    )
                }) to emptyList()

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

                val action = DetailedAction.CompilationAction(
                        appId = t1.themePack.appId,
                        targetAppId = theme.appId,
                        type1a = theme.type1a?.get(),
                        type1b = theme.type1b?.get(),
                        type1c = theme.type1c?.get(),
                        type2 = theme.type2?.get(),
                        type3 = t1.themePack.type3?.get(),
                        compileMode = t2.compileMode
                )

                return t1 to listOf(action)

            }
            is DetailedResult.CompilationStatusResult -> {

                val appId = t2.appId
                val themeLocation = t1.themePack?.themes?.indexOfFirst { it.appId == appId } ?: -1

                val compilationStateOptional = detailedViewStateThemePackOptional() +
                        themePackThemes() +
                        listElementOptional(themeLocation) +
                        themeCompilationState()

                val allCompilationsOptional = detailedViewStateNumberOfAllCompilations()
                val finishedCompilationsOptional = detailedViewStateNumberOfFinishedCompilations()

                when(t2) {
                    is DetailedResult.CompilationStatusResult.StartFlow -> {
                        t1
                                .modify(allCompilationsOptional, {it + 1})
                    }
                    is DetailedResult.CompilationStatusResult.StartCompilation -> {
                        t1
                                .modify(compilationStateOptional, {DetailedViewState.CompilationState.COMPILING})
                    }
                    is DetailedResult.CompilationStatusResult.StartInstallation -> {
                        t1
                                .modify(compilationStateOptional, {DetailedViewState.CompilationState.INSTALLING})
                    }
                    is DetailedResult.CompilationStatusResult.FailedCompilation -> {
                        t1
                                .copy(compilationError = t2.error)
                    }
                    is DetailedResult.CompilationStatusResult.CleanError -> {
                        t1.copy(compilationError = null)
                    }

                    is DetailedResult.CompilationStatusResult.EndFlow -> {
                        val t = t1
                                .modify(compilationStateOptional, {DetailedViewState.CompilationState.DEFAULT})
                                .modify(finishedCompilationsOptional, {it + 1})

                        if (t.numberOfAllCompilations == t.numberOfFinishedCompilations) {
                            t.copy(numberOfAllCompilations = 0, numberOfFinishedCompilations = 0)
                        } else {
                            t
                        }
                    }
                } to emptyList()

            }
            is DetailedResult.CompileSelectedResult -> {

                val themeOptions = detailedViewStateThemePackOptional() + themePackThemes()

                t1.modify(themeOptions, {it.map { it.copy(checked = false) }}) to
                        (t1.themePack?.themes ?: listOf()).mapIndexedNotNull { index: Int, theme: DetailedViewState.Theme ->
                            if (theme.checked) {
                                DetailedAction.CompilationLocationAction(index, t2.compileMode)
                            } else {
                                null
                            }
                        }
            }
        }
    }

    private inline fun <S, T, A, B> S.modify(optional: POptional<S, T, A, B>, crossinline f: (A) -> B) = optional.modify(this, f)
    private inline fun <S, T, A, B> S.modify(optional: PLens<S, T, A, B>, crossinline f: (A) -> B) = optional.modify(this, f)

}