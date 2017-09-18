package com.jereksel.libresubstratumlib

import java.io.File
import java.util.zip.ZipFile

class ThemeReaderExtractless {

    fun readThemePack(file: File): ThemePack {

        if (file.isDirectory) {
            throw IllegalArgumentException("File is a directory")
        }

        return ZipFile(file).use { zipFile ->

            val files = zipFile.entries().iterator().asSequence()
                    .map { it.name }
                    .filter { it.startsWith("assets/overlays") }
                    .map { it.removePrefix("assets/overlays/") }
                    .map { it.split("/") }
                    .map { it.dropLastWhile { it.isEmpty() } }
                    .filter { it.size == 2 }
//                    .filterNot { it.last().startsWith(".") }
//                    .filterNot { it.contains("") }
                    .distinct()
//                    .toList()
//                    .sorte

//            println(filesInZip)

//            val a = filesInZip
                    .map { it[0] to it[1] }
                    .groupBy { it.first }
                    .map {
                        it.key to it.value.map { it.second }
                    }
                    .toMap()

//            println(files)
//

            val first = files.entries.first()

            val type3List = first
                    .value
                    .mapNotNull {
                        if (it == "type3") {
                            val entry = zipFile.getEntry("assets/overlays/${first.key}/$it")
                            val name = zipFile.getInputStream(entry).bufferedReader().readText()
                            Type3Extension(name, true)
                        } else {
                            val name = it.removePrefix("type3_")
                            val entry = zipFile.getEntry("assets/overlays/${first.key}/$name")
                            if (entry == null) {
                                //Type3 extensions are directories
                                Type3Extension(it.removePrefix("type3_"), false)
                            } else {
                                null
                            }
                        }
                    }
//                    .sortedBy { it.default.toString() + "_" + it.name }

//            val type3Default = type3.find { it.default }
//
//            val

            val type3 = if (type3List.isNotEmpty()) {

                val defaultType3 = type3List.first { it.default }
                val otherType3 = (type3List - defaultType3).sortedBy { it.name }

                Type3Data(listOf(defaultType3) + otherType3)

            } else {
                null
            }

            val themes = files
                    .entries
                    .map { entry ->
                        val type2 = entry.value
                                .filter { it.startsWith("type2") }
                                .map {
                                    if (it == "type2") {
                                        val entry = zipFile.getEntry("assets/overlays/${entry.key}/$it")
                                        val name = zipFile.getInputStream(entry).bufferedReader().readText()
                                        Type2Extension(name, true)
                                    } else {
                                        Type2Extension(it.removePrefix("type2_"), false)
                                    }
                                }


                        val type2Final = if(type2.isNotEmpty()) {

                            val type2Default = type2.first { it.default }
                            val type2Other = (type2 - type2Default).sortedBy { it.name }

                            Type2Data(listOf(type2Default) + type2Other)

                        } else {
                            null
                        }

                        val type1s = entry.value
                                .filter { it.startsWith("type1") }
                                .groupBy { it[5] }
                                .entries
                                .map { it.key.toString() to it.value }
                                .sortedBy { it.first }
                                .map {
                                    val id = it.first
                                    val files = it.second

                                    val type1extensions = files.map {
                                        if (it == "type1$id") {
                                            val entry = zipFile.getEntry("assets/overlays/${entry.key}/$it")
                                            val name = zipFile.getInputStream(entry).bufferedReader().readText()
                                            Type1Extension(name, true)
                                        } else {
                                            Type1Extension(it.removePrefix("type1${id}_").removeSuffix(".xml"), false)
                                        }
                                    }

                                    Type1Data(type1extensions, id)

                                }

//                        println(type1s)


                        Theme(entry.key, type1s, type2Final)
                    }

//            val type3Final = if (type3Default == null) {
//                val otherType3 = (type3 - type3Default).sortedBy { it.name }
//
//                listOf(type3Default, *otherType3.toTypedArray())
//            } else {
//                null
//            }
//
//            println(type3Final)
//
            ThemePack(themes, type3)

//            val fileMap = filesInZip.asso


//            ZipUtil.iterate(file, { zipEntry ->
//
//
//            })
        }
    }
//
//    private fun getType3Data(file: File) {
//
//        ZipFile(file).use { zipFile ->
//            ZipUtil.iterate(file, { zipEntry ->
//
//                zipEntry.
//
//            })
//        }
//
//    }

}