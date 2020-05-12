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
import com.ztmg.cicmorgan.view.wheelview.ArrayWheelAdapter;
import com.ztmg.cicmorgan.view.wheelview.OnWheelChangedListener;
import com.ztmg.cicmorgan.view.wheelview.WheelView;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommonPopUtil implements OnWheelChangedListener {

    public Context context;

    public CommonPopUtil(Context context) {
        this.context = context;
    }

    /**
     * 所有市区
     */
    protected List<CityEntity> mProvinceDatas;
    protected String[] mProvinceList;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 当前省的名称
     */
    protected CityEntity mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前市的code值
     */
    protected String mCurrentCityCode;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName = "";

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode = "";

    private WheelView mViewProvince;
    private CityListener receiverListener;
    private Button btn_confirm;
    private Button btn_concel;
    private String[] demo;

    public void showCityPopWindow(View v) {

        initProvinceDatas();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_city, null);
        final PopupWindow window = new PopupWindow(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_concel = (Button) view.findViewById(R.id.btn_concel);

        btn_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CityEntity city = showSelectedResult();
                //ToastUtils.show(context, city.getName()+city.getCode()+city.getProID());
                receiverListener.getCityValue(city.getName(), city.getCode(), city.getProID());
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
        //window.setContentView(view);

        // popWindow消失的监听
        // window.setOnDismissListener(listener);
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
     * 解析市区的XML数据
     */
    @SuppressWarnings("unused")
    protected void initProvinceDatas() {
        final DoCacheUtil doutil = DoCacheUtil.get(context);
        String PorvinceId = doutil.getAsString("ProvinceId");
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
                if (PorvinceId.substring(0).toString().equals(json.getString("ProID"))) {
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


        // */ 初始化默认选中的省、市、区
        if (provinceList != null && !provinceList.isEmpty()) {
            mCurrentProviceName = provinceList.get(0);

        }
        // */
        mProvinceDatas = new ArrayList<CityEntity>();
        mProvinceList = new String[provinceList.size()];
        for (int i = 0; i < provinceList.size(); i++) {
            // 遍历所有省的数据
            mProvinceDatas.addAll(provinceList);
            mProvinceList[i] = provinceList.get(i).getName();
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            int pCurrent = mViewProvince.getCurrentItem();
            mCurrentProviceName = mProvinceDatas.get(pCurrent);
        }
    }


    public CityEntity showSelectedResult() {
        return mCurrentProviceName;
    }

    public interface CityListener {
        public void getCityValue(String value, String code, String proId);
    }

    public CityListener getReceiverListener() {
        return receiverListener;
    }

    public void setReceiverListener(CityListener receiverListener) {
        this.receiverListener = receiverListener;
    }


}
