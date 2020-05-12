package com.ztmg.cicmorgan.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

import com.ztmg.cicmorgan.account.entity.CityEntity;
import com.ztmg.cicmorgan.account.entity.ProvinceEntity;

/**
 * 地区匹配code
 *
 * @author sks
 */
public class StrMatchPro {
    private static List<ProvinceEntity> provinceList;
    private static List<CityEntity> cityList;


    public static String ParseJson(Context ctx, String str) {
        final DoCacheUtil doutil = DoCacheUtil.get(ctx);
        String result = "";
        provinceList = new ArrayList<ProvinceEntity>();
        InputStream input;
        try {
            input = ctx.getAssets().open("province.xml");
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
            for (ProvinceEntity bean : provinceList) {
                if (bean.getName().equals(str)) {
                    result = bean.getCode();
                    doutil.put("ProvinceId", result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }


    /**
     * 匹配地區
     *
     * @param ctx
     * @param str
     * @return
     */
    public static String ParseJsonCity(Context ctx, String str) {
        String cityCode = "";
        List<CityEntity> cityList = new ArrayList<CityEntity>();

        InputStream is;
        try {
            is = ctx.getAssets().open("city.xml");
            byte[] arrayOfByte = new byte[is.available()];
            is.read(arrayOfByte);
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

            for (CityEntity bean : cityList) {
                if (bean.getName().equals(str)) {
                    cityCode = bean.getCode();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityCode;

    }


}
