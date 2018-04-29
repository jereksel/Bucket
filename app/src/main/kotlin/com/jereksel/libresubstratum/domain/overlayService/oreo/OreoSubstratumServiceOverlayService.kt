package com.jereksel.libresubstratum.domain.overlayService.oreo

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.substratum.ISubstratumService
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.support.v4.content.ContextCompat
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.common.util.concurrent.*
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.FutureUtils.toFuture
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class OreoSubstratumServiceOverlayService(
        val context: Context
) : OverlayService {

    private val threadFactory = ThreadFactoryBuilder().setNameFormat("oreo-substratum-service-overlay-service-thread-%d").build()!!
    private val executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor(threadFactory))!!

    val log = getLogger()

    lateinit var service: ISubstratumService

    private fun initInterfacer() {
        @SuppressLint("PrivateApi")
        val serviceServiceClass = Class.forName("android.os.ServiceManager")
        val binder = serviceServiceClass.getMethod("getService", String::class.java)
                .invoke(null, "substratum")
        service = ISubstratumService.Stub.asInterface(binder as IBinder?)
    }

    init {
        Handler(Looper.getMainLooper()).post {
            initInterfacer()
        }
    }

    override fun enableOverlay(id: String): ListenableFuture<*> = executor.submit {
        service.switchOverlay(listOf(id), true, false)
        update.set(true)
    }

    override fun disableOverlay(id: String): ListenableFuture<*> = executor.submit {
        service.switchOverlay(listOf(id), false, false)
        update.set(true)
    }

    override fun enableExclusive(id: String): ListenableFuture<*> = executor.submit {
        val state = getCurrentState()
        val application = state.entries().find { it.value.packageName == id }?.key ?: return@submit
        val overlays = state.get(application)
        service.switchOverlay(overlays.map { it.packageName }, false, false)
        service.switchOverlay(listOf(id), true, false)
        update.set(true)
    }

    override fun getOverlayInfo(id: String): ListenableFuture<OverlayInfo?> = executor.sub {
        val state = getCurrentState()
        state.values().find { it.packageName == id }?.let { OverlayInfo(it) }
    }

    override fun getAllOverlaysForApk(appId: String): ListenableFuture<List<OverlayInfo>> = executor.sub {
        val state = getCurrentState()
        state.get(appId).toList().map { OverlayInfo(it) }
    }

    override fun restartSystemUI(): ListenableFuture<*> = executor.submit {
        service.restartSystemUI()
    }

    override fun installApk(apk: File): ListenableFuture<*> = executor.submit {
        service.installOverlay(listOf(apk.absolutePath))
        //FIXME: After installing OMS doesn't update fast enough
        Thread.sleep(500)
        update.set(true)
    }

    override fun uninstallApk(appId: String): ListenableFuture<*> = executor.submit {
        service.uninstallOverlay(listOf(appId), false)
        update.set(true)
    }

    override fun requiredPermissions(): List<String> {
        return allPermissions()
                .filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    private fun allPermissions() = listOf(WRITE_EXTERNAL_STORAGE)

    override fun additionalSteps(): ListenableFuture<String?> {
        val areAllPackagesAllowed = Settings.Secure.getInt(context.contentResolver, "force_authorize_substratum_packages", 0) == 1

        return if (!areAllPackagesAllowed) {
            """Please turn on "Force authorize every theme app" in developer settings""".toFuture()
        } else {
            null.toFuture()
        }
    }

    override fun getOverlaysPrioritiesForTarget(targetAppId: String): ListenableFuture<List<OverlayInfo>> = executor.sub {
        val state = getCurrentState()
        state.get(targetAppId).toList().map { OverlayInfo(it) }.reversed()
    }

    override fun updatePriorities(overlayIds: List<String>): ListenableFuture<*> = executor.submit {
        service.setPriority(overlayIds, false)
        update.set(true)
    }

    @Volatile
    var lastState: Multimap<String, android.content.om.OverlayInfo> = ArrayListMultimap.create()

    val lock = java.lang.Object()

    val update = AtomicBoolean(true)
    var lastUpdate = System.currentTimeMillis()
    val updateTime = TimeUnit.MINUTES.toMillis(1)

    private fun getCurrentState(): Multimap<String, android.content.om.OverlayInfo> = synchronized(lock) {
        if (update.getAndSet(false) || lastUpdate + updateTime < System.currentTimeMillis()) {
            lastUpdate = System.currentTimeMillis()
            lastState = (service.getAllOverlays(0) as Map<String, List<android.content.om.OverlayInfo>>).toMultimap()
        }

        lastState
    }

    private fun <A,B> Map<A, Iterable<B>>.toMultimap(): Multimap<A, B> {
        val mmap = ArrayListMultimap.create<A, B>()
        forEach { t, u -> mmap.putAll(t, u) }
        return mmap
    }

    //Kotlin for some reason by default chooses method that returns ListenableFuture<*>
    private inline fun <T> ListeningExecutorService.sub(crossinline function: () -> T): ListenableFuture<T> =
            this.submit(java.util.concurrent.Callable { function() })

}