package com.jereksel.libresubstratum.infrastructure.subsdatabase

import com.jereksel.libresubstratum.domain.SubsDatabaseDownloader
import com.jereksel.libresubstratum.domain.SubstratumDatabaseTheme
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

class GithubSubsDatabaseDownloader: SubsDatabaseDownloader {

    val api = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(Api::class.java)

    override fun getApps(): Single<List<SubstratumDatabaseTheme>> {

        return api.getApps()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map {
                    XmlConverter.convert(it)
                }

    }

    override fun getClearThemes(): Single<List<SubstratumDatabaseTheme>> {

        return api.getClearThemes()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map {
                    XmlConverter.convert(it)
                }
    }

    override fun getDarkThemes(): Single<List<SubstratumDatabaseTheme>> {

        return api.getDarkThemes()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map {
                    XmlConverter.convert(it)
                }
    }

    override fun getLightThemes(): Single<List<SubstratumDatabaseTheme>> {

        return api.getLightThemes()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map {
                    XmlConverter.convert(it)
                }
    }

    interface Api {

        @GET("substratum/database/master/substratum_tab_apps.xml")
        fun getApps(): Single<String>

        @GET("substratum/database/master/substratum_tab_clearthemes.xml")
        fun getClearThemes(): Single<String>

        @GET("substratum/database/master/substratum_tab_darkthemes.xml")
        fun getDarkThemes(): Single<String>

        @GET("substratum/database/master/substratum_tab_lightthemes.xml")
        fun getLightThemes(): Single<String>

        @GET("substratum/database/master/substratum_tab_plugins.xml")
        fun getPlugins(): Single<String>

        @GET("substratum/database/master/substratum_tab_samsung.xml")
        fun getSamsung(): Single<String>

        @GET("substratum/database/master/substratum_tab_wallpapers.xml")
        fun getWallpapers(): Single<String>

    }
}