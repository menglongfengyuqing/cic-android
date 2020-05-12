package com.ztmg.cicmorgan.entity;

import java.util.List;

public class PagerBean {

    public List<String> imgUrls;
    public List<String> titles;
    public List<String> textUrls;

    public PagerBean(List<String> imgUrls, List<String> titles, List<String> textUrls) {
        super();
        this.imgUrls = imgUrls;
        this.titles = titles;
        this.textUrls = textUrls;
    }
}
