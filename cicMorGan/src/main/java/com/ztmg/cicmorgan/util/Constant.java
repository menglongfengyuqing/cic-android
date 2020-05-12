package com.ztmg.cicmorgan.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.graphics.Bitmap;
import android.os.Environment;

public class Constant {

    public static String CurrentUserBean = "currentUserBean";// 当前登陆者的缓存key
    // TODO 待修改
    public static String CurrentDetailUserBean = "currentUserDetailBean";// 缓存详细信息

    public static String SelectionData = "SelectionData";// 详细的筛选信息

    public static final String SINA_APP_KEY = "2045436852";
    // 新浪默认的回调页
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    public static int NONET = 0; // 没有网络
    public static int WIFINET = 1; // wifi网络
    public static int WAPNET = 2; // 移动梦网
    public static int MOVENET = 3; // 移动数据网络
    public static int TIME_OUT = 10 * 1000; // 网络请求超时
    // 配置API KEY
    public final static String API_KEY = "ybAzlCWy27ULZCBvMZNkYzpvYboHvorg";

    public static boolean isboolean = false;//判断token是否验证完整

    // 配置帐户ID
    public final static String USERID = "9CD8808408744A19";

    // 寻找视频地址的接口
    public final static String VIDEO_ADDRESS_INTER = "http://union.bokecc.com/api/mobile";

    public final static String INNER_DATA = "innerData";
    //
    public static final String COUNTRY_DATA = "countryData";

    public static String CURRENT_COMPANY_TYPE_ONE = "";

    public static String CURRENT_COMPANY_TYPE = "";

    public static final String IS_ALLOW_JPHSH = "isAllowJPUSH";

    public static final String SP_NAME = "sp_name";

    public static final String HAS_LOADED_SPLASHPAGE = "has_loaded";

    public static final String BASE_DIR_PATH = Environment.getExternalStorageDirectory() + "/com.ittn.mobile";

    /*--------详情-----------------*/

    public static final String InvestmentDetailKey = "InvestmentDetail_Key";//出借详情 返回的json 存本地

    // 1、  详情  供应链 2 和安心投 1 ,我的出借  3
    public static final String Investment_Detail_SupplyChain_Relieved_Key = "Investment_Detail_SupplyChain_Relieved_Key";
    // 2、  我的出借      有没有付款方  供应链0  安心投 1
    public static final String SupplyChainInvestmentKey_mine_chujie = "SupplyChainInvestmentKey_chujie";
    // 2、  判断出借协议   是否是供应链 1 和安心投 2
    public static final String MyInvestmentKey = "MyInvestmentKey";

    /**
     * 获得一个imageloader的显示设置参数
     *
     * @param imageRes_id 默认图片的id
     * @param cacheOnDisk true代表在本地做缓存
     * @return
     */
    public static DisplayImageOptions getSimpleDisplayImageOptions(
            int imageRes_id, boolean cacheOnDisk) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(imageRes_id)
                .showImageForEmptyUri(imageRes_id).showImageOnFail(imageRes_id)
                .showImageOnLoading(imageRes_id).cacheInMemory(true)
                .cacheOnDisk(cacheOnDisk).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();// 声明ImageLoader的配置
    }

    //	public static SelectionEntity selectorEntity;

    //	public static DisplayImageOptions getSimpleDisplayImageOptions() {
    //		return new DisplayImageOptions.Builder()
    //				.showImageOnLoading(R.drawable.default_200)
    //				.showImageForEmptyUri(R.drawable.default_200)
    //				.showImageOnFail(R.drawable.default_200)
    //				.showImageOnLoading(R.drawable.default_200).cacheInMemory(true)
    //				.cacheOnDisk(true).considerExifParams(true)
    //				.bitmapConfig(Bitmap.Config.ARGB_8888).build();// 声明ImageLoader的配置
    //	}
}
