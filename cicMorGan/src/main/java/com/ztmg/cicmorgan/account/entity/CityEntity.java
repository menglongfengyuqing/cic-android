package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

public class CityEntity implements Serializable{

	private String code;
	private String name;
	private String ProID;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProID() {
		return ProID;
	}
	public void setProID(String proID) {
		ProID = proID;
	}
	@Override
	public String toString() {
		return "CityEntity [code=" + code + ", name=" + name + ", ProID="
				+ ProID + "]";
	}
	
}
