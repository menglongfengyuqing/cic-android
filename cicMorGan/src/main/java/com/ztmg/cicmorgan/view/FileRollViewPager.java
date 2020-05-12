package com.ztmg.cicmorgan.view;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.entity.PagerBean;
import com.ztmg.cicmorgan.util.CommonUtil;
import com.ztmg.cicmorgan.util.ToastUtils;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class FileRollViewPager extends ViewPager {

	private BitmapUtils bitmapUtils;
	private PagerBean bean;
	private MyAdapter adapter;
	private TextView top_news_title;
	private Context context;
	private int lastPosition = 0;
	private int downX;
	private int downY;
	private ImageView iv_left,iv_right;

	//回调相关
	private OnPageClick onPageClick;
	//回调相关
	public interface OnPageClick{
		public abstract void onClick(String url);
	}

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//设置显示下一个页面
			setCurrentItem(getCurrentItem()+1);
			handler.sendEmptyMessageDelayed(0, 5000);
		};
	};

	//ll_dot用于存放点的线性布局，tv_title用于存放新闻标题，bean是轮播图所需数据
	public FileRollViewPager(final Context context,final LinearLayout ll_dot, TextView tv_title,ImageView iv_left,ImageView iv_right, final PagerBean bean, OnPageClick onPageClick) {
		super(context);
		this.context = context;
		this.iv_left = iv_left;
		this.iv_right = iv_right;
		this.bean = bean;
		this.onPageClick = onPageClick;

		//初始化点操作
		initDot(ll_dot);
		//1，传递文字数据，2传递显示数据的控件
		//		initTitle(bean.titles,tv_title);

		bitmapUtils = new BitmapUtils(context);
		setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {

				int indexPosition = arg0%bean.imgUrls.size();//当前选中小索引
				//				top_news_title.setText(bean.titles.get(indexPosition));

				//上一个点应该设置默认
				ll_dot.getChildAt(lastPosition).setBackgroundResource(R.drawable.dot_normal);
				//当前点设置高亮
				ll_dot.getChildAt(indexPosition).setBackgroundResource(R.drawable.dot_focus);

				lastPosition = indexPosition;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		iv_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCurrentItem(getCurrentItem()+1);
			}
		});
		iv_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCurrentItem(getCurrentItem()-1);
			}
		});
	}

	public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//点击下去的时候，不允许父控件去拦截相应的事件
			getParent().requestDisallowInterceptTouchEvent(true);
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			if(Math.abs(moveX-downX)>Math.abs(moveY-downY)){
				//x轴比Y轴移动的多,事件不可以被拦击，传递到当前viewpager上
				getParent().requestDisallowInterceptTouchEvent(true);
			}else{
				//y轴比x轴移动的多,事件需要被拦截，父控件响应事件(刷新操作)
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	//从界面中移出去后调用的方法
	@Override
	protected void onDetachedFromWindow() {
		//移除当前handler中所有维护的任务
		handler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	}

	public void initTitle(List<String> titleList, TextView top_news_title) {
		if(null!=top_news_title && null!=titleList && titleList.size()>0){
			top_news_title.setText(titleList.get(0));
		}
		this.top_news_title = top_news_title;
	}

	public void startRoll() {
		//0,数据填充viewpager
		if(adapter == null){
			adapter = new MyAdapter();
			this.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		//设置为中心点击-2亿多条的中间-1--肯定是imageViews.size()的倍数
		int middlePosition = Integer.MAX_VALUE/2-Integer.MAX_VALUE/2%bean.imgUrls.size();//(一开始的位置)
		//		middlePosition = 198/2-198/2%bean.imgUrls.size();
		setCurrentItem(middlePosition);
		handler.sendEmptyMessageDelayed(0, 5000);
	}

	class MyAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			//			return 198;
			//			return bean.imgUrls.size();
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View view = View.inflate(context,R.layout.viewpager_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.image);

			//xutils提供的异步下载图片的工具类(缓存图片(下载图片---->文件--(压缩)-->缓存（内存）))
			//java((具有LRU算法，最近最少使用算法)Map<String,Bitmap>最前端，三级缓存)（内存---->文件----->下载）
			bitmapUtils.display(imageView, bean.imgUrls.get(position%bean.imgUrls.size()));
			((FileRollViewPager)container).addView(view);

			view.setOnTouchListener(new OnTouchListener() {
				private long downTime;
				private int downX;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						handler.removeCallbacksAndMessages(null);
						downTime = System.currentTimeMillis();
						downX = (int) event.getX();
						break;
					case MotionEvent.ACTION_UP:
						handler.sendEmptyMessageDelayed(0, 5000);
						//						if(System.currentTimeMillis()-downTime<500 && (int)event.getX() == downX){
						if(System.currentTimeMillis()-downTime<500 && Math.abs((int)event.getX() - downX)<=5){
							//							Toast.makeText(context, bean.imgUrls.get(position%bean.imgUrls.size())+position, Toast.LENGTH_LONG).show();
							//回调相关
							onPageClick.onClick(bean.textUrls.get(position%bean.textUrls.size()));
						}
						break;
						//取消(viewpager中嵌套了一个view)
					case MotionEvent.ACTION_CANCEL:
						handler.sendEmptyMessageDelayed(0, 5000);
						break;
					}
					return true;
				}
			});
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((FileRollViewPager)container).removeView((View)object);
		}
	}

	private void initDot(LinearLayout ll_dot) {
		//封装点的集合
		List<View> dotList = new ArrayList<View> ();
		dotList.clear();
		//如果线性布局中就有点，做移除后添加的操作
		ll_dot.removeAllViews();
		if(bean.imgUrls.size()>0){
			for(int i=0;i<bean.imgUrls.size();i++){
				View view = new View(context);
				if(i==0){
					//选中
					view.setBackgroundResource(R.drawable.dot_focus);
				}else{
					//未选中
					view.setBackgroundResource(R.drawable.dot_normal);
				}
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						CommonUtil.dip2px(context, 6),
						CommonUtil.dip2px(context, 6));
				layoutParams.setMargins(
						CommonUtil.dip2px(context, 4), 0,
						CommonUtil.dip2px(context, 4), 0);
				view.setLayoutParams(layoutParams);

				dotList.add(view);
				ll_dot.addView(view);
			}
		}
		
	}
}
