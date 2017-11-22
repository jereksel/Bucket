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

    fun computeApps() {

//        Schedulers.io().scheduleDirect {
//            Thread.sleep(10000)
//            val list = (1..10).map { "app$it" }
//            apps.postValue(list)
//        }


//        val module = JacksonXmlModule()
//        module.setDefaultUseWrapper(false)
//        val xmlMapper = XmlMapper(module)
//
//        xmlMapper.registerModule(KotlinModule())


/*                .subscribe {
                    apps.postValue(it.themes)
                }*/


    }

    interface Api {

        @GET("substratum/database/master/substratum_tab_apps.xml")
        fun getApps(): Single<String>

    }
}