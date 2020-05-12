package com.ztmg.cicmorgan.net.testA.bean;

import java.io.Serializable;

/**
 * Created by ccb on 2017/10/11.
 */


public class ResponseBean<T> implements Serializable {

    public String state;
    public String message;
    public T data;
    public String token;
    public String username;
    //public T Result;


    @Override
    public String toString() {
        return "ResponseBean{" +
                "state='" + state + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", token='" + token + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}