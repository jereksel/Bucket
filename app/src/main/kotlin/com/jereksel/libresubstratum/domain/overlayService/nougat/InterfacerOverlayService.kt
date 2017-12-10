/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
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

package com.jereksel.libresubstratum.domain.overlayService.nougat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.om.IOverlayManager
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.IBinder
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.common.cache.*
import com.google.common.util.concurrent.ListenableFutureTask
import com.google.common.util.concurrent.Service
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.cache.RemovalListener
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.omslib.OMSLib
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import projekt.substratum.IInterfacerInterface
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.guava.asListenableFuture
import kotlinx.coroutines.experimental.guava.await
import kotlinx.coroutines.experimental.rx2.await
import kotlinx.coroutines.experimental.rx2.awaitFirst
import kotlinx.coroutines.experimental.rx2.awaitSingle
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

abstract class InterfacerOverlayService(val context: Context): OverlayService {

    private val log = getLogger()

    //http://www.donnfelker.com/rxjava-with-aidl-services/
    private val omsRx: BehaviorSubject<IOverlayManager> = BehaviorSubject.create()
    private val interfacerRx: BehaviorSubject<IInterfacerInterface> = BehaviorSubject.create()

    private val oms: IOverlayManager = OMSLib.getOMS()

    val executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10))

    val INTERFACER_PACKAGE = "projekt.interfacer"
    val INTERFACER_SERVICE = INTERFACER_PACKAGE + ".services.JobService"
    val INTERFACER_BINDED = INTERFACER_PACKAGE + ".INITIALIZE"
    val STATUS_CHANGED = INTERFACER_PACKAGE + ".STATUS_CHANGED"

    private val interfacerServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            interfacerRx.onNext(IInterfacerInterface.Stub.asInterface(service))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            interfacerRx.onComplete()
        }

    }

    init {

        val intent = Intent(INTERFACER_BINDED)
        intent.`package` = INTERFACER_PACKAGE
        // binding to remote service
        context.bindService(intent, interfacerServiceConnection, Context.BIND_AUTO_CREATE);

    }

    override fun enableOverlay(id: String) = async(CommonPool) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.enableOverlay(listOf(id), false)
    }.asListenableFuture()

    override fun disableOverlay(id: String) = async(CommonPool) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.disableOverlay(listOf(id), false)
    }.asListenableFuture()

    override fun enableExclusive(id: String) = async(CommonPool) {

        val overlayInfo = getOverlayInfo(id) ?: return@async

        getAllOverlaysForApk(overlayInfo.targetId)
                .filter { it.enabled }
                .forEach { disableOverlay(it.overlayId).await() }

        enableOverlay(id).await()


    }.asListenableFuture()

    override fun getOverlayInfo(id: String): OverlayInfo? {
        val info = oms.getOverlayInfo(id, 0)
        return if (info != null) {
            OverlayInfo(id, info.targetPackageName, info.isEnabled)
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getAllOverlaysForApk(appId: String): List<OverlayInfo> {
        val map = oms.getOverlayInfosForTarget(appId, 0) as List<android.content.om.OverlayInfo>
        return map.map { OverlayInfo(it.packageName, it.targetPackageName, it.isEnabled) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getOverlaysPrioritiesForTarget(targetAppId: String): ListenableFuture<List<OverlayInfo>> {

        return executor.submit(Callable<List<OverlayInfo>> {
            val list = oms.getOverlayInfosForTarget(targetAppId, 0) as List<android.content.om.OverlayInfo>
            list.map { OverlayInfo(it.packageName, it.targetPackageName, it.isEnabled) }.reversed()
        })

    }

    override fun updatePriorities(overlayIds: List<String>) = async(CommonPool) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.changePriority(overlayIds.reversed(), false)
    }.asListenableFuture()

    override fun restartSystemUI() = async(CommonPool) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.restartSystemUI()
    }.asListenableFuture()

    override fun installApk(apk: File) = async(CommonPool) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.installPackage(listOf(apk.absolutePath))
    }.asListenableFuture()

    override fun uninstallApk(appId: String) = async(CommonPool) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.uninstallPackage(listOf(appId), false)
    }.asListenableFuture()

    override fun requiredPermissions(): List<String> {
        return allPermissions()
                .filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    abstract fun allPermissions(): List<String>
}