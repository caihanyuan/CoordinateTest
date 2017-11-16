package com.keepmoving.to.coordinatetest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.keepmoving.to.coordinatetest.model.AddressBean;

/**
 * Created by caihanyuan on 2017/11/15.
 */

public class LocationHelper implements BaiduAddress.AddressCallback {

    private final static String TAG = LocationHelper.class.getSimpleName();
    public final static int LOCATION_REQUEST = 0;

    private Context mContext;
    private Location mLocation;
    private LocationManager mLocationManager;

    private BaiduAddress mBaiduAddress;
    private LocationCallback mLocationCallback;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            Log.d(TAG, mLocation.toString());
            getAddress(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "provider [" + provider + "] enable");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "provider [" + provider + "] disable");
        }
    };

    public static LocationHelper newInstance(Context context) {
        return new LocationHelper(context);
    }

    private LocationHelper(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mBaiduAddress = new BaiduAddress();
        mBaiduAddress.setAddressCallback(this);
    }

    private boolean isNetworkOpen() {
        return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGpsOpen() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getAddress(Location location) {
        mBaiduAddress.getAddress(location);
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (mContext instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            }
            Log.d(TAG, "ACCESS_FINE_LOCATION not granted");
            return;
        }

        String providerName = "";

        if (!isNetworkOpen() && !isGpsOpen()) {
            Log.d(TAG, "please open gps or network");
            if (mLocationCallback != null) {
                mLocationCallback.onOpenGps();
            }
        } else if (!isNetworkOpen() && isGpsOpen()) {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            providerName = LocationManager.GPS_PROVIDER;
        } else if (isNetworkOpen() && !isGpsOpen()) {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            providerName = LocationManager.NETWORK_PROVIDER;
        } else {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            providerName = LocationManager.GPS_PROVIDER;
            if (mLocation == null) {
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                providerName = LocationManager.NETWORK_PROVIDER;
            }
        }

        if (mLocation != null) {
            Log.d(TAG, mLocation.toString());
            getAddress(mLocation);
        } else {
            mLocationManager.requestLocationUpdates(
                    providerName, 2000, 50, mLocationListener);
        }
    }

    public void setLocationCallback(LocationCallback locationCallback) {
        mLocationCallback = locationCallback;
    }

    @Override
    public void onGetAddressSuccess(AddressBean addressBean) {
        if (mLocationCallback != null) {
            mLocationCallback.onGetAddress(addressBean);
        }
    }

    interface LocationCallback {
        void onGetAddress(AddressBean addressBean);

        void onOpenGps();
    }
}


