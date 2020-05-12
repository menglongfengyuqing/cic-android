package com.ztmg.cicmorgan.account.entity;

import java.io.Serializable;

/**
 * 交易记录
 *
 * @author pc
 */
public class AnsactionRecordsEntity implements Serializable {
    private String tranddate;//交易时间
    private String transtype;//交易类型
    private String balancemoney;//剩余金额
    private String amount;//交易金额
    private String transstate;//交易状态
    private String remark;//流标状态
    private String name;
    private String projectid;
    private String type;
    private String prostate;//是否可以投资
    private String projectProductType;//1安心投2供应链
    private String sn;//供应链项目编号
    private String inouttype;//进账还是出账


    public String getInouttype() {
        return inouttype;
    }

    public void setInouttype(String inouttype) {
        this.inouttype = inouttype;
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

    public String getProstate() {
        return prostate;
    }

    public void setProstate(String prostate) {
        this.prostate = prostate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTranddate() {
        return tranddate;
    }

    public void setTranddate(String tranddate) {
        this.tranddate = tranddate;
    }

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public String getBalancemoney() {
        return balancemoney;
    }

    public void setBalancemoney(String balancemoney) {
        this.balancemoney = balancemoney;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransstate() {
        return transstate;
    }

    public void setTransstate(String transstate) {
        this.transstate = transstate;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
