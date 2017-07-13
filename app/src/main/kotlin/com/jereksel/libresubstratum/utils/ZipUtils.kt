package com.jereksel.libresubstratum.utils

import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ZipUtils {

    fun File.extractZip(dest: File) {
        if (dest.exists()) {
            dest.deleteRecursively()
        }
        dest.mkdirs()

        val fis = FileInputStream(this)
        val zis = ZipInputStream(BufferedInputStream(fis))

        zis.generateSequence().forEach { ze ->

            val fileName = ze.name

//            Log.d("extractZip", fileName)

            if (!fileName.startsWith("assets")) {
                return@forEach
            }

            if (ze.isDirectory) {
                File(dest, fileName).mkdirs()
                return@forEach
            }

            val destFile = File(dest, fileName)

            destFile.parentFile.mkdirs()
            destFile.createNewFile()
            val fout = FileOutputStream(destFile)

            zis.copyTo(fout, 1024 * 8)
            fout.close()

        }
        zis.close()
        fis.close()
    }

    //We can't just use second function alone - we will close entry when there is no entry opened yet
    fun ZipInputStream.generateSequence() : Sequence<ZipEntry> {
        return generateSequence({ this.nextEntry }, { this.closeEntry(); this.nextEntry })
    }

}