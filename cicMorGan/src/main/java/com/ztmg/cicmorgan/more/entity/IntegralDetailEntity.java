package com.ztmg.cicmorgan.more.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 积分明细
 */

public class IntegralDetailEntity implements Serializable {


    /**
     * message : 用户积分历史明细查询成功
     * data : {"pageCount":3,"last":3,"totalCount":21,"pageNo":1,"userBounsHistory":[{"amount":1,"id":"c1b103efbea949afae9ff1e6f54102bb","bounsType":"签到","createDate":"2018-08-18 00:38:06"},{"amount":-10,"id":"c404147369bd42528f3d147e18283937","bounsType":"积分抽奖","createDate":"2018-08-17 02:21:10"},{"amount":-10,"id":"76f0610d95994a6fa774ab69a617977c","bounsType":"积分抽奖","createDate":"2018-08-17 02:20:48"},{"amount":-10,"id":"7a9fbd586cc9491db5cf98293701b16e","bounsType":"积分抽奖","createDate":"2018-08-17 02:16:18"},{"amount":-10,"id":"05b0f9f2cae54eedac21da53b87f61e3","bounsType":"积分抽奖","createDate":"2018-08-17 02:14:37"},{"amount":-10,"id":"8d834a7ed89d4fc7818cde5433e62e5f","bounsType":"积分抽奖","createDate":"2018-08-17 02:14:10"},{"amount":-10,"id":"d39fe4abed0c408a98bbb097f4c0f217","bounsType":"积分抽奖","createDate":"2018-08-17 02:13:08"},{"amount":-10,"id":"4ff7bcc5d79b495394e4704fba8749c5","bounsType":"积分抽奖","createDate":"2018-08-17 02:13:02"},{"amount":-10,"id":"18e0cb1def9347718fe1747851f4f24a","bounsType":"积分抽奖","createDate":"2018-08-17 02:11:57"},{"amount":-10,"id":"38796121124b495a9fc278adc4e98690","bounsType":"积分抽奖","createDate":"2018-08-17 02:11:25"}],"pageSize":10}
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
         * pageCount : 3
         * last : 3
         * totalCount : 21
         * pageNo : 1
         * userBounsHistory : [{"amount":1,"id":"c1b103efbea949afae9ff1e6f54102bb","bounsType":"签到","createDate":"2018-08-18 00:38:06"},{"amount":-10,"id":"c404147369bd42528f3d147e18283937","bounsType":"积分抽奖","createDate":"2018-08-17 02:21:10"},{"amount":-10,"id":"76f0610d95994a6fa774ab69a617977c","bounsType":"积分抽奖","createDate":"2018-08-17 02:20:48"},{"amount":-10,"id":"7a9fbd586cc9491db5cf98293701b16e","bounsType":"积分抽奖","createDate":"2018-08-17 02:16:18"},{"amount":-10,"id":"05b0f9f2cae54eedac21da53b87f61e3","bounsType":"积分抽奖","createDate":"2018-08-17 02:14:37"},{"amount":-10,"id":"8d834a7ed89d4fc7818cde5433e62e5f","bounsType":"积分抽奖","createDate":"2018-08-17 02:14:10"},{"amount":-10,"id":"d39fe4abed0c408a98bbb097f4c0f217","bounsType":"积分抽奖","createDate":"2018-08-17 02:13:08"},{"amount":-10,"id":"4ff7bcc5d79b495394e4704fba8749c5","bounsType":"积分抽奖","createDate":"2018-08-17 02:13:02"},{"amount":-10,"id":"18e0cb1def9347718fe1747851f4f24a","bounsType":"积分抽奖","createDate":"2018-08-17 02:11:57"},{"amount":-10,"id":"38796121124b495a9fc278adc4e98690","bounsType":"积分抽奖","createDate":"2018-08-17 02:11:25"}]
         * pageSize : 10
         */

        private String pageCount;
        private String last;
        private String totalCount;
        private String pageNo;
        private String pageSize;
        private List<UserBounsHistoryBean> userBounsHistory;

        public String getPageCount() {
            return pageCount;
        }

        public void setPageCount(String pageCount) {
            this.pageCount = pageCount;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public String getPageNo() {
            return pageNo;
        }

        public void setPageNo(String pageNo) {
            this.pageNo = pageNo;
        }

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        public List<UserBounsHistoryBean> getUserBounsHistory() {
            return userBounsHistory;
        }

        public void setUserBounsHistory(List<UserBounsHistoryBean> userBounsHistory) {
            this.userBounsHistory = userBounsHistory;
        }

        public static class UserBounsHistoryBean {
            /**
             * amount : 1.0
             * id : c1b103efbea949afae9ff1e6f54102bb
             * bounsType : 签到
             * createDate : 2018-08-18 00:38:06
             */

            private String amount;
            private String id;
            private String bounsType;
            private String createDate;

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getBounsType() {
                return bounsType;
            }

            public void setBounsType(String bounsType) {
                this.bounsType = bounsType;
            }

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }
        }
    }
}
