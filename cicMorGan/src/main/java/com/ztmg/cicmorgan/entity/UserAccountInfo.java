package com.ztmg.cicmorgan.entity;

import java.io.Serializable;

/**
 * 用户账户信息
 * @author pc
 *
 */
public class UserAccountInfo implements Serializable{
	private String totalAmount;//资产总额
	private String availableAmount;//可提现金额/可用金额
	private String cashAmount;//提现金额
	private String rechargeAmount;//充值金额
	private String freezeAmount;//冻结金额
	private String totalInterest;//总收益
	private String currentAmount;//活期投资金额
	private String regularDuePrincipal;//定期待收本金
	private String regularDueInterest;//定期待收收益
	private String regularTotalAmount;//定期投资总额
	private String regularTotalInterest;//定期累计收益
	private String currentTotalInterest;//活期累计投资金额
	private String currentYesterdayInterest;//活期昨日收益
	private String reguarYesterdayInterest;//定期昨日收益
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getAvailableAmount() {
		return availableAmount;
	}
	public void setAvailableAmount(String availableAmount) {
		this.availableAmount = availableAmount;
	}
	public String getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(String cashAmount) {
		this.cashAmount = cashAmount;
	}
	public String getRechargeAmount() {
		return rechargeAmount;
	}
	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}
	public String getFreezeAmount() {
		return freezeAmount;
	}
	public void setFreezeAmount(String freezeAmount) {
		this.freezeAmount = freezeAmount;
	}
	public String getTotalInterest() {
		return totalInterest;
	}
	public void setTotalInterest(String totalInterest) {
		this.totalInterest = totalInterest;
	}
	public String getCurrentAmount() {
		return currentAmount;
	}
	public void setCurrentAmount(String currentAmount) {
		this.currentAmount = currentAmount;
	}
	public String getRegularDuePrincipal() {
		return regularDuePrincipal;
	}
	public void setRegularDuePrincipal(String regularDuePrincipal) {
		this.regularDuePrincipal = regularDuePrincipal;
	}
	public String getRegularDueInterest() {
		return regularDueInterest;
	}
	public void setRegularDueInterest(String regularDueInterest) {
		this.regularDueInterest = regularDueInterest;
	}
	public String getRegularTotalAmount() {
		return regularTotalAmount;
	}
	public void setRegularTotalAmount(String regularTotalAmount) {
		this.regularTotalAmount = regularTotalAmount;
	}
	public String getRegularTotalInterest() {
		return regularTotalInterest;
	}
	public void setRegularTotalInterest(String regularTotalInterest) {
		this.regularTotalInterest = regularTotalInterest;
	}
	public String getCurrentTotalInterest() {
		return currentTotalInterest;
	}
	public void setCurrentTotalInterest(String currentTotalInterest) {
		this.currentTotalInterest = currentTotalInterest;
	}
	public String getCurrentYesterdayInterest() {
		return currentYesterdayInterest;
	}
	public void setCurrentYesterdayInterest(String currentYesterdayInterest) {
		this.currentYesterdayInterest = currentYesterdayInterest;
	}
	public String getReguarYesterdayInterest() {
		return reguarYesterdayInterest;
	}
	public void setReguarYesterdayInterest(String reguarYesterdayInterest) {
		this.reguarYesterdayInterest = reguarYesterdayInterest;
	}

}
