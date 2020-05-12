package com.ztmg.cicmorgan.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BuildConfig;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.fragment.AccountFragment;
import com.ztmg.cicmorgan.home.HomeFragment;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.fragment.InvestmentFragment;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.fragment.MoreFragment;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.AndroidUtil;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LogUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.PermissionUtil;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.util.UpdataInfoParser;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements OnClickListener {

    private HomeFragment homeFragment;// 首页
    private InvestmentFragment investmentFragment;// 出借
    private AccountFragment accountFragment;// 账户
    private MoreFragment moreFragment;// 发现

    private View rl_home, ll_investment, ll_account, ll_more;
    private ImageView iv_home_img, iv_investment_img, iv_account_img,
            iv_more_img;
    private TextView tv_home_text, tv_investment_text, tv_account_text,
            tv_more_text;

    private FragmentTransaction Ft_home, Ft_investment, Ft_account, Ft_more;
    private String investment;
    private int oldVersion;
    private int newVersion;
    private String downUrl;
    private Dialog mdialog;
    private TextView tv_new_version;
    private RelativeLayout rl_text_prompt;
    //	private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.ztmg.cicmorgan.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static boolean isForeground = false;
    private List<String> featureList;
    private String appversion;
    private int mIndex;
    private Fragment[] fragments;
    private String isForce;
    private String isOnline;
    private String updateTime;
    private Dialog dialog;
    private TextView tv_updateTime;
    private FragmentTransaction ft;
    private final static int INSTALL_PACKAGES_REQUESTCODE = 0x12;
    File fileTmp;
    public static final int REQUEST_PERMISSION_SD = 502;//SD卡权限申请

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //		if (Build.VERSION.SDK_INT >= 21) {
        //		    View decorView = getWindow().getDecorView();
        //		    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //		            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        //		    decorView.setSystemUiVisibility(option);
        //		    getWindow().setStatusBarColor(Color.TRANSPARENT);
        //		}
        //		ActionBar actionBar = getActionBar();
        //		actionBar.hide();

        EventBus.getDefault().register(this);

        homeFragment = new HomeFragment();
        investmentFragment = new InvestmentFragment();
        accountFragment = new AccountFragment();
        moreFragment = new MoreFragment();
        fragments = new Fragment[]{homeFragment, investmentFragment, moreFragment, accountFragment};

        //开启事务
        ft = getSupportFragmentManager().beginTransaction();
        //添加首页
        ft.add(R.id.rl_container, homeFragment).commit();
        //默认设置为第0个
        setIndexSelected(0);


        Intent intent = getIntent();
        investment = intent.getStringExtra("investment");
        if (!TextUtils.isEmpty(investment) && investment.equals("investmentFrom")) {

            //			Ft_investment = getSupportFragmentManager().beginTransaction();
            //			Ft_investment.replace(R.id.rl_container, investmentFragment);
            //			Ft_investment.commit();
            setIndexSelected(1);
        } else if (!TextUtils.isEmpty(investment) && investment.equals("accountfrom")) {

            //			Ft_investment = getSupportFragmentManager().beginTransaction();
            //			Ft_investment.replace(R.id.rl_container, accountFragment);
            //			Ft_investment.commit();
            setIndexSelected(3);
        } else {

            //			Ft_home = getSupportFragmentManager().beginTransaction();
            //			Ft_home.replace(R.id.rl_container, homeFragment);
            //			Ft_home.commit();
            //默认设置为第0个
            setIndexSelected(0);
        }
        initView();
        inital();
        //旧的版本号
        oldVersion = AndroidUtil.getAppVersionCode(MainActivity.this);
        //获取版本号
        requestVersionNum("3");
        //registerMessageReceiver();

        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true);

        /**
         * 设置日志加密
         * 参数：boolean 默认为false（不加密）
         */
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(MainActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("--------------------------------------onNewIntent----" + intent);
        rl_text_prompt.setVisibility(View.VISIBLE);
        tv_home_text.setTextColor(getResources().getColor(R.color.main_bellow_text));
        tv_investment_text.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
        tv_account_text.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
        tv_more_text.setTextColor(getResources().getColor(R.color.main_bellow_text_no));
        iv_home_img.setImageResource(R.drawable.pic_home_select);
        iv_investment_img.setImageResource(R.drawable.pic_investment_noselect);
        iv_account_img.setImageResource(R.drawable.pic_find_no_select);
        iv_more_img.setImageResource(R.drawable.pic_new_account_no_select);
        //默认设置为第0个
        setIndexSelected(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    private void initView() {
        rl_home = findViewById(R.id.rl_home);
        ll_investment = findViewById(R.id.ll_investment);
        ll_account = findViewById(R.id.ll_account);
        ll_more = findViewById(R.id.ll_more);
        iv_home_img = (ImageView) findViewById(R.id.iv_home_img);
        iv_investment_img = (ImageView) findViewById(R.id.iv_investment_img);
        iv_account_img = (ImageView) findViewById(R.id.iv_account_img);
        iv_more_img = (ImageView) findViewById(R.id.iv_more_img);

        tv_home_text = (TextView) findViewById(R.id.tv_home_text);
        tv_investment_text = (TextView) findViewById(R.id.tv_investment_text);
        tv_account_text = (TextView) findViewById(R.id.tv_account_text);
        tv_more_text = (TextView) findViewById(R.id.tv_more_text);
        rl_text_prompt = (RelativeLayout) findViewById(R.id.rl_text_prompt);//文字提示
        findViewById(R.id.tv_text).setOnClickListener(this);
    }

    private void inital() {

        if (!TextUtils.isEmpty(investment) && investment.equals("investmentFrom")) {
            tv_home_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_investment_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text));
            tv_account_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_more_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            rl_text_prompt.setVisibility(View.GONE);

            iv_home_img.setImageResource(R.drawable.pic_home_no_select);
            iv_investment_img.setImageResource(R.drawable.pic_investment_select);
            iv_account_img.setImageResource(R.drawable.pic_find_no_select);
            iv_more_img.setImageResource(R.drawable.pic_new_account_no_select);

        } else if (!TextUtils.isEmpty(investment) && investment.equals("accountfrom")) {
            tv_home_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_investment_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_account_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_more_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text));

            iv_home_img.setImageResource(R.drawable.pic_home_no_select);
            iv_investment_img.setImageResource(R.drawable.pic_investment_noselect);
            iv_account_img.setImageResource(R.drawable.pic_find_no_select);
            iv_more_img.setImageResource(R.drawable.pic_new_account_select);
            rl_text_prompt.setVisibility(View.VISIBLE);

            //			Ft_account = getSupportFragmentManager().beginTransaction();
            //			Ft_account.replace(R.id.rl_container, accountFragment);
            //			Ft_account.commit();
            setIndexSelected(3);

        } else {
            tv_home_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text));
            tv_investment_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_account_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_more_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            rl_text_prompt.setVisibility(View.VISIBLE);
        }
        rl_home.setOnClickListener(this);
        ll_investment.setOnClickListener(this);
        ll_account.setOnClickListener(this);
        ll_more.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_home:
                onEvent(this, "101001_tab_home_click");
                rl_text_prompt.setVisibility(View.VISIBLE);
                tv_home_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text));
                tv_investment_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));
                tv_account_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));
                tv_more_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));
                iv_home_img.setImageResource(R.drawable.pic_home_select);
                iv_investment_img.setImageResource(R.drawable.pic_investment_noselect);
                iv_account_img.setImageResource(R.drawable.pic_find_no_select);
                iv_more_img.setImageResource(R.drawable.pic_new_account_no_select);

                //			Ft_home = getSupportFragmentManager().beginTransaction();
                //			Ft_home.replace(R.id.rl_container, homeFragment);
                //			Ft_home.commit();
                setIndexSelected(0);
                break;
            case R.id.ll_investment:
                onEvent(this, "102001_tab_lend_click");
                rl_text_prompt.setVisibility(View.GONE);

                tv_home_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));
                tv_investment_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text));
                tv_account_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));
                tv_more_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));

                iv_home_img.setImageResource(R.drawable.pic_home_no_select);
                iv_investment_img.setImageResource(R.drawable.pic_investment_select);
                iv_account_img.setImageResource(R.drawable.pic_find_no_select);
                iv_more_img.setImageResource(R.drawable.pic_new_account_no_select);

                //			Ft_investment = getSupportFragmentManager().beginTransaction();
                //			Ft_investment.replace(R.id.rl_container, investmentFragment);
                //			Ft_investment.commit();
                //			investmentFragment = new InvestmentFragment();
                setIndexSelected(1);
                break;
            case R.id.ll_account://发现
                onEvent(this, "103001_tab_discover_click");
                rl_text_prompt.setVisibility(View.VISIBLE);

                tv_home_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));
                tv_investment_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));
                tv_account_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text));
                tv_more_text.setTextColor(getResources().getColor(
                        R.color.main_bellow_text_no));

                iv_home_img.setImageResource(R.drawable.pic_home_no_select);
                iv_investment_img.setImageResource(R.drawable.pic_investment_noselect);
                iv_account_img.setImageResource(R.drawable.pic_find_select);
                iv_more_img.setImageResource(R.drawable.pic_new_account_no_select);
                //
                //			Ft_more = getSupportFragmentManager().beginTransaction();
                //			Ft_more.replace(R.id.rl_container, moreFragment);
                //			Ft_more.commit();
                setIndexSelected(2);
                break;
            case R.id.ll_more://账户
                onEvent(this, "104001_tab_account_click");
                if (LoginUserProvider.getUser(MainActivity.this) != null) {
                    DoCacheUtil util = DoCacheUtil.get(this);
                    String str = util.getAsString("isLogin");
                    if (str != null) {
                        if (str.equals("isLogin")) {//已登录

                            tv_home_text.setTextColor(getResources().getColor(
                                    R.color.main_bellow_text_no));
                            tv_investment_text.setTextColor(getResources().getColor(
                                    R.color.main_bellow_text_no));
                            tv_account_text.setTextColor(getResources().getColor(
                                    R.color.main_bellow_text_no));
                            tv_more_text.setTextColor(getResources().getColor(
                                    R.color.main_bellow_text));

                            iv_home_img.setImageResource(R.drawable.pic_home_no_select);
                            iv_investment_img.setImageResource(R.drawable.pic_investment_noselect);
                            iv_account_img.setImageResource(R.drawable.pic_find_no_select);
                            iv_more_img.setImageResource(R.drawable.pic_new_account_select);
                            rl_text_prompt.setVisibility(View.VISIBLE);

                            //						Ft_account = getSupportFragmentManager().beginTransaction();
                            //						Ft_account.replace(R.id.rl_container, accountFragment);
                            //						Ft_account.commit();
                            setIndexSelected(3);

                        } else {//未登录
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.putExtra("overtime", "6");
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("overtime", "6");
                    startActivity(intent);
                }
                break;
            case R.id.tv_text:
                Intent riskTipFirstIntent = new Intent(MainActivity.this, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
        }
    }

    private long exitTime = 0;

    /**
     * eventbus
     */
    @Subscribe
    public void onThreadMainEvent(MyEvent event) {
        if (event.getType().equals("UX")) {
            rl_text_prompt.setVisibility(View.GONE);

            tv_home_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_investment_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text));
            tv_account_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));
            tv_more_text.setTextColor(getResources().getColor(
                    R.color.main_bellow_text_no));

            iv_home_img.setImageResource(R.drawable.pic_home_no_select);
            iv_investment_img.setImageResource(R.drawable.pic_investment_select);
            iv_account_img.setImageResource(R.drawable.pic_find_no_select);
            iv_more_img.setImageResource(R.drawable.pic_new_account_no_select);

            setIndexSelected(1);
        }

    }

    /**
     * 判断选中的fragment
     *
     * @param index
     */
    private void setIndexSelected(int index) {
        // Common.mBoolean = false;
        if (mIndex == index) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //隐藏
        ft.hide(fragments[mIndex]);

        //判断是否添加
        if (!fragments[index].isAdded()) {
            ft.add(R.id.rl_container, fragments[index]).show(fragments[index]);
        } else {
            ft.show(fragments[index]);
        }
        ft.commit();
        //再次赋值
        mIndex = index;

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String feature;

    //获取版本号
    private void requestVersionNum(String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MainActivity.this));
        String url = Urls.APPVERSION;
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
                        newVersion = dataObj.getInt("versionCode");//版本号
                        appversion = dataObj.getString("appversion");//版本name
                        if (dataObj.has("isForce")) {
                            isForce = dataObj.getString("isForce");//0强制 1非强制
                        }
                        if (dataObj.has("isOnline")) {
                            isOnline = dataObj.getString("isOnline");//0正在上线；1非上线
                            //isOnline = "1";//测试
                        }
                        if (dataObj.has("updateTime")) {
                            updateTime = dataObj.getString("updateTime");//更新时间
                        }
                        JSONArray array = dataObj.getJSONArray("featureList");//内容
                       /* if ("0".equals(isOnline)) {
                            DialogUpgrade();
                            dialog.show();
                        } else*/ //if ("1".equals(isOnline)) {
                        featureList = new ArrayList<String>();
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                feature = array.getString(i);
                                featureList.add(feature);
                            }
                        }
                        boolean b = PermissionUtil.requestSD(MainActivity.this);
                        //downUrl = "http://192.168.1.14:8082/CicMorGan.apk";//升级地址
                        downUrl = dataObj.getString("url");//升级地址
                        //dialog("0");
                        if (oldVersion != 0 && newVersion != 0) {
                            if (oldVersion < newVersion) {
                                if ("0".equals(isOnline)) {
                                    DialogUpgrade();
                                    dialog.show();
                                } else if ("1".equals(isOnline)) {
                                    DoCacheUtil util = DoCacheUtil.get(MainActivity.this);
                                    String str = util.getAsString("isUpdate");
                                    if (str == null) {
                                        util.put("isUpdate", "");
                                    }
                                    if (isForce.equals("0")) {//0强制 1非强制
                                        UpDataApp(b);
                                    } else {
                                        if (str == null) {
                                            UpDataApp(b);
                                        } else {
                                            if (!str.equals("isUpdate")) {
                                                UpDataApp(b);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //更新APP
    private void UpDataApp(boolean b) {
        if (b) {
            dialog(isForce);
        } else {
            ToastUtils.show(MainActivity.this, "当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");
        }
    }

    // 版本更新的对话框
    private void dialog(String isUpdate) {
        mdialog = new Dialog(MainActivity.this, R.style.MyDialogUpVersion);
        mdialog.setContentView(R.layout.dialog_version);
        mdialog.setCancelable(false);
        //		tv_new_version = (TextView) mdialog.findViewById(R.id.tv_new_version);
        //		tv_new_version.setText("V"+appversion);
        TextView tv_content = (TextView) mdialog.findViewById(R.id.tv_content);
        LinearLayout ll_no_update = (LinearLayout) mdialog.findViewById(R.id.ll_no_update);//暂不更新
        Button bt_force_update = (Button) mdialog.findViewById(R.id.bt_force_update);//强制更新
        //0强制 1非强制
        if (isUpdate.equals("0")) {
            ll_no_update.setVisibility(View.GONE);
            bt_force_update.setVisibility(View.VISIBLE);
        } else if (isUpdate.equals("1")) {
            ll_no_update.setVisibility(View.VISIBLE);
            bt_force_update.setVisibility(View.GONE);
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < featureList.size(); i++) {
            sb.append(featureList.get(i) + "\n");
        }
        //		for (int i = 0; i < 5; i++) {
        //			if(i==0){
        //				sb.append("1信息纰漏透明信息纰漏透明信息纰漏透明信息纰漏透明信息纰漏透明"+"\n");
        //			}
        //			if(i==1){
        //				sb.append("2信息纰漏透明"+"\n");
        //			}if(i==2){
        //				sb.append("3信息纰漏透明信息纰漏透明信息纰漏透明信息纰漏透明信息纰漏透明"+"\n");
        //			}
        //		}

        tv_content.setText(sb.toString());


        //强制立即更新
        mdialog.findViewById(R.id.bt_force_update).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                downLoadApk(downUrl);
                mdialog.dismiss();
            }
        });

        //立即更新
        mdialog.findViewById(R.id.bt_new).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                downLoadApk(downUrl);
                mdialog.dismiss();
            }
        });

        //稍暂不更新
        mdialog.findViewById(R.id.bt_not_update).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DoCacheUtil util = DoCacheUtil.get(MainActivity.this);
                util.put("isUpdate", "isUpdate");
                mdialog.dismiss();
            }
        });
        mdialog.show();
    }

    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk(final String downloadURL) {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    fileTmp = UpdataInfoParser.getFileFromServer(downloadURL, pd);
                    sleep(3000);
//                    installApk(file);
                    checkIsAndroidO(fileTmp);
                    pd.dismiss();
                } catch (Exception e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
            }
        }.start();
    }



    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    /**
     * 判断是否是8.0系统,是的话需要获取此权限，判断开没开，没开的话处理未知应用来源权限问题,否则直接安装
     */
    public void checkIsAndroidO(File file) {
        if (Build.VERSION.SDK_INT >= 26) {
            //PackageManager类中在Android Oreo版本中添加了一个方法：判断是否可以安装未知来源的应用
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (b) {
                Log.i("ztmg","开始安装");
                //安装应用的逻辑(写自己的就可以)
                installApk(file);
            } else {
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
            }
        } else {
            Log.i("ztmg","版本<26，开始安装");
            installApk(file);
        }
    }


    // 安装apk
    protected void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(this, "com.ztmg.cicmorgan.fileProvider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //i.setAction("android.intent.action.VIEW");
        //i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //i.setDataAndType(Uri.parse("file:///" + file.getPath()), "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        DoCacheUtil util = DoCacheUtil.get(MainActivity.this);
        util.put("isUpdate", "");
    }

    //	@Override
    //	protected void onResume() {
    //		super.onResume();
    //		if(isNeedCheck){
    //            checkPermissions(needPermissions);
    //        }
    //	}

    /**
     * @since 2.5.0
     * requestPermissions方法是请求某一权限，
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     * checkSelfPermission方法是在用来判断是否app已经获取到某一个权限
     * shouldShowRequestPermissionRationale方法用来判断是否
     * 显示申请权限对话框，如果同意了或者不在询问则返回false
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissonList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, perm)) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限结果的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }else  if(requestCode == INSTALL_PACKAGES_REQUESTCODE) {
            if (paramArrayOfInt.length > 0 && paramArrayOfInt[0] == PackageManager.PERMISSION_GRANTED) {
                installApk(fileTmp);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                startActivityForResult(intent, INSTALL_PACKAGES_REQUESTCODE);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == INSTALL_PACKAGES_REQUESTCODE) {
            installApk(fileTmp);
        }else if(resultCode == 0){
            installApk(fileTmp);
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消", null);

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    // 上线期间禁掉一切点击事件
    public void DialogUpgrade() {
        dialog = new Dialog(this, R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_upgrade);
        tv_updateTime = (TextView) dialog.findViewById(R.id.tv_updateTime);
        if (!StringUtils.isEmpty(updateTime))
            tv_updateTime.setText("预计维护时间 " + updateTime);
        //如果没有登录，按物理返回键 就把当前activity 也关闭掉
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }

}
