package com.ztmg.cicmorgan.home.entity;

import java.io.Serializable;

/**
 * 首页公告轮播
 *
 * @author pc
 */
public class NoticeEntity implements Serializable {
    private String title;
    private String id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
