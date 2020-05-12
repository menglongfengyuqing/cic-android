package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

/**
 * 我的投资
 * @author pc
 *
 */
public class MyInvestmentEntity implements Serializable{
	private String bidId;
	private String projectName;
	private String amount;
	private String state;
	private String bid_signature;
	private String endDate;
	private String interest;
	private String rate;
	private String span;
	private String dtime;
	private String sn;
	private String projectId;
	private String creditName;
	private String creditUrl;
	
	
	public String getCreditName() {
		return creditName;
	}
	public void setCreditName(String creditName) {
		this.creditName = creditName;
	}
	public String getCreditUrl() {
		return creditUrl;
	}
	public void setCreditUrl(String creditUrl) {
		this.creditUrl = creditUrl;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getDtime() {
		return dtime;
	}
	public void setDtime(String dtime) {
		this.dtime = dtime;
	}
	public String getBidId() {
		return bidId;
	}
	public void setBidId(String bidId) {
		this.bidId = bidId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBid_signature() {
		return bid_signature;
	}
	public void setBid_signature(String bid_signature) {
		this.bid_signature = bid_signature;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getInterest() {
		return interest;
	}
	public void setInterest(String interest) {
		this.interest = interest;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getSpan() {
		return span;
	}
	public void setSpan(String span) {
		this.span = span;
	}

}
