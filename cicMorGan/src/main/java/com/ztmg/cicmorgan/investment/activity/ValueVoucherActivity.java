package com.ztmg.cicmorgan.investment.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.adapter.ValueVoucherAdapter;
import com.ztmg.cicmorgan.investment.entity.ValueVoucherEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券
 *
 * @author pc
 */
public class ValueVoucherActivity extends BaseActivity implements OnItemClickListener {
    private ListView lv_value_voucher_list;
    private ValueVoucherAdapter adapter;
    private ValueVoucherEntity entity;
    private List<ValueVoucherEntity> valueVoucherList;
    private List<ValueVoucherEntity> usableList;//已推荐使用优惠券的集合
    private double usableCoupons;//已推荐优惠券集合的和

    private String inputmoney;
    private View footView;
    private String projectid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(ValueVoucherActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_value_voucher);
        footView = View.inflate(ValueVoucherActivity.this, R.layout.activity_test, null);
        initView();
        initData();
        Intent intent = getIntent();
        inputmoney = intent.getStringExtra("inputmoney");
        usableList = (List<ValueVoucherEntity>) intent.getSerializableExtra("usableList");
        usableCoupons = intent.getDoubleExtra("usableCoupons", 0);
        projectid = intent.getStringExtra("projectid");

        getData(LoginUserProvider.getUser(ValueVoucherActivity.this).getToken(), "3", "1", projectid);
    }

    @Override
    protected void initView() {
        setTitle("优惠券");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setRightText("完成", new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("valueVoucherList", (Serializable) valueVoucherList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        lv_value_voucher_list = (ListView) findViewById(R.id.lv_value_voucher_list);
        lv_value_voucher_list.setOnItemClickListener(this);
        lv_value_voucher_list.addFooterView(footView);
    }

    @Override
    protected void initData() {

    }

    private void getData(String token, String from, String state, String projectid) {
        CustomProgress.show(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(ValueVoucherActivity.this));
        String url = Urls.GETUSERAWARDSHISTORYLIST;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("state", state);
        params.put("projectId", projectid);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                CustomProgress.CustomDismis();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        valueVoucherList = new ArrayList<ValueVoucherEntity>();
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        JSONArray array = dataObj.getJSONArray("awardsList");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                entity = new ValueVoucherEntity();
                                JSONObject obj = array.getJSONObject(i);
                                entity.setId(obj.getString("id"));
                                entity.setGetDate(obj.getString("getDate"));
                                entity.setLimitAmount(obj.getString("limitAmount"));
                                entity.setOverdueDate(obj.getString("overdueDate"));
                                entity.setState(obj.getString("state"));
                                entity.setType(obj.getString("type"));
                                entity.setValue(obj.getString("value"));
                                double inputmoneyDou = Double.parseDouble(inputmoney);
                                double limitAmountDou = Double.parseDouble(obj.getString("limitAmount"));
                                if (inputmoneyDou >= limitAmountDou) {
                                    valueVoucherList.add(entity);
                                }
                            }
                            for (int i = 0; i < usableList.size(); i++) {

                                for (ValueVoucherEntity entity : valueVoucherList) {
                                    if (entity.getId().equals(usableList.get(i).getId())) {
                                        valueVoucherList.remove(entity);
                                        break;
                                    }
                                }
                                usableList.get(i).setCheck(true);
                            }
                            valueVoucherList.addAll(0, usableList);
                        }
                        adapter = new ValueVoucherAdapter(ValueVoucherActivity.this, valueVoucherList);
                        lv_value_voucher_list.setAdapter(adapter);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(ValueVoucherActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(ValueVoucherActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(ValueVoucherActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(ValueVoucherActivity.this);
                        //						LoginUserProvider.cleanDetailData(ValueVoucherActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(ValueVoucherActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(ValueVoucherActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ValueVoucherActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(ValueVoucherActivity.this, "请检查网络", 0).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        //		Intent intent = new Intent();
        //		intent.putExtra("value", valueVoucherList.get(position).getValue());
        //		intent.putExtra("date", valueVoucherList.get(position).getOverdueDate());
        //		intent.putExtra("pid", valueVoucherList.get(position).getId());
        //		intent.putExtra("type", valueVoucherList.get(position).getType());//1抵用券2加息券
        //		setResult(RESULT_OK, intent);
        //		finish();
        double tempValue = 0;
        ValueVoucherEntity entity = valueVoucherList.get(position);
        for (ValueVoucherEntity entity1 : valueVoucherList) {
            if (entity1.isCheck()) {
                tempValue += Double.parseDouble(entity1.getValue());
            }
        }
        if (tempValue > usableCoupons) {
            ValueVoucherOverDialog dialog = new ValueVoucherOverDialog(ValueVoucherActivity.this, R.style.valueVoucherOverDialog, usableCoupons);
            //			Window dialogWindow = dialog.getWindow();
            //			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            //			lp.width = LayoutParams.FILL_PARENT;
            //			dialogWindow.setAttributes(lp);
            dialog.show();

            //			Toast.makeText(ValueVoucherActivity.this, "集合中选中的和大于了优惠金额", Toast.LENGTH_SHORT).show();
        } else {

            if (entity.isCheck()) {
                entity.setCheck(false);
            } else {
                entity.setCheck(true);
                tempValue += Double.parseDouble(entity.getValue());
                if (tempValue > usableCoupons) {
                    entity.setCheck(false);
                    ValueVoucherOverDialog dialog = new ValueVoucherOverDialog(ValueVoucherActivity.this, R.style.valueVoucherOverDialog, usableCoupons);
                    //					Window dialogWindow = dialog.getWindow();
                    //					WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    //					lp.width = LayoutParams.FILL_PARENT;
                    //					dialogWindow.setAttributes(lp);
                    dialog.show();
                    //					Toast.makeText(ValueVoucherActivity.this, "集合中选中的和大于了优惠金额", Toast.LENGTH_SHORT).show();
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    //联系客服弹框
    public class ValueVoucherOverDialog extends Dialog {
        Context context;

        public ValueVoucherOverDialog(Context context) {
            super(context);
            this.context = context;
        }

        public ValueVoucherOverDialog(Context context, int theme, double money) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_value_voucher_over);

            TextView tv_text = (TextView) findViewById(R.id.tv_text);//在线客服
            tv_text.setText("抵扣金额最多不超过" + money + "元");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 2000);
        }
    }
}
