package com.ztmg.cicmorgan.investment.entity;

import java.io.Serializable;
/**
 * 供应链项目详情gridview
 * @author pc
 *
 */
public class FileGridViewEntity implements Serializable{
	private String url;
	private String name;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
