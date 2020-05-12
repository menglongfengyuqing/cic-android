package com.ztmg.cicmorgan.investment.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.investment.activity.InvestmentDetailBorrowActivity;
import com.ztmg.cicmorgan.investment.adapter.InvestmentAdapter;
import com.ztmg.cicmorgan.investment.entity.InvestmentEntity;
import com.ztmg.cicmorgan.investment.entity.SupplyChainInversMentEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 供应链
 *
 * @author pc
 */
public class SupplyChainInvestmentFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
    private View view;
    private PullToRefreshListView ll_supply_chain_investment_list;
    private InvestmentAdapter adapter;
    private InvestmentEntity entity;
    private List<InvestmentEntity> projectList;
    private List<InvestmentEntity> investmentList;
    private List<InvestmentEntity> investmentTotalList;

    private List<SupplyChainInversMentEntity.DataBean.ListZCBean> totalSupplyChainList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private static int sortType = 0;//综合排序
    private int last;//总页数
    private static int type = 3;//类型
    private String state = "2";
    //	private SlowlyProgressBar slowlyProgressBar;
    //	int mindex;
    //	int newProgress=0;
    //	Handler mHandler=new Handler(){
    //		@Override
    //		public void handleMessage(Message msg) {
    //			super.handleMessage(msg);
    //			mindex++;
    //			if(mindex>=5){
    //				newProgress+=10;
    //				slowlyProgressBar.onProgressChange(newProgress);
    //				mHandler.sendEmptyMessage(1);
    //
    //			}else{
    //				newProgress+=5;
    //				slowlyProgressBar.onProgressChange(newProgress);
    //				mHandler.sendEmptyMessageDelayed(1,1500);
    //			}
    //		}
    //	};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_investment_supply_chain, null);
        initView(view);
        pageNo = 1;
        //from pageNo pageSize,projecttype orderby
        //		getDate("3","2",1,20,REFRESH);
        getSupplyChainListData(3, pageNo, pageSize, "2", REFRESH);
        ll_supply_chain_investment_list.setMode(PullToRefreshBase.Mode.BOTH);
        return view;
    }

    //	@Override
    //	public void onPause() {
    //		super.onPause();
    //		mHandler.removeMessages(1);
    //	}

    private void initView(View v) {

        totalSupplyChainList = new ArrayList<>();
        investmentTotalList = new ArrayList<InvestmentEntity>();
        ll_supply_chain_investment_list = (PullToRefreshListView) v.findViewById(R.id.ll_supply_chain_investment_list);
        ll_supply_chain_investment_list.setMode(PullToRefreshBase.Mode.BOTH);
        ll_supply_chain_investment_list.setOnRefreshListener(this);
        ll_supply_chain_investment_list.setOnItemClickListener(this);
        //adapter = new InvestmentAdapter(getActivity(), investmentTotalList,2);
        adapter = new InvestmentAdapter(getActivity(), totalSupplyChainList, 2);
        ll_supply_chain_investment_list.setAdapter(adapter);

        //slowlyProgressBar = new SlowlyProgressBar((ProgressBar) v.findViewById(R.id.progressBar));
        //slowlyProgressBar.onProgressStart();


    }

    //请求数据
    private void getDate(String from, final String projecttype, int pageNo, int pageSize, final int RequestCode) {
        //		CustomProgress.show(getActivity());
        //		newProgress=0;
        //		mindex=0;
        //		slowlyProgressBar.setProgress(0);
        //		slowlyProgressBar.onProgressStart();
        //		mHandler.sendEmptyMessageDelayed(1,1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.INDEXH5;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("projecttype", projecttype);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        projectList = new ArrayList<InvestmentEntity>();
                        JSONArray projectListArray = dataObj.getJSONArray("projectList");
                        if (projectListArray.length() > 0) {
                            for (int i = 0; i < projectListArray.length(); i++) {
                                JSONObject obj = projectListArray.getJSONObject(i);
                                entity = new InvestmentEntity();
                                entity.setProjectid(obj.getString("projectid"));
                                entity.setName(obj.getString("name"));
                                entity.setRate(obj.getString("rate"));
                                entity.setPercentage(obj.getString("percentage"));
                                entity.setSpan(obj.getString("span"));
                                entity.setProstate(obj.getString("prostate"));
                                entity.setIsRecommend("0");//0推荐标，1普通标
                                if (obj.has("label")) {
                                    entity.setLabel(obj.getString("label"));
                                }
                                if (obj.has("loandate")) {
                                    entity.setLoandate(obj.getString("loandate"));
                                }
                                entity.setProjectProductType(obj.getString("projectProductType"));
                                entity.setProjecttype(projecttype);
                                if (obj.has("sn")) {
                                    entity.setSn(obj.getString("sn"));
                                }
                                if (obj.has("creditName")) {
                                    entity.setCreditName(obj.getString("creditName"));
                                }
                                if (obj.has("creditUrl")) {
                                    entity.setCreditUrl(obj.getString("creditUrl"));
                                }
                                projectList.add(entity);
                            }
                        }
                        getDate3(type, "3", 1, 20, projectList, RequestCode);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        CustomProgress.CustomDismis();
                        String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                        util.put("isLogin", "");
                    } else {
                        CustomProgress.CustomDismis();
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //请求数据type为3
    private void getDate3(int from, String projecttype, final int pageNo, final int pageSize, final List<InvestmentEntity> list, final int RequestCode) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //			AsyncHttpClient client = new AsyncHttpClient();
        //			client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.INDEXH5;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("projecttype", projecttype);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        JSONArray projectListArray = dataObj.getJSONArray("projectList");
                        if (projectListArray.length() > 0) {
                            for (int i = 0; i < projectListArray.length(); i++) {
                                JSONObject obj = projectListArray.getJSONObject(i);
                                entity = new InvestmentEntity();
                                entity.setProjectid(obj.getString("projectid"));
                                entity.setName(obj.getString("name"));
                                entity.setRate(obj.getString("rate"));
                                entity.setPercentage(obj.getString("percentage"));
                                entity.setSpan(obj.getString("span"));
                                entity.setProstate(obj.getString("prostate"));
                                entity.setIsRecommend("0");//0推荐标，1普通标
                                if (obj.has("label")) {
                                    entity.setLabel(obj.getString("label"));
                                }
                                if (obj.has("loandate")) {
                                    entity.setLoandate(obj.getString("loandate"));
                                }
                                entity.setProjectProductType(obj.getString("projectProductType"));
                                entity.setProjecttype(obj.getString("projectType"));
                                if (obj.has("sn")) {
                                    entity.setSn(obj.getString("sn"));
                                }
                                if (obj.has("creditName")) {
                                    entity.setCreditName(obj.getString("creditName"));
                                }
                                if (obj.has("creditUrl")) {
                                    entity.setCreditUrl(obj.getString("creditUrl"));
                                }
                                list.add(entity);
                            }

                            //								setView(projectList,RequestCode);
                            //								lv_home_project.onRefreshComplete();
                            //								lv_home_project.setMode(Mode.PULL_FROM_START);
                        }
                        //标的产品类型 1：安心投类，2：供应链类
                        initData(type, pageNo, pageSize, state, sortType, REFRESH);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //加载数据
    //	private void initData(String from,String pageNo,String pageSize,String span/*,String ratebegin,String rateend,String state,String projectsn*/){
    //projectProductType 标的产品类型 1：安心投类，2：供应链类
    private void getSupplyChainListData(int from, final int pageNo, int pageSize, String projectProductType, final int RequestCode) {
        //		CustomProgress.show(getActivity());
        //		newProgress=0;
        //		mindex=0;
        //		slowlyProgressBar.setProgress(0);
        //		slowlyProgressBar.onProgressStart();
        //		mHandler.sendEmptyMessageDelayed(1,1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETPROJECTLISTWAP;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("projectProductType", projectProductType);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //CustomProgress.CustomDismis();
                //mindex=5;
                //mHandler.sendEmptyMessage(1);
                String result = new String(responseBody);
                try {
                    SupplyChainInversMentEntity supplyChainInversMentEntity = new Gson().fromJson(result, SupplyChainInversMentEntity.class);
                    if (supplyChainInversMentEntity != null) {
                        String state = supplyChainInversMentEntity.getState();
                        if (state.equals("0")) {
                            int last = supplyChainInversMentEntity.getLast();
                            if (last == pageNo) {
                                ll_supply_chain_investment_list.onRefreshComplete();
                                ll_supply_chain_investment_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                ll_supply_chain_investment_list.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (supplyChainInversMentEntity.getData() != null) {

                                List<SupplyChainInversMentEntity.DataBean.ListZCBean> supplyChainHotProjectList = supplyChainInversMentEntity.getData().getListTJ();
                                List<SupplyChainInversMentEntity.DataBean.ListZCBean> supplyChainProjectList = supplyChainInversMentEntity.getData().getListZC();

                                //if (adapter == null) {
                                //    adapter = new InvestmentAdapter(getActivity(), totalSupplyChainList, 2);
                                //    ll_supply_chain_investment_list.setAdapter(adapter);
                                //}
                                setSupplyChainView(supplyChainHotProjectList, supplyChainProjectList, RequestCode);
                            }
                            //ll_supply_chain_investment_list.onRefreshComplete();
                        } else if (state.equals("4")) {//系统超时
                            CustomProgress.CustomDismis();
                            String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
                            if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                                //设置手势密码
                                Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
                                intent.putExtra("overtime", "0");
                                startActivity(intent);
                            } else {
                                //未设置手势密码
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("overtime", "0");
                                startActivity(intent);
                            }
                            //						LoginUserProvider.cleanData(getActivity());
                            //						LoginUserProvider.cleanDetailData(getActivity());
                            DoCacheUtil util = DoCacheUtil.get(getActivity());
                            util.put("isLogin", "");
                        } else {
                            CustomProgress.CustomDismis();
                            Toast.makeText(getActivity(), supplyChainInversMentEntity.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
                ll_supply_chain_investment_list.onRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                ll_supply_chain_investment_list.onRefreshComplete();
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSupplyChainView(List<SupplyChainInversMentEntity.DataBean.ListZCBean> supplyChainHotProjectList, List<SupplyChainInversMentEntity.DataBean.ListZCBean> supplyChainProjectList, int requestCode) {

        if (requestCode == REFRESH) {
            totalSupplyChainList.clear();
            if (supplyChainHotProjectList != null) {
                for (SupplyChainInversMentEntity.DataBean.ListZCBean bean : supplyChainHotProjectList) {
                    bean.setHot(true);
                }
                totalSupplyChainList.addAll(supplyChainHotProjectList);
            }
            if (supplyChainProjectList != null) {
                totalSupplyChainList.addAll(supplyChainProjectList);
            }
            pageNo = 1;
        } else if (requestCode == LOADMORE) {
            if (supplyChainProjectList != null && supplyChainProjectList.size() > 0) {
                totalSupplyChainList.addAll(supplyChainProjectList);
            }
            pageNo += 1;
        }
        if (adapter == null) {
            adapter = new InvestmentAdapter(getActivity(), totalSupplyChainList, 2);
            ll_supply_chain_investment_list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    private void initData(int from, final int pageNo, int pageSize, String projectProductType, int orderby, final int RequestCode) {
        //		CustomProgress.show(getActivity());
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETPROJECTLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("projectProductType", projectProductType);
        params.put("orderby", orderby);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                //				CustomProgress.CustomDismis();
                //				mindex=5;
                //				mHandler.sendEmptyMessage(1);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        String totalCount = jsonObject.getString("totalCount");
                        last = jsonObject.getInt("last");//总页数
                        investmentList = new ArrayList<InvestmentEntity>();
                        if (RequestCode == REFRESH) {//刷新
                            if (projectList != null) {
                                investmentList.addAll(projectList);
                            }
                        }
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                entity = new InvestmentEntity();
                                JSONObject obj = dataArray.getJSONObject(i);
                                entity.setAmount(obj.getString("amount"));
                                entity.setBalanceamount(obj.getString("balanceamount"));
                                entity.setRate(obj.getString("rate"));
                                entity.setPercentage(obj.getString("percentage"));
                                entity.setCurrentamount(obj.getString("currentamount"));
                                entity.setName(obj.getString("name"));
                                entity.setProjectid(obj.getString("projectid"));
                                entity.setSpan(obj.getString("span"));
                                if (obj.has("protype")) {
                                    entity.setProtype(obj.getString("protype"));
                                }
                                entity.setProstate(obj.getString("prostate"));
                                entity.setIsNewType(obj.getString("isNewType"));
                                entity.setProjectProductType(obj.getString("projectProductType"));//1安心投2供应链
                                entity.setIsRecommend("1");//普通标
                                if (obj.has("loandate")) {
                                    entity.setLoandate(obj.getString("loandate"));
                                }
                                if (obj.has("sn")) {
                                    entity.setSn(obj.getString("sn"));
                                }
                                if (obj.has("creditName")) {
                                    entity.setCreditName(obj.getString("creditName"));
                                }
                                if (obj.has("creditUrl")) {
                                    entity.setCreditUrl(obj.getString("creditUrl"));
                                }
                                investmentList.add(entity);
                            }
                        }
                        setview(investmentList, RequestCode);
                        if (last == pageNo) {
                            ll_supply_chain_investment_list.onRefreshComplete();
                            ll_supply_chain_investment_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            ll_supply_chain_investment_list.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    ll_supply_chain_investment_list.onRefreshComplete();
                } catch (JSONException e) {
                    ll_supply_chain_investment_list.onRefreshComplete();
                    e.printStackTrace();
                    if (CustomProgress.show(getActivity()).isShowing()) {
                        CustomProgress.CustomDismis();
                    }
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                ll_supply_chain_investment_list.onRefreshComplete();
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void setview(List<InvestmentEntity> investmentList, int requestCode) {
        if (requestCode == REFRESH) {
            investmentTotalList.clear();
            if (investmentList != null && investmentList.size() > 0) {
                investmentTotalList.addAll(investmentList);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            investmentTotalList.addAll(investmentList);
            pageNo += 1;
        }
        if (adapter == null) {
            adapter = new InvestmentAdapter(getActivity(), totalSupplyChainList, 2);
            ll_supply_chain_investment_list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        onEvent(getActivity(), " 102002_project_list_click");
        //TODO dong  新版详情的跳转
        //Intent intent = new Intent(getActivity(), InvestmentDetailActivity.class);
        Intent intent = new Intent(getActivity(), InvestmentDetailBorrowActivity.class);
        ACache.get(getContext()).put(Constant.Investment_Detail_SupplyChain_Relieved_Key, "0");//供应链 1
        intent.putExtra("projectid", totalSupplyChainList.get(position - 1).getId());
        intent.putExtra("loanDate", totalSupplyChainList.get(position - 1).getLoanDate());
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getSupplyChainListData(3, pageNo, pageSize, "2", REFRESH);
        //		initData(type,pageNo,pageSize,state,sortType,REFRESH);
        adapter = null;
        //ll_supply_chain_investment_list.removeAllViews();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getSupplyChainListData(3, pageNo + 1, pageSize, "2", LOADMORE);
        //		initData(type,pageNo+1,pageSize,state,sortType,LOADMORE);
    }
}
