package com.keepmoving.to.coordinatetest;

import android.app.Application;

import com.keepmoving.to.coordinatetest.address.AddressData;

/**
 * Created by caihanyuan on 2017/11/15.
 */

public class CoordinateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AddressData.init(this);
    }
}
