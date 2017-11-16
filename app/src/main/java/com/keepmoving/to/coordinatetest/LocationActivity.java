package com.keepmoving.to.coordinatetest;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.keepmoving.to.coordinatetest.model.AddressBean;
import com.keepmoving.to.coordinatetest.model.AddressBean.ResultBean.AddressComponentBean;
import com.keepmoving.to.coordinatetest.model.Area;
import com.keepmoving.to.coordinatetest.model.BaseBean;
import com.keepmoving.to.coordinatetest.model.City;
import com.keepmoving.to.coordinatetest.model.Province;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends Activity implements LocationHelper.LocationCallback {

    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private Spinner areaSpinner;
    private EditText mInfoText;

    private SpinnerAdapeter<Province> provinceSpinnerAdapeter;
    private SpinnerAdapeter<City> citySpinnerAdapeter;
    private SpinnerAdapeter<Area> areaSpinnerAdapeter;

    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        provinceSpinner = (Spinner) findViewById(R.id.province_spinner);
        citySpinner = (Spinner) findViewById(R.id.city_spinner);
        areaSpinner = (Spinner) findViewById(R.id.area_spinner);
        mInfoText = (EditText) findViewById(R.id.address_info_text);

        initData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[LocationHelper.LOCATION_REQUEST] == PackageManager.PERMISSION_GRANTED) {
            locationHelper.getLocation();
        }
    }

    @Override
    public void onGetAddress(AddressBean location) {
        if (location != null) {
            AddressBean.ResultBean resultBean = location.getResult();
            if (resultBean != null) {
                String address = location.getResult().getFormatted_address();
                if (!TextUtils.isEmpty(address)) {
                    mInfoText.setText(address);
                }
            }

            AddressComponentBean addressComponentBean = resultBean.getAddressComponent();
            if (addressComponentBean != null) {
                changeSpinnerSelection(addressComponentBean);
            }
        }
    }

    @Override
    public void onOpenGps() {

    }

    private void initData() {
        List<Province> provinces = AreaDataUtil.getProvinces();
        List<City> cityList = new ArrayList<>();
        List<Area> areaList = new ArrayList<>();

        List<City> cityTmpList = AreaDataUtil.getCitys(provinces.get(0).getId());
        if (cityTmpList != null) {
            cityList.addAll(cityTmpList);
        }
        if (!cityList.isEmpty()) {
            List<Area> areaTmpList = AreaDataUtil.getAreas(cityList.get(0).getId());
            if (areaTmpList != null) {
                areaList.addAll(areaTmpList);
            }
        }

        provinceSpinnerAdapeter = new SpinnerAdapeter(provinces);
        citySpinnerAdapeter = new SpinnerAdapeter<>(cityList);
        areaSpinnerAdapeter = new SpinnerAdapeter(areaList);

        provinceSpinner.setAdapter(provinceSpinnerAdapeter);
        citySpinner.setAdapter(citySpinnerAdapeter);
        areaSpinner.setAdapter(areaSpinnerAdapeter);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province province = (Province) view.getTag();
                changeProvince(province.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City city = (City) view.getTag();
                changeCity(city.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        locationHelper = LocationHelper.newInstance(this);
        locationHelper.setLocationCallback(this);
        locationHelper.getLocation();
    }

    private void changeProvince(String proviceId) {
        List<City> cityList = AreaDataUtil.getCitys(proviceId);
        List<Area> areaList = new ArrayList();
        if (cityList != null && cityList.size() > 0) {
            List<Area> areaOther = AreaDataUtil.getAreas(cityList.get(0).getId());
            if (areaOther != null) {
                areaList.addAll(areaOther);
            }
        }
        citySpinnerAdapeter.setData(cityList);
        areaSpinnerAdapeter.setData(areaList);
    }

    private void changeCity(String cityId) {
        List<Area> areaList = new ArrayList();
        List<Area> areaOther = AreaDataUtil.getAreas(cityId);
        if (areaOther != null) {
            areaList.addAll(areaOther);
        }
        areaSpinnerAdapeter.setData(areaList);
    }

    private void changeSpinnerSelection(AddressComponentBean addressComponentBean) {
        String province = addressComponentBean.getProvince();
        String city = addressComponentBean.getCity();
        String area = addressComponentBean.getDistrict();

        int provinceIndex = provinceSpinnerAdapeter.findPosition(province);
        if (provinceIndex != -1) {
            provinceSpinner.setSelection(provinceIndex);
            Province provinceBean = provinceSpinnerAdapeter.getItem(provinceIndex);

            List<City> cityList = AreaDataUtil.getCitys(provinceBean.getId());
            citySpinnerAdapeter.setData(cityList);

            int cityIndex = citySpinnerAdapeter.findPosition(city);
            cityIndex = cityIndex == -1 ? 0 : cityIndex;
            citySpinner.setSelection(cityIndex);
            City cityBean = citySpinnerAdapeter.getItem(cityIndex);

            List<Area> areaOther = AreaDataUtil.getAreas(cityBean.getId());
            areaSpinnerAdapeter.setData(areaOther);

            int areaIndex = areaSpinnerAdapeter.findPosition(area);
            if (areaIndex != -1) {
                areaSpinner.setSelection(areaIndex);
            }
        }
    }

    class SpinnerAdapeter<T extends BaseBean> extends BaseAdapter {

        List<T> mData;

        public SpinnerAdapeter(List<T> data) {
            mData = data;
        }

        public void setData(List<T> data) {
            if (mData != null) {
                mData.clear();
                mData.addAll(data);
            } else {
                mData = data;
            }
            notifyDataSetChanged();
        }

        public int findPosition(String beanName) {
            int position = -1, size = getCount();
            for (int i = 0; i < size; i++) {
                BaseBean baseBean = mData.get(i);
                if (TextUtils.equals(beanName, baseBean.getName())) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public T getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(LocationActivity.this)
                        .inflate(R.layout.spinner_item, parent, false);
            }
            T baseData = getItem(position);
            ((TextView) convertView).setText(baseData.getName());
            convertView.setTag(baseData);
            return convertView;
        }
    }
}
