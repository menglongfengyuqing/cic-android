package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

/**
 * 回款计划
 * @author pc
 *
 */
public class PaymentPlanEntity implements Serializable{
	private String projectId;
	private String projectName;
	private String amount;
	private String dtime;
	private String state;
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
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
	public String getDtime() {
		return dtime;
	}
	public void setDtime(String dtime) {
		this.dtime = dtime;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

}
