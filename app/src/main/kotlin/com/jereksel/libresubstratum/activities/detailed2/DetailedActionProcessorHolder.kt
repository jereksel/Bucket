package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratum.domain.IPackageManager
import com.github.kittinunf.result.Result
import com.google.common.annotations.VisibleForTesting
import com.google.common.collect.ArrayListMultimap
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.adapters.ThemePackAdapterView
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.ThemeNameUtils
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Extension
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.guava.await
import java.io.File
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
//                        val installedApps = it.themes.filter { packageManager.isPackageInstalled(it.application) }
//                        ThemePack(installedApps, it.type3)
                        DetailedResult.ListLoaded(it.type3?.extensions ?: listOf())
                    }
//                    .map {
//                        ThemePack(it.themes.sortedBy { packageManager.getAppName(it.application) }, it.type3)
//                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable()
        }

    }

    internal val actionProcessor =
            ObservableTransformer<DetailedAction, DetailedResult> { actions ->
                actions.publish { shared ->
                    shared.ofType(DetailedAction.InitialAction::class.java).compose(loadListProcessor)
                }
            }

}