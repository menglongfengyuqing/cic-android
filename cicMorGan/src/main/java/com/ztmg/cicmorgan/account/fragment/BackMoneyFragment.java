package com.ztmg.cicmorgan.account.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.AnsactionRecordsAdapter;
import com.ztmg.cicmorgan.account.entity.AnsactionRecordsEntity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 交易记录-还款
 *
 * @author pc
 */
public class BackMoneyFragment extends Fragment implements OnRefreshListener2<ListView>, OnItemClickListener {
    private PullToRefreshListView lv_back_money;
    private List<AnsactionRecordsEntity> backMoneyList;
    private List<AnsactionRecordsEntity> backMoneyTotalList;
    private AnsactionRecordsAdapter adapter;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;// 当前页数
    private static int pageSize = 10;// 当前页面的内容数目
    private static int from = 3;// 来源
    private static int type = 4;// 类型
    private String TokenStr;

    private int last;//总共页数
    private RelativeLayout rl_no_back_money_records;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_back_money, null);
        initView(view);
        return view;
    }

    private void initView(View v) {
        rl_no_back_money_records = (RelativeLayout) v.findViewById(R.id.rl_no_back_money_records);
        backMoneyTotalList = new ArrayList<>();
        lv_back_money = (PullToRefreshListView) v
                .findViewById(R.id.lv_back_money);
        lv_back_money.setOnItemClickListener(this);
        lv_back_money.setMode(Mode.BOTH);
        lv_back_money.setOnRefreshListener(this);
        adapter = new AnsactionRecordsAdapter(getActivity(),
                backMoneyTotalList, "4");
        lv_back_money.setAdapter(adapter);
    }


    private void initData(String token, final int pageNo, int pageSize, int from,
                          int type, final int RequestCode) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETCGBUSERTRANSDETAILLIST;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("from", from);
        params.put("type", type);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        last = dataObj.getInt("last");
                        backMoneyList = new ArrayList<AnsactionRecordsEntity>();
                        JSONArray array = dataObj.getJSONArray("translist");
                        if (array.length() > 0) {
                            lv_back_money.setVisibility(View.VISIBLE);
                            rl_no_back_money_records.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                AnsactionRecordsEntity entity = new AnsactionRecordsEntity();
                                entity.setProjectid(obj.getString("projectid"));
                                entity.setAmount(obj.getString("amount"));
                                entity.setBalancemoney(obj.getString("balancemoney"));
                                entity.setTranddate(obj.getString("tranddate"));
                                entity.setTransstate(obj.getString("transstate"));
                                entity.setTranstype(obj.getString("transtype"));
                                entity.setName(obj.getString("name"));
                                entity.setType(obj.getString("type"));
                                entity.setProjectProductType(obj.getString("projectProductType"));
                                if (obj.has("sn")) {
                                    entity.setSn(obj.getString("sn"));
                                }
                                if (obj.has("inouttype")) {
                                    entity.setInouttype(obj.getString("inouttype"));
                                }
                                backMoneyList.add(entity);
                            }
                            setView(backMoneyList, RequestCode);
                            if (last == pageNo) {
                                lv_back_money.onRefreshComplete();
                                lv_back_money.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_back_money.setMode(Mode.BOTH);
                            }
                        } else {
                            lv_back_money.setVisibility(View.GONE);
                            rl_no_back_money_records.setVisibility(View.VISIBLE);
                        }
                        lv_back_money.onRefreshComplete();

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
                        Toast.makeText(getActivity(),
                                jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    lv_back_money.onRefreshComplete();
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //				CustomProgress.CustomDismis();
                lv_back_money.onRefreshComplete();
                Toast.makeText(getActivity(), "请检查网络", 0).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginUserProvider.currentStatus) {
            TokenStr = LoginUserProvider.getUser(getActivity()).getToken();
        }
        pageNo = 1;
        initData(TokenStr, pageNo, pageSize, from, type, REFRESH);
        lv_back_money.setMode(Mode.BOTH);
    }

    protected void setView(List<AnsactionRecordsEntity> backMoneyList,
                           int requestCode) {
        if (requestCode == REFRESH) {
            backMoneyTotalList.clear();
            if (backMoneyList != null && backMoneyList.size() > 0) {
                backMoneyTotalList.addAll(backMoneyList);
                pageNo = 1;
            }
        } else if (requestCode == LOADMORE) {
            backMoneyTotalList.addAll(backMoneyList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        initData(TokenStr, pageNo, pageSize, from, type, REFRESH);

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        initData(TokenStr, pageNo, pageSize, from, type, LOADMORE);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        //		if(backMoneyTotalList.get(position-1).getProjectProductType().equals("1")){//1安心投2供应链
        //			Intent intent = new Intent(getActivity(),SafeInvestmentDetailActivity.class);
        //			intent.putExtra("projectid", backMoneyTotalList.get(position-1).getProjectid());
        //			startActivity(intent);
        //		}else if(backMoneyTotalList.get(position-1).getProjectProductType().equals("2")){
        //			Intent intent = new Intent(getActivity(),InvestmentDetailActivity.class);
        //			intent.putExtra("projectid", backMoneyTotalList.get(position-1).getProjectid());
        //			startActivity(intent);
        //		}
    }
}
