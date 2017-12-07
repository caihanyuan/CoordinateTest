package com.keepmoving.to.coordinatetest.model;

/**
 * Created by caihanyuan on 2017/12/4.
 * <p>
 *
 * 省
 */
public class ProvinceBean extends BaseBean {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProvinceBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}