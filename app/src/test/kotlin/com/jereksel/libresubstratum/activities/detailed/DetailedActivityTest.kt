package com.jereksel.libresubstratum.activities.detailed

import android.view.View
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratum.ShadowSnackbar
import com.jereksel.libresubstratum.extensions.getAllStrings
import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension
import io.reactivex.observers.TestObserver
import kotlinx.android.synthetic.main.activity_detailed.*
import org.assertj.android.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.anko.find
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowToast

class DetailedActivityTest: BaseRobolectricTest() {

    lateinit var activityController : ActivityController<DetailedActivity>
    lateinit var activity : DetailedActivity

    var type3Spinner by ResettableLazy { activity.spinner }
    var progressBar by ResettableLazy { activity.progressBar }
    var recyclerView by ResettableLazy { activity.recyclerView }

    lateinit var subscriber: TestObserver<DetailedAction>

    @Before
    fun setup() {
        activityController = Robolectric.buildActivity(DetailedActivity::class.java).create().resume()
        activity = activityController.get()
        ShadowToast.reset()
        ShadowSnackbar.reset()
        subscriber = TestObserver.create()
        activity.getActions().subscribe(subscriber)
    }

    @After
    fun cleanup() {
        activityController.destroy()
        subscriber.cancel()
        type3Spinner = null
        progressBar = null
        recyclerView = null
    }

    @Test
    fun `When value returned from toast() is not null toast is shown`() {
        val viewState = DetailedViewState.INITIAL.copy(toast =  { "Test toast" } )
        activity.render(viewState)
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Test toast")
    }

    @Test
    fun `When value returned from toast() is null toast is not shown`() {
        val viewState = DetailedViewState.INITIAL.copy(toast =  { null } )
        activity.render(viewState)
        assertThat(ShadowToast.getLatestToast()).isNull()
    }

    @Test
    fun `When compilation error is shown Snackbar is shown`() {
        val exception = RuntimeException("My awesome message")
        val viewState = DetailedViewState.INITIAL.copy(compilationError = exception)
        activity.render(viewState)
        assertThat(ShadowSnackbar.getLatestSnackbar()).isNotNull()
    }

    @Test
    fun `When type3 is empty type3 spinner is hidden`() {
        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", emptyList(),
                        DetailedViewState.Type3(
                                data = emptyList(),
                                position = 0
                        )
                ))

        activity.render(viewState)
        assertThat(type3Spinner).isNotVisible
    }

    @Test
    fun `When type3 is non empty spinner is shown and has items`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", emptyList(),
                        DetailedViewState.Type3(
                                data = listOf(
                                        Type3Extension("a", true),
                                        Type3Extension("b", false),
                                        Type3Extension("c", false)
                                ),
                                position = 1
                        )
                ))

        activity.render(viewState)
        assertThat(type3Spinner).isVisible
        assertThat(type3Spinner.adapter.count).isEqualTo(3)
        assertThat(type3Spinner.getAllStrings()).containsExactlyElementsOf(listOf("a", "b", "c"))
        assertThat(type3Spinner.selectedItemPosition).isEqualTo(1)

        //Phantom select (some AOSP quirk)
        type3Spinner.setSelection(1)

        type3Spinner.setSelection(2)
        val lastItem = subscriber.values().last()

        assertThat(lastItem).isEqualTo(DetailedAction.ChangeType3SpinnerSelection(2))
    }

    @Test
    fun `When compilation ended progress bar is hidden`() {
        val viewState = DetailedViewState.INITIAL
        activity.render(viewState)
        assertThat(progressBar).isNotVisible
    }

    @Test
    fun `When compilation is in progress bar is shown`() {
        val viewState = DetailedViewState.INITIAL.copy(numberOfFinishedCompilations = 1, numberOfAllCompilations = 4)
        activity.render(viewState)
        assertThat(progressBar).isVisible
        assertThat(progressBar).hasProgress(1)
        assertThat(progressBar).hasMaximum(4)
    }

    @Test
    fun `When card is clicked checkbox changes and change request is sent for unchecked`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = null,
                                type1b = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.DEFAULT,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        assertThat(card.find<CheckBox>(R.id.checkbox)).isNotChecked

        card.performClick()

        val lastItem = subscriber.values().last()

        assertThat(lastItem).isEqualTo(DetailedAction.ToggleCheckbox(0, true))

        //It'll change state during next render
        assertThat(card.find<CheckBox>(R.id.checkbox)).isNotChecked
    }

    @Test
    fun `When card is clicked checkbox changes and change request is sent for checked`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = null,
                                type1b = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.DEFAULT,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = true
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        assertThat(card.find<CheckBox>(R.id.checkbox)).isChecked

        card.performClick()

        val lastItem = subscriber.values().last()

        assertThat(lastItem).isEqualTo(DetailedAction.ToggleCheckbox(0, false))

        //It'll change state during next render
        assertThat(card.find<CheckBox>(R.id.checkbox)).isChecked
    }

    @Test
    fun `Clicking on checkbox sends action`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = null,
                                type1b = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.DEFAULT,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        val checkbox = card.find<CheckBox>(R.id.checkbox)

        checkbox.performClick()
        checkbox.performClick()

        val lastItems = subscriber.values().takeLast(2)

        assertThat(lastItems).containsExactlyElementsOf(listOf(DetailedAction.ToggleCheckbox(0, true), DetailedAction.ToggleCheckbox(0, false)))

    }

    @Test
    fun `Long click on card sends compilation action`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = null,
                                type1b = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.DEFAULT,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)
        card.performLongClick()

        val lastItem = subscriber.values().last()

        assertThat(lastItem).isEqualTo(DetailedAction.CompilationLocationAction(0, DetailedAction.CompileMode.DISABLE_COMPILE_AND_ENABLE))

    }

    @Test
    fun `Default compilation state test`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = null,
                                type1b = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.DEFAULT,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        assertThat(card.find<View>(R.id.overlay)).isNotVisible

    }

    @Test
    fun `Compiling compilation state test`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = null,
                                type1b = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.COMPILING,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        assertThat(card.find<View>(R.id.overlay)).isVisible
        assertThat(card.find<TextView>(R.id.overlay_text)).hasText("Compiling")

    }

    @Test
    fun `Installing compilation state test`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = null,
                                type1b = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.INSTALLING,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        assertThat(card.find<View>(R.id.overlay)).isVisible
        assertThat(card.find<TextView>(R.id.overlay_text)).hasText("Installing")
    }

    @Test
    fun `Type1a spinner test`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = DetailedViewState.Type1(
                                        position = 1,
                                        data = listOf(
                                                Type1Extension("a", true),
                                                Type1Extension("b", false),
                                                Type1Extension("c", false)
                                        )
                                ),
                                type1b = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.INSTALLING,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        val type1 = card.find<View>(R.id.type1aview).find<Spinner>(R.id.spinner)

        assertThat(type1.selectedItemPosition).isEqualTo(1)

        type1.setSelection(2)

        val lastItem = subscriber.values().last()

        assertThat(lastItem).isEqualTo(DetailedAction.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(0, 2))

    }


    @Test
    fun `Type1b spinner test`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1b = DetailedViewState.Type1(
                                        position = 1,
                                        data = listOf(
                                                Type1Extension("a", true),
                                                Type1Extension("b", false),
                                                Type1Extension("c", false)
                                        )
                                ),
                                type1a = null,
                                type1c = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.INSTALLING,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        val type1 = card.find<View>(R.id.type1bview).find<Spinner>(R.id.spinner)

        assertThat(type1.selectedItemPosition).isEqualTo(1)

        type1.setSelection(2)

        val lastItem = subscriber.values().last()

        assertThat(lastItem).isEqualTo(DetailedAction.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(0, 2))

    }


    @Test
    fun `Type1c spinner test`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1c = DetailedViewState.Type1(
                                        position = 1,
                                        data = listOf(
                                                Type1Extension("a", true),
                                                Type1Extension("b", false),
                                                Type1Extension("c", false)
                                        )
                                ),
                                type1a = null,
                                type1b = null,
                                type2 = null,
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.INSTALLING,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        val type1 = card.find<View>(R.id.type1cview).find<Spinner>(R.id.spinner)

        assertThat(type1.selectedItemPosition).isEqualTo(1)

        type1.setSelection(2)

        val lastItem = subscriber.values().last()

        assertThat(lastItem).isEqualTo(DetailedAction.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(0, 2))

    }


    @Test
    fun `Type2 spinner test`() {

        val viewState = DetailedViewState.INITIAL.copy(
                themePack = DetailedViewState.ThemePack("", "", listOf(
                        DetailedViewState.Theme(
                                appId = "app1",
                                name = "My app",
                                overlayId = "overlayId",
                                type1a = null,
                                type1b = null,
                                type1c = null,
                                type2 = DetailedViewState.Type2(
                                        position = 1,
                                        data = listOf(
                                                Type2Extension("a", true),
                                                Type2Extension("b", false),
                                                Type2Extension("c", false)
                                        )
                                ),
                                compilationError = null,
                                compilationState = DetailedViewState.CompilationState.INSTALLING,
                                enabledState = DetailedViewState.EnabledState.DISABLED,
                                installedState = DetailedViewState.InstalledState.Removed,
                                checked = false
                        )
                ), null)
        )

        activity.render(viewState)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        val card = recyclerView.getChildAt(0)

        val type1 = card.find<Spinner>(R.id.spinner_2)

        assertThat(type1.selectedItemPosition).isEqualTo(1)

        type1.setSelection(2)

        val lastItem = subscriber.values().last()

        assertThat(lastItem).isEqualTo(DetailedAction.ChangeSpinnerSelection.ChangeType2SpinnerSelection(0, 2))

    }

}