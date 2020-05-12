package com.ztmg.cicmorgan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dongdong on 2018/5/23.
 * 新版详情的bean
 */

public class InvestmentDetailEntity implements Serializable {


    /**
     * message : 项目信息查询成功
     * data : {"borrowerRegisterDate":null,"stepamount":100,"isCanUseCoupon":"0","projectProductType":"2","ztmgLoanBasicInfoEntity":{"id":"159f01f6fb3548bfa60192f1f6e32ccb","isNewRecord":false,"remarks":null,"createDate":"2018-05-07 15:27:46","updateDate":"2018-05-16 18:07:32","creditUserId":"7511338568033266260","province":"天津市","city":"天津市","county":"河西区","street":"三十年河东三十年河西","contributedCapital":"2503187","industry":"租赁和商务服务业","annualRevenue":"2503187","liabilities":"2503187","creditInformation":"周文敏测试","otherCreditInformation":"多谢","remark":"借款人基本信息","shareholdersJsonArrayStr":null,"creditAnnexFileJsonArrayStr":null,"ztmgLoanShareholdersInfos":[{"id":"b961deca109a4a778454e799879efc00","isNewRecord":false,"remarks":null,"createDate":"2018-05-07 15:27:47","updateDate":"2018-05-07 15:27:48","loanBasicId":"159f01f6fb3548bfa60192f1f6e32ccb","shareholdersType":"SHAREHOLDERS_TYPE_02","shareholdersCertType":"SHAREHOLDERS_CERT_TYPE_01","shareholdersName":"安抚","remark":"借款人股东信息"}],"creditAnnexFilePojosList":[{"id":"23c9afc0aaaa4e8d85b24d30e528f050","otherId":"7511338568033266260","url":"2018/5/7/IMG_ffa5c88f72484e338816b0d13dcabc56.png","type":"18"}]},"creditName":"北京爱XXXXX有限公司","endDate":"2018-10-29","purpose":"发送","countdowndate":"2018-07-31 00:00:00","amount":1000000,"borrowerCompanyName":"郑州小XXXXX公司  ","replaceRepayCompanyName":"北京爱XXXXX有限公司","isCanUsePlusCoupon":"0","rate":8,"maxamount":100000,"currentamount":407000,"isNewType":"3","litigationSituation":"无涉诉","borrowerRegisterAmount":null,"platformOverdueSituation":"在本平台历史逾期0次，当前逾期金额0.00万元","agentPersonName":null,"sn":"AQ20180510001","percentage":"40.7%","minamount":1000,"administrativePunishmentSituation":"无","bidList":[{"investDate":"2018-05-22","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-22","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-22","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-16","userPhone":"170****2222","investAmount":80000},{"investDate":"2018-05-16","userPhone":"135****9214","investAmount":10000},{"investDate":"2018-05-15","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-15","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-15","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-15","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"156****0350","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":2000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-13","userPhone":"187****4032","investAmount":2000},{"investDate":"2018-05-12","userPhone":"187****4032","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"156****3272","investAmount":50000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"131****7840","investAmount":100000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"156****8547","investAmount":50000},{"investDate":"2018-05-11","userPhone":"155****3027","investAmount":30000},{"investDate":"2018-05-11","userPhone":"155****3027","investAmount":20000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-10","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":10000},{"investDate":"2018-05-10","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":50000}],"balanceamount":"593000.00","borrowerRegistAddress":null,"businessFinancialSituation":"良好","address":"天津市天津市XXXXX","repaytype":"分期付息到期还本","loandate":"2018-07-31 00:00:00","proState":"4","sourceOfRepayment":"阿道夫","projectName":"爱亲母亲节测试项目","creditUrl":"www.baidu.com?middlemenId=5685145015583919274","projectcase":"","abilityToRepaySituation":"月收入无变化","span":90}
     * state : 0
     */

    public String message;
    public DataBean data;
    public String state;

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
         * borrowerRegisterDate : null
         * stepamount : 100.0
         * isCanUseCoupon : 0
         * projectProductType : 2   标的产品类型 1：安心投类，2：供应链类
         * ztmgLoanBasicInfoEntity : {"id":"159f01f6fb3548bfa60192f1f6e32ccb","isNewRecord":false,"remarks":null,"createDate":"2018-05-07 15:27:46","updateDate":"2018-05-16 18:07:32","creditUserId":"7511338568033266260","province":"天津市","city":"天津市","county":"河西区","street":"三十年河东三十年河西","contributedCapital":"2503187","industry":"租赁和商务服务业","annualRevenue":"2503187","liabilities":"2503187","creditInformation":"周文敏测试","otherCreditInformation":"多谢","remark":"借款人基本信息","shareholdersJsonArrayStr":null,"creditAnnexFileJsonArrayStr":null,"ztmgLoanShareholdersInfos":[{"id":"b961deca109a4a778454e799879efc00","isNewRecord":false,"remarks":null,"createDate":"2018-05-07 15:27:47","updateDate":"2018-05-07 15:27:48","loanBasicId":"159f01f6fb3548bfa60192f1f6e32ccb","shareholdersType":"SHAREHOLDERS_TYPE_02","shareholdersCertType":"SHAREHOLDERS_CERT_TYPE_01","shareholdersName":"安抚","remark":"借款人股东信息"}],"creditAnnexFilePojosList":[{"id":"23c9afc0aaaa4e8d85b24d30e528f050","otherId":"7511338568033266260","url":"2018/5/7/IMG_ffa5c88f72484e338816b0d13dcabc56.png","type":"18"}]}
         * creditName : 北京爱XXXXX有限公司
         * endDate : 2018-10-29
         * purpose : 发送
         * countdowndate : 2018-07-31 00:00:00
         * amount : 1000000.0
         * borrowerCompanyName : 郑州小XXXXX公司
         * replaceRepayCompanyName : 北京爱XXXXX有限公司
         * isCanUsePlusCoupon : 0
         * level: "4"
         * rate : 8.0
         * maxamount : 100000.0
         * currentamount : 407000.0
         * isNewType : 3
         * litigationSituation : 无涉诉
         * borrowerRegisterAmount : null
         * platformOverdueSituation : 在本平台历史逾期0次，当前逾期金额0.00万元
         * agentPersonName : null
         * sn : AQ20180510001
         * percentage : 40.7%
         * minamount : 1000.0
         * administrativePunishmentSituation : 无
         * bidList : [{"investDate":"2018-05-22","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-22","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-22","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-16","userPhone":"170****2222","investAmount":80000},{"investDate":"2018-05-16","userPhone":"135****9214","investAmount":10000},{"investDate":"2018-05-15","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-15","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-15","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-15","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"156****0350","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":2000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"135****9214","investAmount":1000},{"investDate":"2018-05-14","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-13","userPhone":"187****4032","investAmount":2000},{"investDate":"2018-05-12","userPhone":"187****4032","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"156****3272","investAmount":50000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"131****7840","investAmount":100000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-11","userPhone":"156****8547","investAmount":50000},{"investDate":"2018-05-11","userPhone":"155****3027","investAmount":30000},{"investDate":"2018-05-11","userPhone":"155****3027","investAmount":20000},{"investDate":"2018-05-11","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-10","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":5000},{"investDate":"2018-05-10","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":10000},{"investDate":"2018-05-10","userPhone":"183****7679","investAmount":1000},{"investDate":"2018-05-10","userPhone":"170****2222","investAmount":50000}]
         * balanceamount : 593000.00
         * borrowerRegistAddress : null
         * businessFinancialSituation : 良好
         * address : 天津市天津市XXXXX
         * repaytype : 分期付息到期还本
         * loandate : 2018-07-31 00:00:00
         * proState : 4
         * sourceOfRepayment : 阿道夫
         * projectName : 爱亲母亲节测试项目
         * creditUrl : www.baidu.com?middlemenId=5685145015583919274
         * projectcase :
         * abilityToRepaySituation : 月收入无变化
         * span : 90
         */

        public String borrowerRegisterDate;
        public String stepamount;
        public String isCanUseCoupon;
        public String projectProductType;
        public ZtmgLoanBasicInfoEntityBean ztmgLoanBasicInfoEntity;
        public String creditName;
        public String endDate;
        public String purpose;
        public String countdowndate;
        public String amount;
        public String borrowerCompanyName;
        public String replaceRepayCompanyName;
        public String isCanUsePlusCoupon;
        public String rate;
        public String interestRateIncrease;
        public String maxamount;
        public String currentamount;
        public String isNewType;
        public String litigationSituation;
        public String borrowerRegisterAmount;
        public String platformOverdueSituation;
        public String agentPersonName;
        public String sn;
        public String percentage;
        public String minamount;
        public String administrativePunishmentSituation;
        public String balanceamount;
        public String borrowerRegistAddress;
        public String businessFinancialSituation;
        public String address;
        public String repaytype;
        public String loandate;
        public String proState;
        public String sourceOfRepayment;
        public String repaymentGuaranteeMeasures;
        public String projectName;
        public String creditUrl;
        public String projectcase;
        public String abilityToRepaySituation;
        public String level;
        public int span;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public List<BidListBean> bidList;

        public String getBorrowerRegisterDate() {
            return borrowerRegisterDate;
        }

        public void setBorrowerRegisterDate(String borrowerRegisterDate) {
            this.borrowerRegisterDate = borrowerRegisterDate;
        }

        public String getStepamount() {
            return stepamount;
        }

        public void setStepamount(String stepamount) {
            this.stepamount = stepamount;
        }

        public String getIsCanUseCoupon() {
            return isCanUseCoupon;
        }

        public void setIsCanUseCoupon(String isCanUseCoupon) {
            this.isCanUseCoupon = isCanUseCoupon;
        }

        public String getProjectProductType() {
            return projectProductType;
        }

        public void setProjectProductType(String projectProductType) {
            this.projectProductType = projectProductType;
        }

        public ZtmgLoanBasicInfoEntityBean getZtmgLoanBasicInfoEntity() {
            return ztmgLoanBasicInfoEntity;
        }

        public void setZtmgLoanBasicInfoEntity(ZtmgLoanBasicInfoEntityBean ztmgLoanBasicInfoEntity) {
            this.ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoEntity;
        }

        public String getCreditName() {
            return creditName;
        }

        public void setCreditName(String creditName) {
            this.creditName = creditName;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }

        public String getCountdowndate() {
            return countdowndate;
        }

        public void setCountdowndate(String countdowndate) {
            this.countdowndate = countdowndate;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getBorrowerCompanyName() {
            return borrowerCompanyName;
        }

        public void setBorrowerCompanyName(String borrowerCompanyName) {
            this.borrowerCompanyName = borrowerCompanyName;
        }

        public String getReplaceRepayCompanyName() {
            return replaceRepayCompanyName;
        }

        public void setReplaceRepayCompanyName(String replaceRepayCompanyName) {
            this.replaceRepayCompanyName = replaceRepayCompanyName;
        }

        public String getIsCanUsePlusCoupon() {
            return isCanUsePlusCoupon;
        }

        public void setIsCanUsePlusCoupon(String isCanUsePlusCoupon) {
            this.isCanUsePlusCoupon = isCanUsePlusCoupon;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getInterestRateIncrease() {
            return interestRateIncrease;
        }

        public void setInterestRateIncrease(String interestRateIncrease) {
            this.interestRateIncrease = interestRateIncrease;
        }

        public String getMaxamount() {
            return maxamount;
        }

        public void setMaxamount(String maxamount) {
            this.maxamount = maxamount;
        }

        public String getCurrentamount() {
            return currentamount;
        }

        public void setCurrentamount(String currentamount) {
            this.currentamount = currentamount;
        }

        public String getIsNewType() {
            return isNewType;
        }

        public void setIsNewType(String isNewType) {
            this.isNewType = isNewType;
        }

        public String getLitigationSituation() {
            return litigationSituation;
        }

        public void setLitigationSituation(String litigationSituation) {
            this.litigationSituation = litigationSituation;
        }

        public String getBorrowerRegisterAmount() {
            return borrowerRegisterAmount;
        }

        public void setBorrowerRegisterAmount(String borrowerRegisterAmount) {
            this.borrowerRegisterAmount = borrowerRegisterAmount;
        }

        public String getPlatformOverdueSituation() {
            return platformOverdueSituation;
        }

        public void setPlatformOverdueSituation(String platformOverdueSituation) {
            this.platformOverdueSituation = platformOverdueSituation;
        }

        public String getAgentPersonName() {
            return agentPersonName;
        }

        public void setAgentPersonName(String agentPersonName) {
            this.agentPersonName = agentPersonName;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getPercentage() {
            return percentage;
        }

        public void setPercentage(String percentage) {
            this.percentage = percentage;
        }

        public String getMinamount() {
            return minamount;
        }

        public void setMinamount(String minamount) {
            this.minamount = minamount;
        }

        public String getAdministrativePunishmentSituation() {
            return administrativePunishmentSituation;
        }

        public void setAdministrativePunishmentSituation(String administrativePunishmentSituation) {
            this.administrativePunishmentSituation = administrativePunishmentSituation;
        }

        public String getBalanceamount() {
            return balanceamount;
        }

        public void setBalanceamount(String balanceamount) {
            this.balanceamount = balanceamount;
        }

        public String getBorrowerRegistAddress() {
            return borrowerRegistAddress;
        }

        public void setBorrowerRegistAddress(String borrowerRegistAddress) {
            this.borrowerRegistAddress = borrowerRegistAddress;
        }

        public String getBusinessFinancialSituation() {
            return businessFinancialSituation;
        }

        public void setBusinessFinancialSituation(String businessFinancialSituation) {
            this.businessFinancialSituation = businessFinancialSituation;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRepaytype() {
            return repaytype;
        }

        public void setRepaytype(String repaytype) {
            this.repaytype = repaytype;
        }

        public String getLoandate() {
            return loandate;
        }

        public void setLoandate(String loandate) {
            this.loandate = loandate;
        }

        public String getProState() {
            return proState;
        }

        public void setProState(String proState) {
            this.proState = proState;
        }

        public String getSourceOfRepayment() {
            return sourceOfRepayment;
        }

        public void setSourceOfRepayment(String sourceOfRepayment) {
            this.sourceOfRepayment = sourceOfRepayment;
        }

        public String getRepaymentGuaranteeMeasures() {
            return repaymentGuaranteeMeasures;
        }

        public void setRepaymentGuaranteeMeasures(String repaymentGuaranteeMeasures) {
            this.repaymentGuaranteeMeasures = repaymentGuaranteeMeasures;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getCreditUrl() {
            return creditUrl;
        }

        public void setCreditUrl(String creditUrl) {
            this.creditUrl = creditUrl;
        }

        public String getProjectcase() {
            return projectcase;
        }

        public void setProjectcase(String projectcase) {
            this.projectcase = projectcase;
        }

        public String getAbilityToRepaySituation() {
            return abilityToRepaySituation;
        }

        public void setAbilityToRepaySituation(String abilityToRepaySituation) {
            this.abilityToRepaySituation = abilityToRepaySituation;
        }

        public int getSpan() {
            return span;
        }

        public void setSpan(int span) {
            this.span = span;
        }

        public List<BidListBean> getBidList() {
            return bidList;
        }

        public void setBidList(List<BidListBean> bidList) {
            this.bidList = bidList;
        }

        public static class ZtmgLoanBasicInfoEntityBean {
            /**
             * id : 159f01f6fb3548bfa60192f1f6e32ccb
             * isNewRecord : false
             * remarks : null
             * createDate : 2018-05-07 15:27:46
             * updateDate : 2018-05-16 18:07:32
             * creditUserId : 7511338568033266260
             * province : 天津市
             * city : 天津市
             * county : 河西区
             * street : 三十年河东三十年河西
             * contributedCapital : 2503187
             * industry : 租赁和商务服务业
             * annualRevenue : 2503187
             * liabilities : 2503187
             * creditInformation : 周文敏测试
             * otherCreditInformation : 多谢
             * remark : 借款人基本信息
             * shareholdersJsonArrayStr : null
             * creditAnnexFileJsonArrayStr : null
             * ztmgLoanShareholdersInfos : [{"id":"b961deca109a4a778454e799879efc00","isNewRecord":false,"remarks":null,"createDate":"2018-05-07 15:27:47","updateDate":"2018-05-07 15:27:48","loanBasicId":"159f01f6fb3548bfa60192f1f6e32ccb","shareholdersType":"SHAREHOLDERS_TYPE_02","shareholdersCertType":"SHAREHOLDERS_CERT_TYPE_01","shareholdersName":"安抚","remark":"借款人股东信息"}]
             * creditAnnexFilePojosList : [{"id":"23c9afc0aaaa4e8d85b24d30e528f050","otherId":"7511338568033266260","url":"2018/5/7/IMG_ffa5c88f72484e338816b0d13dcabc56.png","type":"18"}]
             */

            public String id;
            public String isNewRecord;
            public String remarks;
            public String createDate;
            public String updateDate;
            public String creditUserId;
            public String province;
            public String city;
            public String county;
            public String street;
            public String contributedCapital;
            public String industry;
            public String annualRevenue;
            public String liabilities;
            public String creditInformation;
            public String otherCreditInformation;
            public String remark;
            public String shareholdersJsonArrayStr;
            public String creditAnnexFileJsonArrayStr;
            public String registeredCapital; // 注册资本(元).
            public String operName; // 法定代表人.
            public String registeredAddress; // 注册地址.
            public String setUpTime; // 成立时间.
            public String scope; // 成立时间.
            public List<ZtmgLoanShareholdersInfosBean> ztmgLoanShareholdersInfos;
            public List<CreditAnnexFilePojosListBean> creditAnnexFilePojosList;


            public String getScope() {
                return scope;
            }

            public void setScope(String scope) {
                this.scope = scope;
            }

            public String getRegisteredCapital() {
                return registeredCapital;
            }

            public void setRegisteredCapital(String registeredCapital) {
                this.registeredCapital = registeredCapital;
            }

            public String getOperName() {
                return operName;
            }

            public void setOperName(String operName) {
                this.operName = operName;
            }

            public String getRegisteredAddress() {
                return registeredAddress;
            }

            public void setRegisteredAddress(String registeredAddress) {
                this.registeredAddress = registeredAddress;
            }

            public String getSetUpTime() {
                return setUpTime;
            }

            public void setSetUpTime(String setUpTime) {
                this.setUpTime = setUpTime;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIsNewRecord() {
                return isNewRecord;
            }

            public void setIsNewRecord(String isNewRecord) {
                this.isNewRecord = isNewRecord;
            }

            public String getRemarks() {
                return remarks;
            }

            public void setRemarks(String remarks) {
                this.remarks = remarks;
            }

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }

            public String getUpdateDate() {
                return updateDate;
            }

            public void setUpdateDate(String updateDate) {
                this.updateDate = updateDate;
            }

            public String getCreditUserId() {
                return creditUserId;
            }

            public void setCreditUserId(String creditUserId) {
                this.creditUserId = creditUserId;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCounty() {
                return county;
            }

            public void setCounty(String county) {
                this.county = county;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getContributedCapital() {
                return contributedCapital;
            }

            public void setContributedCapital(String contributedCapital) {
                this.contributedCapital = contributedCapital;
            }

            public String getIndustry() {
                return industry;
            }

            public void setIndustry(String industry) {
                this.industry = industry;
            }

            public String getAnnualRevenue() {
                return annualRevenue;
            }

            public void setAnnualRevenue(String annualRevenue) {
                this.annualRevenue = annualRevenue;
            }

            public String getLiabilities() {
                return liabilities;
            }

            public void setLiabilities(String liabilities) {
                this.liabilities = liabilities;
            }

            public String getCreditInformation() {
                return creditInformation;
            }

            public void setCreditInformation(String creditInformation) {
                this.creditInformation = creditInformation;
            }

            public String getOtherCreditInformation() {
                return otherCreditInformation;
            }

            public void setOtherCreditInformation(String otherCreditInformation) {
                this.otherCreditInformation = otherCreditInformation;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getShareholdersJsonArrayStr() {
                return shareholdersJsonArrayStr;
            }

            public void setShareholdersJsonArrayStr(String shareholdersJsonArrayStr) {
                this.shareholdersJsonArrayStr = shareholdersJsonArrayStr;
            }

            public String getCreditAnnexFileJsonArrayStr() {
                return creditAnnexFileJsonArrayStr;
            }

            public void setCreditAnnexFileJsonArrayStr(String creditAnnexFileJsonArrayStr) {
                this.creditAnnexFileJsonArrayStr = creditAnnexFileJsonArrayStr;
            }

            public List<ZtmgLoanShareholdersInfosBean> getZtmgLoanShareholdersInfos() {
                return ztmgLoanShareholdersInfos;
            }

            public void setZtmgLoanShareholdersInfos(List<ZtmgLoanShareholdersInfosBean> ztmgLoanShareholdersInfos) {
                this.ztmgLoanShareholdersInfos = ztmgLoanShareholdersInfos;
            }

            public List<CreditAnnexFilePojosListBean> getCreditAnnexFilePojosList() {
                return creditAnnexFilePojosList;
            }

            public void setCreditAnnexFilePojosList(List<CreditAnnexFilePojosListBean> creditAnnexFilePojosList) {
                this.creditAnnexFilePojosList = creditAnnexFilePojosList;
            }

            public static class ZtmgLoanShareholdersInfosBean {
                /**
                 * id : b961deca109a4a778454e799879efc00
                 * isNewRecord : false
                 * remarks : null
                 * createDate : 2018-05-07 15:27:47
                 * updateDate : 2018-05-07 15:27:48
                 * loanBasicId : 159f01f6fb3548bfa60192f1f6e32ccb
                 * shareholdersType : SHAREHOLDERS_TYPE_02
                 * shareholdersCertType : SHAREHOLDERS_CERT_TYPE_01
                 * shareholdersName : 安抚
                 * remark : 借款人股东信息
                 */

                public String id;
                public String isNewRecord;
                public String remarks;
                public String createDate;
                public String updateDate;
                public String loanBasicId;
                public String shareholdersType;
                public String shareholdersCertType;
                public String shareholdersName;
                public String remark;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getIsNewRecord() {
                    return isNewRecord;
                }

                public void setIsNewRecord(String isNewRecord) {
                    this.isNewRecord = isNewRecord;
                }

                public String getRemarks() {
                    return remarks;
                }

                public void setRemarks(String remarks) {
                    this.remarks = remarks;
                }

                public String getCreateDate() {
                    return createDate;
                }

                public void setCreateDate(String createDate) {
                    this.createDate = createDate;
                }

                public String getUpdateDate() {
                    return updateDate;
                }

                public void setUpdateDate(String updateDate) {
                    this.updateDate = updateDate;
                }

                public String getLoanBasicId() {
                    return loanBasicId;
                }

                public void setLoanBasicId(String loanBasicId) {
                    this.loanBasicId = loanBasicId;
                }

                public String getShareholdersType() {
                    return shareholdersType;
                }

                public void setShareholdersType(String shareholdersType) {
                    this.shareholdersType = shareholdersType;
                }

                public String getShareholdersCertType() {
                    return shareholdersCertType;
                }

                public void setShareholdersCertType(String shareholdersCertType) {
                    this.shareholdersCertType = shareholdersCertType;
                }

                public String getShareholdersName() {
                    return shareholdersName;
                }

                public void setShareholdersName(String shareholdersName) {
                    this.shareholdersName = shareholdersName;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }
            }

            public static class CreditAnnexFilePojosListBean {
                /**
                 * id : 23c9afc0aaaa4e8d85b24d30e528f050
                 * otherId : 7511338568033266260
                 * url : 2018/5/7/IMG_ffa5c88f72484e338816b0d13dcabc56.png
                 * type : 18
                 */

                public String id;
                public String otherId;
                public String url;
                public String type;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getOtherId() {
                    return otherId;
                }

                public void setOtherId(String otherId) {
                    this.otherId = otherId;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
        }

        public static class BidListBean {
            /**
             * investDate : 2018-05-22
             * userPhone : 183****7679
             * investAmount : 1000.0
             */

            public String investDate;
            public String userPhone;
            public String investAmount;

            public String getInvestDate() {
                return investDate;
            }

            public void setInvestDate(String investDate) {
                this.investDate = investDate;
            }

            public String getUserPhone() {
                return userPhone;
            }

            public void setUserPhone(String userPhone) {
                this.userPhone = userPhone;
            }

            public String getInvestAmount() {
                return investAmount;
            }

            public void setInvestAmount(String investAmount) {
                this.investAmount = investAmount;
            }
        }
    }
}
