package com.ztmg.cicmorgan.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageloaderUtils {

	private static ImageLoader imageLoader;
	private static DisplayImageOptions displayImageOptions;

	/**
	 * 获得一个imageloader的实例
	 * 
	 * @return
	 */
	public static ImageLoader getImageLoader() {
		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
		}
		return imageLoader;
	}

	/**
	 * 获得一个imageloader的显示设置参数
	 * 
	 * @param imageRes_id
	 *            默认图片的id
	 * @param cacheOnDisk
	 *            true代表在本地做缓存
	 * @return
	 */
	public static DisplayImageOptions getSimpleDisplayImageOptions(
			int imageRes_id, boolean cacheOnDisk) {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(imageRes_id)
				.showImageForEmptyUri(imageRes_id).showImageOnFail(imageRes_id)
				.showImageOnLoading(imageRes_id).cacheInMemory(true)
				.cacheOnDisk(cacheOnDisk).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();// 声明ImageLoader的配置
	}

	public static DisplayImageOptions getOptionsCorner(boolean cacheOnDisk,
			int imageRes_id) {
		return new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(cacheOnDisk).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(20))
				.showImageForEmptyUri(imageRes_id).showImageOnFail(imageRes_id)
				.showImageOnLoading(imageRes_id).cacheInMemory(true)
				.cacheOnDisk(true).build();// 声明ImageLoader的配置
	}

	public static DisplayImageOptions getOptionsCorner(int cornerPx,
			int imageRes_id) {
		return new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(cornerPx))
				.showImageForEmptyUri(imageRes_id).showImageOnFail(imageRes_id)
				.showImageOnLoading(imageRes_id).cacheInMemory(true).build();// 声明ImageLoader的配置
	}

	public static DisplayImageOptions getHighQualityOption() {
		return new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();// 声明ImageLoader的配置
	}

	/**
	 * 根据原图添加圆角
	 * 
	 * @param source
	 * @return
	 */
	public static Bitmap createRoundConerImage(int mWidth, int mHeight,
			Bitmap source) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rect, 50f, 50f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

}
