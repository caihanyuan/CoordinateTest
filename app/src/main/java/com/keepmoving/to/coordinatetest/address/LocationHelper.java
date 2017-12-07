package com.keepmoving.to.coordinatetest.address;

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

import com.keepmoving.to.coordinatetest.model.BaiduAddressBean;

import java.lang.ref.WeakReference;

/**
 * Created by caihanyuan on 2017/11/15.
 * <p>
 * 获取经纬度并返回地址信息
 */

public class LocationHelper implements BaiduAddress.AddressCallback {

    private final static String TAG = LocationHelper.class.getSimpleName();
    public final static int LOCATION_REQUEST = 0;

    private Location mLocation;
    private LocationManager mLocationManager;
    private WeakReference<Context> mContext;

    private BaiduAddress mBaiduAddress;
    private WeakReference<LocationCallback> mLocationCallback;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            mLocation = location;
            Log.d(TAG, mLocation.toString());
            getAddress(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "provider [" + provider + "] onStatusChanged:" + status);
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
        mContext = new WeakReference<>(context);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
        //如果Context(Activity)已经被回收，就没必要再请求数据了
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
            }
            Log.d(TAG, "ACCESS_FINE_LOCATION not granted");
            return;
        }

        String providerName = "";

        if (!isNetworkOpen() && !isGpsOpen()) {
            Log.d(TAG, "please open gps or network");
            if (mLocationCallback != null) {
                mLocationCallback.get().needOpenGps();
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
            mLocationManager.requestSingleUpdate(providerName, mLocationListener, null);
        }
    }

    public void setLocationCallback(LocationCallback locationCallback) {
        mLocationCallback = new WeakReference<>(locationCallback);
    }

    @Override
    public void onGetAddressSuccess(BaiduAddressBean addressBean) {
        if (mLocationCallback.get() != null) {
            mLocationCallback.get().onGetAddress(addressBean);
        }
    }

    @Override
    public void onGetAddressFaile() {
        // TODO: 2017/12/6 获取失败让用户填写即可, 如果有需求可以回调通知
    }

    /**
     * 获取当前位置对应的地址信息回调接口
     */
    public interface LocationCallback {
        /**
         * 获取地址数据成功
         *
         * @param addressBean
         */
        void onGetAddress(BaiduAddressBean addressBean);

        /**
         * 需要打开GPS
         */
        void needOpenGps();
    }
}


