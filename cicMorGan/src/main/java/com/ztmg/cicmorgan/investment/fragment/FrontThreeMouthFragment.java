package com.ztmg.cicmorgan.investment.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.investment.activity.SafeInvestmentDetailActivity;
import com.ztmg.cicmorgan.investment.adapter.InvestmentAdapter;
import com.ztmg.cicmorgan.investment.entity.InvestmentEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 综合排序
 *
 * @author pc
 */
public class FrontThreeMouthFragment extends Fragment implements OnItemClickListener, OnRefreshListener2<ListView> {
    private PullToRefreshListView ll_investment_list;
    private InvestmentAdapter adapter;
    private InvestmentEntity entity;
    private List<InvestmentEntity> projectList;
    private List<InvestmentEntity> investmentList;
    private List<InvestmentEntity> investmentTotalList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 20;//当前页面的内容数目
    private static int sortType = 0;//综合排序
    private static int type = 3;//类型
    private int last;//总页数
    private String state;
    //0-按综合排序1-按期限2-按利率
    //	public FrontThreeMouthFragment(String state) {
    //		this.state = state;
    //	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investment_list, null);
        initView(view);
        //		initData(type,pageNo,pageSize,dealine,REFRESH);
        pageNo = 1;
        //from pageNo pageSize,projecttype orderby
        getDate("3", "2", 1, 20, REFRESH);
        //		initData(type,pageNo,pageSize,state,sortType,REFRESH);
        ll_investment_list.setMode(Mode.BOTH);
        return view;
    }

    private void initView(View v) {
        investmentTotalList = new ArrayList<InvestmentEntity>();
        ll_investment_list = (PullToRefreshListView) v.findViewById(R.id.ll_investment_list);
        ll_investment_list.setMode(Mode.BOTH);
        ll_investment_list.setOnRefreshListener(this);
        ll_investment_list.setOnItemClickListener(this);

        //		adapter = new InvestmentAdapter(getActivity(), investmentTotalList);
        //		ll_investment_list.setAdapter(adapter);

    }

    //请求数据
    private void getDate(String from, final String projecttype, int pageNo, int pageSize, final int RequestCode) {
        CustomProgress.show(getActivity());
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
                        Toast.makeText(getActivity(), jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    CustomProgress.CustomDismis();
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", 0).show();
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
                        Toast.makeText(getActivity(), jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", 0).show();
            }
        });
    }


    //加载数据
    //	private void initData(String from,String pageNo,String pageSize,String span/*,String ratebegin,String rateend,String state,String projectsn*/){
    //projectProductType 标的产品类型 1：安心投类，2：供应链类
    private void initData(int from, final int pageNo, int pageSize, String projectProductType, int orderby, final int RequestCode) {
        CustomProgress.show(getActivity());
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
                CustomProgress.CustomDismis();
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
                            ll_investment_list.onRefreshComplete();
                            ll_investment_list.setMode(Mode.PULL_FROM_START);
                        } else {
                            ll_investment_list.setMode(Mode.BOTH);
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
                        Toast.makeText(getActivity(), jsonObject.getString("message"), 0).show();
                    }
                    ll_investment_list.onRefreshComplete();
                } catch (JSONException e) {
                    ll_investment_list.onRefreshComplete();
                    e.printStackTrace();
                    if (CustomProgress.show(getActivity()).isShowing()) {
                        CustomProgress.CustomDismis();
                    }
                    Toast.makeText(getActivity(), "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                ll_investment_list.onRefreshComplete();
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", 0).show();
            }
        });
    }
    //	@Override
    //	public void onResume() {
    //		super.onResume();
    //		pageNo=1;
    ////		initData(type,pageNo,pageSize,dealine,REFRESH);
    ////		ll_investment_list.setMode(Mode.BOTH);
    //	}


    /**
     * 设置
     *
     * @param requestCode
     */
    protected void setview(List<InvestmentEntity> investmentList,
                           int requestCode) {
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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (investmentTotalList.get(position - 1).getProjectProductType().equals("1")) {//1安心投2供应链
            Intent intent = new Intent(getActivity(), SafeInvestmentDetailActivity.class);
            intent.putExtra("projectid", investmentTotalList.get(position - 1).getProjectid());
            intent.putExtra("loanDate", investmentTotalList.get(position - 1).getLoandate());
            startActivity(intent);
        } /*else if (investmentTotalList.get(position - 1).getProjectProductType().equals("2")) {
            Intent intent = new Intent(getActivity(), InvestmentDetailActivity.class);
            intent.putExtra("projectid", investmentTotalList.get(position - 1).getProjectid());
            intent.putExtra("loanDate", investmentTotalList.get(position - 1).getLoandate());
            startActivity(intent);
        }*/
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        initData(type, pageNo, pageSize, state, sortType, REFRESH);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        initData(type, pageNo + 1, pageSize, state, sortType, LOADMORE);
    }
}
