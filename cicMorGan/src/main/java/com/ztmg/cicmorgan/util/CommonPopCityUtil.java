package com.ztmg.cicmorgan.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.CityEntity;
import com.ztmg.cicmorgan.account.entity.ProvinceEntity;
import com.ztmg.cicmorgan.view.wheelview.ArrayWheelAdapter;
import com.ztmg.cicmorgan.view.wheelview.OnWheelChangedListener;
import com.ztmg.cicmorgan.view.wheelview.WheelView;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class CommonPopCityUtil implements OnWheelChangedListener {

    public Context context;

    public CommonPopCityUtil(Context context) {
        this.context = context;
    }

    /**
     * 所有省
     */
    protected List<ProvinceEntity> mProvinceDatas;
    protected String[] mProvinceList;

    /**
     * 当前省的名称
     */
    protected ProvinceEntity mCurrentProviceName;

    private WheelView mViewProvince;
    private ProvinceListener receiverListener;
    private Button btn_confirm;
    private Button btn_concel;

    private ProvinceEntity city;

    public void showCityPopWindow(View v) {
        final DoCacheUtil doutil = DoCacheUtil.get(context);
        initProvinceDatas();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_city, null);
        final PopupWindow window = new PopupWindow(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_concel = (Button) view.findViewById(R.id.btn_concel);
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                city = showSelectedResult();
                //ToastUtils.show(context, city.getName()+city.getCode());
                doutil.put("ProvinceId", city.getCode());
                CityDatas(city.getCode());
                window.dismiss();

            }
        });

        btn_concel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        // 保证点击外围也可以关闭
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        //		window.setContentView(view);

        // popWindow消失的监听
        //window.setOnDismissListener(listener);
        setUpViews(view);
        setUpListener();
        setUpData();
        window.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void setUpViews(View view) {
        mViewProvince = (WheelView) view.findViewById(R.id.id_province);
    }

    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceList));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
    }

    /**
     * 解析省的XML数据
     */
    protected void initProvinceDatas() {
        List<ProvinceEntity> provinceList = new ArrayList<ProvinceEntity>();
        AssetManager asset = context.getAssets();

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

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 初始化默认选中的省
        if (provinceList != null && !provinceList.isEmpty()) {
            mCurrentProviceName = provinceList.get(0);

        }
        mProvinceDatas = new ArrayList<ProvinceEntity>();
        mProvinceList = new String[provinceList.size()];
        for (int i = 0; i < provinceList.size(); i++) {
            // 遍历所有省的数据
            mProvinceDatas.addAll(provinceList);
            mProvinceList[i] = provinceList.get(i).getName();
        }
    }


    /**
     * 解析市区的XML数据
     */
    @SuppressWarnings("unused")
    protected void CityDatas(String mPorvinceId) {
        final DoCacheUtil doutil = DoCacheUtil.get(context);
        //String PorvinceId = doutil.getAsString("ProvinceId");
        List<CityEntity> provinceList = new ArrayList<CityEntity>();
        AssetManager asset = context.getAssets();

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
                if (mPorvinceId.substring(0).toString().equals(json.getString("ProID"))) {
                    entity = new CityEntity();
                    entity.setCode(json.getString("code"));
                    entity.setName(json.getString("name"));
                    entity.setProID(json.getString("ProID"));
                    provinceList.add(entity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 初始化默认选中的市、区
        if (provinceList != null && !provinceList.isEmpty()) {
            mCurrentName = provinceList.get(0);
        }
        //        List<CityEntity> mProvinces = new ArrayList<CityEntity>();
        //        String[] mProvinceLists = new String[provinceList.size()];
        //        for (int i = 0; i < provinceList.size(); i++) {
        //            // 遍历所有省的数据
        //            mProvinces.addAll(provinceList);
        //            mProvinceLists[i] = provinceList.get(i).getName();
        //        }

        receiverListener.getProvinceValue(city.getName(), city.getCode(), mCurrentName.getName());
    }

    protected CityEntity mCurrentName;

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            int pCurrent = mViewProvince.getCurrentItem();
            mCurrentProviceName = mProvinceDatas.get(pCurrent);
        }
    }


    public ProvinceEntity showSelectedResult() {
        return mCurrentProviceName;
    }

    public interface ProvinceListener {
        public void getProvinceValue(String value, String code, String city);
    }

    public ProvinceListener getReceiverListener() {
        return receiverListener;
    }

    public void setReceiverListener(ProvinceListener receiverListener) {
        this.receiverListener = receiverListener;
    }


}
