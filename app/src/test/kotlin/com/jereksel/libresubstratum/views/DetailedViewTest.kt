package com.jereksel.libresubstratum.views

import android.content.DialogInterface.BUTTON_POSITIVE
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
import android.support.design.internal.SnackbarContentLayout
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.jereksel.libresubstratum.*
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.data.Type3ExtensionToString
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Data
import com.jereksel.libresubstratumlib.Type3Extension
import org.jetbrains.anko.find
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowToast
import java.util.*


@Suppress("IllegalIdentifier")
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
    var progressBar by ResettableLazy { activityCasted!!.progressBar }

    val appId = "id1"
    val key = KeyPair(ByteArray(2), ByteArray(2))

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        presenter = app.mockedDetailedPresenter
        val intent = Intent(ShadowApplication.getInstance().applicationContext, DetailedView::class.java)
        val APP_ID_EXTRA = "com.jereksel.libresubstratum.activities.detailed.appIdStarterKey"
        intent.putExtra(APP_ID_EXTRA, appId)
        val KEY_ID_EXTRA = "com.jereksel.libresubstratum.activities.detailed.keyStarterKey"
        intent.putExtra(KEY_ID_EXTRA, key)
        activityController = Robolectric.buildActivity(DetailedView::class.java, intent).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
        activityController.destroy()
        activityCasted = null
        recyclerView = null
        spinner = null
        progressBar = null
    }

    @Test
    fun `readTheme() is invoked after starting`() {
        verify(presenter).readTheme(appId)
    }

    @Test
    fun `setKey() is invoked after starting`() {
        verify(presenter).setKey(key)
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
        activity.hideCompilationProgress()
    }

    @Test
    fun `showCompileDialog indeed shows dialog`() {
        assertThat(progressBar).isNotVisible
        activity.showCompilationProgress(2)
        assertThat(progressBar).isVisible
        assertThat(progressBar).hasMaximum(2)
    }

    @Test
    fun `setProgress test`() {
        activity.showCompilationProgress(4)
        assertThat(progressBar).hasProgress(0)
        assertThat(progressBar).hasMaximum(4)
        activity.increaseDialogProgress()
        assertThat(progressBar).hasProgress(1)
        assertThat(progressBar).hasMaximum(4)
    }

    @Test
    fun `snackBar test`() {
        var clicked = false
        activity.showSnackBar("Text", "Button", { clicked = true })
        val snackBar = ShadowSnackbar.getLatestSnackbar()
        assertEquals("Text", ShadowSnackbar.getTextOfLatestSnackbar())
        val button = ((snackBar.view as ViewGroup).getChildAt(0) as SnackbarContentLayout).actionView
        assertEquals("Button", button.text)
        assertFalse(clicked)
        button.performClick()
        assertTrue(clicked)
    }

    @Test
    fun `Error dialog test`() {
        val errors = listOf("Error1", "Error 2")
        activity.showError(errors)

        val snackBar = ShadowSnackbar.getLatestSnackbar()
        assertEquals("Error occured during compilation", ShadowSnackbar.getTextOfLatestSnackbar())

        val button = ((snackBar.view as ViewGroup).getChildAt(0) as SnackbarContentLayout).actionView
        assertThat(button).hasText("Show error")
        button.performClick()

        val dialog = ShadowDialog.getLatestDialog() as AlertDialog

        val textView = dialog.find<TextView>(R.id.errorTextView)
        assertThat(textView).hasText(errors.joinToString(separator = "\n"))

        dialog.getButton(BUTTON_POSITIVE).performClick()

        verify(presenter).setClipboard(errors.joinToString(separator = "\n"))

    }

    @Test
    fun `Clicking on selectAll invokes it in presenter`() {
        activityCasted?.onOptionsItemSelected(RoboMenuItem(R.id.action_selectall))
        verify(presenter).selectAll()
    }

    @Test
    fun `Clicking on unselectAll invokes it in presenter`() {
        activityCasted?.onOptionsItemSelected(RoboMenuItem(R.id.action_deselectall))
        verify(presenter).deselectAll()
    }


    private fun <T> ArrayAdapter<T>.getAllItems() = (0 until count).map { this.getItem(it) }
}
