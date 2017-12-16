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
import android.os.IBinder
import android.support.v4.content.ContextCompat
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.omslib.OMSLib
import io.reactivex.subjects.BehaviorSubject
import projekt.substratum.IInterfacerInterface
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.guava.asListenableFuture
import kotlinx.coroutines.experimental.guava.await
import kotlinx.coroutines.experimental.rx2.await
import java.io.File
import java.util.concurrent.Executors

abstract class InterfacerOverlayService(val context: Context): OverlayService {

    private val log = getLogger()

    private val omsRx: BehaviorSubject<IOverlayManager> = BehaviorSubject.create()
    private var interfacerRx: BehaviorSubject<IInterfacerInterface> = BehaviorSubject.create()

    private val oms: IOverlayManager = OMSLib.getOMS()

    val threadFactory = ThreadFactoryBuilder().setNameFormat("interfacer-thread-%d").build()

    val dispatcher = Executors.newFixedThreadPool(5, threadFactory).asCoroutineDispatcher()

    val INTERFACER_PACKAGE = "projekt.interfacer"
    val INTERFACER_SERVICE = INTERFACER_PACKAGE + ".services.JobService"
    val INTERFACER_BINDED = INTERFACER_PACKAGE + ".INITIALIZE"
    val STATUS_CHANGED = INTERFACER_PACKAGE + ".STATUS_CHANGED"

    private val interfacerServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            log.debug("Interfacer connected")
            interfacerRx.onNext(IInterfacerInterface.Stub.asInterface(service))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            log.debug("Interfacer disconnected")
        }

    }

    private fun initInterfacer() {
        val intent = Intent(INTERFACER_BINDED)
        intent.`package` = INTERFACER_PACKAGE
        // binding to remote service
        context.bindService(intent, interfacerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    init {
        initInterfacer()
    }

    override fun enableOverlay(id: String) = async(dispatcher) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.enableOverlay(listOf(id), false)
    }.asListenableFuture()

    override fun disableOverlay(id: String) = async(dispatcher) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.disableOverlay(listOf(id), false)
    }.asListenableFuture()

    //When we use CommonPool on everything we have possibility of deadlock
    //enableExclusive is only method that uses other async methods
    override fun enableExclusive(id: String) = async(dispatcher) {

        val overlayInfo = getOverlayInfo(id).await() ?: return@async

        getAllOverlaysForApk(overlayInfo.targetId).await()
                .filter { it.enabled }
                .forEach { disableOverlay(it.overlayId).await() }

        enableOverlay(id).get()

    }.asListenableFuture()

    override fun getOverlayInfo(id: String) = async(dispatcher) {
        val info = oms.getOverlayInfo(id, 0)
        if (info != null) {
            OverlayInfo(id, info.targetPackageName, info.isEnabled)
        } else {
            null
        }
    }.asListenableFuture()

    override fun getAllOverlaysForApk(appId: String) = async(dispatcher) {
        @Suppress("UNCHECKED_CAST")
        val map = oms.getOverlayInfosForTarget(appId, 0) as List<android.content.om.OverlayInfo>
        map.map { OverlayInfo(it.packageName, it.targetPackageName, it.isEnabled) }
    }.asListenableFuture()

    override fun getOverlaysPrioritiesForTarget(targetAppId: String) = async(dispatcher) {
        @Suppress("UNCHECKED_CAST")
        val list = oms.getOverlayInfosForTarget(targetAppId, 0) as List<android.content.om.OverlayInfo>
        list.map { OverlayInfo(it.packageName, it.targetPackageName, it.isEnabled) }.reversed()
    }.asListenableFuture()

    override fun updatePriorities(overlayIds: List<String>) = async(dispatcher) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.changePriority(overlayIds.reversed(), false)
    }.asListenableFuture()

    override fun restartSystemUI() = async(dispatcher) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.restartSystemUI()
    }.asListenableFuture()

    override fun installApk(apk: File) = async(dispatcher) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.installPackage(listOf(apk.absolutePath))
    }.asListenableFuture()

    override fun uninstallApk(appId: String) = async(dispatcher) {
        val interfacer = interfacerRx.firstOrError().await()
        interfacer.uninstallPackage(listOf(appId), false)
    }.asListenableFuture()

    override fun requiredPermissions(): List<String> {
        return allPermissions()
                .filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    abstract fun allPermissions(): List<String>
}