package com.ztmg.cicmorgan.entity;

import java.io.Serializable;

/**
 * dong
 * 2018年7月2日 19:26:38
 * 获取可用余额和冻结金额
 */

public class CgbUserTolAmountEntity implements Serializable {


    /**
     * message : 获取用户存管宝的可用余额和冻结金额成功
     * data : {"respSubCode":"000000","respCode":"00","unclearedBalance":"10000000","freezeBalance":"0","userId":"4530762433399091063","respMsg":"成功","availableBalance":"10000000"}
     * state : 0
     */

    private String message;
    private DataBean data;
    private String state;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static class DataBean {
        /**
         * respSubCode : 000000
         * respCode : 00
         * unclearedBalance : 10000000
         * freezeBalance : 0
         * userId : 4530762433399091063
         * respMsg : 成功
         * availableBalance : 10000000
         */

        private String respSubCode;
        private String respCode;
        private String unclearedBalance;
        private String freezeBalance;
        private String userId;
        private String respMsg;
        private String availableBalance;

        public String getRespSubCode() {
            return respSubCode;
        }

        public void setRespSubCode(String respSubCode) {
            this.respSubCode = respSubCode;
        }

        public String getRespCode() {
            return respCode;
        }

        public void setRespCode(String respCode) {
            this.respCode = respCode;
        }

        public String getUnclearedBalance() {
            return unclearedBalance;
        }

        public void setUnclearedBalance(String unclearedBalance) {
            this.unclearedBalance = unclearedBalance;
        }

        public String getFreezeBalance() {
            return freezeBalance;
        }

        public void setFreezeBalance(String freezeBalance) {
            this.freezeBalance = freezeBalance;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRespMsg() {
            return respMsg;
        }

        public void setRespMsg(String respMsg) {
            this.respMsg = respMsg;
        }

        public String getAvailableBalance() {
            return availableBalance;
        }

        public void setAvailableBalance(String availableBalance) {
            this.availableBalance = availableBalance;
        }
    }
}
