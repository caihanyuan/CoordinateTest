package com.keepmoving.to.coordinatetest.model;

/**
 * Created by caihanyuan on 2017/11/15.
 * <p>
 * 市
 */

public class CityBean extends BaseBean {
    private String province;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public String getLastLevelId() {
        return this.province;
    }
}
