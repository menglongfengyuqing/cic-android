package com.ztmg.cicmorgan.investment.entity;

import java.io.Serializable;
/**
 * 还款计划
 * @author pc
 *
 */
public class PaymentPlanEntity implements Serializable{
	private String repaysort;//还款期数
	private String repaydate;//还款时间
	private String amount;//还款金额
	public String getRepaysort() {
		return repaysort;
	}
	public void setRepaysort(String repaysort) {
		this.repaysort = repaysort;
	}
	public String getRepaydate() {
		return repaydate;
	}
	public void setRepaydate(String repaydate) {
		this.repaydate = repaydate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

}
