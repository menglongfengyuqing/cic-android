package com.ztmg.cicmorgan.entity;

import java.io.Serializable;

/**
 * Created by dongdong on 2018/6/29.
 */

public class PromptEntity implements Serializable {


    /**
     * data : {"amount":1000,"remark":null,"state":"1","projectName":"供应链测试项目a","projectSn":"MTH201806280001"}
     * state : 0
     * message : 出借订单结果查询
     */

    private DataBean data;
    private String state;
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * amount : 1000
         * remark : null
         * state : 1
         * projectName : 供应链测试项目a
         * projectSn : MTH201806280001
         */

        private String amount;
        private String remark;
        private String state;
        private String projectName;
        private String projectSn;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public Object getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

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
    }
}
