package com.jereksel.libresubstratum.views

import android.app.ProgressDialog
import android.os.Build
import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.nhaarman.mockito_kotlin.verify
import kotlinx.android.synthetic.main.activity_detailed.*
import org.junit.*
import org.junit.Assert.*
import org.assertj.android.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import android.content.Intent
import android.provider.Settings
import android.support.design.internal.SnackbarContentLayout
import android.support.design.widget.Snackbar
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.jereksel.libresubstratum.*
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.data.Type3ExtensionToString
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Data
import com.jereksel.libresubstratumlib.Type3Extension
import com.nhaarman.mockito_kotlin.mock
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowToast
import java.util.*


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP),
        shadows = arrayOf(ShadowSnackbar::class)
)
class DetailedViewTest: BaseRobolectricTest() {

    lateinit var activityController : ActivityController<DetailedView>
    lateinit var activity : DetailedContract.View
    lateinit var presenter: DetailedContract.Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }
    var spinner by ResettableLazy { activityCasted!!.spinner }

    val appId = "id1"

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        presenter = app.mockedDetailedPresenter
        val intent = Intent(ShadowApplication.getInstance().applicationContext, DetailedView::class.java)
        val APP_ID_EXTRA = "com.jereksel.libresubstratum.activities.detailed.appIdStarterKey"
        intent.putExtra(APP_ID_EXTRA, appId)
        activityController = Robolectric.buildActivity(DetailedView::class.java, intent).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
        activityController.destroy()
        activityCasted = null
        recyclerView = null
        spinner = null
    }

    @Test
    fun `readTheme() is invoked after starting`() {
        verify(presenter).readTheme(appId)
    }

    @Test
    fun `type3 spinner has list of type3 extensions`() {
        val colors = listOf("black", "white", "green")
        val type3 = Type3Data(colors.map { Type3Extension(it, false) })
        activity.addThemes(ThemePack(listOf(), type3))
        assertThat(spinner).isVisible
        assertEquals(type3.extensions.map { Type3ExtensionToString(it) }, (spinner.adapter as ArrayAdapter<*>).getAllItems())
    }

    @Test
    fun `type3 spinner is no visible whenno type3 extensions are available`() {
        activity.addThemes(ThemePack(listOf()))
        assertThat(spinner).isNotVisible
    }

    @Test
    fun `toast is shown after invoking showToast`() {
        val text = UUID.randomUUID().toString()
        activity.showToast(text)

        assertNotNull(ShadowToast.getLatestToast())
        assertEquals(text, ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun `hideCompileDialog doesn't crash without dialog`() {
        activity.hideCompileDialog()
    }

    @Test
    fun `showCompileDialog indeed shows dialog`() {
        activity.showCompileDialog(2)
        val dialog = ShadowDialog.getLatestDialog()
        assertNotNull(dialog)
        assertTrue(dialog.isShowing)
    }

    @Test
    fun `setProgress test`() {
        activity.showCompileDialog(4)
        val dialog = ShadowDialog.getLatestDialog() as ProgressDialog
        assertNotNull(dialog)
        assertEquals(0, dialog.progress)
        activity.increaseDialogProgress()
        assertEquals(1, dialog.progress)
    }

    @Test
    fun `snackBar test`() {
//        val f: () -> Unit = mock()
        var clicked = false
        activity.showSnackBar("Text", "Button", { clicked = true })
        val snackbar = ShadowSnackbar.getLatestSnackbar()
        assertEquals("Text", ShadowSnackbar.getTextOfLatestSnackbar())
        val button = ((snackbar.view as ViewGroup).getChildAt(0) as SnackbarContentLayout).actionView
        assertEquals("Button", button.text)
        assertFalse(clicked)
        button.performClick()
        assertTrue(clicked)
    }

    private fun <T> ArrayAdapter<T>.getAllItems() = (0..count-1).map { this.getItem(it) }
}

