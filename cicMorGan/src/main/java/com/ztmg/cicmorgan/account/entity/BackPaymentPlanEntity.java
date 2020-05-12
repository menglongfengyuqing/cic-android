package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

/**
 * 回款计划
 * @author pc
 *
 */
public class BackPaymentPlanEntity implements Serializable{

	private String date;
	private String money;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}

}
