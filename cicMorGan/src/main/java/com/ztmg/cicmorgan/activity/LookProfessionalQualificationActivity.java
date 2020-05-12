package com.ztmg.cicmorgan.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.entity.ProfessionalQualificationEntity;
import com.ztmg.cicmorgan.util.ImageloaderUtils;
/**
 * 查看大图
 * @author admina
 *
 */
public class LookProfessionalQualificationActivity extends Activity{

	private ViewPager viewPager;
	private List<String> list;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	private List<View> imageList;
	private TextView tv_now;
	private TextView tv_total;
	private RelativeLayout rl_lookpic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_look_professional_qualification);
		initView();
		initData();
	
	}
	public void initView() {
		imageList=new ArrayList<View>();
		mImageLoader = ImageloaderUtils.getImageLoader();
		mDisplayImageOptions = ImageloaderUtils.getSimpleDisplayImageOptions(
				R.drawable.iv_scroll_default, true);
		list = (List<String>) getIntent()
				.getSerializableExtra("picList");
		int index = getIntent().getIntExtra("index", 0);//1
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		tv_now = (TextView) findViewById(R.id.tv_now);
		tv_total = (TextView)findViewById(R.id.tv_total);
		rl_lookpic = (RelativeLayout) findViewById(R.id.rl_lookpic);
		if(list!=null&&list.size()>0){
			ImageView imgView;
			for (int i = 0; i < list.size(); i++) {
				imageList.add(getPageView(i,list));
			}
			tv_now.setText("1");
		}
		rl_lookpic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}


	/**
	 * 构造ViewPage页面
	 * 
	 * @param context
	 * @param imgResId
	 * @return
	 */
	private View getPageView(int index,List<String> list) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View pageView = inflater.inflate(R.layout.item_viewger, null);
		ImageView iv_image = (ImageView) pageView.findViewById(R.id.iv_image);
		tv_total.setText(list.size()+"");
		if (!TextUtils.isEmpty(list.get(index))) {
			mImageLoader.displayImage(list.get(index), iv_image, mDisplayImageOptions);
		}
		return pageView;
	}

	public void initData() {
		PicAdapter adapter = new PicAdapter();
		viewPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				tv_now.setText(arg0+1+"");
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	class PicAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(imageList.get(position));
			
			return imageList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imageList.get(position));
		}

		@Override
		public int getCount() {
			return imageList != null && imageList.size() > 0 ? imageList.size() : 0;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
	}

	
}
