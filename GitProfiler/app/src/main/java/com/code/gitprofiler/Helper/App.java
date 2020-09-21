package com.code.gitprofiler.Helper;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {

    private static App _application;

    public static App getApplication() {
        return _application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        App._application = this;
        Timber.plant(new Timber.DebugTree());
    }
}
