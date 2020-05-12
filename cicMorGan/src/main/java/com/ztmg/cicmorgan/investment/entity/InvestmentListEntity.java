package com.ztmg.cicmorgan.investment.entity;

import java.io.Serializable;

/**
 * 投资列表
 * @author pc
 *
 */
public class InvestmentListEntity implements Serializable{
	private String name;
	private String createdate;
	private String amount;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

}
