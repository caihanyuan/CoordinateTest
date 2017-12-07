package com.keepmoving.to.coordinatetest.address;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;

import com.keepmoving.to.coordinatetest.model.AreaBean;
import com.keepmoving.to.coordinatetest.model.CityBean;
import com.keepmoving.to.coordinatetest.model.ProvinceBean;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by caihanyuan on 2017/11/15.
 * <p>
 * 省市区列表数据
 * <p>
 * https://github.com/wecatch/china_regions  //github上抓取省市区的开源项目_1
 * https://github.com/airyland/china-area-data //github上抓取省市区的开源项目_2
 * 这里采用第一个项目抓取的数据
 * <p>
 * 以上项目是抓取国标的数据： http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201703/t20170310_1471429.html
 * <p>
 * 百度地图省市区excel表格：http://lbsyun.baidu.com/index.php?title=open/dev-res
 */

public class AddressData {

    private static Map<String, ProvinceBean> mProvince;

    private static Map<String, List<CityBean>> mCitys;

    private static Map<String, List<AreaBean>> mAreas;

    /**
     * 读取省市区信息到内存
     *
     * @param context
     */
    public static void init(Context context) {
        mProvince = new TreeMap<>();
        mCitys = new TreeMap<>();
        mAreas = new TreeMap<>();

        AssetManager assetManager = context.getAssets();
        try {
            //省
            JsonReader jsonReader = new JsonReader(new InputStreamReader
                    (assetManager.open("province.json")));

            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                jsonReader.beginObject();
                ProvinceBean province = new ProvinceBean();
                while (jsonReader.hasNext()) {
                    String keyname = jsonReader.nextName();
                    if ("id".equals(keyname)) {
                        province.setId(jsonReader.nextString());
                    } else if ("name".equals(keyname)) {
                        province.setName(jsonReader.nextString());
                    }
                }
                mProvince.put(province.getId(), province);
                jsonReader.endObject();
            }
            jsonReader.endArray();
            jsonReader.close();

            //市
            jsonReader = new JsonReader(new InputStreamReader
                    (assetManager.open("city.json")));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String provinceId = jsonReader.nextName();
                List<CityBean> cityList = new ArrayList<>();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    CityBean city = new CityBean();
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String keyName = jsonReader.nextName();
                        if ("id".equals(keyName)) {
                            city.setId(jsonReader.nextString());
                        } else if ("name".equals(keyName)) {
                            city.setName(jsonReader.nextString());
                        } else if ("province".equals(keyName)) {
                            city.setProvince(jsonReader.nextString());
                        }
                    }
                    cityList.add(city);
                    jsonReader.endObject();
                }
                jsonReader.endArray();
                mCitys.put(provinceId, cityList);
            }
            jsonReader.endObject();
            jsonReader.close();

            //区
            jsonReader = new JsonReader(new InputStreamReader
                    (assetManager.open("area.json")));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String cityId = jsonReader.nextName();
                List<AreaBean> areaList = new ArrayList<>();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    AreaBean area = new AreaBean();
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String keyName = jsonReader.nextName();
                        if ("id".equals(keyName)) {
                            area.setId(jsonReader.nextString());
                        } else if ("name".equals(keyName)) {
                            area.setName(jsonReader.nextString());
                        } else if ("city".equals(keyName)) {
                            area.setCity(jsonReader.nextString());
                        }
                    }
                    areaList.add(area);
                    jsonReader.endObject();
                }
                jsonReader.endArray();
                mAreas.put(cityId, areaList);
            }
            jsonReader.endObject();
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ProvinceBean> getProvinces() {
        return new ArrayList(mProvince.values());
    }

    public static List<CityBean> getCitys(String provinceId) {
        return mCitys.get(provinceId);
    }

    public static List<AreaBean> getAreas(String cityId) {
        return mAreas.get(cityId);
    }
}
