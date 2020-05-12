package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

public class ProvinceEntity implements Serializable {

    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ProvinceEntity [name=" + name + ", code=" + code + "]";
    }

}
