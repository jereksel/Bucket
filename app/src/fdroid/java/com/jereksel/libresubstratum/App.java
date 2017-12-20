package com.jereksel.libresubstratum;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.jereksel.libresubstratum.activities.ErrorActivity;
import com.jereksel.libresubstratum.dagger.components.AppComponent;
import com.jereksel.libresubstratum.dagger.components.BaseComponent;
import com.jereksel.libresubstratum.dagger.components.DaggerAppComponent;
import com.jereksel.libresubstratum.dagger.modules.AppModule;

import cat.ereza.customactivityoncrash.config.CaocConfig;

import static cat.ereza.customactivityoncrash.config.CaocConfig.BACKGROUND_MODE_CRASH;

public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        CaocConfig.Builder.create()
                .backgroundMode(BACKGROUND_MODE_CRASH)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                .errorActivity(ErrorActivity.class)
                .apply();
    }

    @VisibleForTesting
    protected AppComponent createComponent() {
        return DaggerAppComponent.builder()
                .appModule(getAppModule())
                .build();
    }

    private AppModule getAppModule() {
        return new AppModule(this);
    }

    public BaseComponent getAppComponent(Context context) {
        App app = (App) context.getApplicationContext();
        if (app.component == null) {
            app.component = app.createComponent();
        }
        return app.component;
    }

}
