package com.ztmg.cicmorgan.investment.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.RollViewActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.PagerBean;
import com.ztmg.cicmorgan.investment.entity.ImgEntity;
import com.ztmg.cicmorgan.investment.entity.InvestmentListEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.FileRollViewPager;

/**
 * 投资详情，相关文件
 */
public class InvestmentDetailFileActivity extends BaseActivity implements OnClickListener{

	private List<ImgEntity> docimgsList,wgimgList,imgList;//资质照片，风控照片，项目照片
	private LinearLayout ll_top_view_pager;//用于存放viewpager
	private LinearLayout ll_dot;//用于存放点
	private TextView tv_title;//轮播对应描述信息title
	private PagerBean pagerBean;//轮播数据
	private List<String> imgUrls,imgsUrls,docimgsUrls;//风控文件，项目照片，资质照片
	private ImageView iv_left,iv_right;
	private TextView tv_risk_file,tv_project_file,tv_loan_file;
	private TextView tv_none;
	private View il_have;
	private ImgEntity entity;
	private String creditInfoId;
	private String isNewType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_investment_detail_file);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
			setTranslucentStatus(true); 
			SystemBarTintManager tintManager = new SystemBarTintManager(InvestmentDetailFileActivity.this); 
			tintManager.setStatusBarTintEnabled(true); 
			tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
		} 
		Intent intent = getIntent();
//		isNewType = intent.getStringExtra("isNewType");
//		if(isNewType.equals("2")||isNewType.equals("3")){//供应链
//			creditInfoId = intent.getStringExtra("creditInfoId"); 
//		}else{
			docimgsList = (List<ImgEntity>) intent.getSerializableExtra("docimgsList");
			wgimgList = (List<ImgEntity>) intent.getSerializableExtra("wgimgList");
			imgList = (List<ImgEntity>) intent.getSerializableExtra("imgList");
//		}
		initView();
		initData();
	}

	@Override
	protected void initView() {
		setTitle("相关文件");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ll_top_view_pager = (LinearLayout) findViewById(R.id.ll_top_view_pager);
		iv_left = (ImageView) findViewById(R.id.iv_left);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		tv_title = (TextView) findViewById(R.id.tv_title);
		ll_dot = (LinearLayout) findViewById(R.id.ll_dot);
		tv_risk_file = (TextView) findViewById(R.id.tv_risk_file);//风控文件
		tv_project_file = (TextView) findViewById(R.id.tv_project_file);//项目照片
		tv_loan_file = (TextView) findViewById(R.id.tv_loan_file);//借款资质
		tv_risk_file.setOnClickListener(this);
		tv_project_file.setOnClickListener(this);
		tv_loan_file.setOnClickListener(this);
		tv_none = (TextView) findViewById(R.id.tv_none);
		il_have = findViewById(R.id.il_have);

//		if(isNewType.equals("2")||isNewType.equals("3")){//供应链
//			tv_risk_file.setText("贸易背景");
//			tv_project_file.setText("风控文件");
//			tv_loan_file.setText("项目文件");
//		}else{
			tv_risk_file.setText("风控文件");
			tv_project_file.setText("项目照片");
			tv_loan_file.setText("借款资质");
//		}

	}
	@Override
	protected void initData(){
//		if(isNewType.equals("2")||isNewType.equals("3")){//供应链
//		wgimgList = new ArrayList<ImgEntity>();
//		imgList = new ArrayList<ImgEntity>();
//		docimgsList = new ArrayList<ImgEntity>();
//		getPicDate("1",creditInfoId,"0");//1-贸易背景  2-项目资质  3-风控资质
//		}else{
			if(wgimgList.size()>0){
				initPagerBean("1");
				//viewpager你放到那里去都能用，组装思想
				FileRollViewPager rollViewPager = new FileRollViewPager(InvestmentDetailFileActivity.this, ll_dot, tv_title,iv_left,iv_right,pagerBean, 
						//回调相关
						new FileRollViewPager.OnPageClick() {
					@Override
					public void onClick(String url) {
					}
				}
						); 
				//滚动自定义viewpager方法
				rollViewPager.startRoll();

				ll_top_view_pager.removeAllViews();
				ll_top_view_pager.addView(rollViewPager);
			}else{
				il_have.setVisibility(View.GONE);
				tv_none.setVisibility(View.VISIBLE);
			}
//		}
	}
	private void initPagerBean(String type) {
		List<String> titles = new ArrayList<String>();
		titles.add("title1");
		titles.add("title2");
		titles.add("title3");
		titles.add("title4");
		List<String> textUrls = new ArrayList<String>();
		textUrls.add("title1");
		textUrls.add("title2");
		textUrls.add("title3");
		textUrls.add("title4");
		//		imgUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502771816651&di=87d20415ca08040107cd875b2a54e586&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F12%2F95%2F68%2F62V58PICMT4.jpg");
		//		imgUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502771816651&di=ca750c976336e39de6e28f99992e86f0&imgtype=0&src=http%3A%2F%2Fimg.hb.aicdn.com%2F6f2e352868d7c0d402bfa096efd95fb88eb04e381e03c-1wYAGj_fw580");
		//		imgUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502771816650&di=ce94d969149a71e43919c26e203b4820&imgtype=0&src=http%3A%2F%2Fpic.qiantucdn.com%2F58pic%2F18%2F23%2F90%2F07v58PICWAT_1024.jpg");
		//		imgUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502771816650&di=7b4db338ce852fd75b0a060869fbb6ed&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201511%2F25%2F20151125233456_8nCUi.thumb.700_0.jpeg");
		//		imgUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502771816650&di=2cb0ad14ce2bbf4bec127b813f1c0b23&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3D4e3e0e04b4096b6395145613645aed31%2Ff7246b600c338744af80e6575b0fd9f9d72aa050.jpg");
		//		imgUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502771816649&di=9745b6c8e149714e236ac33309a0381e&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F15%2F67%2F59%2F23B58PIC6NG_1024.png");
		//		imgUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502771816649&di=9076b2c7c764da1846c80aedfe306d6c&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F12%2F29%2F68%2F12558PICryv.jpg");

		if(type.equals("1")){//风控文件
			imgUrls = new ArrayList<String>();
			if(wgimgList.size()>0){
				il_have.setVisibility(View.VISIBLE);
				tv_none.setVisibility(View.GONE);
				for(int i=0;i<wgimgList.size();i++){
					imgUrls.add(wgimgList.get(i).getImg());
				}
				pagerBean = new PagerBean(imgUrls, titles,textUrls);
			}else{
				il_have.setVisibility(View.GONE);
				tv_none.setVisibility(View.VISIBLE);
			}

		}else if(type.equals("3")){//项目照片
			imgsUrls = new ArrayList<String>();
			if(imgList.size()>0){
				il_have.setVisibility(View.VISIBLE);
				tv_none.setVisibility(View.GONE);
				for(int j = 0;j<imgList.size();j++){
					imgsUrls.add(imgList.get(j).getImg());
				}	
				pagerBean = new PagerBean(imgsUrls, titles,textUrls);
			}else{
				il_have.setVisibility(View.GONE);
				tv_none.setVisibility(View.VISIBLE);
			}

		}else{//资质照片
			docimgsUrls = new ArrayList<String>();
			if(docimgsList.size()>0){
				il_have.setVisibility(View.VISIBLE);
				tv_none.setVisibility(View.GONE);
				for(int z = 0;z<docimgsList.size();z++){
					docimgsUrls.add(docimgsList.get(z).getImg());
				}
				pagerBean = new PagerBean(docimgsUrls, titles,textUrls);
			}else{
				il_have.setVisibility(View.GONE);
				tv_none.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_risk_file://风控文件
			tv_risk_file.setTextColor(this.getResources().getColor(R.color.white));
			tv_risk_file.setBackgroundColor(this.getResources().getColor(R.color.text_a11c3f));
			tv_project_file.setTextColor(this.getResources().getColor(R.color.text_666666));
			tv_project_file.setBackgroundColor(this.getResources().getColor(R.color.white));
			tv_loan_file.setTextColor(this.getResources().getColor(R.color.text_666666));
			tv_loan_file.setBackgroundColor(this.getResources().getColor(R.color.white));
//			if(isNewType.equals("2")||isNewType.equals("3")){//供应链
//				getPicDate("1",creditInfoId,"1");//1-贸易背景  2-项目资质  3-风控资质
//			}else{
				initPagerBean("1");
				//viewpager你放到那里去都能用，组装思想
				if(imgUrls.size()==0){
					return;
				}
				FileRollViewPager rollViewPager = new FileRollViewPager(InvestmentDetailFileActivity.this, ll_dot, tv_title,iv_left,iv_right,pagerBean, 
						//回调相关
						new FileRollViewPager.OnPageClick() {
					@Override
					public void onClick(String url) {
					}
				}
						); 
				//滚动自定义viewpager方法
				rollViewPager.startRoll();

				ll_top_view_pager.removeAllViews();
				ll_top_view_pager.addView(rollViewPager);
//			}

			break;
		case R.id.tv_project_file://项目照片
			tv_risk_file.setTextColor(this.getResources().getColor(R.color.text_666666));
			tv_risk_file.setBackgroundColor(this.getResources().getColor(R.color.white));
			tv_project_file.setTextColor(this.getResources().getColor(R.color.white));
			tv_project_file.setBackgroundColor(this.getResources().getColor(R.color.text_a11c3f));
			tv_loan_file.setTextColor(this.getResources().getColor(R.color.text_666666));
			tv_loan_file.setBackgroundColor(this.getResources().getColor(R.color.white));
//			if(isNewType.equals("2")||isNewType.equals("3")){//供应链
//				getPicDate("3",creditInfoId,"1");//1-贸易背景  2-项目资质  3-风控资质
//			}else{
				
				initPagerBean("3");
				if(imgsUrls.size()==0){
					return;
				}
				//viewpager你放到那里去都能用，组装思想
				FileRollViewPager projectRollViewPager = new FileRollViewPager(InvestmentDetailFileActivity.this, ll_dot, tv_title,iv_left,iv_right,pagerBean, 
						//回调相关
						new FileRollViewPager.OnPageClick() {
					@Override
					public void onClick(String url) {
					}
				}
						); 
				//滚动自定义viewpager方法
				projectRollViewPager.startRoll();

				ll_top_view_pager.removeAllViews();
				ll_top_view_pager.addView(projectRollViewPager);
//			}
			break;
		case R.id.tv_loan_file://借款资质
			tv_risk_file.setTextColor(this.getResources().getColor(R.color.text_666666));
			tv_risk_file.setBackgroundColor(this.getResources().getColor(R.color.white));
			tv_project_file.setTextColor(this.getResources().getColor(R.color.text_666666));
			tv_project_file.setBackgroundColor(this.getResources().getColor(R.color.white));
			tv_loan_file.setTextColor(this.getResources().getColor(R.color.white));
			tv_loan_file.setBackgroundColor(this.getResources().getColor(R.color.text_a11c3f));
//			if(isNewType.equals("2")||isNewType.equals("3")){//供应链
//				getPicDate("2",creditInfoId,"1");//1-贸易背景  2-项目资质  3-风控资质
//			}else{
				initPagerBean("2");
				if(docimgsUrls.size()==0){
					return;	
				}
				//viewpager你放到那里去都能用，组装思想
				FileRollViewPager loanRollViewPager = new FileRollViewPager(InvestmentDetailFileActivity.this, ll_dot, tv_title,iv_left,iv_right,pagerBean, 
						//回调相关
						new FileRollViewPager.OnPageClick() {
					@Override
					public void onClick(String url) {
					}
				}
						); 
				//滚动自定义viewpager方法
				loanRollViewPager.startRoll();

				ll_top_view_pager.removeAllViews();
				ll_top_view_pager.addView(loanRollViewPager);
//			}
			break;

		default:
			break;
		}		
	}
	//获取图片信息
	private void getPicDate(final String type,String creditInfoId,final String isClick){//1-贸易背景  2-项目资质  3-风控资质
		CustomProgress.show(this);
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentDetailFileActivity.this));  
		String url = Urls.GETINVENTORY;
		RequestParams params = new RequestParams();
		params.put("type", type);
		params.put("creditInfoId", creditInfoId);
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String result = new String(responseBody);
				CustomProgress.CustomDismis();
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getString("state").equals("0")) {
						JSONObject dataObj = jsonObject.getJSONObject("data");
						JSONArray imgArray = dataObj.getJSONArray("imgList");
						if (imgArray.length() > 0) {
							for (int i = 0; i < imgArray.length(); i++) {
								entity = new ImgEntity();
								String obj = imgArray.getString(i);
								entity.setImg(obj);
								if(type.equals("1")){//贸易背景
									wgimgList.add(entity);
								}else if(type.equals("3")){//风控文件
									imgList.add(entity);
								}else if(type.equals("2")){//项目文件
									docimgsList.add(entity);
								}
							}
						} 
						if(isClick.equals("0")){//初始化
							if(wgimgList.size()>0){
								initPagerBean("1");
								//viewpager你放到那里去都能用，组装思想
								FileRollViewPager rollViewPager = new FileRollViewPager(InvestmentDetailFileActivity.this, ll_dot, tv_title,iv_left,iv_right,pagerBean, 
										//回调相关
										new FileRollViewPager.OnPageClick() {
									@Override
									public void onClick(String url) {
									}
								}
										); 
								//滚动自定义viewpager方法
								rollViewPager.startRoll();

								ll_top_view_pager.removeAllViews();
								ll_top_view_pager.addView(rollViewPager);
							}else{
								il_have.setVisibility(View.GONE);
								tv_none.setVisibility(View.VISIBLE);
							}
						}else{
							if(type.equals("1")){
								initPagerBean("1");
								//viewpager你放到那里去都能用，组装思想
								if(imgUrls.size()==0){
									return;
								}
								FileRollViewPager rollViewPager = new FileRollViewPager(InvestmentDetailFileActivity.this, ll_dot, tv_title,iv_left,iv_right,pagerBean, 
										//回调相关
										new FileRollViewPager.OnPageClick() {
									@Override
									public void onClick(String url) {
									}
								}
										); 
								//滚动自定义viewpager方法
								rollViewPager.startRoll();

								ll_top_view_pager.removeAllViews();
								ll_top_view_pager.addView(rollViewPager);
							}else if(type.equals("2")){
								initPagerBean("2");
								if(docimgsUrls.size()==0){
									return;	
								}
								//viewpager你放到那里去都能用，组装思想
								FileRollViewPager loanRollViewPager = new FileRollViewPager(InvestmentDetailFileActivity.this, ll_dot, tv_title,iv_left,iv_right,pagerBean, 
										//回调相关
										new FileRollViewPager.OnPageClick() {
									@Override
									public void onClick(String url) {
									}
								}
										); 
								//滚动自定义viewpager方法
								loanRollViewPager.startRoll();

								ll_top_view_pager.removeAllViews();
								ll_top_view_pager.addView(loanRollViewPager);
							}else if(type.equals("3")){
								initPagerBean("3");
								if(imgsUrls.size()==0){
									return;
								}
								//viewpager你放到那里去都能用，组装思想
								FileRollViewPager projectRollViewPager = new FileRollViewPager(InvestmentDetailFileActivity.this, ll_dot, tv_title,iv_left,iv_right,pagerBean, 
										//回调相关
										new FileRollViewPager.OnPageClick() {
									@Override
									public void onClick(String url) {
									}
								}
										); 
								//滚动自定义viewpager方法
								projectRollViewPager.startRoll();

								ll_top_view_pager.removeAllViews();
								ll_top_view_pager.addView(projectRollViewPager);
							}
						}




					}else if(jsonObject.getString("state").equals("4")){//系统超时
						String mGesture = LoginUserProvider.getUser(InvestmentDetailFileActivity.this).getGesturePwd();
						if(mGesture.equals("1")&&!mGesture.equals("")&&mGesture!=null){// 判断是否设置手势密码
							//设置手势密码
							Intent intent = new Intent(InvestmentDetailFileActivity.this,UnlockGesturePasswordActivity.class);
							intent.putExtra("overtime", "0");
							startActivity(intent);
						}else{
							//未设置手势密码
							Intent intent = new Intent(InvestmentDetailFileActivity.this,LoginActivity.class);
							intent.putExtra("overtime", "0");
							startActivity(intent);
						}
						DoCacheUtil util=DoCacheUtil.get(InvestmentDetailFileActivity.this);
						util.put("isLogin", "");

					}else{
						Toast.makeText(InvestmentDetailFileActivity.this, jsonObject.getString("message"), 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode,org.apache.http.Header[] headers, byte[] responseBody,
					Throwable error) {
				Toast.makeText(InvestmentDetailFileActivity.this, "请检查网络", 0).show();
				CustomProgress.CustomDismis();
			}
		});
	}
}
