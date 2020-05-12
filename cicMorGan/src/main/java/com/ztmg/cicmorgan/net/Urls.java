package com.ztmg.cicmorgan.net;

/**
 * 访问网络存放地址
 *
 * @author pc
 */
public interface Urls {

    // String WEB_SERVER_PATH = "http://192.168.1.14:8082/svc/services";// 正式环境服务地址
    // String WEB_SERVER_PATH = "http://192.168.1.14:8082/svc/services";// 测试
    //String WEB_SERVER_PATH = "http://192.168.1.11:8082/svc/services";// 测试
    //String WEB_SERVER_PATH = "http://192.168.1.12:8082/svc/services";// 测试

    //String WEB_SERVER_PATH = "http://192.168.1.35:8082/svc/services";// 测试
    //String WEB_SERVER_PATH = "http://192.168.1.7:8082/svc/services";// 测试
//    String WEB_SERVER_PATH = "http://182.92.114.130:8082/svc/services";// 测试
    //String WEB_SERVER_PATH = "http://123.56.100.217:8088/svc/services";// 灰度测试


//    String WEB_SERVER_PATH = "http://182.92.114.130:8082/svc/services";// 测试

    String WEB_SERVER_PATH = "https://www.cicmorgan.com/svc/services";//  正式


    String WEB_SERVER_PATH_H5 = "http://www.cicmorgan.com";//h5页面，安全保障，关于我们，企业荣誉，信息披露，出借人F风险教育  //  正式

//    String WEB_SERVER_PATH_H5 = "http://182.92.114.130:8080";//h5页面，安全保障，关于我们，企业荣誉，信息披露，出借人风险教育  // 测试

    /*------------------银行存管H5界面---------------------------*/

//    String url_H5 = "http://sandbox.firstpay.com/hk-fsgw/gateway";//测试 2018年9月24日

    String url_H5 = "https://cgpt.unitedbank.cn/gateway";//正式
    /*---------------------------------------------*/

    boolean debug = false;

    //检查手机号是否注册过
    String CHECKMOBILEPHONEISREGISTERED = WEB_SERVER_PATH + "/verify/checkMobilePhoneIsRegistered";
    //发送验证码
    String SEND_CODE = WEB_SERVER_PATH + "/sm/sendSmsCode";
    //校验验证码是否正确
    String VERIFYSMSCODE = WEB_SERVER_PATH + "/sm/verifySmsCode";
    //注册
    String NEWREGIST = WEB_SERVER_PATH + "/newRegist";//密码加密
    // String REGIST = WEB_SERVER_PATH + "/regist";
    //登录
    String NEWLOGIN = WEB_SERVER_PATH + "/newLogin";//新版 登陆加密
    // String LOGIN = WEB_SERVER_PATH + "/login";
    //首页轮播图
    String GETCMSLISTBYTYPE = WEB_SERVER_PATH + "/cms/getCmsListByType";
    //首页数据
    String HOMEDATE = WEB_SERVER_PATH + "/project/index";
    //定期投资
    String SAVEUSERINVEST = WEB_SERVER_PATH + "/invest/saveUserInvest";
    //投资列表
    String GETPROJECTLIST = WEB_SERVER_PATH + "/project/getProjectList";
    //定期项目投资记录
    String GETPROJECTBIDLIST = WEB_SERVER_PATH + "/project/getProjectBidList";
    //定期项目还款计划
    String GETPROJECTREPAYPLANLIST = WEB_SERVER_PATH + "/project/getProjectRepayPlanList";

    //定期项目基本信息
    // String GETPROJECTINFO = WEB_SERVER_PATH + "/project/getProjectInfo";

    //出借 风控
    String GETPROJECTINFO = WEB_SERVER_PATH + "/project/getProjectInfoWap";

    String GETPROJECTINFOANNEX = WEB_SERVER_PATH + "/project/getProjectInfoAnnex";

    //活动抵用券列表
    String GETUSERVOUCHERSLIST = WEB_SERVER_PATH + "/activity/getUserVouchersList";
    //活动加息券列表
    String GETUSERRATECOUPONLIST = WEB_SERVER_PATH + "/activity/getUserRateCouponList";
    //活动优惠券
    String GETUSERAWARDSHISTORYLIST = WEB_SERVER_PATH + "/activity/getUserAwardsHistoryList";
    //交易记录列表
    String GETUSERTRANSDETAILLIST = WEB_SERVER_PATH + "/trans/getUserTransDetailList";

    //新版交易记录列表
    String GETCGBUSERTRANSDETAILLIST = WEB_SERVER_PATH + "/trans/getcgbUserTransDetailList";
    //投资成功H5返回
    String SEACHINVESTRESULT = WEB_SERVER_PATH + "/newinvest/seachInvestResult";

    //用户账户
    String GETUSERACCOUNT = WEB_SERVER_PATH + "/user/getUserAccount";
    //用户账户
    String GETCGBUSERACCOUNT = WEB_SERVER_PATH + "/user/getcgbUserAccount";
    //用户账户 冻结金额和可用余额
    String GETCGBUSERTOLAMOUNT = WEB_SERVER_PATH + "/user/getCgbUserTolAmount";
    //邀请好友
    String GETUSERBROKERAGE = WEB_SERVER_PATH + "/activity/getUserBrokerage";
    //邀请好友获得投资好友人数
    String GETINVITEINVESTMENTFRIENDS = WEB_SERVER_PATH + "/activity/getInviteInvestmentFriends";
    //邀请好友界面
    String GETINVITEFRIENDS = WEB_SERVER_PATH + "/activity/getInviteFriends";
    //邀请好友列表
    String GETINVITEFRIENDSLIST = WEB_SERVER_PATH + "/activity/getInviteFriendsList";
    //邀请好友获取佣金列表
    String GETBROKERAGELIST = WEB_SERVER_PATH + "/activity/getBrokerageList";
    //分享内容
    String GETWECHATSHAREINFO = WEB_SERVER_PATH + "/weChatShare/getWeChatShareInfo";
    //首页年报分享内容
    String SHAREANNUALREPORT = WEB_SERVER_PATH + "/weChatShare/shareAnnualReport_2018";
    //修改手机号
    String UPDATEUSERPHONE = WEB_SERVER_PATH + "/user/updateUserPhone";
    //绑定银行卡
    String BINDCARDAPP = WEB_SERVER_PATH + "/pay/bindCardApp";
    //获取用户信息
    String GETUSERINFO = WEB_SERVER_PATH + "/user/getUserInfoNew";
    //设置找回交易密码
    String FINDTRADEPASSWORD = WEB_SERVER_PATH + "/user/findTradePassword";
    //修改登录密码
    String NEWUPDATEUSERPWD = WEB_SERVER_PATH + "/user/newUpdateUserPwd";//密码加密
    // String UPDATEUSERPWD = WEB_SERVER_PATH + "/user/updateUserPwd";
    //联系地址
    String UPDATEADDRESS = WEB_SERVER_PATH + "/user/updateAddress";
    //紧急联系人
    String UPDATEEMERGENCY = WEB_SERVER_PATH + "/user/updateEmergency";
    //忘记登录密码
    String NEWFORGETPASSWORD = WEB_SERVER_PATH + "/newForgetPassword";//密码加密
    // String FORGETPASSWORD = WEB_SERVER_PATH + "/forgetPassword";
    //我的投资
    String GETMYBIDSDETAIL = WEB_SERVER_PATH + "/user/getMyBidsdetail";
    //我的回款计划
    String getMyBidsrepay = WEB_SERVER_PATH + "/user/getMyBidsrepay";
    //检验登录密码是否正确
    String NEWCHECKOLDPWD = WEB_SERVER_PATH + "/user/newCheckOldPwd";//密码加密
    // String CHECKOLDPWD = WEB_SERVER_PATH + "/user/checkOldPwd";
    //校验银行卡是否是信用卡
    String QUERYCARDBIN = WEB_SERVER_PATH + "/pay/queryCardBin";
    //充值界面查询银行卡信息
    String GETUSERBANKCARD = WEB_SERVER_PATH + "/user/getUserBankCard";
    //充值界面查询银行卡信息
    String GETCGBUSERBANKCARD = WEB_SERVER_PATH + "/user/getcgbUserBankCard";
    //认证充值
    String AUTHRECHARGEAPP = WEB_SERVER_PATH + "/pay/authRechargeApp";
    //申请提现
    String CASH = WEB_SERVER_PATH + "/pay/cash";
    //设置手势密码
    String SETGESTUREPWD = WEB_SERVER_PATH + "/gesture/setGesturePwd";
    //手势密码登录
    String VERIFYGESTUREPWD = WEB_SERVER_PATH + "/verify/loginByGesturePwd";
    //手势密码取消接口
    String CANCELGESTUREPWD = WEB_SERVER_PATH + "/gesture/cancelGesturePwd";
    //投诉与建议
    String SAVESUGGESTION = WEB_SERVER_PATH + "/more/saveSuggestion";
    //账户消息
    String STATIONLIST = WEB_SERVER_PATH + "/station/stationList";
    //账户消息 消息已读
    String CHANGELETTERSTATE = WEB_SERVER_PATH + "/station/changeLetterState";
    //账户消息 消息 详细信息
    String LETTERINFO = WEB_SERVER_PATH + "/station/letterInfo";
    //版本信息
    String APPVERSION = WEB_SERVER_PATH + "/app/appVersion";
    //用户签到
    String USERSIGNED = WEB_SERVER_PATH + "/signed/userSigned";
    //用户积分
    String USERBOUNS = WEB_SERVER_PATH + "/bouns/userBouns";
    //奖品
    String GETAWARDINFOLIST = WEB_SERVER_PATH + "/awardInfo/getAwardInfoList";
    //优惠劵和商品
    String GETNEWAWARDINFOLIST = WEB_SERVER_PATH + "/awardInfo/getNewAwardInfoList";//改版
    //奖品详情
    String GETAWARDINFO = WEB_SERVER_PATH + "/awardInfo/getAwardInfo";
    //立即兑换
    String AWARDTOUSER = WEB_SERVER_PATH + "/awardInfo/awardToUser";
    //收货地址列表
    String ADDRESSLIST = WEB_SERVER_PATH + "/userConsignee/addressList";
    //添加或修改地址
    String ADDNEWADDRESS = WEB_SERVER_PATH + "/userConsignee/addNewAddress";
    //设置默认地址
    String SETONEADDRESSDEFAULT = WEB_SERVER_PATH + "/userConsignee/setOneAddressDefault";
    //获取单个收货地址
    String GETONEADDRESS = WEB_SERVER_PATH + "/userConsignee/getOneAddress";
    //删除收货地址
    String DELETEONEADDRESS = WEB_SERVER_PATH + "/userConsignee/deleteOneAddress";
    //奖品领取
    String MYAWARDINFO = WEB_SERVER_PATH + "/awardInfo/myAwardInfo";
    //我的奖品列表
    String NEWUSERAWARDLIST = WEB_SERVER_PATH + "/awardInfo/newUserAwardList";
    //抽奖
    String DRAWLOTTERY = WEB_SERVER_PATH + "/userDrawLottery/drawLottery";
    //剩余次数
    String USERDRAWLOTTERYNUM = WEB_SERVER_PATH + "/userDrawLottery/userDrawLotteryNum";//新
    //订单详情
    String GETUSERAWARDINFO = WEB_SERVER_PATH + "/awardInfo/getUserAwardInfo";
    //测试题目
    String GETQUESTIONLIST = WEB_SERVER_PATH + "/question/getQuestionList";
    //提交测试题目答案
    String SAVEUSERANSWER = WEB_SERVER_PATH + "/question/saveUserAnswer";
    //获取图像验证码
    String GETPICTURECODE = WEB_SERVER_PATH + "/sm/getPictureCode";
    //校验图像验证码
    String CHECKPICTURECODE = WEB_SERVER_PATH + "/sm/checkPictureCode";
    //注册找回密码发送验证码
    String NEWSENDSMSCODE = WEB_SERVER_PATH + "/sm/newSendSmsCode";
    //充值H5
    String UTHRECHARGEH5 = WEB_SERVER_PATH + "/newpay/authRechargeH5";
    //开户H5
    String ACCOUNTCREATEH5 = WEB_SERVER_PATH + "/cgbPay/accountCreateH5";
    //提现H5
    String WITHDRAWH5 = WEB_SERVER_PATH + "/newwithdraw/withdrawH5";
    //投资H5
    String NEWINVESTSAVEUSERINVEST = WEB_SERVER_PATH + "/newinvest/saveUserInvest";
    //授权H5
    String USERAUTHORIZATIONH5 = WEB_SERVER_PATH + "/authorization/userAuthorizationH5";
    //大额转账
    String LARGE_RECHARGE_H5 = WEB_SERVER_PATH + "/newpay/largeRechargeH5";


    //新加首页项目列表
    String INDEXH5 = WEB_SERVER_PATH + "/project/indexH5";
    //新加发现活动列表
    String GETCMSLIST = WEB_SERVER_PATH + "/cms/getCmsList";
    //回款计划
    // String GETUSERINTERESTCOUNT = WEB_SERVER_PATH + "/user/getUserInterestCount";
    String GETNEWUSERINTERESTCOUNT = WEB_SERVER_PATH + "/user/getNewUserInterestCount";//回款计划  新接口

    //回款计划项目列表
    // String FINDUSERREPAYPLANSTATISTICAL = WEB_SERVER_PATH + "/user/findUserRepayPlanStatistical";
    String FINDNEWUSERREPAYPLANSTATISTICAL = WEB_SERVER_PATH + "/user/findNewUserRepayPlanStatistical";//回款计划 新接口

    //积分明细
    String USERBOUNSHISTORY = WEB_SERVER_PATH + "/bouns/userBounsHistory";

    //个人投资记录接口
    //String GETMYBIDSDETAILH5 = WEB_SERVER_PATH + "/user/getMyBidsdetailH5";
    String GETNEWMYBIDSDETAILH5 = WEB_SERVER_PATH + "/user/getNewMyBidsdetailH5";
    //抽奖获奖列表接口
    String USERBOUNSLIST = WEB_SERVER_PATH + "/awardInfo/userBounsList";
    //安全保障h5
    String MORESAFETYCONTROL = WEB_SERVER_PATH_H5 + "/more_safety_control.html";
    //关于我们h5  新换
    String MOREABOUTUS = WEB_SERVER_PATH_H5 + "/more_about_us.html";
    //企业荣誉h5
    String MOREHONOR = WEB_SERVER_PATH_H5 + "/more_honor.html";
    //信息披露h5
    String MOREDISCLOSURE = WEB_SERVER_PATH_H5 + "/more_disclosure.html";

    //新换信息披露h5
    String DISCLOSUREHOME = WEB_SERVER_PATH_H5 + "/disclosure_home.html";
    //新换运营数据h5
    String MOREDATA = WEB_SERVER_PATH_H5 + "/more_data.html";
    //出借人风险教育
    String MOREEDUCATION = WEB_SERVER_PATH_H5 + "/more_education.html";

    //邀请好友获得的积分
    String GETUSERFRIENDSBOUNS = WEB_SERVER_PATH + "/bouns/getUserFriendsBouns";
    //项目详情获取图片信息
    String GETINVENTORY = WEB_SERVER_PATH + "/creditInfo/getInventory";
    //邀请好友获取二维码图片
    String GETUSERQRCODE = WEB_SERVER_PATH + "/user/getUserQRCode";
    //更改银行卡
    String CHANGEBANKCARDH5 = WEB_SERVER_PATH + "/cgbPay/changeBankCardH5";
    //更换银行卡预留手机号
    String CHANGEBANKPHONEH5 = WEB_SERVER_PATH + "/cgbPay/changeBankPhoneH5";
    //上传头像
    String UPLOADAVATAR = WEB_SERVER_PATH + "/authorization/uploadAvatar";
    //消息是否已读
    String LETTERSTATE = WEB_SERVER_PATH + "/station/letterState";

    //投资详情投资新换接口
    // String USERTOINVEST = WEB_SERVER_PATH + "/newinvest/userToInvest";
    String NEWUSERTOINVEST = WEB_SERVER_PATH + "/newinvest/userToInvest2_2_1";//新
    //邀请好友获得的积分详情接口
    String GETMYINVITELIST = WEB_SERVER_PATH + "/user/getMyInviteList";
    //核心企业列表
    String GETMIDDLEMENLIST = WEB_SERVER_PATH + "/cms/getMiddlemenList";

    //我的投资详情回款列表
    String GETUSERINTERESTLIST = WEB_SERVER_PATH + "/user/getUserInterestList";
    //股东信息
    String GETZTMGLOANBASICINFO = WEB_SERVER_PATH + "/project/getZtmgLoanBasicInfo";

    //电子签章h5
    String ZTELECTRONICSIGNATURE = WEB_SERVER_PATH_H5 + "/zt_electronic_signature.html";
    //风险提示书h5
    String ZTPROTOCOLRISK = WEB_SERVER_PATH_H5 + "/zt_protocol_risk.html";
    //网络禁止性行为h5
    String ZTPROTOCOLPROHIBIT = WEB_SERVER_PATH_H5 + "/zt_protocol_prohibit.html";
    //出借协议h5
    String INVESTMINEAGREEMENTSCF = WEB_SERVER_PATH_H5 + "/invest_mine_agreement_scf.html";
    //安心投出借协议h5
    String INVESTMINEAGREEMENT = WEB_SERVER_PATH_H5 + "/invest_mine_agreement.html";
    //注册协议h5
    String LOGINAGREEMENT = WEB_SERVER_PATH_H5 + "/login_agreement.html";
    //新手引导
    String USERGUIDANCE = WEB_SERVER_PATH + "/activity/userGuidance";
    //新增项目列表
    String GETPROJECTLISTWAP = WEB_SERVER_PATH + "/project/getProjectListWap";
    //优选
    String GETNEWPROJECTLISTWAP = WEB_SERVER_PATH + "/project/getNewProjectListWap";
    //年报
    String ANNUALREPORT = WEB_SERVER_PATH_H5 + "/zt_annual_report.html";

}
