package com.ztmg.cicmorgan.account.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.ztmg.cicmorgan.integral.activity.IntegralRuleActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 邀请好友
 *
 * @author pc
 */
public class RequestFriendsActivity extends BaseActivity {
    private TextView tv_friend_num, tv_friend_investment_money;
    private String title, text;
    private String inviteUrl;//微信分享链接
    private String inviteLink;//复制邀请链接
    private String logopath;
    private String mFileName;
    private Bitmap mBitmap;
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_test/";
    public static RequestFriendsActivity mContext;
    private ImageView iv_two_code;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    //private Button bg_request_code;
    private Button bt_copy;
    private String isShare = "0";
    private ScrollView sc_content;
    private TextView tv_url;

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
            SystemBarTintManager tintManager = new SystemBarTintManager(RequestFriendsActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_request_friends);
        mContext = this;
        initView();
        initData();

        mInflater = LayoutInflater.from(RequestFriendsActivity.this);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);
        //getInvestmentFriendsNum(LoginUserProvider.getUser(RequestFriendsActivity.this).getToken(),"0","1","10","3");

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(RequestFriendsActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginUserProvider.getUser(RequestFriendsActivity.this) != null) {
            getData(LoginUserProvider.getUser(RequestFriendsActivity.this).getToken(), "3");
        }
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长
    }

    public static RequestFriendsActivity getInstance() {
        return mContext;
    }

    @Override
    protected void initView() {
        setTitle("邀请好友");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(RequestFriendsActivity.this, "218001_yqhy_back_click");
                finish();
            }
        });
        tv_url = (TextView) findViewById(R.id.tv_url);
        tv_friend_num = (TextView) findViewById(R.id.tv_friend_num);
        tv_friend_investment_money = (TextView) findViewById(R.id.tv_friend_investment_money);
        //链接
        findViewById(R.id.bt_request_friends).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(RequestFriendsActivity.this, "218005_yqhy_ljyqhy_click");
                //String token = LoginUserProvider.getUser(RequestFriendsActivity.this).getToken();
                //getWeChatShareInfo(token,"2","3");
                //判断sd中是否包含图片
                //if(){
                isShare = "0";
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(text) && !TextUtils.isEmpty(inviteUrl)) {
                    getWeChatShareInfo(LoginUserProvider.getUser(RequestFriendsActivity.this).getToken(), "2", "3");

                }
            }
        });
        //已邀请好友
        findViewById(R.id.rl_already_obtain).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(RequestFriendsActivity.this, "218003_yqhy_yyqhy_click");
                Intent intent = new Intent(RequestFriendsActivity.this, AlreadyRequestFriendsActivity.class);
                startActivity(intent);
            }
        });
        //已获得积分
        findViewById(R.id.rl_already_request_friends).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(RequestFriendsActivity.this, "218004_yqhy_yhdjf_click");
                Intent intent = new Intent(RequestFriendsActivity.this, AlreadyObtainActivity.class);
                startActivity(intent);
            }
        });
        iv_two_code = (ImageView) findViewById(R.id.iv_two_code);//二维码
        //bg_request_code = (Button) findViewById(R.id.bg_request_code);

        bt_copy = (Button) findViewById(R.id.bt_copy);
        bt_copy.setOnClickListener(new OnClickListener() {//复制

            @Override
            public void onClick(View v) {
                copy(bt_copy.getText().toString(), RequestFriendsActivity.this);
                Toast.makeText(RequestFriendsActivity.this, "复制成功", 0).show();
            }
        });
        //积分规则
        findViewById(R.id.iv_title_img).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(RequestFriendsActivity.this, "218002_yqhy_jfgz_click");
                Intent intent = new Intent(RequestFriendsActivity.this, IntegralRuleActivity.class);
                startActivity(intent);
            }
        });

        sc_content = (ScrollView) findViewById(R.id.sc_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {

    }

    //邀请好友界面
    private void getData(String token, String from) {
        //CustomProgress.show(this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RequestFriendsActivity.this));
        String url = Urls.GETINVITEFRIENDS;
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
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                        String inviteFriends = dataObj.getString("inviteFriends");
                        tv_friend_num.setText(inviteFriends);//邀请人数
                        //String brokerage=dataObj.getString("brokerage");
                        //String brokerageDot = decimalFormat.format(Double.parseDouble(brokerage));//佣金
                        //tv_friend_investment_money.setText(brokerageDot+"元");
                        inviteLink = dataObj.getString("inviteLink");//邀请链接
                        tv_url.setText(inviteLink);
                        getRequestIntegral(LoginUserProvider.getUser(RequestFriendsActivity.this).getToken(), "3");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(RequestFriendsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(RequestFriendsActivity.this);
                        //LoginUserProvider.cleanDetailData(RequestFriendsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RequestFriendsActivity.this);
                        util.put("isLogin", "");

                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(RequestFriendsActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(RequestFriendsActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(RequestFriendsActivity.this, "请检查网络", 0).show();
                CustomProgress.CustomDismis();
            }
        });
    }

    private void showShare() {

        OnekeyShare oks = new OnekeyShare();

        Bitmap enableLogo = BitmapFactory.decodeResource(RequestFriendsActivity.this.getResources(), R.drawable.pic_copy_link);
        String label = "复制链接";
        OnClickListener listener = new OnClickListener() {
            public void onClick(View v) {
                if (!TextUtils.isEmpty(inviteLink)) {
                    copy(inviteLink, RequestFriendsActivity.this);
                    Toast.makeText(RequestFriendsActivity.this, "复制成功", 0).show();
                }
            }
        };
        oks.setCustomerLogo(enableLogo, label, listener);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //		 oks.setNotification(R.drawable.account_icon,"11");
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(inviteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);//后台获取文本qq
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
         oks.setImagePath("/sdcard/download_test/test.jpg");//确保SDcard下面存在此张图片
//        oks.setImageUrl(logopath);
        //oks.setImageUrl("http://erp.cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(inviteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(inviteUrl);

        // 启动分享GUI
        oks.show(this);
    }

    //获取分享内容
    private void getWeChatShareInfo(String token, String ordersum, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RequestFriendsActivity.this));
        String url = Urls.GETWECHATSHAREINFO;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("ordersum", ordersum);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //CustomProgress.CustomDismis();
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                sc_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        title = dataObj.getString("title");
                        text = dataObj.getString("text");
                        inviteUrl = jsonObject.getString("inviteUrl");
                        logopath = dataObj.getString("logopath");
                        //logopath = "http://erp.cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png";
                        getUrl();
                        if (isShare.equals("0")) {
                            showShare();
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(RequestFriendsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(RequestFriendsActivity.this);
                        //						LoginUserProvider.cleanDetailData(RequestFriendsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RequestFriendsActivity.this);
                        util.put("isLogin", "");

                    } else if (jsonObject.getString("state").equals("5")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(RequestFriendsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(RequestFriendsActivity.this);
                        //						LoginUserProvider.cleanDetailData(RequestFriendsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RequestFriendsActivity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(RequestFriendsActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RequestFriendsActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(RequestFriendsActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //邀请好友获得的积分
    private void getRequestIntegral(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RequestFriendsActivity.this));
        String url = Urls.GETUSERFRIENDSBOUNS;
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
                        double bounsTotalAmountDouble = dataObj.getDouble("bounsTotalAmount");
                        int bounsTotalAmountInt = (int) bounsTotalAmountDouble;
                        tv_friend_investment_money.setText(bounsTotalAmountInt + "");//邀请获得积分
                        getRequestCode(LoginUserProvider.getUser(RequestFriendsActivity.this).getToken(), "3");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(RequestFriendsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(RequestFriendsActivity.this);
                        //LoginUserProvider.cleanDetailData(RequestFriendsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RequestFriendsActivity.this);
                        util.put("isLogin", "");

                    } else if (jsonObject.getString("state").equals("5")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(RequestFriendsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(RequestFriendsActivity.this);
                        //LoginUserProvider.cleanDetailData(RequestFriendsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RequestFriendsActivity.this);
                        util.put("isLogin", "");
                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(RequestFriendsActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(RequestFriendsActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(RequestFriendsActivity.this, "请检查网络", 0).show();
            }
        });
    }


    //邀请好友获得二维码
    private void getRequestCode(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(RequestFriendsActivity.this));
        String url = Urls.GETUSERQRCODE;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        //									JSONObject dataObj = jsonObject.getJSONObject("data");
                        //									double bounsTotalAmountDouble = dataObj.getDouble("bounsTotalAmount");
                        //									int bounsTotalAmountInt = (int)bounsTotalAmountDouble;
                        //									tv_friend_investment_money.setText(bounsTotalAmountInt+"分");

                        String path = jsonObject.getString("path");
                        String refCode = jsonObject.getString("refCode");
                        mImageLoader.displayImage(path, iv_two_code, mDisplayImageOptions);
                        bt_copy.setText(refCode);
                        isShare = "1";
                        getWeChatShareInfo(LoginUserProvider.getUser(RequestFriendsActivity.this).getToken(), "2", "3");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(RequestFriendsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(RequestFriendsActivity.this);
                        //						LoginUserProvider.cleanDetailData(RequestFriendsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RequestFriendsActivity.this);
                        util.put("isLogin", "");

                    } else if (jsonObject.getString("state").equals("5")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(RequestFriendsActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(RequestFriendsActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(RequestFriendsActivity.this);
                        //						LoginUserProvider.cleanDetailData(RequestFriendsActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(RequestFriendsActivity.this);
                        util.put("isLogin", "");
                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(RequestFriendsActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(RequestFriendsActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(RequestFriendsActivity.this, "请检查网络", 0).show();
            }
        });
    }

    private void getUrl() {
        new Thread() {
            public void run() {
                try {
                    //logopath = "http://img.my.csdn.net/uploads/201211/21/1353511891_4579.jpg";
                    mFileName = "test.jpg";
                    //以下是取得图片的两种方法
                    //////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap
                    byte[] data = getImage(logopath);
                    if (data != null) {
                        mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                        sleep(2000);
                        saveBitmapToFile(mBitmap, ALBUM_PATH);
                    } else {
                        Toast.makeText(RequestFriendsActivity.this, "Image error!", 1).show();
                        logopath = "http://erp.cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png";
                    }
                    connectHanlder.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    //					Toast.makeText(RequestFriendsActivity.this,"无法链接网络！", 1).show();
                    //					logopath = "http://cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png";
                }
            }
        }.start();
    }

    public byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return readStream(inStream);
        }
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    private Handler connectHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Log.d(TAG, "display image");
            // 更新UI，显示图片
            if (mBitmap != null) {
                //mImageView.setImageBitmap(mBitmap);// display image
            }
        }
    };

    public static void saveBitmapToFile(Bitmap bitmap, String _file)
            throws IOException {
        // 3.保存Bitmap
        try {
            File path = new File(ALBUM_PATH);
            // 文件
            String filepath = ALBUM_PATH + "/test.jpg";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }
}
