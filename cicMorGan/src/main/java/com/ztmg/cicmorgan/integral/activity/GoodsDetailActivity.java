package com.ztmg.cicmorgan.integral.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.AndroidUtil;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 商品详情
 *
 * @author pc
 */
public class GoodsDetailActivity extends BaseActivity {
    private ImageView iv_goods_img;
    private TextView tv_goods_integral, tv_introduce, tv_shop;
    private String awardId;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private String needAmount;
    private String docs;
    private String imgWeb;
    private static GoodsDetailActivity mContext;
    private TextView tv_goods_spec, tv_flow;
    private String score;
    private String name;
    private String isLottery;//1是虚拟0实物
    private Dialog cardResult;
    private RelativeLayout rl_content;
    private SlowlyProgressBar slowlyProgressBar;
    int mindex;
    int newProgress = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mindex++;
            if (mindex >= 5) {
                newProgress += 10;
                slowlyProgressBar.onProgressChange(newProgress);
                mHandler.sendEmptyMessage(1);

            } else {
                newProgress += 5;
                slowlyProgressBar.onProgressChange(newProgress);
                mHandler.sendEmptyMessageDelayed(1, 1500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(GoodsDetailActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_goods_detail);
        mContext = this;
        Intent intent = getIntent();
        awardId = intent.getStringExtra("awardId");
        initView();
        initData();
        getAwardInfo("3", awardId);
        mInflater = LayoutInflater.from(GoodsDetailActivity.this);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.img_mall_defaultforgoods, false, false, false);

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(GoodsDetailActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);


    }

    public static GoodsDetailActivity getInstance() {
        return mContext;
    }

    public void fActivity() {
        finish();
    }


    @Override
    protected void initView() {
        setTitle("商品详情");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(GoodsDetailActivity.this, "501001_spxq_back_click");
                finish();
            }
        });
        iv_goods_img = (ImageView) findViewById(R.id.iv_goods_img);
        //		tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        tv_shop = (TextView) findViewById(R.id.tv_shop);
        tv_goods_integral = (TextView) findViewById(R.id.tv_goods_integral);
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);
        tv_goods_spec = (TextView) findViewById(R.id.tv_goods_spec);//商品规格
        tv_flow = (TextView) findViewById(R.id.tv_flow);//兑换流程
        findViewById(R.id.bt_exchange).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AndroidUtil.isFastDoubleClick()) {
                    return;
                }
                onEvent(GoodsDetailActivity.this, "501002_spxq_ljdh_click");
                if (LoginUserProvider.getUser(GoodsDetailActivity.this) != null) {
                    DoCacheUtil util = DoCacheUtil.get(GoodsDetailActivity.this);
                    String str = util.getAsString("isLogin");
                    if (str != null) {
                        if (str.equals("isLogin")) {//已登录
                            //奖品兑换
                            userBouns(LoginUserProvider.getUser(GoodsDetailActivity.this).getToken(), "3");

                        } else {//未登录
                            Intent intent = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "5");//无论登录界面返回还是登录成功，都是finish当前界面
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                        intent.putExtra("overtime", "5");
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                    intent.putExtra("overtime", "5");
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.tv_tishi).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent riskTipFirstIntent = new Intent(GoodsDetailActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
            }
        });

        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void initData() {

    }

    //商品详情
    private void getAwardInfo(String from, String awardId) {
        //		CustomProgress.show(GoodsDetailActivity.this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(GoodsDetailActivity.this));
        String url = Urls.GETAWARDINFO;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("awardId", awardId);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //CustomProgress.CustomDismis();
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                rl_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String awardId = dataObj.getString("awardId");
                        isLottery = dataObj.getString("isLottery");//1是虚拟0实物
                        String state = dataObj.getString("state");//0奖品上架
                        needAmount = dataObj.getString("needAmount");
                        String isTrue = dataObj.getString("isTrue");
                        tv_goods_integral.setText(needAmount);
                        name = dataObj.getString("name");
                        //tv_goods_name.setText(name);
                        docs = dataObj.getString("docs");
                        tv_introduce.setText(name + " " + docs);
                        if (dataObj.getString("awardStandard").equals("null") || TextUtils.isEmpty(dataObj.getString("awardStandard"))) {
                            tv_goods_spec.setText("");//商品规格
                        } else {
                            tv_goods_spec.setText(dataObj.getString("awardStandard"));//商品规格
                        }
                        if (isLottery.equals("1")) {
                            tv_shop.setText("红包");
                        } else if (isLottery.equals("0")) {
                            tv_shop.setText("商品");
                        }
                        tv_flow.setText(dataObj.getString("exchangeFlow"));//兑换流程
                        imgWeb = dataObj.getString("imgWap");
                        mImageLoader.displayImage(imgWeb, iv_goods_img, mDisplayImageOptions);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(GoodsDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(GoodsDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(GoodsDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(GoodsDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GoodsDetailActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                //				CustomProgress.CustomDismis();
                Toast.makeText(GoodsDetailActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //立即兑换
    private void awardToUser(String from, String token, String awardId, final String needAmount) {
        CustomProgress.show(GoodsDetailActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(GoodsDetailActivity.this));
        String url = Urls.AWARDTOUSER;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("awardId", awardId);
        params.put("needAmount", needAmount);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String awardIsTrue = dataObj.getString("awardIsTrue");
                        String myAwardId = dataObj.getString("userAwardId");
                        //userBouns(LoginUserProvider.getUser(GoodsDetailActivity.this).getToken(),"3");
                        if (awardIsTrue.equals("0")) {
                            //订单确定
                            Intent intent = new Intent(GoodsDetailActivity.this, OrderConfirmActivity.class);
                            intent.putExtra("myAwardId", myAwardId);
                            intent.putExtra("isReceive", "receive");
                            startActivity(intent);
                        } else if (awardIsTrue.equals("1")) {
                            //优惠券兑换成功弹框
                            cardSuccessDialog();
                        }
                    } else if (jsonObject.getString("state").equals("3")) {
                        //积分不足
                        //IntegralNotEnough noIntegralDialog = new IntegralNotEnough(GoodsDetailActivity.this, R.style.MyDialogDeletAddress);
                        //noIntegralDialog.show();
                        new CustomToastUtils(GoodsDetailActivity.this, jsonObject.getString("message")).show();
                        //ToastUtils.show(GoodsDetailActivity.this, "您的积分不足,无法兑换");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(GoodsDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(GoodsDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(GoodsDetailActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(GoodsDetailActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GoodsDetailActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(GoodsDetailActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //兑换弹框
    public class ExchangeDialog extends Dialog {
        Context context;
        private Button bt_exchange;

        public ExchangeDialog(Context context) {
            super(context);
            this.context = context;
        }

        public ExchangeDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_goods_detail);
            ImageView iv_close = (ImageView) findViewById(R.id.iv_close);//关闭
            bt_exchange = (Button) findViewById(R.id.bt_exchange);
            //            if (isLottery.equals("0")) {//实物
            //                bt_exchange.setText("填写相关信息并兑换");
            //            } else if (isLottery.equals("1")) {//虚拟
            //                bt_exchange.setText("兑换");
            //            }
            TextView commodity_name = (TextView) findViewById(R.id.commodity_name);//商品名字
            TextView tv_integral = (TextView) findViewById(R.id.tv_integral);//使用积分
            TextView tv_my_integral = (TextView) findViewById(R.id.tv_my_integral);//我的积分
            TextView commodity_type = (TextView) findViewById(R.id.commodity_type);//商品类型
            ImageView image = (ImageView) findViewById(R.id.image);//图片
            mImageLoader.displayImage(imgWeb, image, mDisplayImageOptions);
            commodity_name.setText(name);
            tv_integral.setText("积分：" + tv_goods_integral.getText().toString());
            tv_my_integral.setText(score);
            if (isLottery.equals("1")) {
                commodity_type.setText("出借红包");
            } else if (isLottery.equals("0")) {
                commodity_type.setText("热门商品");
            }
            bt_exchange.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onEvent(GoodsDetailActivity.this, "501003_spxq_qrdh_click");
                    if (AndroidUtil.isFastDoubleClick()) {
                        return;
                    }
                    //订单确定
                    awardToUser("3", LoginUserProvider.getUser(GoodsDetailActivity.this).getToken(), awardId, needAmount);
                    dismiss();
                }
            });
            iv_close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    //用户积分
    private void userBouns(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(GoodsDetailActivity.this));
        String url = Urls.USERBOUNS;
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
                        score = dataObj.getString("score");
                        ExchangeDialog dialog = new ExchangeDialog(GoodsDetailActivity.this, R.style.SelectPicDialog);
                        Window dialogWindow = dialog.getWindow();
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        lp.width = LayoutParams.FILL_PARENT;
                        dialogWindow.setAttributes(lp);
                        dialog.show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(GoodsDetailActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(GoodsDetailActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(getActivity());
                        //LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(GoodsDetailActivity.this);
                        util.put("isLogin", "");
                    } else {
                        // Toast.makeText(GoodsDetailActivity.this, jsonObject.getString("message"), 0).show();
                        new CustomToastUtils(GoodsDetailActivity.this, jsonObject.getString("message")).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GoodsDetailActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(GoodsDetailActivity.this, "请检查网络", 0).show();
            }
        });
    }

    private void cardSuccessDialog() {
        cardResult = new Dialog(GoodsDetailActivity.this, R.style.MyDialog);
        cardResult.setContentView(R.layout.dl_card_result);

        TextView tv_know = (TextView) cardResult.findViewById(R.id.tv_know);
        tv_know.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cardResult.dismiss();
                Intent intent = new Intent(GoodsDetailActivity.this, GoodsListActivity.class);
                startActivity(intent);
            }
        });
        cardResult.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

}
