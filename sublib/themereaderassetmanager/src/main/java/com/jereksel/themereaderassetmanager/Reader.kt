package com.jereksel.themereaderassetmanager

import android.content.res.AssetManager
import android.content.res.AssetManager.ACCESS_BUFFER
import com.jereksel.libresubstratumlib.Theme
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type1Data
import com.jereksel.libresubstratumlib.Type1Extension
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

object Reader {

    fun read(am: AssetManager): ThemePack {

        val apps = am.list("overlays")

        val list = Observable.fromArray(*apps)
                .flatMap {
                    Observable.fromCallable { readTheme(am, it) }
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.io())
                }
                .toList()
                .blockingGet()


        return ThemePack(list.sortedBy { it.application })

    }

    fun readTheme(am: AssetManager, id: String): Theme {

        val dir = "overlays/$id"

        val files = am.list(dir)

        val type1s = files
                .filter { it.startsWith("type1") }

                .groupBy { it[5].toString() }
                .map {
                    val type = it.key
                    val extensions = it.value
                            .map {
                                if (it.length == 6) {
                                    Type1Extension(am.read("$dir/$it"), true)
                                } else {
                                    val name = it.substring(7).removeSuffix(".xml")
                                    Type1Extension(name, false)
                                }
                            }
                            //We want true to be first
                            .sortedWith(compareBy({ !it.default }, { it.name }))

                    Type1Data(extensions, type)
                }
                .sortedBy { it.suffix }

        return Theme(id, type1s)
    }

    private fun AssetManager.read(file: String): String = this.open(file, ACCESS_BUFFER).bufferedReader().use { it.readText() }

}