package com.jereksel.libresubstratum.adapters

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import com.jereksel.libresubstratum.*
import com.jereksel.libresubstratum.activities.navigationbar.NavigationBarContract
import com.jereksel.libresubstratum.data.NavigationBarOverlay
import kotlinx.android.synthetic.main.activity_navigationbar.*
import org.jetbrains.anko.find
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowBitmap

@Suppress("IllegalIdentifier")
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class NavigationBarAdapterTest: BaseRobolectricTest() {

//    val resources : File = File(javaClass.classLoader.getResource("happy.png").path).parentFile

    lateinit var activityController: ActivityController<RecViewActivity>
    lateinit var activity: AppCompatActivity

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    @Mock
    lateinit var presenter: NavigationBarContract.Presenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        activityController = Robolectric.buildActivity(RecViewActivity::class.java).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
//        activityController.destroy()
        activityCasted = null
        recyclerView = null
    }

    @Test
    fun `Bitmap test`() {

        val leftBA = (1..10).map { 1.toByte() }.toByteArray()
        val centerBA = (1..10).map { 2.toByte() }.toByteArray()
        val rightBA = (1..10).map { 3.toByte() }.toByteArray()

        println(leftBA)
        println(centerBA)
        println(rightBA)

        val navigationBar = NavigationBarOverlay("overlayid", leftBA, centerBA, rightBA)

        val adapter_ = NavigationBarAdapter(listOf(navigationBar))

        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }

        val child = recyclerView.layoutManager.findViewByPosition(0)

        val leftDrawable = ((child.find<ImageView>(R.id.left).drawable as BitmapDrawable).bitmap.robodata).createdFromBytes
        val centerDrawable = ((child.find<ImageView>(R.id.center).drawable as BitmapDrawable).bitmap.robodata).createdFromBytes
        val rightDrawable = ((child.find<ImageView>(R.id.right).drawable as BitmapDrawable).bitmap.robodata).createdFromBytes

        assertEquals(leftBA, leftDrawable)
        assertEquals(centerBA, centerDrawable)
        assertEquals(rightBA, rightDrawable)

    }

    private val Bitmap.robodata: ShadowBitmap
        get() {
            val field = Bitmap::class.java.getDeclaredField("__robo_data__")
            field.isAccessible = true
            return field.get(this) as ShadowBitmap
        }

}

