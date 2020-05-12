package com.ztmg.cicmorgan.integral.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.integral.entity.AddressManagerEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.CommonPopCityUtil;
import com.ztmg.cicmorgan.util.CommonPopCityUtil.ProvinceListener;
import com.ztmg.cicmorgan.util.CommonPopUtil;
import com.ztmg.cicmorgan.util.CommonPopUtil.CityListener;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.StrMatchPro;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * dong
 * 新建收货地址
 * 2018年8月3日
 */
public class BuildReceiptAddressActivity extends BaseActivity implements OnClickListener {
    private TextView tv_province, tv_city;
    private CommonPopUtil con;
    private CommonPopCityUtil conCity;
    private String proCode;//省份id
    private String cityCode;//城市id
    private String temps[];//省份城市详细地址集合
    private Button bt_is_default_address;
    private boolean isOpen;
    private String flage;//new 新增收货地址 modify修改收货地址
    private String isDefault;//是否有默认地址
    private EditText et_name, et_phone, et_detail_address;
    private String id;
    private DoCacheUtil doutil;
    ContactServiceDialog dialog;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(BuildReceiptAddressActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_build_address);
        doutil = DoCacheUtil.get(this);
        Intent intent = getIntent();
        isDefault = intent.getStringExtra("isDefault");
        flage = intent.getStringExtra("newifmodify");
        if (isDefault.equals("否")) {
            if (!TextUtils.isEmpty(flage) && flage.equals("new")) {
                isOpen = false;
            } else {
                //isOpen = true;
                id = intent.getStringExtra("id");
                //获取某个收货地址
                if (!TextUtils.isEmpty(id)) {
                    getOneAddress("3", LoginUserProvider.getUser(BuildReceiptAddressActivity.this).getToken(), id);
                }
            }
        } else {
            isOpen = true;
            if (!TextUtils.isEmpty(flage) && flage.equals("modify")) {
                //isOpen = false;
                id = intent.getStringExtra("id");
                //获取某个收货地址
                if (!TextUtils.isEmpty(id)) {
                    getOneAddress("3", LoginUserProvider.getUser(BuildReceiptAddressActivity.this).getToken(), id);
                }
            }
        }

        initView();
        initData();

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(BuildReceiptAddressActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
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

    @Override
    protected void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_delete);
        relativeLayout.setOnClickListener(this);
        //保存
        //findViewById(R.id.bt_save).setOnClickListener(this);
        if (!TextUtils.isEmpty(flage) && flage.equals("new")) {
            setTitle("新建收货地址");
            relativeLayout.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(flage) && flage.equals("modify")) {
            setTitle("编辑收货地址");
            relativeLayout.setVisibility(View.VISIBLE);
        }
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(BuildReceiptAddressActivity.this, "801001_bjshdz_back_click");
                finish();
            }
        });
        setRightText("保存", new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(BuildReceiptAddressActivity.this, "801002_bjshdz_save_click");
                //联系地址存储格式：省份编码（code）--城市编码（code）--街道小区信息--详细信息（eg：110000--110000--海淀区莲花桥XXX--XXXXXXX）
                //String addressTotal = proCode+"--"+cityCode+"--"+address+"--"+addressDetail;
                //是否设置为默认地址 0-否 1-是
                String name = et_name.getText().toString();
                String phone = et_phone.getText().toString();
                String detailAddress = et_detail_address.getText().toString();
                String isDefault = "";
                Pattern p = Pattern.compile("[1][123456789]\\d{9}");
                Matcher m = p.matcher(phone);
                if (isOpen) {//true 选为默认地址
                    isDefault = "1";
                } else {
                    isDefault = "0";
                }
                if (!StringUtils.isNotEmpty(proCode)) {//获取赋值的省，去得到省codeId
                    proCode = StrMatchPro.ParseJson(BuildReceiptAddressActivity.this, tv_province.getText().toString());
                }
                if (!StringUtils.isNotEmpty(cityCode)) {
                    cityCode = StrMatchPro.ParseJsonCity(BuildReceiptAddressActivity.this, tv_city.getText().toString());
                }
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(BuildReceiptAddressActivity.this, "请填写姓名", 0).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(BuildReceiptAddressActivity.this, "请填写联系方式", 0).show();
                    return;
                }
                if (!phone.contains("*")) {
                    if (!m.matches()) {
                        //手机格式不正确
                        Toast.makeText(BuildReceiptAddressActivity.this, "请检查手机号是否正确", 0).show();
                        return;
                    }
                } else {
                    phone = mobile;
                }
                if (StringUtils.isNotEmpty(tv_province.getText().toString())) {
                    if (tv_province.getText().toString().equals("请选择所在省份")) {
                        Toast.makeText(BuildReceiptAddressActivity.this, "请选择所在省份", 0).show();
                        return;
                    }
                }
                if (StringUtils.isNotEmpty(tv_city.getText().toString())) {
                    if (tv_city.getText().toString().equals("请选择所在市区")) {
                        Toast.makeText(BuildReceiptAddressActivity.this, "请选择所在市区", 0).show();
                        return;
                    }
                }
                if (TextUtils.isEmpty(detailAddress)) {
                    Toast.makeText(BuildReceiptAddressActivity.this, "请填写详细地址", 0).show();
                    return;
                }
                if (!TextUtils.isEmpty(flage) && flage.equals("new")) {
                    addNewAddress("3", LoginUserProvider.getUser(BuildReceiptAddressActivity.this).getToken(), "", name, phone, proCode, cityCode, detailAddress, isDefault);
                } else if (!TextUtils.isEmpty(flage) && flage.equals("modify")) {
                    addNewAddress("3", LoginUserProvider.getUser(BuildReceiptAddressActivity.this).getToken(), id, name, phone, proCode, cityCode, detailAddress, isDefault);
                }
            }
        });
        //省份
        findViewById(R.id.ll_province).setOnClickListener(this);
        //市区
        findViewById(R.id.ll_city).setOnClickListener(this);
        tv_province = (TextView) findViewById(R.id.tv_province);
        tv_city = (TextView) findViewById(R.id.tv_city);

        et_name = (EditText) findViewById(R.id.et_name);//姓名
        et_phone = (EditText) findViewById(R.id.et_phone);//电话
        et_phone.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_phone.setText("");
                    mobile = "";
                    // 获得焦点
                } else {
                    // 失去焦点
                }
            }
        });
        et_detail_address = (EditText) findViewById(R.id.et_detail_address);//详细地址

        bt_is_default_address = (Button) findViewById(R.id.bt_is_default_address);
        bt_is_default_address.setOnClickListener(this);
        if (isOpen) {
            isOpen = false;
            bt_is_default_address.setBackgroundResource(R.drawable.hand_close);
        } else {
            isOpen = true;
            bt_is_default_address.setBackgroundResource(R.drawable.hand_open);
        }
    }

    @Override
    protected void initData() {
        conCity = new CommonPopCityUtil(BuildReceiptAddressActivity.this);
        con = new CommonPopUtil(BuildReceiptAddressActivity.this);
        conCity.setReceiverListener(new ProvinceListener() {

            @Override
            public void getProvinceValue(String value, String code, String city) {
                tv_province.setText(value);
                proCode = code;
                cityCode = "";
                doutil.put("ProvinceId", code);
                //if (!tv_city.getText().toString().equals("请选择所在市区"))
                //  tv_city.setText("");
                //else
                tv_city.setText(city);

            }
        });
        con.setReceiverListener(new CityListener() {
            @Override
            public void getCityValue(String value, String code, String proId) {
                tv_city.setText(value);
                proCode = proId;
                cityCode = code;
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_province:
                doutil.put("ProvinceId", "");
                conCity.showCityPopWindow(v);
                break;
            case R.id.ll_city:
                if (!TextUtils.isEmpty(flage) && flage.equals("new")) {
                } else if (!TextUtils.isEmpty(flage) && flage.equals("modify")) {
                    StrMatchPro.ParseJson(this, tv_province.getText().toString());
                }
                //StrMatchPro.ParseJson(this, tv_province.getText().toString());
                if (TextUtils.isEmpty(tv_province.getText().toString())) {
                    Toast.makeText(this, "请先选择省份", 0).show();
                } else {
                    con.showCityPopWindow(v);
                }
                break;
            case R.id.bt_is_default_address:
                if (isOpen) {
                    onEvent(BuildReceiptAddressActivity.this, "801004_bjshdz_gbswmrdz_click");
                    isOpen = false;
                    bt_is_default_address.setBackgroundResource(R.drawable.hand_close);
                } else {
                    onEvent(BuildReceiptAddressActivity.this, "801003_bjshdz_dkswmrdz_click");
                    isOpen = true;
                    bt_is_default_address.setBackgroundResource(R.drawable.hand_open);
                }
                break;
            case R.id.rl_delete:
                onEvent(BuildReceiptAddressActivity.this, "801005_bjshdz_scgdz_click");
                //删除地址
                dialog = new ContactServiceDialog(this, R.style.SelectPicDialog);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
                break;
            default:
                break;
        }
    }

    //删除弹出框
    public class ContactServiceDialog extends Dialog {
        Context context;

        public ContactServiceDialog(Context context) {
            super(context);
            this.context = context;
        }

        public ContactServiceDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_delete_confirm_address);

            TextView tv_delete = (TextView) findViewById(R.id.tv_delete);
            TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onEvent(BuildReceiptAddressActivity.this, "801006_bjshdz_qrscgdz_click");
                    dialog.dismiss();
                    deleteOneAddress("3", LoginUserProvider.getUser(mContext).getToken(), id);
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    //点击空白处隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {/*** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    //添加或修改地址
    private void addNewAddress(String from, String token, String id, String name, String mobile, String provinceCode, String cityCode, String address, String isDefault) {
        CustomProgress.show(BuildReceiptAddressActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(NewBuildReceiptAddressActivity.this));
        String url = Urls.ADDNEWADDRESS;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("id", id);
        params.put("name", name);
        params.put("mobile", mobile);
        params.put("provinceCode", provinceCode);
        params.put("cityCode", cityCode);
        params.put("address", address);
        params.put("isDefault", isDefault);

        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        Toast.makeText(BuildReceiptAddressActivity.this, "保存成功", 0).show();
                        if (!TextUtils.isEmpty(flage) && flage.equals("new")) {
                            Intent intent = new Intent();
                            AddressManagerEntity entity = new AddressManagerEntity();
                            entity.setName(et_name.getText().toString());
                            entity.setMobile(et_phone.getText().toString());
                            entity.setProvince(tv_province.getText().toString());
                            entity.setCity(tv_city.getText().toString());
                            entity.setAddress(et_detail_address.getText().toString());
                            intent.putExtra("address", entity);
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BuildReceiptAddressActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BuildReceiptAddressActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BuildReceiptAddressActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(BuildReceiptAddressActivity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(BuildReceiptAddressActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BuildReceiptAddressActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(BuildReceiptAddressActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取单个收货地址
    private void getOneAddress(String from, String token, String id) {
        CustomProgress.show(BuildReceiptAddressActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(NewBuildReceiptAddressActivity.this));
        String url = Urls.GETONEADDRESS;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("id", id);

        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        et_name.setText(dataObj.getString("name"));
                        //et_phone.setText(StringUtils.phoneEncrypt(dataObj.getString("mobile")));
                        mobile = dataObj.getString("mobile");
                        et_phone.setText(StringUtils.phoneEncrypt(dataObj.getString("mobile")));
                        tv_province.setText(dataObj.getString("province"));
                        tv_city.setText(dataObj.getString("city"));
                        et_detail_address.setText(dataObj.getString("address"));
                        proCode = dataObj.getString("provinceCode");
                        cityCode = dataObj.getString("cityCode");
                        if (dataObj.getString("isDefault").equals("是")) {
                            isOpen = true;
                            bt_is_default_address.setBackgroundResource(R.drawable.hand_open);
                        } else if (dataObj.getString("isDefault").equals("否")) {
                            isOpen = false;
                            bt_is_default_address.setBackgroundResource(R.drawable.hand_close);
                        }

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(BuildReceiptAddressActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(BuildReceiptAddressActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(BuildReceiptAddressActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(BuildReceiptAddressActivity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(BuildReceiptAddressActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BuildReceiptAddressActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(BuildReceiptAddressActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //删除地址
    private void deleteOneAddress(String from, String token, String id) {
        CustomProgress.show(mContext);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(mContext));
        String url = Urls.DELETEONEADDRESS;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("id", id);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        //mOnUpdateAddring.onDeleteItem(position);
                        Toast.makeText(mContext, "地址删除成功", 0).show();
                        finish();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(mContext).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(mContext, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            mContext.startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            mContext.startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(mContext);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(mContext, "请检查网络", 0).show();
            }
        });
    }
}
