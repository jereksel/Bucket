package android.content.om

import android.content.om.OverlayInfo
import android.os.RemoteException

class InterfacerManager {

    fun installPackage(paths: List<String>) {
        TODO()
    }

    fun uninstallPackage(packages: List<String>) {
        TODO()
    }

    fun enableOverlay(packages: List<String>) {
        TODO()
    }

    fun disableOverlay(packages: List<String>) {
        TODO()
    }

    fun getOverlayInfo(packageName: String): OverlayInfo? {
        TODO()
    }

    fun getOverlayInfosForTarget(packageName: String): List<OverlayInfo> {
        TODO()
    }
}