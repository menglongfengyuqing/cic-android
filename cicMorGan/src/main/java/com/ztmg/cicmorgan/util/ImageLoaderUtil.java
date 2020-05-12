package com.ztmg.cicmorgan.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageLoaderUtil {
	public static ImageLoader getImageLoader() {
		return ImageLoader.getInstance();
	}

	public static DisplayImageOptions getDisplayImageOptions(int drawable_res,
			boolean cacheInMemory, boolean cacheOnDisk,
			boolean considerExifParams) {
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(drawable_res)
				.showImageForEmptyUri(drawable_res)
				.showImageOnFail(drawable_res).cacheInMemory(cacheInMemory)
				.cacheOnDisk(cacheOnDisk)
				.considerExifParams(considerExifParams)
				.bitmapConfig(Bitmap.Config.RGB_565).build();// ����ImageLoader������
		return displayImageOptions;
	}
}
