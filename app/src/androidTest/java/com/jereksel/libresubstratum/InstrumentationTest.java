package com.jereksel.libresubstratum;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.jereksel.libresubstratum.activities.main.MainView;
import com.jereksel.libresubstratum.domain.IPackageManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class InstrumentationTest {
    @Rule
    public final ActivityTestRule<MainView> main = new ActivityTestRule<>(MainView.class, false, false);

    private IPackageManager packageManager;

    @Before
    public void setUp() {
        App app = (App) getTargetContext().getApplicationContext();
        MockAppComponent component = (MockAppComponent) App.getAppComponent(app);
        packageManager = component.getPackageManager();
        main.launchActivity(null);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = getTargetContext();
        assertEquals("com.jereksel.libresubstratum", appContext.getPackageName());
    }
}

