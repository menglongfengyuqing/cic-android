package com.ztmg.cicmorgan.net;

import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * 存放一些变量
 *
 * @author pc
 */
public class Common {

    public static boolean mBoolean = false;
    public static List<String> dateList = new ArrayList<>();
    //系统照片数据库uri
    public static final Uri Media_Url = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
}

