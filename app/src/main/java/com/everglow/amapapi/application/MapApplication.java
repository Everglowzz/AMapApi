package com.everglow.amapapi.application;
import android.app.Application;


/**
 * Created by EverGlow on 2017/8/16 12:04
 */

public    class MapApplication extends Application   {

    public static MapApplication  app ;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        intData();
    }

    private void intData() {

    }
}
