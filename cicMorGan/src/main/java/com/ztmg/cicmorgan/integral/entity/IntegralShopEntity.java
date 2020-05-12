package com.ztmg.cicmorgan.integral.entity;

import java.io.Serializable;

/**
 * 积分商城entity
 * @author pc
 *
 */
public class IntegralShopEntity implements Serializable{
	
	private String awardId;
	private String name;
	private String needAmount;
	private String docs;
	private String imgWeb;
	public String getAwardId() {
		return awardId;
	}
	public void setAwardId(String awardId) {
		this.awardId = awardId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNeedAmount() {
		return needAmount;
	}
	public void setNeedAmount(String needAmount) {
		this.needAmount = needAmount;
	}
	public String getDocs() {
		return docs;
	}
	public void setDocs(String docs) {
		this.docs = docs;
	}
	public String getImgWeb() {
		return imgWeb;
	}
	public void setImgWeb(String imgWeb) {
		this.imgWeb = imgWeb;
	}
}
