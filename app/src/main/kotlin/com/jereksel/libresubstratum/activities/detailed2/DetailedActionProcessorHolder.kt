package com.jereksel.libresubstratum.activities.detailed2

import com.google.common.annotations.VisibleForTesting
import com.jereksel.libresubstratum.domain.ClipboardManager
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
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
                    .observeOn(AndroidSchedulers.mainThread())
        }

    }

    @VisibleForTesting
    val changeSpinnerPositionProcessor = ObservableTransformer<DetailedAction.ChangeSpinnerSelection, DetailedResult> { actions ->

        actions.flatMap { selection ->

            Observable.just(when(selection) {
                is DetailedAction.ChangeSpinnerSelection.ChangeType1aSpinnerSelection -> DetailedResult.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(selection.rvPosition, selection.position)
                is DetailedAction.ChangeSpinnerSelection.ChangeType1bSpinnerSelection -> DetailedResult.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(selection.rvPosition, selection.position)
                is DetailedAction.ChangeSpinnerSelection.ChangeType1cSpinnerSelection -> DetailedResult.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(selection.rvPosition, selection.position)
                is DetailedAction.ChangeSpinnerSelection.ChangeType2SpinnerSelection -> DetailedResult.ChangeSpinnerSelection.ChangeType2SpinnerSelection(selection.rvPosition, selection.position)
            })

//            when(it) {
//            }


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