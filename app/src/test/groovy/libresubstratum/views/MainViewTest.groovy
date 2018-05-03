package libresubstratum.views

import android.app.Activity
import android.os.Build
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.themelist.MainView
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.views.MockedApp
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler

import static junit.framework.Assert.*

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        application = MockedApp.class,
        sdk = Build.VERSION_CODES.LOLLIPOP)
class MainViewTest {

    IPackageManager packageManager;
    Activity activity;
    TestScheduler scheduler;

    @Lazy(soft = true)
            swipeToRefresh = activity.findViewById(R.id.swiperefresh) as SwipeRefreshLayout
    @Lazy(soft = true)
            recyclerView = activity.findViewById(R.id.recyclerView) as RecyclerView

    @Before
    def void setup() {
        def app = RuntimeEnvironment.application as MockedApp
        packageManager = app.mockedPackageManager;
        swipeToRefresh = null
        recyclerView = null
        scheduler = new TestScheduler()

        RxJavaHooks.clear()
        RxJavaHooks.setOnIOScheduler({ Schedulers.immediate() })
        def hook = [
                "getMainThreadScheduler": scheduler,
        ] as RxAndroidSchedulersHook

        RxAndroidPlugins.instance.reset()
        RxAndroidPlugins.instance.registerSchedulersHook(hook)
    }

    @Test
    def void noApps() {
        Mockito.when(packageManager.getApplications()).thenReturn([])
        activity = Robolectric.buildActivity(MainView.class).create().get();
        assertTrue(swipeToRefresh.refreshing)
        scheduler.triggerActions()
        assertFalse(swipeToRefresh.refreshing)
        assertEquals(0, recyclerView.adapter.itemCount)
    }

    @Test
    def void withApps() {
        Mockito.when(packageManager.getApplications()).thenReturn([])
        activity = Robolectric.buildActivity(MainView.class).create().get();
        assertTrue(swipeToRefresh.refreshing)
        scheduler.triggerActions()
        assertFalse(swipeToRefresh.refreshing)
        assertEquals(0, recyclerView.adapter.itemCount)
    }

}