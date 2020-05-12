package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

/**
 * 优惠券
 * @author pc
 *
 */
public class CardEntity implements Serializable{
	private String id;
	private String getDate;
	private String overdueDate;
	private String state;
	private String type;
	private String value;
	private String limitAmount;
	private String spans;


	public String getSpans() {
		return spans;
	}
	public void setSpans(String spans) {
		this.spans = spans;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGetDate() {
		return getDate;
	}
	public void setGetDate(String getDate) {
		this.getDate = getDate;
	}
	public String getOverdueDate() {
		return overdueDate;
	}
	public void setOverdueDate(String overdueDate) {
		this.overdueDate = overdueDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLimitAmount() {
		return limitAmount;
	}
	public void setLimitAmount(String limitAmount) {
		this.limitAmount = limitAmount;
	}
}
