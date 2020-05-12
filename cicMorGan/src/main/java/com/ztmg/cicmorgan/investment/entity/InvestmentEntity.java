package com.ztmg.cicmorgan.investment.entity;

import java.io.Serializable;

/**
 * 投资列表entity
 * @author pc
 *
 */
public class InvestmentEntity implements Serializable{
	private String amount;// 融资金额
	private String balanceamount;// 剩余金额
	private String rate;// 年化收益
	private String percentage;// 融资进度
	private String currentamount;// 当前融资金额
	private String name;// 项目名称
	private String projectid;// 项目id
	private String span;// 项目期限
	private String protype;// 标的类型
	private String prostate;// 标的状态
	private String isNewType;//是否是新手0其他1新手
	private String loandate;//项目到期时间
	private String isRecommend;//是否是推荐标，0推荐标1普通标
	private String projecttype;//区分新手和优惠
	private String label;//推荐标才有的标签
	private String state;//个人投资项目状态
	private String projectProductType;//1安心投2供应链
	private String sn;//项目编号
	private String creditName;//供应链名字
	private String creditUrl;//供应链链接
	
	
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
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getProjectProductType() {
		return projectProductType;
	}
	public void setProjectProductType(String projectProductType) {
		this.projectProductType = projectProductType;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getProjecttype() {
		return projecttype;
	}
	public void setProjecttype(String projecttype) {
		this.projecttype = projecttype;
	}
	public String getIsRecommend() {
		return isRecommend;
	}
	public void setIsRecommend(String isRecommend) {
		this.isRecommend = isRecommend;
	}

	public String getLoandate() {
		return loandate;
	}
	public void setLoandate(String loandate) {
		this.loandate = loandate;
	}
	public String getIsNewType() {
		return isNewType;
	}
	public void setIsNewType(String isNewType) {
		this.isNewType = isNewType;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBalanceamount() {
		return balanceamount;
	}
	public void setBalanceamount(String balanceamount) {
		this.balanceamount = balanceamount;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	public String getCurrentamount() {
		return currentamount;
	}
	public void setCurrentamount(String currentamount) {
		this.currentamount = currentamount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProjectid() {
		return projectid;
	}
	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}
	public String getSpan() {
		return span;
	}
	public void setSpan(String span) {
		this.span = span;
	}
	public String getProtype() {
		return protype;
	}
	public void setProtype(String protype) {
		this.protype = protype;
	}
	public String getProstate() {
		return prostate;
	}
	public void setProstate(String prostate) {
		this.prostate = prostate;
	}
}
