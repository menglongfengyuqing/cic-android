package com.ztmg.cicmorgan.integral.entity;

import java.io.Serializable;
/**
 * 奖品名单
 * @author pc
 *
 */
public class AwardEntity implements Serializable{
	private String userPhone;
	private String awardName;
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public String getAwardName() {
		return awardName;
	}
	public void setAwardName(String awardName) {
		this.awardName = awardName;
	}

}
