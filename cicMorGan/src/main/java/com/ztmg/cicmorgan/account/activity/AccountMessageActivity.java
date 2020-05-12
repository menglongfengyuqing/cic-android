package com.ztmg.cicmorgan.account.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ztmg.cicmorgan.account.picture.BitmapUtils;
import com.ztmg.cicmorgan.account.picture.CircleImageView;
import com.ztmg.cicmorgan.account.picture.ImgEntity;
import com.ztmg.cicmorgan.account.picture.PicGridActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.test.TestQuestionFirstActivity;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 账户消息
 *
 * @author pc
 */
public class AccountMessageActivity extends BaseActivity implements OnClickListener {
    private TextView tv_user_name;
    private TextView tv_test_type;
    private static AccountMessageActivity mContext;
    private static final int TAKE_PHOTO_WITH_DATA = 105;//拍照
    private static final int TAKE_PIC = 101;
    private String picPath;
    private String path = Environment.getExternalStorageDirectory().getPath();
    File file;
    Uri imgUri;
    private List<ImgEntity> tempList;//临时带图片用
    private List<ImgEntity> list;
    private CircleImageView iv_header;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private static String resultMessage;//返回信息
    private TextView tv_user_phonoe;
    private TextView tv_depository_account;
    private ImageView iv_cunguan_enter, iv_idcard_enter;
    private TextView tv_idcard;
    private LinearLayout ll_cunguan_account, ll_id_card;
    private TextView tv_my_bank_card;

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

    private static final int PERMISSIONRESULT = 108;//权限

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(AccountMessageActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        setContentView(R.layout.activity_account_message);
        mContext = this;
        initView();
        refresh();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(AccountMessageActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public static AccountMessageActivity getInstance() {
        return mContext;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo(LoginUserProvider.getUser(AccountMessageActivity.this).getToken(), "3");
        checkPermissions(needPermissions);
        MobclickAgent.onResume(this); //统计时长
        //		Permission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }
    // Manifest.permission.ACCESS_COARSE_LOCATION,
    //    Manifest.permission.ACCESS_FINE_LOCATION,
    //    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    //    Manifest.permission.READ_EXTERNAL_STORAGE,
    //    Manifest.permission.READ_PHONE_STATE,
    //    Manifest.permission.CAMERA

    private void Permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this,
                    "android.permission.CAMERA");
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.CAMERA"
                        },
                        PERMISSIONRESULT);
            } else {
                //版本更新
                showMissingPermissionDialog();
            }
        }
        //else {
        //    //版本更新
        //	showMissingPermissionDialog();
        //}
    }


    @Override
    protected void initView() {

        setTitle("个人信息");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(AccountMessageActivity.this, "203001_grxx_back_click");
                finish();
            }
        });
        findViewById(R.id.rl_bank_card).setOnClickListener(this);//银行卡
        findViewById(R.id.rl_test).setOnClickListener(this);//风险测评
        tv_test_type = (TextView) findViewById(R.id.tv_test_type);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        if (TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getRealName()) || LoginUserProvider.getUser(AccountMessageActivity.this).getRealName()
                .equals("null")) {
            tv_user_name.setText(LoginUserProvider.getUser(AccountMessageActivity.this).getPhone());
        } else {
            tv_user_name.setText(LoginUserProvider.getUser(AccountMessageActivity.this).getRealName());
        }
        tv_user_phonoe = (TextView) findViewById(R.id.tv_user_phonoe);
        if (!TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getPhone())) {
            String newPhone = LoginUserProvider.getUser(AccountMessageActivity.this).getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            tv_user_phonoe.setText(newPhone);
        }
        iv_header = (CircleImageView) findViewById(R.id.iv_header);
        findViewById(R.id.rl_header).setOnClickListener(this);
        tv_depository_account = (TextView) findViewById(R.id.tv_depository_account);
        iv_cunguan_enter = (ImageView) findViewById(R.id.iv_cunguan_enter);
        tv_idcard = (TextView) findViewById(R.id.tv_idcard);
        iv_idcard_enter = (ImageView) findViewById(R.id.iv_idcard_enter);
        tv_my_bank_card = (TextView) findViewById(R.id.tv_my_bank_card);
        ll_cunguan_account = (LinearLayout) findViewById(R.id.ll_cunguan_account);
        ll_cunguan_account.setOnClickListener(this);
        ll_id_card = (LinearLayout) findViewById(R.id.ll_id_card);
        ll_id_card.setOnClickListener(this);
        findViewById(R.id.rl_modify_phone).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        tempList = new ArrayList<ImgEntity>();
        list = new ArrayList<ImgEntity>();
    }

    public void refresh() {
        if (TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getUserType())) {//未测评
            tv_test_type.setText("立即测评");
            tv_test_type.setTextColor(getResources().getColor(R.color.text_a11c3f));
        } else {
            tv_test_type.setText(LoginUserProvider.getUser(AccountMessageActivity.this).getUserType());
            tv_test_type.setTextColor(getResources().getColor(R.color.text_989898));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_modify_phone://修改手机号
                onEvent(AccountMessageActivity.this, "203003_grxx_phone_click");
                Intent modifyPhoneIntent = new Intent(AccountMessageActivity.this, BindPhoneActivity.class);
                startActivity(modifyPhoneIntent);
                break;
            case R.id.rl_test://风险测评
                onEvent(AccountMessageActivity.this, "203007_grxx_fxcp_click");
                Intent teamIntent = new Intent(AccountMessageActivity.this, TestQuestionFirstActivity.class);
                teamIntent.putExtra("isInvestment", "1");
                startActivity(teamIntent);
                break;
            case R.id.ll_cunguan_account:
                onEvent(AccountMessageActivity.this, "203004_grxx_yhcg_click");
                if (!TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getIsBindBank()) && LoginUserProvider.getUser(AccountMessageActivity.this)
                        .getIsBindBank().equals("1")) {
                    Intent bindCardIntent = new Intent(AccountMessageActivity.this, BindBankCardActivity.class);
                    bindCardIntent.putExtra("isBackAccount", "1");
                    startActivity(bindCardIntent);
                } else {
                    Intent myBankCardIntent = new Intent(AccountMessageActivity.this, MyBankCardActivity.class);
                    startActivity(myBankCardIntent);
                }
                break;
            case R.id.ll_id_card:
                onEvent(AccountMessageActivity.this, "203006_grxx_yhk_click");
                if (!TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getIsBindBank()) && LoginUserProvider.getUser(AccountMessageActivity.this)
                        .getIsBindBank().equals("1")) {
                    Intent bindCardIntent = new Intent(AccountMessageActivity.this, BindBankCardActivity.class);
                    bindCardIntent.putExtra("isBackAccount", "1");
                    startActivity(bindCardIntent);
                } else {
                    Intent myBankCardIntent = new Intent(AccountMessageActivity.this, MyBankCardActivity.class);
                    startActivity(myBankCardIntent);
                }
                break;
            case R.id.rl_bank_card://是否绑定银行卡 1未绑定 2已绑定
                onEvent(AccountMessageActivity.this, "203004_grxx_yhcg_click");
                if (!TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getIsBindBank()) && LoginUserProvider.getUser(AccountMessageActivity.this)
                        .getIsBindBank().equals("1")) {
                    Intent bindCardIntent = new Intent(AccountMessageActivity.this, BindBankCardActivity.class);
                    bindCardIntent.putExtra("isBackAccount", "1");
                    startActivity(bindCardIntent);
                } else {
                    Intent myBankCardIntent = new Intent(AccountMessageActivity.this, MyBankCardActivity.class);
                    startActivity(myBankCardIntent);
                }

                break;
            case R.id.rl_header:
                onEvent(AccountMessageActivity.this, "203002_grxx_tx_click");
                SelectPicDialog selectDialog = new SelectPicDialog(AccountMessageActivity.this, R.style.SelectPicDialog);
                Window dialogWindow1 = selectDialog.getWindow();
                WindowManager.LayoutParams lp1 = dialogWindow1.getAttributes();
                lp1.width = LayoutParams.FILL_PARENT;
                dialogWindow1.setAttributes(lp1);
                selectDialog.show();
                break;
            default:
                break;
        }
    }

    //拍照对话框
    public class SelectPicDialog extends Dialog {
        Context context;

        public SelectPicDialog(Context context) {
            super(context);
            this.context = context;
        }

        public SelectPicDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_select_pic);
            TextView tv_camera = (TextView) findViewById(R.id.tv_camera);
            TextView tv_photo = (TextView) findViewById(R.id.tv_photo);
            TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_camera.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //相机
                    takePic();
                    dismiss();
                }
            });
            tv_photo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //相册
                    Intent intent = new Intent(AccountMessageActivity.this, PicGridActivity.class);
                    if (tempList != null && tempList.size() > 0) {
                        List<ImgEntity> listname = new ArrayList<ImgEntity>();
                        for (ImgEntity en : tempList) {
                            if (en.getName() != null) {
                                if (en.getName().equals("default_pic") || en.getName().equals("example_pic")) {
                                    listname.add(en);
                                }
                            }
                        }
                        tempList.removeAll(listname);
                    }
                    intent.putExtra("ImgList", (Serializable) tempList);
                    intent.putExtra("ImgSize", 1);
                    startActivityForResult(intent, TAKE_PIC);
                    dismiss();
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //取消
                    dismiss();
                }
            });

        }
    }

    /**
     * 开启拍照
     */
    private void takePic() {
        long time = System.currentTimeMillis();

        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        String times = mCalendar.get(Calendar.HOUR) + "-" + mCalendar.get(Calendar.MINUTE) + "-" + mCalendar.get(Calendar.SECOND);

        picPath = path + "/" + times + ".jpg";
        file = new File(picPath);
        if (file.exists()) {
            file.delete();
        }
        imgUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, TAKE_PHOTO_WITH_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PIC:
                    list.clear();
                    List<ImgEntity> backImageBeanList = (List<ImgEntity>) data.getSerializableExtra("PIC");
                    list.addAll(backImageBeanList);
                    if (list.size() > 0) {
                        CustomProgress.show(AccountMessageActivity.this);
                        new Thread(networkTask).start();
                    }
                    break;
                case TAKE_PHOTO_WITH_DATA://拍照
                    list.clear();
                    if (imgUri != null) {
                        ImgEntity entity = new ImgEntity();
                        entity.setUri(imgUri.toString());
                        entity.setPath(file.getPath());
                        list.add(entity);
                        if (list.size() > 0) {
                            CustomProgress.show(AccountMessageActivity.this);
                            new Thread(networkTask).start();
                        }
                    }
                    break;
                default:
                    break;
            }
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
//                getBankInfo(LoginUserProvider.getUser(AccountMessageActivity.this).getToken(), "3");
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        UserInfo info = new UserInfo();
                        info.setToken(token);
                        info.setPhone(dataObj.getString("name"));
                        info.setBankPas(dataObj.getString("businessPwd"));//交易密码
                        info.setEmail(dataObj.getString("email"));//邮箱
                        String cgbBindBankCardState = dataObj.getString("cgbBindBankCardState");
                        if(cgbBindBankCardState.equals("null")){
                            info.setIsBindBank("1");//是否绑定银行卡 1未绑定 2已绑定
                        }else{
                            info.setIsBindBank(cgbBindBankCardState);
                        }
                        String certificateChecked = dataObj.getString("certificateChecked");
                        if(certificateChecked.equals("null")){
                            info.setCertificateChecked("1");
                        }else{
                            info.setCertificateChecked(dataObj.getString("certificateChecked"));//是否注册 1未开户  2已开户
                        }
                        info.setGesturePwd(dataObj.getString("gesturePwd"));//是否设置过手势密码，0设置1已设置
                        info.setAddress(dataObj.getString("address"));//地址
                        info.setEmergencyUser(dataObj.getString("emergencyUser"));//紧急联系人
                        info.setEmergencyTel(dataObj.getString("emergencyTel"));//紧急联系电话
                        info.setRealName(dataObj.getString("realName"));//真实姓名
                        info.setIdCard(dataObj.getString("IdCard"));//身份证号
                        info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));//银行卡号
                        info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(AccountMessageActivity.this);
                        if(LoginUserProvider.getUser(AccountMessageActivity.this).getCertificateChecked().equals("2")){
                            //存管账户
                            tv_depository_account.setText("已开通");
                            tv_depository_account.setTextColor(getResources().getColor(R.color.text_989898));
                            iv_cunguan_enter.setVisibility(View.GONE);
                            ll_cunguan_account.setClickable(false);
                            //实名认证
                            tv_idcard.setText(LoginUserProvider.getUser(AccountMessageActivity.this).getIdCard());
                            tv_idcard.setTextColor(getResources().getColor(R.color.text_989898));
                            iv_idcard_enter.setVisibility(View.GONE);
                            ll_id_card.setClickable(false);
                            if(LoginUserProvider.getUser(AccountMessageActivity.this).getIsBindBank().equals("1")){
                                //我的银行卡
                                tv_my_bank_card.setText("立即绑定");
                                tv_my_bank_card.setTextColor(getResources().getColor(R.color.text_a11c3f));
                            }else if(LoginUserProvider.getUser(AccountMessageActivity.this).getIsBindBank().equals("2")){
                                //银行卡
                                if (!TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getBindBankCardNo().trim()) && !LoginUserProvider.getUser(AccountMessageActivity.this).getBindBankCardNo().trim().equals("") && !LoginUserProvider.getUser(AccountMessageActivity.this).getBindBankCardNo().trim().equals(" ")) {
//                                    tv_my_bank_card.setText(bankName + "(" + date(bindBankCardNo) + ")");
                                    tv_my_bank_card.setText(dataObj.getString("bankName") +"(" + date(LoginUserProvider.getUser(AccountMessageActivity.this).getBindBankCardNo()) + ")");
                                    tv_my_bank_card.setTextColor(getResources().getColor(R.color.text_989898));
                                }
                            }
                        }else if(LoginUserProvider.getUser(AccountMessageActivity.this).getCertificateChecked().equals("1")){
                            //存管账户
                            tv_depository_account.setText("立即开通");
                            tv_depository_account.setTextColor(getResources().getColor(R.color.text_a11c3f));
                            iv_cunguan_enter.setVisibility(View.VISIBLE);
                            ll_cunguan_account.setClickable(true);
                            //实名认证
                            tv_idcard.setText("立即认证");
                            tv_idcard.setTextColor(getResources().getColor(R.color.text_a11c3f));
                            iv_idcard_enter.setVisibility(View.VISIBLE);
                            ll_id_card.setClickable(true);
                            //我的银行卡
                            tv_my_bank_card.setText("立即绑定");
                            tv_my_bank_card.setTextColor(getResources().getColor(R.color.text_a11c3f));
                        }
                        //						Intent intent = new Intent(BindBankCardActivity.this,BindBankSuccessActivity.class);//绑卡成功
                        //						intent.putExtra("isBackAccount", isBackAccount);
                        //						startActivity(intent);
                        if (TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getRealName()) || LoginUserProvider.getUser(AccountMessageActivity.this)
                                .getRealName().equals("null")) {
                            tv_user_name.setText(LoginUserProvider.getUser(AccountMessageActivity.this).getPhone());
                        } else {
                            tv_user_name.setText(LoginUserProvider.getUser(AccountMessageActivity.this).getRealName());
                        }

                        if (!TextUtils.isEmpty(LoginUserProvider.getUser(AccountMessageActivity.this).getAvatarPath()) && !LoginUserProvider.getUser(AccountMessageActivity.this)
                                .getAvatarPath().equals("null")) {
                            mInflater = LayoutInflater.from(AccountMessageActivity.this);
                            mImageLoader = ImageLoaderUtil.getImageLoader();
                            mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_defaultheadimage, false, false, false);
                            mImageLoader.displayImage(LoginUserProvider.getUser(AccountMessageActivity.this).getAvatarPath(), iv_header, mDisplayImageOptions);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(AccountMessageActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(AccountMessageActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(AccountMessageActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(BindBankCardActivity.this);
                        //						LoginUserProvider.cleanDetailData(BindBankCardActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(AccountMessageActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(AccountMessageActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AccountMessageActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //CustomProgress.CustomDismis();
                Toast.makeText(AccountMessageActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            final Map<String, String> params = new HashMap<String, String>();
            if (LoginUserProvider.getUser(AccountMessageActivity.this) != null) {
                params.put("token", LoginUserProvider.getUser(AccountMessageActivity.this).getToken());
            }
            final Map<String, File> files = new HashMap<String, File>();
            files.put("file_1", saveMyBitmap(list.get(0)));
            try {
                post(Urls.UPLOADAVATAR, params, files);

            } catch (IOException e) {
                //CustomProgress.CustomDismis();
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(resultMessage)) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", resultMessage);
                msg.setData(data);
                handler.sendMessage(msg);
            }

            //CustomProgress.CustomDismis();
        }
    };

    public File saveMyBitmap(ImgEntity beanImg) {

        File file = null;
        Uri uri = Uri.parse(beanImg.getUri());
        Bitmap mpBitmap = null;

        try {

            mpBitmap = BitmapUtils.getBitmap(getContentResolver(), uri);
            file = new File(beanImg.getPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (mpBitmap != null) {
            mpBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //			}

        return file;
    }

    //上传图片
    public static String post(String url, Map<String, String> params, Map<String, File> files)
            throws IOException {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(10 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        // 发送文件数据
        if (files != null)

            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getValue().getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                outStream.write(LINEND.getBytes());
            }
        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        StringBuilder sb2 = new StringBuilder();
        if (res == 200) {
            int ch;
            while ((ch = in.read()) != -1) {
                sb2.append((char) ch);
            }
        }
        outStream.close();
        conn.disconnect();
        resultMessage = sb2.toString();
        return sb2.toString();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // TODO
            // UI界面的更新等相关操作
            //			CustomProgress.CustomDismis();
            Bundle data = msg.getData();
            String val = data.getString("value");
            try {
                JSONObject object = new JSONObject(val);
                if (object.getString("state").equals("0")) {
                    ToastUtils.show(AccountMessageActivity.this, "提交成功");
                    getUserInfo(LoginUserProvider.getUser(AccountMessageActivity.this).getToken(), "3");
                    CustomProgress.CustomDismis();
                } else if (object.getString("state").equals("4")) {//系统超时
                    String mGesture = LoginUserProvider.getUser(AccountMessageActivity.this).getGesturePwd();
                    if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                        //设置手势密码
                        Intent intent = new Intent(AccountMessageActivity.this, UnlockGesturePasswordActivity.class);
                        intent.putExtra("overtime", "0");
                        startActivity(intent);
                    } else {
                        //未设置手势密码
                        Intent intent = new Intent(AccountMessageActivity.this, LoginActivity.class);
                        intent.putExtra("overtime", "0");
                        startActivity(intent);
                    }
                    DoCacheUtil util = DoCacheUtil.get(AccountMessageActivity.this);
                    util.put("isLogin", "");
                } else if (object.getString("state").equals("5")) {//重复添加
                    ToastUtils.show(AccountMessageActivity.this, "基本信息，请勿重复添加");

                } else if (object.getString("state").equals("2")) {
                    ToastUtils.show(AccountMessageActivity.this, "缺少参数");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("mylog", "请求结果为-->" + val);
        }
    };

    /**
     * @param
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {

        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
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

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
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
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    public String date(String str) {
        //		String str = "weicc-20100107-00001";
        str = str.substring(str.length() - 4, str.length());
        return str;
    }

    //获取银行信息
    private void getBankInfo(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETCGBUSERBANKCARD;
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
                    if (jsonObject.get("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String bindBankCardNo = dataObj.getString("bindBankCardNo");
                        String bankName = dataObj.getString("bankName");
                        String bankCode = dataObj.getString("bankCode");
                        String dayLimitAmount = dataObj.getString("dayLimitAmount");//日限额
                        String singleLimitAmount = dataObj.getString("singleLimitAmount");//每次限额
                        String limitAmountTxt = dataObj.getString("limitAmountTxt");//单日限额
                        //存管账户
                        tv_depository_account.setText("已开通");
                        tv_depository_account.setTextColor(getResources().getColor(R.color.text_989898));
                        iv_cunguan_enter.setVisibility(View.GONE);
                        ll_cunguan_account.setClickable(false);
                        //实名认证
                        tv_idcard.setText(LoginUserProvider.getUser(AccountMessageActivity.this).getIdCard());
                        tv_idcard.setTextColor(getResources().getColor(R.color.text_989898));
                        iv_idcard_enter.setVisibility(View.GONE);
                        ll_id_card.setClickable(false);
                        //银行卡
                        if (!TextUtils.isEmpty(bindBankCardNo) || !bindBankCardNo.equals("")) {
                            tv_my_bank_card.setText(bankName + "(" + date(bindBankCardNo) + ")");
                            tv_my_bank_card.setTextColor(getResources().getColor(R.color.text_989898));
                        }
                    } else if (jsonObject.get("state").equals("5")) {//未绑卡
                        //存管账户
                        tv_depository_account.setText("立即开通");
                        tv_depository_account.setTextColor(getResources().getColor(R.color.text_a11c3f));
                        iv_cunguan_enter.setVisibility(View.VISIBLE);
                        ll_cunguan_account.setClickable(true);
                        //实名认证
                        tv_idcard.setText("立即认证");
                        tv_idcard.setTextColor(getResources().getColor(R.color.text_a11c3f));
                        iv_idcard_enter.setVisibility(View.VISIBLE);
                        ll_id_card.setClickable(true);
                        //我的银行卡
                        tv_my_bank_card.setText("立即绑定");
                        tv_my_bank_card.setTextColor(getResources().getColor(R.color.text_a11c3f));
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(AccountMessageActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(AccountMessageActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(AccountMessageActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(AccountMessageActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(AccountMessageActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AccountMessageActivity.this, "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(AccountMessageActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
