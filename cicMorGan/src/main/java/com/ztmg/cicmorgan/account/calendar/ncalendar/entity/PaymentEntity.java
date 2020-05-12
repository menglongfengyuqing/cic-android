package com.ztmg.cicmorgan.account.calendar.ncalendar.entity;

import java.io.Serializable;
import java.util.List;

public class PaymentEntity implements Serializable {
    /**
     * message : 接口请求成功！
     * data : {"plans":[{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":53.42,"remainingRepayAmount":5231.55,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":53.42,"remainingRepayAmount":5231.55,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":53.42,"remainingRepayAmount":5231.55,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":106.85,"remainingRepayAmount":10463.01,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":10.68,"remainingRepayAmount":1046.34,"status":"2","type":"2"}],"pageCount":5,"count":56,"pageNo":1,"pageSize":13}
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
         * plans : [{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":53.42,"remainingRepayAmount":5231.55,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":53.42,"remainingRepayAmount":5231.55,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":53.42,"remainingRepayAmount":5231.55,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":21.37,"remainingRepayAmount":2092.6,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":106.85,"remainingRepayAmount":10463.01,"status":"2","type":"2"},{"projectName":"某汽车销售公司借款0111","projectSn":"201711291","repaymentDate":"2018-08-07","nowRepayAmount":10.68,"remainingRepayAmount":1046.34,"status":"2","type":"2"}]
         * pageCount : 5
         * count : 56
         * pageNo : 1
         * pageSize : 13
         */

        private String pageCount;
        private String count;
        private String pageNo;
        private String pageSize;
        private List<PlansBean> plans;

        public String getPageCount() {
            return pageCount;
        }

        public void setPageCount(String pageCount) {
            this.pageCount = pageCount;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
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

        public List<PlansBean> getPlans() {
            return plans;
        }

        public void setPlans(List<PlansBean> plans) {
            this.plans = plans;
        }

        public static class PlansBean {
            /**
             * projectName : 某汽车销售公司借款0111
             * projectSn : 201711291
             * repaymentDate : 2018-08-07
             * nowRepayAmount : 53.42
             * remainingRepayAmount : 5231.55
             * status : 2
             * type : 2
             */

            private String projectName;
            private String projectSn;
            private String repaymentDate;
            private String nowRepayAmount;
            private String remainingRepayAmount;
            private String status;
            private String type;

            public String getProjectName() {
                return projectName;
            }

            public void setProjectName(String projectName) {
                this.projectName = projectName;
            }

            public String getProjectSn() {
                return projectSn;
            }

            public void setProjectSn(String projectSn) {
                this.projectSn = projectSn;
            }

            public String getRepaymentDate() {
                return repaymentDate;
            }

            public void setRepaymentDate(String repaymentDate) {
                this.repaymentDate = repaymentDate;
            }

            public String getNowRepayAmount() {
                return nowRepayAmount;
            }

            public void setNowRepayAmount(String nowRepayAmount) {
                this.nowRepayAmount = nowRepayAmount;
            }

            public String getRemainingRepayAmount() {
                return remainingRepayAmount;
            }

            public void setRemainingRepayAmount(String remainingRepayAmount) {
                this.remainingRepayAmount = remainingRepayAmount;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
    //	private String projectName;
    //	private String nowRepayAmount;
    //	private String remainingRepayAmount;
    //	private String status;
    //	private String type;
    //	private String projectSn;

}
