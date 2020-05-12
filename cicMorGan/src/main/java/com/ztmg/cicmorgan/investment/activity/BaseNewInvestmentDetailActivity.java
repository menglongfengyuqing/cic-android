package com.ztmg.cicmorgan.investment.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.InvestmentDetailEntity;
import com.ztmg.cicmorgan.entity.UserAccountInfo;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlideDetailsLayout;

import org.apache.http.Header;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * dong
 * 2018/6/1.
 * 项目详情 基础类
 */

public class BaseNewInvestmentDetailActivity extends BaseActivity {

    public SlideDetailsLayout sv_switch;
    public Dialog dialog;
    public String projectid;
    public boolean isJiJiang = false;
    public boolean isLiJi = false;
    public boolean isLook;
    public InvestmentDetailEntity investmentDetailEntity;
    public SlideDetailsLayout.Status mStatus;
    public LinearLayout category_layout;


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    //4个参数按顺序分别是左上右下
    public void margin(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    // 去登录的dialog
    public void DialogLogin() {
        dialog = new Dialog(mContext, R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_login_new);
        dialog.setCanceledOnTouchOutside(true);
        TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_yes);
        //TextView tv_no = (TextView) testdialog.findViewById(R.id.tv_no);
        tv_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CustomProgress.CustomDismis();
                if (LoginUserProvider.getUser(mContext) != null) {
                    String mGesture = LoginUserProvider.getUser(mContext).getGesturePwd();
                    if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                        // 设置手势密码
                        Intent intent = new Intent(mContext, UnlockGesturePasswordActivity.class);
                        intent.putExtra("overtime", "0");
                        startActivity(intent);
                    } else {
                        // 未设置手势密码
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("overtime", "0");
                        startActivity(intent);
                    }
                    DoCacheUtil util = DoCacheUtil.get(mContext);
                    util.put("isLogin", "");
                    ////跳转到登录
                    //Intent testIntent = new Intent(mContext, LoginActivity.class);
                    //testIntent.putExtra("overtime", "5");
                    //startActivity(testIntent);
                } else {
                    // 重新登录
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra("overtime", "0");
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        //如果没有登录，按物理返回键 就把当前activity 也关闭掉
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

       /* tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不做测试，关闭弹框
                testdialog.dismiss();
            }
        });*/
        // dialog.show();
    }

    /**
     * 判断是否登录
     */
    public void login() {
        DialogLogin();
        if (LoginUserProvider.getUser(mContext) != null) {
            DoCacheUtil util = DoCacheUtil.get(this);
            String str = util.getAsString("isLogin");
            if (!StringUtils.isEmpty(util.getAsString("isLogin"))) {
                // if (str != null) {
                if (str.equals("isLogin")) {//已登录
                    //弹框消失
                    dialog.dismiss();
                    //去验证Token是否有效
                    initAccountDataToken(LoginUserProvider.getUser(mContext).getToken(), "3");
                } else {//未登录
                    //弹框显示
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }
            } else {
                //弹框显示
                if (!dialog.isShowing()) {
                    dialog.show();
                    sv_switch.smoothClose(true);
                }
            }
        } else {
            //弹框显示 登录
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    /**
     * 验证token 是否失效
     *
     * @param token
     * @param from
     */

    public void initAccountDataToken(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETCGBUSERACCOUNT;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        UserAccountInfo info = new UserAccountInfo();
                        info.setTotalAmount(dataObj.getString("totalAmount"));
                        BigDecimal bd = new BigDecimal(dataObj.getString("availableAmount"));
                        String dialogAvailableAmount = bd.toPlainString();
                        info.setAvailableAmount(dialogAvailableAmount);
                        info.setCashAmount(dataObj.getString("cashAmount"));
                        info.setRechargeAmount(dataObj.getString("rechargeAmount"));
                        info.setFreezeAmount(dataObj.getString("freezeAmount"));
                        info.setTotalInterest(dataObj.getString("totalInterest"));
                        info.setCurrentAmount(dataObj.getString("currentAmount"));
                        info.setRegularDuePrincipal(dataObj.getString("regularDuePrincipal"));
                        info.setRegularDueInterest(dataObj.getString("regularDueInterest"));
                        info.setRegularTotalAmount(dataObj.getString("regularTotalAmount"));
                        info.setRegularTotalInterest(dataObj.getString("regularTotalInterest"));
                        info.setCurrentTotalInterest(dataObj.getString("currentTotalInterest"));
                        info.setCurrentYesterdayInterest(dataObj.getString("currentYesterdayInterest"));
                        info.setReguarYesterdayInterest(dataObj.getString("reguarYesterdayInterest"));
                        LoginUserProvider.setUserDetail(info);

                        if (Constant.isboolean) {
                            Intent intent = new Intent(BaseNewInvestmentDetailActivity.this, OnceInvestmentActivity.class);
                            intent.putExtra("projectid", projectid);
                            startActivity(intent);
                        }
                        //                        if (isJiJiang) {
                        //                            CustomToastUtils customToast = new CustomToastUtils(BaseNewInvestmentDetailActivity.this, "您未出借该项目，暂时不能查看相关信息");
                        //                            customToast.show(2000);
                        //                            //phone(mStatus);
                        //                        } else if (isLiJi) {
                        //                            // phone(mStatus);
                        //                            CustomToastUtils customToast = new CustomToastUtils(BaseNewInvestmentDetailActivity.this, "您未出借该项目，暂时不能查看相关信息");
                        //                            customToast.show(2000);
                        //                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        Constant.isboolean = false;
                        //弹框显示
                        if (dialog != null) {
                            if (!dialog.isShowing()) {
                                dialog.show();
                            }
                        } else {
                            DialogLogin();
                            dialog.show();
                        }

                    } else {
                        CustomProgress.CustomDismis();
                        ToastUtils.show(mContext, jsonObject.getString("message"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // Toast.makeText(InvestmentDetailActivity.this, "解析异常", 0).show();
                    ToastUtils.show(mContext, "解析异常");
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                //Toast.makeText(InvestmentDetailActivity.this, "请检查网络", 0).show();
                ToastUtils.show(mContext, "请检查网络");
            }
        });
    }

    public void phone(SlideDetailsLayout.Status status) {
        //验证是否能查看投资
        // 手机号脱敏处理
        if (status.name().equals("OPEN")) {
            String newPhone = LoginUserProvider.getUser(this).getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            for (InvestmentDetailEntity.DataBean.BidListBean entity : investmentDetailEntity.getData().getBidList()) {
                String phone = entity.getUserPhone();
                if (newPhone.equals(phone)) {
                    isLook = true;
                    break;
                } else {
                    isLook = false;//出借列表没有当前登录的手机号，不能查看详情
                }
            }
            if (!isLook) {
                CustomToastUtils customToast = new CustomToastUtils(this, "您未出借该项目，暂时不能查看相关信息");
                customToast.show(2000);
                LogUtil.e("11111111111111111111111111111111111");
                sv_switch.smoothClose(true); //关闭上拉
            } else {
                category_layout.setVisibility(View.VISIBLE);

            }
        }
    }
}
