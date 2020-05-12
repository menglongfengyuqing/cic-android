package com.ztmg.cicmorgan.more.entity;

import java.io.Serializable;

/**
 * 发现活动
 *
 * @author pc
 */
public class ActionCenterEntity implements Serializable {

    private String imgUrl;
    private String type;
    private String text;
    private String state;
    private String id;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
