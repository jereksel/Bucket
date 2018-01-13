package com.jereksel.libresubstratum.activities.detailed2

import com.google.common.annotations.VisibleForTesting
import com.jereksel.libresubstratum.domain.ClipboardManager
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
import com.jereksel.libresubstratum.utils.ThemeNameUtils
import com.jereksel.libresubstratumlib.ThemePack
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class DetailedActionProcessorHolder @Inject constructor(
        private val packageManager: IPackageManager,
        private val getThemeInfoUseCase: IGetThemeInfoUseCase,
        @Named("logged") private val overlayService: OverlayService,
        private val activityProxy: IActivityProxy,
        private val compileThemeUseCase: ICompileThemeUseCase,
        private val clipboardManager: ClipboardManager
) {

    @VisibleForTesting
    val loadListProcessor = ObservableTransformer<DetailedAction.InitialAction, DetailedResult> { actions ->

        actions.flatMap {

            val appId = it.appId

            Single.fromCallable { getThemeInfoUseCase.getThemeInfo(appId) }
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .map {
                        //Remove apps that are not installed
                        val installedApps = it.themes.filter { packageManager.isPackageInstalled(it.application) }
                        val themePack = ThemePack(installedApps, it.type3)

                        DetailedResult.ListLoaded(themePack.themes.map {
                            DetailedResult.ListLoaded.Theme(
                                    it.application,
                                    packageManager.getAppName(it.application),
                                    it.type1.find { it.suffix == "a" }?.let{ DetailedResult.ListLoaded.Type1(it.extension) },
                                    it.type1.find { it.suffix == "b" }?.let{ DetailedResult.ListLoaded.Type1(it.extension) },
                                    it.type1.find { it.suffix == "c" }?.let{ DetailedResult.ListLoaded.Type1(it.extension) },
                                    it.type2?.let { DetailedResult.ListLoaded.Type2(it.extensions) }
                            )
                        },
                                themePack.type3?.run {
                                    DetailedResult.ListLoaded.Type3(
                                            this.extensions
                                    )
                                }
                              )

                    }
                    .toObservable()
                    .flatMap {

                        val type3 = it.type3?.data?.get(0)

                        val actions = it.themes.map {

                            val type1a = it.type1a?.data?.get(0)
                            val type1b = it.type1b?.data?.get(0)
                            val type1c = it.type1c?.data?.get(0)
                            val type2 = it.type2?.data?.get(0)

                            DetailedAction.GetInfoAction(
                                    appId = appId,
                                    targetAppId = it.appId,
                                    type1a = type1a,
                                    type1b = type1b,
                                    type1c = type1c,
                                    type2 = type2,
                                    type3 = type3
                            )

                        }

                        Observable.merge(
                            Observable.just(it),
                            getInfoProcessor.apply(Observable.fromIterable(actions))
                        )

                    }
                    .observeOn(AndroidSchedulers.mainThread())
        }

    }

    val getInfoProcessor = ObservableTransformer<DetailedAction.GetInfoAction, DetailedResult> { actions ->

        actions.flatMap { selection ->

            Observable.fromCallable {

                val themeName = packageManager.getAppName(selection.appId)

                val overlayId = ThemeNameUtils.getTargetOverlayName(
                        appId = selection.targetAppId,
                        themeName = themeName,
                        type1a = selection.type1a,
                        type1b = selection.type1b,
                        type1c = selection.type1c,
                        type2 = selection.type2,
                        type3 = selection.type3
                )

                val isInstalled = packageManager.isPackageInstalled(overlayId)

                if (isInstalled) {
                    DetailedResult.InstalledStateResult(selection.targetAppId, DetailedViewState.InstalledState.INSTALLED)
                } else {
                    DetailedResult.InstalledStateResult(selection.targetAppId, DetailedViewState.InstalledState.REMOVED)
                }

            }
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())


        }

    }

    @VisibleForTesting
    val changeSpinnerPositionProcessor = ObservableTransformer<DetailedAction.ChangeSpinnerSelection, DetailedResult> { actions ->

        actions.flatMap { selection ->

            val result: DetailedResult
            val action: DetailedAction.GetInfoAction

//            Observable.just(when(selection) {
            when(selection) {
                is DetailedAction.ChangeSpinnerSelection.ChangeType1aSpinnerSelection -> {
                    result = DetailedResult.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(selection.rvPosition, selection.position)

                }
                is DetailedAction.ChangeSpinnerSelection.ChangeType1bSpinnerSelection -> {
                    result = DetailedResult.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(selection.rvPosition, selection.position)
                }
                is DetailedAction.ChangeSpinnerSelection.ChangeType1cSpinnerSelection -> {
                    result = DetailedResult.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(selection.rvPosition, selection.position)
                }
                is DetailedAction.ChangeSpinnerSelection.ChangeType2SpinnerSelection -> {
                    result = DetailedResult.ChangeSpinnerSelection.ChangeType2SpinnerSelection(selection.rvPosition, selection.position)
                }
            }

//            })

                Observable.just(result)

        }
    }

    internal val actionProcessor =
            ObservableTransformer<DetailedAction, DetailedResult> { actions ->
                actions.publish { shared ->
                    Observable.merge(
                            shared.ofType(DetailedAction.InitialAction::class.java).compose(loadListProcessor),
                            shared.ofType(DetailedAction.ChangeSpinnerSelection::class.java).compose(changeSpinnerPositionProcessor)
                    )
                }
            }

}