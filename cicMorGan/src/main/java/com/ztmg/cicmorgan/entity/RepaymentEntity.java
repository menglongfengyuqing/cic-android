package com.ztmg.cicmorgan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ${dong} on 2018/5/30.
 * <p>
 * 回款计划
 */

public class RepaymentEntity implements Serializable {


    /**
     * data : {"userPlanList":[{"principal":"2","amount":"7.40","repaymentDate":"2018-06-30","state":"2"},{"principal":"2","amount":"7.40","repaymentDate":"2018-07-30","state":"2"},{"principal":"2","amount":"7.70","repaymentDate":"2018-08-29","state":"2"},{"principal":"1","amount":1000,"repaymentDate":"2018-08-29","state":"2"}]}
     * state : 0
     * message : 展示金额回款成功
     */
    private DataEntity data;
    private String state;
    private String message;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataEntity getData() {
        return data;
    }

    public String getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public class DataEntity {
        /**
         * userPlanList : [{"principal":"2","amount":"7.40","repaymentDate":"2018-06-30","state":"2"},{"principal":"2","amount":"7.40","repaymentDate":"2018-07-30","state":"2"},{"principal":"2","amount":"7.70","repaymentDate":"2018-08-29","state":"2"},{"principal":"1","amount":1000,"repaymentDate":"2018-08-29","state":"2"}]
         */
        private List<UserPlanListEntity> userPlanList;

        public void setUserPlanList(List<UserPlanListEntity> userPlanList) {
            this.userPlanList = userPlanList;
        }

        public List<UserPlanListEntity> getUserPlanList() {
            return userPlanList;
        }

        public class UserPlanListEntity {
            /**
             * principal : 2
             * amount : 7.40
             * repaymentDate : 2018-06-30
             * state : 2
             */
            private String principal;
            private String amount;
            private String repaymentDate;
            private String state;

            public void setPrincipal(String principal) {
                this.principal = principal;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public void setRepaymentDate(String repaymentDate) {
                this.repaymentDate = repaymentDate;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getPrincipal() {
                return principal;
            }

            public String getAmount() {
                return amount;
            }

            public String getRepaymentDate() {
                return repaymentDate;
            }

            public String getState() {
                return state;
            }
        }
    }
}
