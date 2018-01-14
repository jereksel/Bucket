/*
 * Copyright (C) 2018 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.activities.detailed2

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.BehaviorSubject
import java.util.function.BiFunction

class DetailedSimpleUIActionProcessor(
        val appId: String,
        val viewState: Observable<DetailedViewState>
) {

    val changeSpinnerSelection = ObservableTransformer<Pair<DetailedSimpleUIAction.ChangeSpinnerSelection, DetailedViewState>, DetailedAction> { actions ->

        actions
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap {

                    //ZipWith causes some weird issues
//                    val state = viewState.blockingFirst()

                    val (action, state) = it
//
//                    val action = it

//                    Observable.just(null)


                    val actions = when(action) {
                        is DetailedSimpleUIAction.ChangeSpinnerSelection.ChangeType1aSpinnerSelection -> {

                            val theme = state.themePack?.themes?.get(action.rvPosition)

                            if (theme != null) {
                                val type1a = theme.type1a?.data?.get(action.position)
                                val type1b = theme.type1b?.data?.get(theme.type1b.position)
                                val type1c = theme.type1c?.data?.get(theme.type1c.position)
                                val type2 = theme.type2?.data?.get(theme.type2.position)
                                val type3 = state.themePack.type3?.data?.get(state.themePack.type3.position)

                                listOf(
                                        DetailedAction.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(theme, action.rvPosition, action.position),
                                        DetailedAction.GetInfoAction(
                                                appId = appId,
                                                targetAppId = theme.appId,
                                                type1a = type1a,
                                                type1b = type1b,
                                                type1c = type1c,
                                                type2 = type2,
                                                type3 = type3
                                        )
                                )

                            } else {
                                emptyList()
                            }

                        }
                        is DetailedSimpleUIAction.ChangeSpinnerSelection.ChangeType1bSpinnerSelection -> {

                            val theme = state.themePack?.themes?.get(action.rvPosition)

                            if (theme != null) {
                                val type1a = theme.type1a?.data?.get(theme.type1a.position)
                                val type1b = theme.type1b?.data?.get(action.position)
                                val type1c = theme.type1c?.data?.get(theme.type1c.position)
                                val type2 = theme.type2?.data?.get(theme.type2.position)
                                val type3 = state.themePack.type3?.data?.get(state.themePack.type3.position)

                                listOf(
                                        DetailedAction.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(theme, action.rvPosition, action.position),
                                        DetailedAction.GetInfoAction(
                                                appId = appId,
                                                targetAppId = theme.appId,
                                                type1a = type1a,
                                                type1b = type1b,
                                                type1c = type1c,
                                                type2 = type2,
                                                type3 = type3
                                        )
                                )

                            } else {
                                emptyList()
                            }


                        }
                        is DetailedSimpleUIAction.ChangeSpinnerSelection.ChangeType1cSpinnerSelection -> {

                            val theme = state.themePack?.themes?.get(action.rvPosition)

                            if (theme != null) {
                                val type1a = theme.type1a?.data?.get(theme.type1a.position)
                                val type1b = theme.type1b?.data?.get(theme.type1b.position)
                                val type1c = theme.type1c?.data?.get(action.position)
                                val type2 = theme.type2?.data?.get(theme.type2.position)
                                val type3 = state.themePack.type3?.data?.get(state.themePack.type3.position)

                                listOf(
                                        DetailedAction.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(theme, action.rvPosition, action.position),
                                        DetailedAction.GetInfoAction(
                                                appId = appId,
                                                targetAppId = theme.appId,
                                                type1a = type1a,
                                                type1b = type1b,
                                                type1c = type1c,
                                                type2 = type2,
                                                type3 = type3
                                        )
                                )

                            } else {
                                emptyList()
                            }


                        }
                        is DetailedSimpleUIAction.ChangeSpinnerSelection.ChangeType2SpinnerSelection -> {

                            val theme = state.themePack?.themes?.get(action.rvPosition)

                            if (theme != null) {
                                val type1a = theme.type1a?.data?.get(theme.type1a.position)
                                val type1b = theme.type1b?.data?.get(theme.type1b.position)
                                val type1c = theme.type1c?.data?.get(theme.type1c.position)
                                val type2 = theme.type2?.data?.get(action.position)
                                val type3 = state.themePack.type3?.data?.get(state.themePack.type3.position)

                                listOf(
                                        DetailedAction.ChangeSpinnerSelection.ChangeType2SpinnerSelection(theme, action.rvPosition, action.position),
                                        DetailedAction.GetInfoAction(
                                                appId = appId,
                                                targetAppId = theme.appId,
                                                type1a = type1a,
                                                type1b = type1b,
                                                type1c = type1c,
                                                type2 = type2,
                                                type3 = type3
                                        )
                                )

                            } else {
                                emptyList()
                            }


                        }
                    }


                    actions.toObservable()

//                    Observable.fromIterable(actions)

                }

    }

    val type3Change = ObservableTransformer<Pair<DetailedSimpleUIAction.ChangeType3SpinnerSelection, DetailedViewState>, DetailedAction> { actions ->

        actions.flatMap {

            val (action, state) = it

            val detailedAction = DetailedAction.ChangeType3SpinnerSelection(action.position)

            val getInfoAction = state.themePack?.themes?.map { theme ->

                val type1a = theme.type1a?.data?.get(theme.type1a.position)
                val type1b = theme.type1b?.data?.get(theme.type1b.position)
                val type1c = theme.type1c?.data?.get(theme.type1c.position)
                val type2 = theme.type2?.data?.get(theme.type2.position)
                val type3 = state.themePack.type3?.data?.get(action.position)

                DetailedAction.GetInfoAction(
                        appId = appId,
                        targetAppId = theme.appId,
                        type1a = type1a,
                        type1b = type1b,
                        type1c = type1c,
                        type2 = type2,
                        type3 = type3
                )

            } ?: listOf()

            listOf(detailedAction, *getInfoAction.toTypedArray()).toObservable()

        }

    }

    internal val actionProcessor = ObservableTransformer<DetailedSimpleUIAction, DetailedAction> { actions ->

        actions.publish { shared ->

            Observable.merge(
                    shared.ofType(DetailedSimpleUIAction.ChangeSpinnerSelection::class.java).withLatestFromToPair(viewState).compose(changeSpinnerSelection),
                    shared.ofType(DetailedSimpleUIAction.ChangeType3SpinnerSelection::class.java).withLatestFromToPair(viewState).compose(type3Change)
            )

        }

    }

    private fun <T, R> Observable<T>.withLatestFromToPair(viewState: Observable<R>): Observable<Pair<T,R>> =
            withLatestFrom(viewState, { a, b -> Pair(a,b)})

}

