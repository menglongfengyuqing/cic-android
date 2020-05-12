package com.ztmg.cicmorgan.util;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @param ：数据解析
 * @author:
 * @date: 17/4/11
 * @Company:
 */

public class GsonManager {
    /**
     * 日志类型
     */
    public static String TAG = GsonManager.class.getSimpleName();

    public static Gson getGson() {
        return new GsonBuilder().serializeNulls().create();
    }

    /**
     * Gson实例创建，整个应用只有一个
     */
    private final static Gson gson = new Gson();

    public static JsonObject getJsonObject(String result) {
        return new JsonParser().parse(result).getAsJsonObject();
    }

    public static Gson getmGson() {
        return new GsonBuilder().registerTypeAdapter(String.class, new StringConverter()).serializeNulls().create();
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
        Object object = getGson().fromJson(json, (Type) classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }

    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    @Nullable
    public static <T> T fromJsonNew(JsonElement jsonStr, Class<T> clazz) {
        /* 参数异常处理 */
        if (clazz == null) {
            LogUtil.e("==fromJson,参数异常-->" + clazz);
            return null;
        }
        /* 正常处理 */
        T t = null;
        try {
            t = gson.fromJson(jsonStr, clazz);
        } catch (Exception e) {
            LogUtil.e("==fromJson,clazz-->" + clazz);
        }
        return t;
    }

    /**
     * /** 将一个String字符串转换成指定的JavaBean
     *
     * @param jsonStr json字符串
     * @param clazz   需要转换的实体
     * @return 需要转换的实体对话，如果转换失败，返回结果为空
     * @author wds
     * @date 2016-3-1 下午3:40:40
     */
    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        /* 参数异常处理 */
        if (clazz == null) {
            LogUtil.e("==fromJson,参数异常-->" + clazz);
            return null;
        }
        /* 正常处理 */
        T t = null;
        try {
            t = gson.fromJson(jsonStr, clazz);
        } catch (Exception e) {
            LogUtil.e("==fromJson,clazz-->" + clazz);
        }

        return t;
    }


    /**
     * 将一个列表数据字符串转换成指定的JavaBean的集合
     *
     * @param jsonStr
     * @param clazzs
     * @param <T>
     * @return
     */
    public static <T> List<Class> fromJsonArray(String jsonStr, List<T> clazzs) {
        /* 参数异常处理 */
        if (clazzs == null) {
            return null;
        }
        /*正常处理*/
        List<Class> ts = null;
        try {
            Type type = new TypeToken<List<T>>() {
            }.getType();
            ts = getGson().fromJson(jsonStr, type);
        } catch (JsonSyntaxException e) {
        }

        return ts;
    }

    /**
     * 需要转换的javaBean
     *
     * @param javaBean 一个java对象（注：不可以是List<JavaBean>）
     * @return 如果出现异常，则默认值为null
     * @author wds
     * @date 2016-3-1 下午3:33:45
     */
    @Nullable
    public static String toJson(Object javaBean) {
        /* 参数检测 */
        if (javaBean == null) {
            return null;
        }
        /* 开始返回结果 */
        String jsonStr = null;
        try {
            jsonStr = gson.toJson(javaBean);
        } catch (Exception e) {

        }
        return jsonStr;
    }
}
