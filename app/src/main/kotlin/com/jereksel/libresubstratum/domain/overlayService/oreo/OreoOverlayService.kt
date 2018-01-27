/*
 * Copyright (C) 2018 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.domain.overlayService.oreo

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import eu.chainfire.libsuperuser.Shell
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class OreoOverlayService(
        val context: Context
) : OverlayService {

    val threadFactory = ThreadFactoryBuilder().setNameFormat("oreo-overlay-service-thread-%d").build()!!
    val installationThreadFactory = ThreadFactoryBuilder().setNameFormat("oreo-overlay-service-thread-installation-%d").build()!!

    val executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5, threadFactory))!!
    val installionExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor(installationThreadFactory))

    override fun enableOverlay(id: String): ListenableFuture<*> = executor.submit {
        Shell.SU.run("cmd overlay enable $id")
        update.set(true)
    }

    override fun disableOverlay(id: String): ListenableFuture<*> = executor.submit {
        Shell.SU.run("cmd overlay disable $id")
        update.set(true)
    }

    override fun enableExclusive(id: String): ListenableFuture<*> = executor.submit {
        val state = getCurrentState()
        val application = state.entries().find { it.value.overlayId == id }?.key ?: return@submit
        state[application]
                .filter { it.enabled }
                .forEach {
                    Shell.SU.run("cmd overlay disable ${it.overlayId}")
                }

        Shell.SU.run("cmd overlay enable $id")
        update.set(true)
    }

    override fun getOverlayInfo(id: String) = executor.sub {
        val state = getCurrentState()
        state.values().find { it.overlayId == id }
    }

    override fun getAllOverlaysForApk(appId: String) = executor.sub {
        getCurrentState()[appId].toList()
    }

    override fun restartSystemUI(): ListenableFuture<*> = executor.submit {
        Shell.SU.run("pkill -f com.android.systemui")
    }

    override fun installApk(apk: File): ListenableFuture<*> = installionExecutor.submit {
        Shell.SU.run("pm install -r ${apk.absolutePath}")
        update.set(true)
    }

    override fun uninstallApk(appId: String): ListenableFuture<*> = installionExecutor.submit {
        Shell.SU.run("pm uninstall $appId")
        update.set(true)
    }

    var suAccepted = false

    override fun additionalSteps(): ListenableFuture<String?> = executor.sub {

        if (suAccepted) {
            return@sub null
        }

        val suAvailable = Shell.SU.available()

        if (suAvailable) {
            suAccepted = true
            null
        } else {
            "Please enable root permission for Bucket"
        }

    }

    override fun getOverlaysPrioritiesForTarget(targetAppId: String): ListenableFuture<List<OverlayInfo>> = executor.sub {
        getCurrentState()[targetAppId].toList().reversed()
    }

    override fun updatePriorities(overlayIds: List<String>): ListenableFuture<*> = executor.submit {
        val appIds = overlayIds.reversed()
        for (i in 0 until appIds.size - 1) {
            Shell.SU.run("cmd overlay set-priority ${overlayIds[i+1]} ${overlayIds[i]}")
        }
        update.set(true)
    }

    override fun requiredPermissions(): List<String> {
        return allPermissions()
                .filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    var lastState: Multimap<String, OverlayInfo> = ArrayListMultimap.create()
    var lastChecksum: ByteArray = byteArrayOf()
    val lock = java.lang.Object()

    val update = AtomicBoolean(true)
    var lastUpdate = System.currentTimeMillis()
    val updateTime = TimeUnit.MINUTES.toMillis(1)

    private fun getCurrentState(): Multimap<String, OverlayInfo> = synchronized(lock) {
        if (update.getAndSet(false) || lastUpdate + updateTime < System.currentTimeMillis()) {
            lastUpdate = System.currentTimeMillis()
            val output = Shell.SU.run("cmd overlay list").joinToString(separator = "\n")
            val checksum = DigestUtils(MD5).digest(output)
            if (!Arrays.equals(lastChecksum, checksum)) {
                val state = OreoOverlayReader.read(output)
                lastState = state
                lastChecksum = checksum
            }

        }

        lastState
    }

    private fun allPermissions(): List<String> = listOf(WRITE_EXTERNAL_STORAGE)

    //Kotlin for some reason by default chooses method that returns ListenableFuture<*>
    private inline fun <T> ListeningExecutorService.sub(crossinline function: () -> T): ListenableFuture<T> =
            this.submit(java.util.concurrent.Callable { function() })

}

