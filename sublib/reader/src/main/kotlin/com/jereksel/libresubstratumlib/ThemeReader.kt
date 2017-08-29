package com.jereksel.libresubstratumlib

import java.io.File

class ThemeReader {

    fun readThemePack(location: String) : ThemePack = readThemePack(File(location))

    fun readThemePack(location: File) : ThemePack {

        val overlaysLocation = File(location, "overlays")

        if (!overlaysLocation.exists()) {
            throw IllegalArgumentException("Overlays directory doesn't exists: $overlaysLocation")
        }

        val themedPackages = overlaysLocation
                .listFiles()
                .filter { it.isDirectory }
                .map { readTheme(it) }

        return ThemePack(themedPackages, readType3Data(overlaysLocation))
    }

    fun readTheme(location: File) = Theme(location.name, readType1Data(location), readType2Data(location))

    fun readType1Data(location: File) : List<Type1Data> {

        val typeMap = mutableMapOf<String, MutableList<Type1Extension>>()

        location.listFiles()
                .filter { it.name.startsWith("type1") }
                .forEach {
                    //TODO: Refactor this
                    val type = "${it.name[5]}"
                    if (it.name.length == 6) {
                        typeMap.getOrPut(type, { mutableListOf() }).add(Type1Extension(it.readText(), true))
                    } else {
                        val name = it.name.substring(7).removeSuffix(".xml")
                        typeMap.getOrPut(type, { mutableListOf() }).add(Type1Extension(name, false))
                    }
                }

        return typeMap.map { Type1Data(it.value, it.key) }.sortedBy { it.suffix }
    }

    fun readType2Data(dir: File) : Type2Data? {
        val type2File = File(dir, "type2")
        if (!type2File.exists()) {
            return null
        }

        val firstType = Type2Extension(type2File.readText().trim(), true)
        val rest = dir.listFiles()
                .filter { it.isDirectory }
                .filter { it.name.startsWith("type2") }
                .map { it.name.removePrefix("type2_") }
                .map { Type2Extension(it, false) }
                .sortedBy { it.name }
                .toTypedArray()

        return Type2Data(listOf(firstType, *rest))

    }

    fun readType3Data(location: File) : Type3Data? {
        val dir = location.listFiles()[0]

        val type3File = File(dir, "type3")
        if (!type3File.exists()) {
            return null
        }

        val firstType = Type3Extension(type3File.readText().trim(), true)
        val rest = dir.listFiles()
                .filter { it.isDirectory }
                .filter { it.name.startsWith("type3") }
                .map { it.name.removePrefix("type3_") }
                .map { Type3Extension(it, false) }
                .sortedBy { it.name }
                .toTypedArray()

        return Type3Data(listOf(firstType, *rest))

    }
}