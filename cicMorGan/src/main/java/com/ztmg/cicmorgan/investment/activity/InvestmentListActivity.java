package com.ztmg.cicmorgan.investment.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.investment.adapter.InvestmentListAdapter;
import com.ztmg.cicmorgan.investment.entity.InvestmentListEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.view.CustomProgress;
/**
 * 投资列表
 * @author pc
 *
 */
public class InvestmentListActivity extends BaseActivity implements OnRefreshListener2<ListView>{
	private PullToRefreshListView lv_investment_list;
	private InvestmentListAdapter adapter;
	private List<InvestmentListEntity> investmentList;
	private List<InvestmentListEntity> investmentTotalList;
	private final  int REFRESH=101;
	private final int LOADMORE=102;
	private static int pageNo=1;//当前页数
	private static int pageSize=13;//当前页面的内容数目
	private String projectid;
	private TextView tv_tips;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_investment_list);
		Intent intent = getIntent();
		projectid = intent.getStringExtra("projectid");
		initView();
		//		getData("3",pageNo,pageSize,projectid,REFRESH);
		initData();

	}
	@Override
	protected void initView() {
		investmentTotalList = new ArrayList<InvestmentListEntity>();
		lv_investment_list = (PullToRefreshListView) findViewById(R.id.lv_investment_list);

		lv_investment_list.setMode(Mode.BOTH);
		lv_investment_list.setOnRefreshListener(this);
		adapter = new InvestmentListAdapter(InvestmentListActivity.this, investmentTotalList);
		lv_investment_list.setAdapter(adapter);
		setTitle("出借列表");
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_tips = (TextView) findViewById(R.id.tv_tips);
		tv_tips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent riskTipFirstIntent = new Intent(InvestmentListActivity.this,AgreementActivity.class);
				riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
				riskTipFirstIntent.putExtra("title", "风险提示书");
				startActivity(riskTipFirstIntent);
			}
		});
	}
	@Override
	protected void initData() {

	}
	//获取数据
	private void getData(String from,final int pageNo,int pageSize,String projectid,final int RequestCode) {
		CustomProgress.show(this);
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(InvestmentListActivity.this));  
		String url = Urls.GETPROJECTBIDLIST;
		RequestParams params = new RequestParams();
		params.put("from", from);
		params.put("pageNo", pageNo);
		params.put("pageSize", pageSize);
		params.put("projectid", projectid);
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
						int last = dataObj.getInt("last");//总页数
						investmentList = new ArrayList<InvestmentListEntity>();
						JSONArray bidlistArray = dataObj.getJSONArray("bidlist");
						if(bidlistArray.length()>0){
							for(int i=0;i<bidlistArray.length();i++){
								JSONObject obj = bidlistArray.getJSONObject(i);
								InvestmentListEntity entity = new InvestmentListEntity();
								entity.setName(obj.getString("name"));
								entity.setAmount(obj.getString("amount"));
								entity.setCreatedate(obj.getString("createdate"));
								investmentList.add(entity);
							}
							setView(investmentList,RequestCode);
							if(last == pageNo){
								lv_investment_list.onRefreshComplete();
								lv_investment_list.setMode(Mode.PULL_FROM_START);
							}else{
								lv_investment_list.setMode(Mode.BOTH);
							}
						}else{
							tv_tips.setVisibility(View.VISIBLE);
						}
					}else if(jsonObject.getString("state").equals("4")){//系统超时
						String mGesture = LoginUserProvider.getUser(InvestmentListActivity.this).getGesturePwd();
						if(mGesture.equals("1")&&!mGesture.equals("")&&mGesture!=null){// 判断是否设置手势密码
							//设置手势密码
							Intent intent = new Intent(InvestmentListActivity.this,UnlockGesturePasswordActivity.class);
							intent.putExtra("overtime", "0");
							startActivity(intent);
						}else{
							//未设置手势密码
							Intent intent = new Intent(InvestmentListActivity.this,LoginActivity.class);
							intent.putExtra("overtime", "0");
							startActivity(intent);
						}
						//						LoginUserProvider.cleanData(InvestmentListActivity.this);
						//						LoginUserProvider.cleanDetailData(InvestmentListActivity.this);
						DoCacheUtil util=DoCacheUtil.get(InvestmentListActivity.this);
						util.put("isLogin", "");

					}else{
						Toast.makeText(InvestmentListActivity.this, jsonObject.getString("message"), 0).show();
					}
					lv_investment_list.onRefreshComplete();
				} catch (JSONException e) {
					e.printStackTrace();
					lv_investment_list.onRefreshComplete();
					Toast.makeText(InvestmentListActivity.this, "解析异常", 0).show();
				}
			}

			@Override
			public void onFailure(int statusCode,org.apache.http.Header[] headers, byte[] responseBody,
					Throwable error) {
				lv_investment_list.onRefreshComplete();
				Toast.makeText(InvestmentListActivity.this, "请检查网络", 0).show();
				CustomProgress.CustomDismis();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		pageNo=1;
		getData("3",pageNo,pageSize,projectid,REFRESH);
		lv_investment_list.setMode(Mode.BOTH);
	}
	//@Override
	//protected void onResume() {
	//	super.onResume();
	//	pageNo=1;
	//	getData("3",pageNo,pageSize,projectid,REFRESH);
	//	lv_investment_list.setMode(Mode.BOTH);
	//}
	protected void setView(List<InvestmentListEntity> investmentList,
			int requestCode) {
		if(requestCode==REFRESH){
			investmentTotalList.clear();
			if(investmentList!=null&&investmentList.size()>0){
				investmentTotalList.addAll(investmentList);
				pageNo=1;
			}

		}else if(requestCode==LOADMORE){
			investmentTotalList.addAll(investmentList);
			pageNo+=1;
		}
		adapter.notifyDataSetChanged();
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageNo=1;
		getData("3",pageNo,pageSize,projectid,REFRESH);
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getData("3",pageNo+1,pageSize,projectid,LOADMORE);
	}
}
