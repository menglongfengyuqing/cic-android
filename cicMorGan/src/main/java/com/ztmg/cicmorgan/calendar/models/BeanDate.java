package com.ztmg.cicmorgan.calendar.models;

import java.io.Serializable;

public class BeanDate implements Serializable {

    private String date;
    private String money;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

}
