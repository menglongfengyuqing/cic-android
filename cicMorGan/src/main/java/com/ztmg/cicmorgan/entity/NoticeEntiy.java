package com.ztmg.cicmorgan.entity;

public class NoticeEntiy {

    private String name;
    private int index;

    public NoticeEntiy(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "NoticeEntiy [name=" + name + ", index=" + index + "]";
    }

}
