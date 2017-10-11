package com.jereksel.themereaderassetmanager

import android.content.res.AssetManager
import android.content.res.AssetManager.ACCESS_BUFFER
import com.jereksel.libresubstratumlib.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.InputStream

object Reader {

    fun read(am: AssetManager, transformer: (InputStream) -> (InputStream) = { it }): ThemePack {

        val apps = am.list("overlays")

        val list = Observable.fromArray(*apps)
                .flatMap {
                    Observable.fromCallable { readTheme(am, it, transformer) }
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.io())
                }
                .toList()
                .blockingGet()

        val type3Data: Type3Data?

        if (apps.isEmpty()) {
            type3Data = null
        } else {
            val app = apps[0]

            val type3extensions = am.list("overlays/$app")
                    .filter { it.startsWith("type3") }
                    .map {
                        if (it == "type3" || it == "type3.enc") {
                            Type3Extension(am.read("overlays/$app/$it", transformer), true)
                        } else {
                            Type3Extension(it.removePrefix("type3_"), false)
                        }
                    }
                    .sortedWith(compareBy({ !it.default }, { it.name }))

            if (type3extensions.isEmpty()) {
                type3Data = null
            } else {
                type3Data = Type3Data(type3extensions)
            }

        }

        return ThemePack(list.sortedBy { it.application }, type3Data)
    }

    fun readTheme(am: AssetManager, id: String, transformer: (InputStream) -> (InputStream)): Theme {

        val dir = "overlays/$id"

        val files = am.list(dir)

        val type1s = files
                .filter { it.startsWith("type1") }
                .groupBy { it[5] }
                .map {
                    val type = it.key
                    val extensions = it.value
                            .map {
                                if (it.length == 6 || (it.length == 10 && it.endsWith(".enc"))) {
                                    Type1Extension(am.read("$dir/$it", transformer), true)
                                } else {
                                    val name = it.substring(7).removeSuffix(".xml").removeSuffix(".xml.enc")
                                    Type1Extension(name, false)
                                }
                            }
                            //We want true to be first
                            .sortedWith(compareBy({ !it.default }, { it.name }))

                    Type1Data(extensions, type.toString())
                }
                .sortedBy { it.suffix }

        val type2 = files
                .filter { it.startsWith("type2") }
                .map {
                    if (it == "type2" || it == "type2.enc") {
                        Type2Extension(am.read("$dir/$it", transformer), true)
                    } else {
                        Type2Extension(it.removePrefix("type2_"), false)
                    }
                }
//                .ifNotEmptyAdd(Type2Extension("Type2 extension", true))

        val type22: List<Type2Extension>

        if (type2.find { it.default } == null && !type2.isEmpty()) {
            type22 = type2 + Type2Extension("Type2 extension", true)
        } else {
            type22 = type2
        }

        val finalType2 = type22
                .sortedWith(compareBy({ !it.default }, { it.name }))

        val type2Data: Type2Data?

        type2Data = if (finalType2.isEmpty()) {
            null
        } else {
            Type2Data(finalType2)
        }

        return Theme(id, type1s, type2Data)
    }

    private fun AssetManager.read(file: String, transformer: (InputStream) -> (InputStream)): String =
            transformer(this.open(file, ACCESS_BUFFER)).bufferedReader().use { it.readText() }
}
