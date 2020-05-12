package com.ztmg.cicmorgan.activity.bigimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ztmg.cicmorgan.R;

public class ImageLoaderUtils {
	private static ImageLoaderUtils instance = null;
	private static ImageLoader loader = null;
	private static DisplayImageOptions defaultOption;

	private static Context context;

	public ImageLoaderUtils(Context context) {
		this.context = context;
	}

	public static void init(Context context) {
		instance = new ImageLoaderUtils(context);
		loader = ImageLoader.getInstance();
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration
				.createDefault(context);
		loader.init(configuration);
		defaultOption = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
				.showImageForEmptyUri(R.drawable.iv_scroll_default)
				.showImageOnFail(R.drawable.iv_scroll_default).build();
	}

	// 不使用defalutOption
	public void displayImage(String uri, ImageView image,
			DisplayImageOptions options) {
		if (getImageLoader() != null) {
			getImageLoader().displayImage(uri, image, options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {

						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {

						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {

						}

						@Override
						public void onLoadingStarted(String arg0, View arg1) {

						}
					});
		}
	}

	// 需要监听事件
	public void displayImage(String uri, ImageView image,
			ImageLoadingListener listener) {
		if (getImageLoader() != null) {
			getImageLoader().displayImage(uri, image, listener);
		}
	}

	// 使用defaultOptions
	public void displayImage(String uri, ImageView image) {
		this.displayImage(uri, image, defaultOption);
	}

	public static ImageLoader getImageLoader() {
		loader = ImageLoader.getInstance();
		if (loader == null) {
			throw new IllegalArgumentException("需要在application中初始化");
		}
		return loader;
	}

	public static ImageLoaderUtils getIntance() {
		instance = new ImageLoaderUtils(context);
		if (instance == null) {
			throw new IllegalArgumentException("需要在application中初始化");
		}
		return instance;
	}

	public void loadImage(final String uri, final ImageView imageView) {
		loader.loadImage(uri, defaultOption, new ImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				if (imageView.getTag().equals(imageUri)) {
					imageView.setImageBitmap(loadedImage);
				}
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}
		});
	}
}
