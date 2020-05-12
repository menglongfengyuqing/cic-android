package com.ztmg.cicmorgan.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.bigimage.DataTools;
import com.ztmg.cicmorgan.activity.bigimage.ImageLoaderUtils;
import com.ztmg.cicmorgan.activity.bigimage.My_Dialog;
import com.ztmg.cicmorgan.activity.bigimage.PhotoViewAttacher;
import com.ztmg.cicmorgan.activity.bigimage.PhotoViewAttacher.OnPhotoTapListener;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.ImageloaderUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class FullScreenImageActivity extends Activity implements
		View.OnClickListener, OnPageChangeListener {

	private ViewPager fullViewPager;
	private ArrayList<String> urls;
	private int currentPosition;
	private ImageView[] dots;
	private int currIndex;
	private ImageView imageView;
	private LinearLayout ll_popup;
	private TextView tv_now,tv_total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_image);
		super.setTheme(R.style.FullImageStyle);
		urls = getIntent().getStringArrayListExtra("urls");
		fullViewPager = (ViewPager) findViewById(R.id.search_mine_viewpager);
		currentPosition = getIntent().getIntExtra("currentPosition", 0);
		initViewPager();
		setCurDot(currentPosition);

	}

	public void initViewPager() {
		initDots(urls.size());
		fullViewPager.setAdapter(new FullScreenViewPagerAdapter(this, urls));
		fullViewPager.setOnPageChangeListener(this);
		if (urls != null) {
			fullViewPager.setCurrentItem(currentPosition);
		}

	}

	public class FullScreenViewPagerAdapter extends PagerAdapter {

		private List<String> urls;

		private Context context;

		private List<View> views;

		public FullScreenViewPagerAdapter(Context context, List<String> urls) {
			this.context = context;
			this.urls = urls;
			views = new ArrayList<View>();
			if (urls != null) {
				for (int i = 0; i < urls.size(); i++) {
					ImageView imageView = getSomoothImageView(urls.get(i), i);
					views.add(imageView);
				}
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// �����������ﴦ��removeView
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = views.get(position);
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			return urls.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	private ProgressDialog mProgressDialog;

	public ImageView getSomoothImageView(final String url, final int position) {
		imageView = new ImageView(this);
		final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				FullScreenImageActivity.this.finish();
			}
		});
		if (url != null) {
			ImageLoaderUtils.getIntance().displayImage(url, imageView,
					new ImageLoadingListener() {
						// 图片加载的等待框
						My_Dialog myDialog = new My_Dialog(
								FullScreenImageActivity.this);

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							mAttacher.update();
							myDialog.DismissDialog();
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							myDialog.DismissDialog();
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							myDialog.DismissDialog();
						}

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							myDialog.ShowDialog();
						}
					});
		}
		return imageView;

	}

	public void initDots(int size) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.search_wine_dots_parent);
		tv_now = (TextView) findViewById(R.id.tv_now);
//		tv_now.setVisibility(View.GONE);
		tv_total = (TextView) findViewById(R.id.tv_total);
//		tv_total.setVisibility(View.GONE);
		dots = new ImageView[size];
		for (int i = 0; i < size; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setVisibility(View.GONE);
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.dot));
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin = DataTools.dip2px(this, 13);
			ll.addView(imageView, params);
			
			dots[i] = imageView;
			dots[i].setEnabled(true);
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);
//			tv_now.setText(i+"");
			tv_total.setText(size+"");
		}
		currIndex = 0;
		dots[currIndex].setEnabled(false);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public void setCurDot(int position) {
		if (position < 0 || position > urls.size() - 1 || currIndex == position) {
			return;
		}
		dots[position].setEnabled(false);
		dots[currIndex].setEnabled(true);
		currIndex = position;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		setCurDot(position);
		currIndex = position;
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		tv_now.setText(position+1+"");

	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}
}
