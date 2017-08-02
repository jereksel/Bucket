package com.jereksel.libresubstratum.domain

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.jereksel.omslib.OMSLib
import projekt.substratum.IInterfacerInterface

class InterfacerOverlayService(context: Context): OverlayService {

    val oms = OMSLib.getOMS()!!

    private lateinit var service: IInterfacerInterface

    val INTERFACER_PACKAGE = "projekt.interfacer"
    val INTERFACER_SERVICE = INTERFACER_PACKAGE + ".services.JobService"
    val INTERFACER_BINDED = INTERFACER_PACKAGE + ".INITIALIZE"
    val STATUS_CHANGED = INTERFACER_PACKAGE + ".STATUS_CHANGED"

    init {
        val serviceConnection = object: ServiceConnection {

            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                Log.d(InterfacerOverlayService::class.java.canonicalName, "Service connected")
                service = IInterfacerInterface.Stub.asInterface(binder)
            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        }

        val intent = Intent(INTERFACER_BINDED)
        intent.`package` = INTERFACER_PACKAGE
        // binding to remote service
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    override fun enableOverlay(id: String) {
        service.enableOverlay(listOf(id), false)
    }

    override fun disableOverlay(id: String) {
        service.disableOverlay(listOf(id), false)
    }

    override fun getOverlayInfo(id: String): OverlayInfo {
        val info = oms.getOverlayInfo(id, 0)
        return OverlayInfo(id, info.isEnabled)
    }

    override fun restartSystemUI() {
        service.restartSystemUI()
    }
}