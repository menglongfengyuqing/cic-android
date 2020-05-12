package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

public class RequestFriendsEntity implements Serializable{
	private String mobilePhone;//邀请好友列表 手机号  邀请好友佣金列表  手机号
	private String realName;//邀请好友列表 姓名
	private String registerDate;//邀请好友列表 注册时间
	private String amount;//邀请好友佣金列表  佣金
	private String createDate;//邀请好友佣金列表  创建时间
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


}
