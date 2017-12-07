package com.keepmoving.to.coordinatetest.model;

/**
 * Created by caihanyuan on 2017/11/15.
 *
 * 区
 */

public class AreaBean extends BaseBean {
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getLastLevelId() {
        return this.city;
    }
}
