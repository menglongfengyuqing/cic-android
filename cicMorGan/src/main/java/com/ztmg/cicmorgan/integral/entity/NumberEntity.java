package com.ztmg.cicmorgan.integral.entity;

/**
 * Created by dongdong on 2018/8/3.
 * 抽奖次数
 */

public class NumberEntity {


    /**
     * message : 用户抽奖次数获取成功
     * num : 8
     * state : 0
     */

    private String message;
    private String num;
    private String state;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
