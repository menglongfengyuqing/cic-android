package com.ztmg.cicmorgan.integral.entity;

import java.io.Serializable;

/**
 * 抽中奖品实例
 *
 * @author pc
 */
public class GoodsListEntity implements Serializable {

    private String myAwardId;
    private String awardId;
    private String awardimgWeb;
    private String docs;
    private String expressNo;
    private String state;
    private String awardDate;
    private String isTrue;
    private String awardName;
    private String awardNeedAmount;
    private String deadline;

    public String getMyAwardId() {
        return myAwardId;
    }

    public void setMyAwardId(String myAwardId) {
        this.myAwardId = myAwardId;
    }

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    public String getAwardimgWeb() {
        return awardimgWeb;
    }

    public void setAwardimgWeb(String awardimgWeb) {
        this.awardimgWeb = awardimgWeb;
    }

    public String getDocs() {
        return docs;
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(String awardDate) {
        this.awardDate = awardDate;
    }

    public String getIsTrue() {
        return isTrue;
    }

    public void setIsTrue(String isTrue) {
        this.isTrue = isTrue;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public String getAwardNeedAmount() {
        return awardNeedAmount;
    }

    public void setAwardNeedAmount(String awardNeedAmount) {
        this.awardNeedAmount = awardNeedAmount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
