package com.jereksel.libresubstratum.activities.database

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET


class DatabaseViewModel: ViewModel() {

//    @Volatile
//    private var appsInvoked = false
    val apps = MutableLiveData<List<theme>>()
    val clearTheme = MutableLiveData<List<String>>()
    val darkTheme = MutableLiveData<List<String>>()
    val lightThemes = MutableLiveData<List<String>>()
    val plugin = MutableLiveData<List<String>>()
    val samsung = MutableLiveData<List<String>>()
    val wallpapers = MutableLiveData<List<String>>()

    init {
        computeApps()
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

        val retrofit = Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/")
//                .addConverterFactory(JacksonConverterFactory.create(xmlMapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)

        api.getApps()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    apps.postValue(it.themes)
                }


    }

    interface Api {

        @GET("substratum/database/master/substratum_tab_apps.xml")
        fun getApps(): Observable<substratum>

    }

    data class substratum(
//        @JsonProperty
//        @JacksonXmlProperty(localName = "event")
        val themes: List<theme>
    )

    data class theme(
            val author: String
    )

//    fun getApps() {
//        apps.observe()
//    }

}