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
import io.reactivex.rxkotlin.ofType
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.guava.asListenableFuture
import kotlinx.coroutines.experimental.guava.await
import kotlinx.coroutines.experimental.rx2.asSingle
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

    lateinit var appId: String

    val backflow = BehaviorSubject.create<DetailedAction>()

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
                                    appId = it.application,
                                    name = packageManager.getAppName(it.application),
                                    type1a = it.type1.find { it.suffix == "a" }?.let{ DetailedResult.ListLoaded.Type1(it.extension) },
                                    type1b = it.type1.find { it.suffix == "b" }?.let{ DetailedResult.ListLoaded.Type1(it.extension) },
                                    type1c = it.type1.find { it.suffix == "c" }?.let{ DetailedResult.ListLoaded.Type1(it.extension) },
                                    type2 = it.type2?.let { DetailedResult.ListLoaded.Type2(it.extensions) }
                            )
                        }.sortedBy {
                            it.name.toLowerCase()
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
        }

    }

    @VisibleForTesting
    val getInfoProcessor = ObservableTransformer<DetailedAction.GetInfoAction, DetailedResult> { actions ->

        actions.flatMap { selection ->

            async {

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
                    val (versionCode, versionName) = packageManager.getAppVersion(overlayId)
                    val overlayInfo = overlayService.getOverlayInfo(overlayId).await()
                    val enabledState = if (overlayInfo?.enabled == true) {
                        DetailedViewState.EnabledState.ENABLED
                    } else {
                        DetailedViewState.EnabledState.DISABLED
                    }
                    DetailedResult.InstalledStateResult(selection.targetAppId, overlayId, DetailedViewState.InstalledState.Installed(versionName, versionCode), enabledState)
                } else {
                    DetailedResult.InstalledStateResult(selection.targetAppId, overlayId, DetailedViewState.InstalledState.Removed, DetailedViewState.EnabledState.UNKNOWN)
                }

            }
                    .asSingle(CommonPool)
                    .toObservable()

                    //It's so fast, this is not required
//                    .startWith(DetailedResult.InstalledStateResult(selection.targetAppId, DetailedViewState.InstalledState.UNKNOWN))
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())


        }

    }

    @VisibleForTesting
    val changeSpinnerPositionProcessor = ObservableTransformer<DetailedAction.ChangeSpinnerSelection, DetailedResult> { actions ->

        actions.map { selection ->

            when(selection) {
                is DetailedAction.ChangeSpinnerSelection.ChangeType1aSpinnerSelection -> {
                    DetailedResult.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(selection.rvPosition, selection.position)
                }
                is DetailedAction.ChangeSpinnerSelection.ChangeType1bSpinnerSelection -> {
                    DetailedResult.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(selection.rvPosition, selection.position)
                }
                is DetailedAction.ChangeSpinnerSelection.ChangeType1cSpinnerSelection -> {
                    DetailedResult.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(selection.rvPosition, selection.position)
                }
                is DetailedAction.ChangeSpinnerSelection.ChangeType2SpinnerSelection -> {
                    DetailedResult.ChangeSpinnerSelection.ChangeType2SpinnerSelection(selection.rvPosition, selection.position)
                }
            }

//            Observable.just(result)

        }
    }

    @VisibleForTesting
    val type3SpinnerProcessor = ObservableTransformer<DetailedAction.ChangeType3SpinnerSelection, DetailedResult> {actions ->
        actions.map {
            DetailedResult.ChangeType3SpinnerSelection(it.position)
        }
    }

    @VisibleForTesting
    val checkboxToggleProcessor = ObservableTransformer<DetailedAction.ToggleCheckbox, DetailedResult> { actions ->
        actions.map {
            DetailedResult.ToggleCheckbox(it.position, it.state)
        }
    }

    @VisibleForTesting
    val longClickProcessor = ObservableTransformer<DetailedAction.LongClick, DetailedResult> { actions ->
        actions.flatMap { selection ->

            async {

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

                if (packageManager.isPackageInstalled(overlayId)) {

                }


                Unit as DetailedResult

            }.asSingle(CommonPool)
                    .toObservable()

//            Observable.empty<DetailedResult>()
////            DetailedResult.ToggleCheckbox(it.position, it.state)
        }
    }

    internal val actionProcessor =
            ObservableTransformer<DetailedAction, DetailedResult> { actions ->
                actions.publish { shared ->
                    Observable.mergeArray(
                            shared.ofType(DetailedAction.InitialAction::class.java).compose(loadListProcessor),
                            shared.ofType(DetailedAction.ChangeSpinnerSelection::class.java).compose(changeSpinnerPositionProcessor),
                            shared.ofType(DetailedAction.GetInfoAction::class.java).compose(getInfoProcessor),
                            shared.ofType(DetailedAction.ChangeType3SpinnerSelection::class.java).compose(type3SpinnerProcessor),
                            shared.ofType(DetailedAction.ToggleCheckbox::class.java).compose(checkboxToggleProcessor),
                            shared.ofType(DetailedAction.LongClick::class.java).compose(longClickProcessor)
                    )
                }
            }

}