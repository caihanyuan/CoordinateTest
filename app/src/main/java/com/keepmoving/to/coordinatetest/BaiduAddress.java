package com.keepmoving.to.coordinatetest;

import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keepmoving.to.coordinatetest.model.AddressBean;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by caihanyuan on 2017/11/16.
 */

public class BaiduAddress {
    private static final String TAG = BaiduAddress.class.getSimpleName();
    private static final String BASE_URL = "http://api.map.baidu.com/";

    private AddressCallback mAddressCallback;

    public void setAddressCallback(AddressCallback addressCallback) {
        mAddressCallback = addressCallback;
    }

    public void getAddress(Location location) {
        if (location == null)
            return;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(httpLoggingInterceptor);
        OkHttpClient okHttpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        IAddressService addressService = retrofit.create(IAddressService.class);
        Map<String, String> params = new HashMap<>(2);
        params.put("output", "json");
        params.put("location", location.getLatitude() + "," + location.getLongitude());
        Call<AddressBean> call = addressService.getAddress(params);
        call.enqueue(new Callback<AddressBean>() {
            @Override
            public void onResponse(Call<AddressBean> call, Response<AddressBean> response) {
                AddressBean addressBean = response.body();
                if (TextUtils.equals("OK", addressBean.getStatus()) && mAddressCallback != null) {
                    mAddressCallback.onGetAddressSuccess(addressBean);
                }
            }

            @Override
            public void onFailure(Call<AddressBean> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }


    public interface IAddressService {
        @GET("geocoder")
        Call<AddressBean> getAddress(@QueryMap Map<String, String> params);
    }

    interface AddressCallback {
        void onGetAddressSuccess(AddressBean addressBean);
    }
}
