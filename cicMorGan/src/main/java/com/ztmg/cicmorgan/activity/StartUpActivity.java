package com.ztmg.cicmorgan.activity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.CityEntity;
import com.ztmg.cicmorgan.account.entity.ProvinceEntity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.CellEntity;
import com.ztmg.cicmorgan.util.DoCacheUtil;

/**
 * 启动页
 *
 * @author pc
 */
public class StartUpActivity extends BaseActivity {

    private ImageView iv_icon;
    private boolean isfirst;// 是不是第一次来
    private SharedPreferences sp;

    private CellEntity data[] = {new CellEntity(0, 0), new CellEntity(1, 0),
            new CellEntity(2, 0), new CellEntity(0, 1), new CellEntity(1, 1),
            new CellEntity(2, 1), new CellEntity(0, 2), new CellEntity(1, 2),
            new CellEntity(2, 2)};
    private String mGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_startup);
        if (mUser == null) {
        } else {
            mGesture = mUser.getGesturePwd();
        }
        initView();
        initData();
        //解析省份
        initProvinceDatas();
        //解析市
        initCityDatas();

    }

    @Override
    protected void initView() {
        sp = getSharedPreferences("txt", MODE_PRIVATE);
        isfirst = sp.getBoolean("isFirst", true);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
    }

    @Override
    protected void initData() {
        new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (isfirst) {
                    sp.edit().putBoolean("isFirst", false).commit();
                    startActivity(new Intent(StartUpActivity.this, SplashActivity.class));
                } else {
                    DoCacheUtil util = DoCacheUtil.get(StartUpActivity.this);
                    String str = util.getAsString("isLogin");
                    List<CellEntity> list = (List<CellEntity>) util.getAsObject("cellList");

                    StringBuffer sb = new StringBuffer();

                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < data.length; i++) {
                            for (CellEntity entity : list) {
                                if (entity.getX() == data[i].getX() && entity.getY() == data[i].getY()) {
                                    sb.append(String.valueOf(i));
                                }
                            }
                        }
                    }
                    sb.toString();
                    //if (list != null && list.size() > 0) {
                    if (str != null && str.equals("isLogin")) {//判断是登录状态
                        if (mGesture != null && mGesture.equals("1") && !mGesture.equals("")) {// 判断是否设置手势密码
                            Intent intent = new Intent(StartUpActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "1");
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(StartUpActivity.this, MainActivity.class));
                        }
                    } else {
                        startActivity(new Intent(StartUpActivity.this, MainActivity.class));
                    }
                    //} else {
                    //	startActivity(new Intent(StartUpActivity.this,
                    //			MainActivity.class));
                    //}
                }
                finish();
            }
        }.sendEmptyMessageDelayed(0, 2000);
    }

    /**
     * 解析省的XML数据
     */
    protected void initProvinceDatas() {
        List<ProvinceEntity> provinceList = new ArrayList<ProvinceEntity>();
        AssetManager asset = StartUpActivity.this.getAssets();

        InputStream input;
        try {
            input = asset.open("province.xml");
            byte[] arrayOfByte = new byte[input.available()];
            input.read(arrayOfByte);
            String address = EncodingUtils.getString(arrayOfByte, "UTF-8");
            JSONArray jsonList = new JSONArray(address);
            ProvinceEntity entity;
            for (int i = 0; i < jsonList.length(); i++) {
                JSONObject json = jsonList.getJSONObject(i);
                entity = new ProvinceEntity();
                entity.setCode(json.getString("code"));
                entity.setName(json.getString("name"));
                provinceList.add(entity);
            }
            DoCacheUtil cityDoutil = DoCacheUtil.get(StartUpActivity.this);
            cityDoutil.put("provinceList", (Serializable) provinceList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析市区的XML数据
     */
    @SuppressWarnings("unused")
    protected void initCityDatas() {
        final DoCacheUtil doutil = DoCacheUtil.get(StartUpActivity.this);
        List<CityEntity> cityList = new ArrayList<CityEntity>();
        AssetManager asset = StartUpActivity.this.getAssets();

        InputStream input;
        try {
            input = asset.open("city.xml");
            byte[] arrayOfByte = new byte[input.available()];
            input.read(arrayOfByte);
            String address = EncodingUtils.getString(arrayOfByte, "UTF-8");
            JSONArray jsonList = new JSONArray(address);
            CityEntity entity;
            for (int i = 0; i < jsonList.length(); i++) {
                JSONObject json = jsonList.getJSONObject(i);
                entity = new CityEntity();
                entity.setCode(json.getString("code"));
                entity.setName(json.getString("name"));
                entity.setProID(json.getString("ProID"));
                cityList.add(entity);
            }
            DoCacheUtil cityDoutil = DoCacheUtil.get(StartUpActivity.this);
            cityDoutil.put("cityList", (Serializable) cityList);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
