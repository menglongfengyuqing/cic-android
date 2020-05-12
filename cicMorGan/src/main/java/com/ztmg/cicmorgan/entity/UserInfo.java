package com.ztmg.cicmorgan.entity;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author pc
 */
public class UserInfo implements Serializable {
    private String token;
    private String phone;
    private String bankPas;//交易密码
    private String email;//邮箱
    private String realName;//真实姓名
    private String IdCard;//身份证号
    private String isBindBank;//是否绑定银行卡
    private String gesturePwd;//是否设置过手势密码
    private String address;//联系地址
    private String emergencyUser;//紧急联系人
    private String emergencyTel;//紧急联系人电话
    private String bindBankCardNo;//银行卡号
    private String signed;//3：未签到，2：已签到
    private String isTest;//是否测试0未测试1已测试
    private String userType;//测试类型
    private String score;//积分
    private String voucherNum;//优惠券个数
    private String avatarPath;//头像地址


    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getVoucherNum() {
        return voucherNum;
    }

    public void setVoucherNum(String voucherNum) {
        this.voucherNum = voucherNum;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getBindBankCardNo() {
        return bindBankCardNo;
    }

    public void setBindBankCardNo(String bindBankCardNo) {
        this.bindBankCardNo = bindBankCardNo;
    }

    public String getEmergencyUser() {
        return emergencyUser;
    }

    public void setEmergencyUser(String emergencyUser) {
        this.emergencyUser = emergencyUser;
    }

    public String getEmergencyTel() {
        return emergencyTel;
    }

    public void setEmergencyTel(String emergencyTel) {
        this.emergencyTel = emergencyTel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGesturePwd() {
        return gesturePwd;
    }

    public void setGesturePwd(String gesturePwd) {
        this.gesturePwd = gesturePwd;
    }

    public String getIsBindBank() {
        return isBindBank;
    }

    public void setIsBindBank(String isBindBank) {
        this.isBindBank = isBindBank;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return IdCard;
    }

    public void setIdCard(String idCard) {
        IdCard = idCard;
    }

    public String getBankPas() {
        return bankPas;
    }

    public void setBankPas(String bankPas) {
        this.bankPas = bankPas;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSigned() {
        return signed;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }

    public String getIsTest() {
        return isTest;
    }

    public void setIsTest(String isTest) {
        this.isTest = isTest;
    }
}
