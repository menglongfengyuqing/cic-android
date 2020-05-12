package com.ztmg.cicmorgan.home.activity;

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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.RequestFriendsActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
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

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by dell on 2019/1/21.
 */

public class HomeYearReportH5Activity extends BaseActivity implements View.OnClickListener{
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_test/";
    private String title, text;
    private String inviteUrl;//微信分享链接
    private String logopath;
    private WebView wv_year_report;
    private String url;
    private WebSettings ws;
    private String mFileName;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_home_year_report);
        Intent intent = getIntent();
        url = intent.getStringExtra("Url");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(HomeYearReportH5Activity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }

        getUserInfo(LoginUserProvider.getUser(HomeYearReportH5Activity.this).getToken(), "3");
        initView();
        initData();

    }

    @Override
    protected void initView() {
        findViewById(R.id.img_share).setOnClickListener(this);
        findViewById(R.id.img_close).setOnClickListener(this);
            wv_year_report = (WebView) findViewById(R.id.wv_year_report);
            ws = wv_year_report.getSettings();
            ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
            ws.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
            ws.setLoadWithOverviewMode(true);
            ws.setJavaScriptEnabled(true);
            ws.setBuiltInZoomControls(true);
            ws.setSupportZoom(true);
            wv_year_report.setWebChromeClient(new WebChromeClient() {
                //刚加载完页面时，进度发生变化时，调用此方法
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                }
            });
            wv_year_report.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
//                boolean a = url.contains("type=close&name=disclosure");
//                if(url.contains("type=close&name=disclosure")|| url.contains("type=close&name=education")){
//                    finish();
//                }
                    return true;
                }


                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

            });
            wv_year_report.clearCache(true);
    }

    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.img_share://分享年报
        getWeChatShareInfo("3");
        break;
    case R.id.img_close:
        finish();
        break;
}
    }

    @Override
    protected void initData() {
        wv_year_report.loadUrl(url);
    }

    //获取分享内容
    private void getWeChatShareInfo(String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.SHAREANNUALREPORT;
        RequestParams params = new RequestParams();
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        title = dataObj.getString("title");
                        text = dataObj.getString("text");
                        inviteUrl = jsonObject.getString("inviteUrl");
                        logopath = dataObj.getString("logopath");
//                        logopath = "http://erp.cicmorgan.com/erp/userfiles/1/images/photo/2016/WeChat/logo-z.png";
                        getUrl();
                        showShare();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(HomeYearReportH5Activity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(HomeYearReportH5Activity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(HomeYearReportH5Activity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(HomeYearReportH5Activity.this);
                        util.put("isLogin", "");

                    } else if (jsonObject.getString("state").equals("5")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(HomeYearReportH5Activity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(HomeYearReportH5Activity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(HomeYearReportH5Activity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(HomeYearReportH5Activity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(HomeYearReportH5Activity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HomeYearReportH5Activity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(HomeYearReportH5Activity.this, "请检查网络", 0).show();
            }
        });
    }
    private void showShare() {

        OnekeyShare oks = new OnekeyShare();

        Bitmap enableLogo = BitmapFactory.decodeResource(HomeYearReportH5Activity.this.getResources(), R.drawable.pic_copy_link);
        String label = "复制链接";
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                if (!TextUtils.isEmpty(inviteUrl)) {
                    copy(inviteUrl, HomeYearReportH5Activity.this);
                    Toast.makeText(HomeYearReportH5Activity.this, "复制成功", 0).show();
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
                        Toast.makeText(HomeYearReportH5Activity.this, "Image error!", 1).show();
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
            //Log.d(TAG, "display image");0000000000000000000000000000000000000000000000000000000000000
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

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        //CustomProgress.show(AccountMessageActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETUSERINFO;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(HomeYearReportH5Activity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(HomeYearReportH5Activity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                            finish();
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(HomeYearReportH5Activity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                            finish();
                        }
                        //						LoginUserProvider.cleanData(BindBankCardActivity.this);
                        //						LoginUserProvider.cleanDetailData(BindBankCardActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(HomeYearReportH5Activity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(HomeYearReportH5Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HomeYearReportH5Activity.this, "解析异常", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //CustomProgress.CustomDismis();
                Toast.makeText(HomeYearReportH5Activity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
