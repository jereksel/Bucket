package com.jereksel.libresubstratum;


import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.jereksel.libresubstratum.dagger.components.AppComponent;
import com.jereksel.libresubstratum.dagger.components.DaggerAppComponent;
import com.jereksel.libresubstratum.dagger.modules.AppModule;

public class App extends Application {

    private AppComponent component;

    @VisibleForTesting
    protected AppComponent createComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getAppComponent(Context context) {
        App app = (App) context.getApplicationContext();
        if (app.component == null) {
            app.component = app.createComponent();
        }
        return app.component;
    }

}
