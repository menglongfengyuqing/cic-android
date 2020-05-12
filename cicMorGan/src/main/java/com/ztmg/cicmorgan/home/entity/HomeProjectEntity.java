package com.ztmg.cicmorgan.home.entity;
/**
 * 首页项目列表
 * @author pc
 *
 */
public class HomeProjectEntity {
	private String projectid;
	private String name;
	private String rate;
	private String span;
	private String loandate;
	private String projectType;
	private String prostate;
	private String projectProductType;
	private String isRecommend;
	private String sn;
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
	public String getIsRecommend() {
		return isRecommend;
	}
	public void setIsRecommend(String isRecommend) {
		this.isRecommend = isRecommend;
	}
	public String getProstate() {
		return prostate;
	}
	public void setProstate(String prostate) {
		this.prostate = prostate;
	}
	public String getProjectid() {
		return projectid;
	}
	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getLoandate() {
		return loandate;
	}
	public void setLoandate(String loandate) {
		this.loandate = loandate;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
}
