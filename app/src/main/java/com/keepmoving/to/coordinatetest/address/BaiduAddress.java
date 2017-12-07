package com.keepmoving.to.coordinatetest.address;

import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keepmoving.to.coordinatetest.model.BaiduAddressBean;

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
 * <p>
 * 根据GPS获取的经纬度，调用百度的地址转换接口获取地址信息
 */

public class BaiduAddress {
    private static final String TAG = BaiduAddress.class.getSimpleName();
    private static final String BASE_URL = "http://api.map.baidu.com/";

    private AddressCallback mAddressCallback;

    void setAddressCallback(AddressCallback addressCallback) {
        mAddressCallback = addressCallback;
    }

    void getAddress(Location location) {
        if (location == null)
            return;

        //经纬度坐标系转换成百度坐标系
        double[] bdLoc = CoordinateTransformUtil.wgs84tobd09(location.getLongitude(), location.getLatitude());

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
        params.put("location", bdLoc[1] + "," + bdLoc[0]);
        Call<BaiduAddressBean> call = addressService.getAddress(params);
        call.enqueue(new Callback<BaiduAddressBean>() {
            @Override
            public void onResponse(Call<BaiduAddressBean> call, Response<BaiduAddressBean> response) {
                BaiduAddressBean addressBean = response.body();
                if (TextUtils.equals("OK", addressBean.getStatus()) && mAddressCallback != null) {
                    mAddressCallback.onGetAddressSuccess(addressBean);
                }
            }

            @Override
            public void onFailure(Call<BaiduAddressBean> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }


    interface IAddressService {
        @GET("geocoder")
        Call<BaiduAddressBean> getAddress(@QueryMap Map<String, String> params);
    }

    interface AddressCallback {
        void onGetAddressSuccess(BaiduAddressBean addressBean);

        void onGetAddressFaile();
    }
}
