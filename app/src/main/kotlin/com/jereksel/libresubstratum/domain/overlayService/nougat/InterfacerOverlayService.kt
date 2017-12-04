package com.jereksel.libresubstratum.domain.overlayService.nougat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.om.IOverlayManager
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.support.v4.content.ContextCompat
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.cache.RemovalListener
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.omslib.OMSLib
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

abstract class InterfacerOverlayService(val context: Context): OverlayService {

    private val log = getLogger()

    private val oms: IOverlayManager
        get() = omsCache.get(Unit)

    private val service: AidlIInterfacerInterface
        get() = interfacerCache.get(Unit).second

    @Suppress("JoinDeclarationAndAssignment")
    private val omsCache : LoadingCache<Unit, IOverlayManager>

    private val interfacerCache : LoadingCache<Unit, Pair<ServiceConnection, AidlIInterfacerInterface>>

    val INTERFACER_PACKAGE = "projekt.interfacer"
    val INTERFACER_BINDED = INTERFACER_PACKAGE + ".INITIALIZE"

    init {

        omsCache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(object: CacheLoader<Unit, IOverlayManager>() {
                    override fun load(key: Unit) = OMSLib.getOMS()!!
                })


        interfacerCache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .removalListener(RemovalListener<Unit, Pair<ServiceConnection, AidlIInterfacerInterface>> {
                    notification -> context.unbindService(notification.value.first)
                })
                .build(object: CacheLoader<Unit, Pair<ServiceConnection, AidlIInterfacerInterface>>() {
                    override fun load(key: Unit): Pair<ServiceConnection, AidlIInterfacerInterface> {

                        if (Looper.getMainLooper().thread == Thread.currentThread()) {
                            throw RuntimeException("Cannot be invoked on UI thread")
                        }

                        return Single.fromPublisher<Pair<ServiceConnection, AidlIInterfacerInterface>> {

                            val serviceConnection = object: ServiceConnection {

                                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                                    log.debug("Interfacer service connected")
                                    val service = AidlIInterfacerInterface.asInterface(binder)!!
                                    it.onNext(Pair(this, service))
                                    it.onComplete()
                                }

                                override fun onServiceDisconnected(name: ComponentName) {
                                    it.onError(RuntimeException("Service disconnected"))
                                }
                            }

                            val intent = Intent(INTERFACER_BINDED)
                            intent.`package` = INTERFACER_PACKAGE
                            // binding to remote service
                            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                        }
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(Schedulers.computation())
                                .blockingGet()
                    }
                })


    }

    override fun enableOverlay(id: String) {
        service.enableOverlays(listOf(id))
    }

    override fun disableOverlay(id: String) {
        service.disableOverlays(listOf(id))
    }

    override fun getOverlayInfo(id: String): OverlayInfo? {
        val info = oms.getOverlayInfo(id, 0)
        return if (info != null) {
            OverlayInfo(id, info.isEnabled)
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getAllOverlaysForApk(appId: String): List<OverlayInfo> {
        val map = oms.getOverlayInfosForTarget(appId, 0) as List<android.content.om.OverlayInfo>
        return map.map { OverlayInfo(it.packageName, it.isEnabled) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getOverlaysPrioritiesForTarget(targetAppId: String): List<OverlayInfo> {
        val list = oms.getOverlayInfosForTarget(targetAppId, 0) as List<android.content.om.OverlayInfo>
        return list.map { OverlayInfo(it.packageName, it.isEnabled) }.reversed()
    }

    override fun updatePriorities(overlayIds: List<String>) {
        service.changePriority(overlayIds.reversed())
    }

    override fun restartSystemUI() {
        service.restartSystemUI()
    }

    override fun installApk(apk: File) {
        service.installPackages(listOf(apk.absolutePath))
    }

    override fun uninstallApk(appId: String) {
        service.removePackages(listOf(appId))
    }

    override fun requiredPermissions(): List<String> {
        return allPermissions()
                .filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    abstract fun allPermissions(): List<String>
}