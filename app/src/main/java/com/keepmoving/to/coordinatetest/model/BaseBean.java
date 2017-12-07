package com.keepmoving.to.coordinatetest.model;

/**
 * Created by caihanyuan on 2017/11/15.
 * <p>
 * 省市区实体类的基类
 */

public class BaseBean {
    protected String name;
    protected String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastLevelId() {
        return "";
    }
}
