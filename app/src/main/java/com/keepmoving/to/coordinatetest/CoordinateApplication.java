package com.keepmoving.to.coordinatetest;

import android.app.Application;

/**
 * Created by caihanyuan on 2017/11/15.
 */

public class CoordinateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AreaDataUtil.init(this);
    }
}
