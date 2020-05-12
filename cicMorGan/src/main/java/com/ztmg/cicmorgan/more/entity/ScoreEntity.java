package com.ztmg.cicmorgan.more.entity;

import java.io.Serializable;

/**
 * 我的积分明细  dong dong
 * on 2018/9/4.
 */

public class ScoreEntity implements Serializable {


    /**
     * data : {"score":50400,"name":"13681297821","createDate":1530525805000}
     * state : 0
     * message : 用户积分信息查询成功
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
         * score : 50400
         * name : 13681297821
         * createDate : 1530525805000
         */

        private String score;
        private String name;
        private String createDate;

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }
    }
}
