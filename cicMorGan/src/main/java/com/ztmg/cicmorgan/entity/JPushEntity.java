package com.ztmg.cicmorgan.entity;

import java.io.Serializable;

public class JPushEntity implements Serializable {

    /**
     * ad_id : 924133542
     * m_content : {"ad_t":0,"n_alert_type":7,"n_category":"","n_content":"你好，中投摩根提醒你，有新的优惠活动。","n_extras":{"path":"https://www.cicmorgan.com/zt_novice_activity.html?app=1"},"n_flag":1,"n_priority":0,"n_style":0,"n_title":""}
     * n_builder_id : 0
     * n_only : 1
     * show_type : 4
     */

    private String ad_id;
    private MContentBean m_content;
    private int n_builder_id;
    private int n_only;
    private int show_type;

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        this.ad_id = ad_id;
    }

    public MContentBean getM_content() {
        return m_content;
    }

    public void setM_content(MContentBean m_content) {
        this.m_content = m_content;
    }

    public int getN_builder_id() {
        return n_builder_id;
    }

    public void setN_builder_id(int n_builder_id) {
        this.n_builder_id = n_builder_id;
    }

    public int getN_only() {
        return n_only;
    }

    public void setN_only(int n_only) {
        this.n_only = n_only;
    }

    public int getShow_type() {
        return show_type;
    }

    public void setShow_type(int show_type) {
        this.show_type = show_type;
    }

    public static class MContentBean {
        /**
         * ad_t : 0
         * n_alert_type : 7
         * n_category :
         * n_content : 你好，中投摩根提醒你，有新的优惠活动。
         * n_extras : {"path":"https://www.cicmorgan.com/zt_novice_activity.html?app=1"}
         * n_flag : 1
         * n_priority : 0
         * n_style : 0
         * n_title :
         */

        private int ad_t;
        private int n_alert_type;
        private String n_category;
        private String n_content;
        private NExtrasBean n_extras;
        private int n_flag;
        private int n_priority;
        private int n_style;
        private String n_title;

        public int getAd_t() {
            return ad_t;
        }

        public void setAd_t(int ad_t) {
            this.ad_t = ad_t;
        }

        public int getN_alert_type() {
            return n_alert_type;
        }

        public void setN_alert_type(int n_alert_type) {
            this.n_alert_type = n_alert_type;
        }

        public String getN_category() {
            return n_category;
        }

        public void setN_category(String n_category) {
            this.n_category = n_category;
        }

        public String getN_content() {
            return n_content;
        }

        public void setN_content(String n_content) {
            this.n_content = n_content;
        }

        public NExtrasBean getN_extras() {
            return n_extras;
        }

        public void setN_extras(NExtrasBean n_extras) {
            this.n_extras = n_extras;
        }

        public int getN_flag() {
            return n_flag;
        }

        public void setN_flag(int n_flag) {
            this.n_flag = n_flag;
        }

        public int getN_priority() {
            return n_priority;
        }

        public void setN_priority(int n_priority) {
            this.n_priority = n_priority;
        }

        public int getN_style() {
            return n_style;
        }

        public void setN_style(int n_style) {
            this.n_style = n_style;
        }

        public String getN_title() {
            return n_title;
        }

        public void setN_title(String n_title) {
            this.n_title = n_title;
        }

        public static class NExtrasBean {
            /**
             * path : https://www.cicmorgan.com/zt_novice_activity.html?app=1
             */

            private String path;

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }
        }
    }
}
