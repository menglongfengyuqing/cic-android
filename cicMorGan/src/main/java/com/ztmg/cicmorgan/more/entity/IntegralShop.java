package com.ztmg.cicmorgan.more.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dongdong on 2018/7/20.
 */

public class IntegralShop implements Serializable {


    /**
     * islock : 0
     * data : {"pageCount":7,"last":7,"pageNo":1,"awardlist":[{"awardId":"6e7f33650b7446e49b15054790b73230","docs":"红包仅限抵扣30天以上（不含30天）项目使用，立即发放","name":"10元抵用券","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-10%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg","needAmount":"200","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-10%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg"},{"awardId":"94e59f64c9c34957addc08c4749ce757","docs":"红包仅限抵扣30天以上（不含30天）项目使用，立即发放","name":"20元抵用券","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-20%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg","needAmount":"380","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-20%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg"},{"awardId":"797418516c1a4ac8956858d2ff884997","docs":"红包仅限抵扣30天以上（不含30天）项目使用，立即发放","name":"50元抵用券","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-50%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg","needAmount":"900","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-50%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg"},{"awardId":"72bef1daeed14d09986acbb777643016","docs":"&nbsp;金龙鱼 黄金产地长粒香大米5kg","name":"金龙鱼大米","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/1%E9%87%91%E9%BE%99%E9%B1%BC%E5%A4%A7%E7%B1%B3.jpg","needAmount":"1000","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/1%E9%87%91%E9%BE%99%E9%B1%BC%E5%A4%A7%E7%B1%B3.jpg"},{"awardId":"644b9c3e79b4456ca27f31e1c8d6fa20","docs":"王老吉凉茶310ml*24罐 整箱","name":"王老吉凉茶","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/2%E7%8E%8B%E8%80%81%E5%90%89%E5%87%89%E8%8C%B6.jpg","needAmount":"1300","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/2%E7%8E%8B%E8%80%81%E5%90%89%E5%87%89%E8%8C%B6.jpg"},{"awardId":"99ad212d3b7142c5a94598746f7103c4","docs":"维达(Vinda) 无芯卷纸 超韧3层100g卫生纸*40卷","name":"维达无芯卷纸","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/3%E7%BB%B4%E8%BE%BE%E6%97%A0%E8%8A%AF%E5%8D%B7%E7%BA%B8.jpg","needAmount":"1400","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/3%E7%BB%B4%E8%BE%BE%E6%97%A0%E8%8A%AF%E5%8D%B7%E7%BA%B8(1).jpg"},{"awardId":"0fa213eec7ee482084bb7d32486ecb3c","docs":"小米（MI）小米蓝牙耳机青春版","name":"小米青春版蓝牙耳机","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/4%E5%B0%8F%E7%B1%B3%E8%93%9D%E7%89%99%E8%80%B3%E6%9C%BA.jpg","needAmount":"1500","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/4%E5%B0%8F%E7%B1%B3%E8%93%9D%E7%89%99%E8%80%B3%E6%9C%BA.jpg"},{"awardId":"19f61fcb247f4402a594b6437c58e236","docs":"Nestle雀巢咖啡醇品黑咖啡罐装 500g 可冲277杯","name":"Nestle雀巢咖啡","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/5%E9%9B%80%E5%B7%A2%E5%92%96%E5%95%A1.jpg","needAmount":"1500","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/5%E9%9B%80%E5%B7%A2%E5%92%96%E5%95%A1.jpg"},{"awardId":"35e6e1935ae44b34b8fc5b0ad754818b","docs":"金士顿（Kingston）16GB U盘 USB3.0 DTSE9G2金属银色亮薄读速100MB/s","name":"金士顿（Kingston）16GB U盘","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/6%E9%87%91%E5%A3%AB%E9%A1%BFU%E7%9B%98.jpg","needAmount":"1500","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/6%E9%87%91%E5%A3%AB%E9%A1%BFU%E7%9B%98.jpg"},{"awardId":"6be4b6d1ea3e4a6cabe96e625136b386","docs":"小米（MI）小米移动电源2（10000mAh）","name":"小米（MI）小米移动电源2（10000mAh）","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/7%E5%B0%8F%E7%B1%B3%E7%A7%BB%E5%8A%A8%E7%94%B5%E6%BA%90.jpg","needAmount":"1500","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/7%E5%B0%8F%E7%B1%B3%E7%A7%BB%E5%8A%A8%E7%94%B5%E6%BA%90.jpg"}],"pageSize":10,"totalCount":66}
     * state : 0
     * message : 奖品信息查询成功
     */

    private String islock;
    private DataBean data;
    private String state;
    private String message;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getIslock() {
        return islock;
    }

    public void setIslock(String islock) {
        this.islock = islock;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * pageCount : 7
         * last : 7
         * pageNo : 1
         * awardlist : [{"awardId":"6e7f33650b7446e49b15054790b73230","docs":"红包仅限抵扣30天以上（不含30天）项目使用，立即发放","name":"10元抵用券","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-10%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg","needAmount":"200","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-10%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg"},{"awardId":"94e59f64c9c34957addc08c4749ce757","docs":"红包仅限抵扣30天以上（不含30天）项目使用，立即发放","name":"20元抵用券","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-20%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg","needAmount":"380","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-20%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg"},{"awardId":"797418516c1a4ac8956858d2ff884997","docs":"红包仅限抵扣30天以上（不含30天）项目使用，立即发放","name":"50元抵用券","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-50%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg","needAmount":"900","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-50%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg"},{"awardId":"72bef1daeed14d09986acbb777643016","docs":"&nbsp;金龙鱼 黄金产地长粒香大米5kg","name":"金龙鱼大米","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/1%E9%87%91%E9%BE%99%E9%B1%BC%E5%A4%A7%E7%B1%B3.jpg","needAmount":"1000","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/1%E9%87%91%E9%BE%99%E9%B1%BC%E5%A4%A7%E7%B1%B3.jpg"},{"awardId":"644b9c3e79b4456ca27f31e1c8d6fa20","docs":"王老吉凉茶310ml*24罐 整箱","name":"王老吉凉茶","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/2%E7%8E%8B%E8%80%81%E5%90%89%E5%87%89%E8%8C%B6.jpg","needAmount":"1300","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/2%E7%8E%8B%E8%80%81%E5%90%89%E5%87%89%E8%8C%B6.jpg"},{"awardId":"99ad212d3b7142c5a94598746f7103c4","docs":"维达(Vinda) 无芯卷纸 超韧3层100g卫生纸*40卷","name":"维达无芯卷纸","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/3%E7%BB%B4%E8%BE%BE%E6%97%A0%E8%8A%AF%E5%8D%B7%E7%BA%B8.jpg","needAmount":"1400","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/3%E7%BB%B4%E8%BE%BE%E6%97%A0%E8%8A%AF%E5%8D%B7%E7%BA%B8(1).jpg"},{"awardId":"0fa213eec7ee482084bb7d32486ecb3c","docs":"小米（MI）小米蓝牙耳机青春版","name":"小米青春版蓝牙耳机","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/4%E5%B0%8F%E7%B1%B3%E8%93%9D%E7%89%99%E8%80%B3%E6%9C%BA.jpg","needAmount":"1500","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/4%E5%B0%8F%E7%B1%B3%E8%93%9D%E7%89%99%E8%80%B3%E6%9C%BA.jpg"},{"awardId":"19f61fcb247f4402a594b6437c58e236","docs":"Nestle雀巢咖啡醇品黑咖啡罐装 500g 可冲277杯","name":"Nestle雀巢咖啡","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/5%E9%9B%80%E5%B7%A2%E5%92%96%E5%95%A1.jpg","needAmount":"1500","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/5%E9%9B%80%E5%B7%A2%E5%92%96%E5%95%A1.jpg"},{"awardId":"35e6e1935ae44b34b8fc5b0ad754818b","docs":"金士顿（Kingston）16GB U盘 USB3.0 DTSE9G2金属银色亮薄读速100MB/s","name":"金士顿（Kingston）16GB U盘","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/6%E9%87%91%E5%A3%AB%E9%A1%BFU%E7%9B%98.jpg","needAmount":"1500","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/6%E9%87%91%E5%A3%AB%E9%A1%BFU%E7%9B%98.jpg"},{"awardId":"6be4b6d1ea3e4a6cabe96e625136b386","docs":"小米（MI）小米移动电源2（10000mAh）","name":"小米（MI）小米移动电源2（10000mAh）","imgWeb":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/7%E5%B0%8F%E7%B1%B3%E7%A7%BB%E5%8A%A8%E7%94%B5%E6%BA%90.jpg","needAmount":"1500","imgWap":"http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/7%E5%B0%8F%E7%B1%B3%E7%A7%BB%E5%8A%A8%E7%94%B5%E6%BA%90.jpg"}]
         * pageSize : 10
         * totalCount : 66
         */

        private String pageCount;
        private String last;
        private String pageNo;
        private String pageSize;
        private String totalCount;
        private List<AwardlistBean> awardlist;

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

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public List<AwardlistBean> getAwardlist() {
            return awardlist;
        }

        public void setAwardlist(List<AwardlistBean> awardlist) {
            this.awardlist = awardlist;
        }

        public static class AwardlistBean {
            /**
             * awardId : 6e7f33650b7446e49b15054790b73230
             * docs : 红包仅限抵扣30天以上（不含30天）项目使用，立即发放
             * name : 10元抵用券
             * imgWeb : http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-10%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg
             * needAmount : 200
             * imgWap : http://erp.cicmorgan.com/erp/userfiles/sharedFiles/images/photo/2018/07/-10%E5%85%83%E4%BC%98%E6%83%A0%E5%88%B8.jpg
             */

            private String awardId;
            private String docs;
            private String name;
            private String imgWeb;
            private String needAmount;
            private String imgWap;

            public String getAwardId() {
                return awardId;
            }

            public void setAwardId(String awardId) {
                this.awardId = awardId;
            }

            public String getDocs() {
                return docs;
            }

            public void setDocs(String docs) {
                this.docs = docs;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImgWeb() {
                return imgWeb;
            }

            public void setImgWeb(String imgWeb) {
                this.imgWeb = imgWeb;
            }

            public String getNeedAmount() {
                return needAmount;
            }

            public void setNeedAmount(String needAmount) {
                this.needAmount = needAmount;
            }

            public String getImgWap() {
                return imgWap;
            }

            public void setImgWap(String imgWap) {
                this.imgWap = imgWap;
            }
        }
    }
}
