package com.jereksel.libresubstratum.activities.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.jereksel.libresubstratum.domain.SubsDatabaseDownloader
import com.jereksel.libresubstratum.domain.SubstratumDatabaseTheme
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET

class DatabaseViewModel(
        val downloader: SubsDatabaseDownloader
): ViewModel() {

    var compositeDisposable = CompositeDisposable()

//    @Volatile
//    private var appsInvoked = false
    private var apps: MutableLiveData<List<SubstratumDatabaseTheme>>? = null
    private var clearTheme: MutableLiveData<List<SubstratumDatabaseTheme>>? = null
    private var darkTheme: MutableLiveData<List<SubstratumDatabaseTheme>>? = null
    private var lightThemes: MutableLiveData<List<SubstratumDatabaseTheme>>? = null
    val plugin = MutableLiveData<List<String>>()
    val samsung = MutableLiveData<List<String>>()
    val wallpapers = MutableLiveData<List<String>>()

    fun getApps(): LiveData<List<SubstratumDatabaseTheme>> {

        val apps = apps

        if (apps == null) {
            val newApps = MutableLiveData<List<SubstratumDatabaseTheme>>()
            this.apps = newApps
            asyncGetApps()
            return newApps
        } else {
            return apps
        }

    }

    fun getClearTheme(): LiveData<List<SubstratumDatabaseTheme>> {

        val themes = clearTheme

        if (themes == null) {
            val newThemes = MutableLiveData<List<SubstratumDatabaseTheme>>()
            this.clearTheme = newThemes
            asyncGetClearThemes()
            return newThemes
        } else {
            return themes
        }

    }

    fun getDarkTheme(): LiveData<List<SubstratumDatabaseTheme>> {

        val themes = darkTheme

        if (themes == null) {
            val newThemes = MutableLiveData<List<SubstratumDatabaseTheme>>()
            this.darkTheme = newThemes
            asyncGetDarkThemes()
            return newThemes
        } else {
            return themes
        }

    }


    fun getLightTheme(): LiveData<List<SubstratumDatabaseTheme>> {

        val themes = lightThemes

        if (themes == null) {
            val newThemes = MutableLiveData<List<SubstratumDatabaseTheme>>()
            this.lightThemes = newThemes
            asyncGetLightThemes()
            return newThemes
        } else {
            return themes
        }

    }


    private fun asyncGetApps() {
        compositeDisposable += downloader.getApps()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { v ->
                    apps?.postValue(v)
                }
    }

    private fun asyncGetClearThemes() {
        compositeDisposable += downloader.getClearThemes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { v ->
                    clearTheme?.postValue(v)
                }
    }

    private fun asyncGetDarkThemes() {
        compositeDisposable += downloader.getDarkThemes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { v ->
                    darkTheme?.postValue(v)
                }

    }

    private fun asyncGetLightThemes() {
        compositeDisposable += downloader.getLightThemes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { v ->
                    lightThemes?.postValue(v)
                }

    }

    override fun onCleared() {
        compositeDisposable.dispose()
    }

}