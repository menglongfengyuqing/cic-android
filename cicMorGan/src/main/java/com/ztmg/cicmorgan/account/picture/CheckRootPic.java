package com.ztmg.cicmorgan.account.picture;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ztmg.cicmorgan.net.Common;

import java.util.ArrayList;
import java.util.List;

public class CheckRootPic {

    private static Context ctx;
    private static ContentResolver moObserver;
    private static List<ImgEntity> list;

    public static List<ImgEntity> getRootPic(Context ctx) {
        list = new ArrayList<ImgEntity>();
        moObserver = ctx.getContentResolver();
        Cursor cursor = moObserver.query(Common.Media_Url, null, null, new String[]{}, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Uri uri_temp = Uri.parse("content://media/external/images/media/" + id);
            ImgEntity entity = new ImgEntity(id, name, path, uri_temp.toString(), false);
            list.add(entity);
        }
        cursor.close();
        return list;
    }

    public static List<ImgEntity> getRootPicImg(Context ctx, String imgName) {
        list = new ArrayList<ImgEntity>();
        moObserver = ctx.getContentResolver();
        Cursor cursor = moObserver.query(Common.Media_Url, null, null, new String[]{}, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            if (name.equals(imgName + ".jpg")) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Uri uri_temp = Uri.parse("content://media/external/images/media/" + id);
                ImgEntity entity = new ImgEntity(id, name, path, uri_temp.toString(), false);
                list.add(entity);
            }
        }
        cursor.close();
        return list;
    }


}
