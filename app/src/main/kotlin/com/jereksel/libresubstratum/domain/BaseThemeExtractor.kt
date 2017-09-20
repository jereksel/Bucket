package com.jereksel.libresubstratum.domain

import android.os.AsyncTask
import com.jereksel.libresubstratum.extensions.getLogger
import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import com.jereksel.libresubstratum.utils.ZipUtils.extractZip

class BaseThemeExtractor : ThemeExtractor {

    val log = getLogger()

    override fun extract(file: File, dest: File): Future<File> {

        val task = FutureTask {
            file.extractZip(dest, {
                log.debug("Extract progress: {}", it)
            })
            dest
        }

        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            task.run()
        }

        return task
    }
}
