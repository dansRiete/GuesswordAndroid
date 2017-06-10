package com.kuzko.aleksey.guessword;

import android.app.Application;

import com.kuzko.aleksey.guessword.database.HelperFactory;

/**
 * Created by Aleks on 10.06.2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
    }
    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
}
