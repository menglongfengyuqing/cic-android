package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

/**
 * 我的投资详情新添回款计划
 * @author pc
 *
 */
public class MyInvestmentDetailPaymentEntity implements Serializable{

	private String amount;
	private String principal;
	private String repaymentDate;
	private String state;
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getRepaymentDate() {
		return repaymentDate;
	}
	public void setRepaymentDate(String repaymentDate) {
		this.repaymentDate = repaymentDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
