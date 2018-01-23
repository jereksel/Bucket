package com.jereksel.libresubstratum.activities.detailed2

import arrow.data.Try
import com.google.common.annotations.VisibleForTesting
import com.google.common.util.concurrent.ThreadFactoryBuilder
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
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.guava.await
import kotlinx.coroutines.experimental.rx2.asSingle
import kotlinx.coroutines.experimental.rx2.awaitSingle
import kotlinx.coroutines.experimental.rx2.rxObservable
import java.util.concurrent.Executors
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

    val threadFactory = ThreadFactoryBuilder().setNameFormat("detailed-action-processor-holder-thread-%d").build()!!
    val actionCoroutineDispatcher = Executors.newFixedThreadPool(10, threadFactory).asCoroutineDispatcher()

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

                        DetailedResult.ListLoaded(
                                themeAppId = appId,
                                themes = themePack.themes.map {
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
                                type3 = themePack.type3?.run {
                                    DetailedResult.ListLoaded.Type3(
                                            this.extensions
                                    )
                                }
                        )

                    }
                    .toObservable()
                    .flatMap {

                        val getInfoBasicActions = it.themes.indices.map { DetailedResult.InstalledStateResult.PositionResult(it) }

                        Observable.just(it as DetailedResult)
                                .mergeWith(Observable.fromIterable(getInfoBasicActions))

                    }
        }

    }

    @VisibleForTesting
    val getInfoProcessor = ObservableTransformer<DetailedAction.GetInfoAction, DetailedResult> { actions ->

        actions.flatMap { selection ->

            async {

                val themeName = packageManager.getInstalledTheme(selection.appId).name

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
                    DetailedResult.InstalledStateResult.Result(selection.targetAppId, overlayId, DetailedViewState.InstalledState.Installed(versionName, versionCode), enabledState)
                } else {
                    DetailedResult.InstalledStateResult.Result(selection.targetAppId, overlayId, DetailedViewState.InstalledState.Removed, DetailedViewState.EnabledState.UNKNOWN)
                }

            }
                    .asSingle(actionCoroutineDispatcher)
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
    val compileProcessor = ObservableTransformer<DetailedAction.CompilationAction, DetailedResult> { actions ->
        actions.flatMap { selection ->

            rxObservable {

                val themeName = packageManager.getInstalledTheme(selection.appId).name

                val overlayId = ThemeNameUtils.getTargetOverlayName(
                        appId = selection.targetAppId,
                        themeName = themeName,
                        type1a = selection.type1a,
                        type1b = selection.type1b,
                        type1c = selection.type1c,
                        type2 = selection.type2,
                        type3 = selection.type3
                )

                val compilationMode = selection.compileMode

                //TODO: Check if package is up to date
                if (packageManager.isPackageInstalled(overlayId)) {

                    if (compilationMode == DetailedAction.CompileMode.COMPILE) {
                        return@rxObservable
                    }

//                    if (compilationMode == DetailedAction.CompileMode.COMPILE_AND_ENABLE
//                            || compilationMode == DetailedAction.CompileMode.DISABLE_COMPILE_AND_ENABLE) {

                    //Crash violently on such errors
                    val overlayInfo = overlayService.getOverlayInfo(overlayId).await()
                            ?: throw Exception("OverlayInfo is null for $overlayId")

                    if (overlayInfo.enabled) {
                        overlayService.disableOverlay(overlayId).await()
                    } else {
                        overlayService.enableExclusive(overlayId).await()
                    }

                    send(DetailedResult.InstalledStateResult.AppIdResult(selection.targetAppId))

                    return@rxObservable

                }

                val themePack = getThemeInfoUseCase.getThemeInfo(selection.appId)

                send(DetailedResult.CompilationStatusResult.StartCompilation(selection.targetAppId))

                val compilation = compileThemeUseCase.execute(
                        themePack = themePack,
                        themeId = selection.appId,
                        destAppId = selection.targetAppId,
                        type1aName = selection.type1a?.name,
                        type1bName = selection.type1b?.name,
                        type1cName = selection.type1c?.name,
                        type2Name = selection.type2?.name,
                        type3Name = selection.type3?.name
                )

                val themeApk = Try.invoke { compilation.observeOn(Schedulers.computation()).observeOn(Schedulers.computation()).awaitSingle() }

                themeApk.fold(
                        fb = { compiledApk ->
                            send(DetailedResult.CompilationStatusResult.StartInstallation(selection.targetAppId))
                            overlayService.installApk(compiledApk).await()
                            if (compilationMode == DetailedAction.CompileMode.COMPILE_AND_ENABLE
                                    || compilationMode == DetailedAction.CompileMode.DISABLE_COMPILE_AND_ENABLE) {
                                overlayService.enableExclusive(overlayId).await()
                            }
                            send(DetailedResult.CompilationStatusResult.EndCompilation(selection.targetAppId))
                            send(DetailedResult.InstalledStateResult.AppIdResult(selection.targetAppId))
                        },
                        fa = { error ->
                            send(DetailedResult.CompilationStatusResult.FailedCompilation(selection.targetAppId, error))
                            send(DetailedResult.CompilationStatusResult.CleanError(selection.targetAppId))
                        }
                )

            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())

        }
//                    .toObservable()
//                    .flatMap { getInfoProcessor.apply(Observable.just(it)) }
    }

    @VisibleForTesting
    val basicGetInfoProcessor = ObservableTransformer<DetailedAction.GetInfoBasicAction, DetailedResult> { actions ->
        actions.map {
            DetailedResult.InstalledStateResult.PositionResult(it.position)
        }
    }

    @VisibleForTesting
    val basicCompileTransformer = ObservableTransformer<DetailedAction.CompilationLocationAction, DetailedResult> { actions ->
        actions.map {
            DetailedResult.LongClickBasicResult(it.position, it.compileMode)
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
                            shared.ofType(DetailedAction.CompilationAction::class.java).compose(compileProcessor),
                            shared.ofType(DetailedAction.GetInfoBasicAction::class.java).compose(basicGetInfoProcessor),
                            shared.ofType(DetailedAction.CompilationLocationAction::class.java).compose(basicCompileTransformer),
                            shared.ofType(DetailedAction.CompileSelectedAction::class.java).compose { it.map { DetailedResult.CompileSelectedResult(it.compileMode) } }
                    )
                }
            }

}