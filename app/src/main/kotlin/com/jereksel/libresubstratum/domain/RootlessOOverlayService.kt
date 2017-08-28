package com.jereksel.libresubstratum.domain

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.jereksel.libresubstratum.bridgecommon.ILibreSubstratumService
import com.jereksel.omslib.OMSLib
import projekt.substratum.IInterfacerInterface
import java.io.File

class RootlessOOverlayService(context: Context): OverlayService {

//    val oms = OMSLib.getOMS()!!

    private lateinit var service: ILibreSubstratumService

    val ROOTLESSO_PACKAGE = "com.jereksel.libresubstratum.rootlesso"
    val ROOTLESSO_SERVICE = "$ROOTLESSO_PACKAGE.RootlessOMSService"

//    val INTERFACER_PACKAGE = "projekt.interfacer"
//    val INTERFACER_SERVICE = INTERFACER_PACKAGE + ".services.JobService"
//    val INTERFACER_BINDED = INTERFACER_PACKAGE + ".INITIALIZE"
//    val STATUS_CHANGED = INTERFACER_PACKAGE + ".STATUS_CHANGED"

    init {
        val serviceConnection = object: ServiceConnection {

            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                Log.d(RootlessOOverlayService::class.java.canonicalName, "Service connected")
                service = ILibreSubstratumService.Stub.asInterface(binder)
            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        }

//        val intent = Intent(ROOTLESSO_SERVICE)
        val intent = Intent("projekt.interfacer.INITIALIZE")
        intent.`package` = ROOTLESSO_PACKAGE
        // binding to remote service
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    override fun enableOverlay(id: String) {
        service.enableOverlay(id)
    }

    override fun disableOverlays(ids: List<String>) {
        ids.forEach { service.disableOverlay(it) }
    }

    override fun getOverlayInfo(id: String): OverlayInfo {
        val info = service.getOverlayInfo(id)
        return OverlayInfo(id, info.isEnabled)
    }

    override fun getAllOverlaysForApk(appId: String): List<OverlayInfo> {
//        val map = oms.getOverlayInfosForTarget(appId, 0) as List<android.content.om.OverlayInfo>
//        return map.map { OverlayInfo(it.packageName, it.isEnabled) }
//        TODO()
//        return listOf()
        return service.getOverlaysForPackage(appId).map { OverlayInfo(it.overlayId, it.isEnabled) }
    }

    override fun restartSystemUI() {
        service.restartSystemUI()
    }

    override fun installApk(apk: File) {
        service.installPackage(listOf(apk.absolutePath))
    }
}