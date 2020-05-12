package com.ztmg.cicmorgan.integral.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ztmg.cicmorgan.integral.entity.AddressManagerEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 订单确认  dong
 *
 * @author pc
 */
public class OrderConfirmActivity extends BaseActivity implements OnClickListener {
    private static final int ADDRESS = 101;
    private static final int ADDMANAGER = 102;
    private static final int REFRESHED = 100;
    private TextView tv_username;
    private String myAwardId;
    private TextView tv_goods_name, tv_surplus, tv_real_integral,
            tv_goods_introduce, tv_deadline, tv_goods_integral, tv_real_lottery,
            tv_transport, tv_express_delivery_number;
    private ImageView iv_goods_img;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private RelativeLayout rl_address, rl_no_address;
    private TextView tv_userphone, tv_address;
    private String addressId;//地址id
    private List<AddressManagerEntity> addressList;
    private Button bt_exchange;
    private String isReceive;
    private ImageView iv_enter;
    private String realNeedAmount;
    private SlowlyProgressBar slowlyProgressBar;
    int mindex;
    int newProgress = 0;
    @BindView(R.id.ll_transport)
    LinearLayout ll_transport;
    @BindView(R.id.ll_express_delivery_number)
    LinearLayout ll_express_delivery_number;
    @BindView(R.id.view5)
    View view5;


    @SuppressLint("HandlerLeak")
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
            SystemBarTintManager tintManager = new SystemBarTintManager(OrderConfirmActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        initView();
        initData();
        Intent intent = getIntent();
        myAwardId = intent.getStringExtra("myAwardId");
        //noReceive未领取的是从我的奖品列表跳转，点击兑换finish,receive从订单详情页跳转的,点击兑换跳转到我的抽奖列表
        isReceive = intent.getStringExtra("isReceive");
        mInflater = LayoutInflater.from(OrderConfirmActivity.this);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);
        getUserAwardInfo("3", LoginUserProvider.getUser(OrderConfirmActivity.this).getToken(), myAwardId);


        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(OrderConfirmActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void initView() {
        setTitle("订单确认");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(OrderConfirmActivity.this, "601001_ddqr_back_click");
                finish();
            }
        });
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_userphone = (TextView) findViewById(R.id.tv_userPhone);
        tv_address = (TextView) findViewById(R.id.tv_address);
        findViewById(R.id.iv_add_address).setOnClickListener(this);
        bt_exchange = (Button) findViewById(R.id.bt_exchange);
        bt_exchange.setOnClickListener(this);
        rl_address = (RelativeLayout) findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
        iv_enter = (ImageView) findViewById(R.id.iv_enter);

        rl_no_address = (RelativeLayout) findViewById(R.id.rl_no_address);
        iv_goods_img = (ImageView) findViewById(R.id.iv_goods_img);
        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        tv_goods_integral = (TextView) findViewById(R.id.tv_goods_integral);
        tv_real_lottery = (TextView) findViewById(R.id.tv_real_lottery);
        tv_transport = (TextView) findViewById(R.id.tv_transport);
        tv_express_delivery_number = (TextView) findViewById(R.id.tv_express_delivery_number);

        //剩余积分
        tv_surplus = (TextView) findViewById(R.id.tv_surplus);
        tv_real_integral = (TextView) findViewById(R.id.tv_real_integral);
        tv_goods_introduce = (TextView) findViewById(R.id.tv_goods_introduce);
        tv_deadline = (TextView) findViewById(R.id.tv_deadline);

        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_address://新增收货地址
                onEvent(OrderConfirmActivity.this, "601002_ddqr_xzshdz_click");
                Intent addAddressIntent = new Intent(OrderConfirmActivity.this, BuildReceiptAddressActivity.class);
                addAddressIntent.putExtra("newifmodify", "new");
                addAddressIntent.putExtra("isDefault", "否");
                startActivityForResult(addAddressIntent, ADDRESS);
                break;
            case R.id.bt_exchange://下单
                onEvent(OrderConfirmActivity.this, "601003_ddqr_qrxd_click");
                //String needIntegral = tv_real_integral.getText().toString();
                if (rl_no_address.getVisibility() != 0) {
                    awardToUser("3", LoginUserProvider.getUser(OrderConfirmActivity.this).getToken(), addressId, realNeedAmount, myAwardId);
                } else {
                    Toast.makeText(OrderConfirmActivity.this, "请选择收货地址", 0).show();
                }
                break;
            case R.id.rl_address://地址管理
                onEvent(OrderConfirmActivity.this, "601004_ddqr_dzlb_click");
                Intent addressIntent = new Intent(OrderConfirmActivity.this, AddressManageActivity.class);
                addressIntent.putExtra("newifmodify", "modify");
                startActivityForResult(addressIntent, ADDMANAGER);
                break;
            default:
                break;
        }
    }


    //收货地址列表
    private void getData(String from, String token, final int pageNo, int pageSize) {
        //		CustomProgress.show(OrderConfirmActivity.this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(OrderConfirmActivity.this));
        String url = Urls.ADDRESSLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //CustomProgress.CustomDismis();
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                //rl_content.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            //有收货地址
                            //rl_address.setVisibility(View.VISIBLE);
                            //rl_no_address.setVisibility(View.GONE);
                            addressList = new ArrayList<AddressManagerEntity>();
                            rl_address.setVisibility(View.VISIBLE);
                            rl_no_address.setVisibility(View.GONE);
                            AddressManagerEntity entity;
                            for (int i = 0; i < dataArray.length(); i++) {
                                entity = new AddressManagerEntity();
                                JSONObject obj = dataArray.getJSONObject(i);
                                entity.setId(obj.getString("id"));
                                entity.setAddress(obj.getString("address"));
                                entity.setIsDefault(obj.getString("isDefault"));
                                entity.setName(obj.getString("name"));
                                entity.setProvince(obj.getString("province"));
                                entity.setMobile(obj.getString("mobile"));
                                entity.setCity(obj.getString("city"));
                                addressList.add(entity);
                            }
                            for (AddressManagerEntity entityObj : addressList) {
                                if (entityObj.getIsDefault().equals("是")) {
                                    // 默認地址
                                    tv_username.setText("收货人：" + entityObj.getName());
                                    tv_userphone.setText(StringUtils.phoneEncrypt(entityObj.getMobile()));
                                    tv_address.setText("收货地址：" + entityObj.getProvince() + entityObj.getCity() + entityObj.getAddress());
                                    addressId = entityObj.getId();
                                    break;
                                } else {
                                    tv_username.setText("收货人：" + addressList.get(0).getName().toString());
                                    tv_userphone.setText(StringUtils.phoneEncrypt(addressList.get(0).getMobile().toString()));
                                    tv_address.setText("收货地址：" + addressList.get(0).getProvince().toString() + addressList.get(0).getCity().toString() + addressList.get(0).getAddress().toString());
                                    addressId = addressList.get(0).getId();
                                }
                            }
                        } else {
                            //没有收货地址
                            rl_address.setVisibility(View.GONE);
                            rl_no_address.setVisibility(View.VISIBLE);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(OrderConfirmActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(OrderConfirmActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(OrderConfirmActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(OrderConfirmActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(OrderConfirmActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OrderConfirmActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                //				CustomProgress.CustomDismis();
                Toast.makeText(OrderConfirmActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //商品详情
    private void getUserAwardInfo(String from, String token, String userAwardId) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(OrderConfirmActivity.this));
        String url = Urls.GETUSERAWARDINFO;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("userAwardId", userAwardId);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String userAvailableBouns = dataObj.getString("userAvailableBouns");//用户可用积分
                        String userAwardId = dataObj.getString("userAwardId");//用户兑换奖品id
                        String awardId = dataObj.getString("awardId");//订单商品id
                        String state = dataObj.getString("state");//用户兑换奖品订单状态
                        realNeedAmount = dataObj.getString("realNeedAmount"); //订单实际所需积分
                        String expressNo = dataObj.getString("expressNo");// 订单快递号
                        String expressName = dataObj.getString("expressName");// 订单快递公司名称
                        String addressId = dataObj.getString("addressId");//订单发货地址id（可没有，虚拟商品没有）
                        String awardName = dataObj.getString("awardName");//订单商品名称
                        String awardDocs = dataObj.getString("awardDocs");//订单商品详情
                        String awardNeedAmount = dataObj.getString("awardNeedAmount");// 商品兑换所需积分
                        String awardIsTrue = dataObj.getString("awardIsTrue");//是否是虚拟商品
                        String awardImgWeb = dataObj.getString("awardImgWap");//图片地址
                        String deadline = dataObj.getString("deadline");//过期时间
                        mImageLoader.displayImage(awardImgWeb, iv_goods_img, mDisplayImageOptions);
                        tv_goods_name.setText(awardName);
                        tv_surplus.setText(userAvailableBouns);
                        tv_transport.setText(expressName != "null" ? expressName : "");
                        tv_express_delivery_number.setText(expressNo != "null" ? expressNo : "");

                        if (realNeedAmount.equals("0")) {
                            tv_real_integral.setText("10");
                            tv_real_lottery.setVisibility(View.VISIBLE);
                        } else {
                            tv_real_integral.setText(realNeedAmount);
                            tv_real_lottery.setVisibility(View.GONE);
                        }
                        tv_goods_integral.setText(awardNeedAmount + "积分");
                        tv_goods_introduce.setText(awardDocs);
                        tv_deadline.setText(deadline);
                        StateType(state);
                        if (!StringUtils.isEmpty(addressId)) {
                            getOneAddress("3", LoginUserProvider.getUser(OrderConfirmActivity.this).getToken(), addressId);
                        } else {
                            getData("3", LoginUserProvider.getUser(OrderConfirmActivity.this).getToken(), 1, 20);
                        }
                    } else {
                        Toast.makeText(OrderConfirmActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {

            }
        });
    }

    /**
     * 根据状态显示文案
     *
     * @param state
     */
    private void StateType(String state) {
        if (state.equals("0")) {
            bt_exchange.setText("确认下单");//待下单
            ll_transport.setVisibility(View.GONE);
            ll_express_delivery_number.setVisibility(View.GONE);
            view5.setVisibility(View.GONE);
        } else if (state.equals("1")) {
            bt_exchange.setText("已下单");
            bt_exchange.setBackgroundResource(R.color.text_cccccc);
            bt_exchange.setEnabled(false);
            rl_address.setOnClickListener(null);
            //iv_enter.setVisibility(View.INVISIBLE);
            tv_deadline.setTextColor(getResources().getColor(R.color.text_cccccc));
        } else if (state.equals("2")) {
            bt_exchange.setText("已发货");
            bt_exchange.setBackgroundResource(R.color.text_cccccc);
            bt_exchange.setEnabled(false);
            rl_address.setOnClickListener(null);
            //iv_enter.setVisibility(View.INVISIBLE);
            tv_deadline.setTextColor(getResources().getColor(R.color.text_cccccc));
        } else if (state.equals("3")) {
            bt_exchange.setText("已结束");
            bt_exchange.setBackgroundResource(R.color.text_cccccc);
            bt_exchange.setEnabled(false);
            rl_address.setOnClickListener(null);
            //iv_enter.setVisibility(View.INVISIBLE);
            tv_deadline.setTextColor(getResources().getColor(R.color.text_cccccc));
        } else if (state.equals("4")) {
            bt_exchange.setText("已兑现");
            bt_exchange.setBackgroundResource(R.color.text_cccccc);
            bt_exchange.setEnabled(false);
            rl_address.setOnClickListener(null);
            //iv_enter.setVisibility(View.INVISIBLE);
            tv_deadline.setTextColor(getResources().getColor(R.color.text_cccccc));
        } else if (state.equals("5")) {
            bt_exchange.setText("已失效");
            bt_exchange.setBackgroundResource(R.color.text_cccccc);
            bt_exchange.setEnabled(false);
            rl_address.setOnClickListener(null);
            //iv_enter.setVisibility(View.INVISIBLE);
            tv_deadline.setTextColor(getResources().getColor(R.color.text_cccccc));
        }
    }

    //下单
    private void awardToUser(String from, String token, String addressId, String needAmount, String myAwardId) {
        CustomProgress.show(OrderConfirmActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(OrderConfirmActivity.this));
        String url = Urls.MYAWARDINFO;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("addressId", addressId);
        params.put("needAmount", needAmount);
        params.put("myAwardId", myAwardId);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        Toast.makeText(OrderConfirmActivity.this, "奖品兑换成功", 0).show();
                        if (isReceive.equals("noReceive")) {
                            finish();
                        } else {
                            //跳转奖品列表
                            Intent exIntent = new Intent(OrderConfirmActivity.this, GoodsListActivity.class);
                            startActivity(exIntent);
                        }
                    } else if (jsonObject.getString("state").equals("3")) {
                        //积分不足
                        Toast.makeText(OrderConfirmActivity.this, jsonObject.getString("message"), 0).show();
                        //IntegralNotEnough noIntegralDialog = new IntegralNotEnough(OrderConfirmActivity.this, R.style.MyDialogDeletAddress);
                        //noIntegralDialog.show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(OrderConfirmActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(OrderConfirmActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(OrderConfirmActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(OrderConfirmActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(OrderConfirmActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OrderConfirmActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(OrderConfirmActivity.this, "请检查网络", 0).show();
            }
        });

    }

    //获取单个收货地址
    private void getOneAddress(String from, String token, String id) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        String url = Urls.GETONEADDRESS;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("id", id);

        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        tv_username.setText("收货人：" + dataObj.getString("name"));
                        tv_userphone.setText(StringUtils.phoneEncrypt(dataObj.getString("mobile")));
                        tv_address.setText("收货地址：" + dataObj.getString("province") + dataObj.getString("city") + dataObj.getString("address"));
                        iv_enter.setVisibility(View.INVISIBLE);
                        rl_no_address.setVisibility(View.GONE);
                        rl_address.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(OrderConfirmActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(OrderConfirmActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(OrderConfirmActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(OrderConfirmActivity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(OrderConfirmActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OrderConfirmActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(OrderConfirmActivity.this, "请检查网络", 0).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADDRESS:
                    //请求网络
                    getData("3", LoginUserProvider.getUser(OrderConfirmActivity.this).getToken(), 1, 20);
                    //TODO
                    break;
                case ADDMANAGER:
                    rl_address.setVisibility(View.VISIBLE);
                    rl_no_address.setVisibility(View.GONE);
                    AddressManagerEntity entity = (AddressManagerEntity) data.getSerializableExtra("address");
                    tv_username.setText("收货人：" + entity.getName());
                    tv_userphone.setText(StringUtils.phoneEncrypt(entity.getMobile()));
                    tv_address.setText("收货地址：" + entity.getProvince() + entity.getCity() + entity.getAddress());
                    addressId = entity.getId();
                    break;

                default:
                    break;
            }
        } else if (resultCode == REFRESHED) {
            getData("3", LoginUserProvider.getUser(OrderConfirmActivity.this).getToken(), 1, 20);
        }
    }
}
