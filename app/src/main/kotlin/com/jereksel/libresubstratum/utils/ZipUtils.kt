package com.jereksel.libresubstratum.utils

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

object ZipUtils {

    fun File.extractZip(dest: File, progressCallback: (Int) -> Unit = {}) {
        if (dest.exists()) {
            dest.deleteRecursively()
        }
        dest.mkdirs()

        val length = ZipFile(this).size()

        FileInputStream(this).use { fis ->
            ZipInputStream(BufferedInputStream(fis)).use { zis ->
                zis.generateSequence().forEachIndexed { index, ze ->

                    val fileName = ze.name

                    progressCallback((index * 100) / length)

//                    Log.d("extractZip", fileName)

                    if (!fileName.startsWith("assets")) {
                        return@forEachIndexed
                    }

                    if (ze.isDirectory) {
                        File(dest, fileName).mkdirs()
                        return@forEachIndexed
                    }

                    val destFile = File(dest, fileName)

                    destFile.parentFile.mkdirs()
                    destFile.createNewFile()
                    val fout = FileOutputStream(destFile)

                    zis.copyTo(fout)
                    fout.close()
                }

            }
        }
    }

    //We can't just use second function alone - we will close entry when there is no entry opened yet
    fun ZipInputStream.generateSequence() : Sequence<ZipEntry> {
        return generateSequence({ this.nextEntry }, { this.closeEntry(); this.nextEntry })
    }

}