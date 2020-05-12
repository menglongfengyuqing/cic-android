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
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.view.CustomProgress;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 交易记录-全部
 *
 * @author pc
 */
public class AnsactionRecordsAllFragment extends Fragment implements OnRefreshListener2<ListView> {
    private AnsactionRecordsAdapter adapter;
    private List<AnsactionRecordsEntity> ansactionList;
    private List<AnsactionRecordsEntity> ansactionTotalList;
    private PullToRefreshListView ll_all;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private static int from = 3;//来源
    private static int type = -1;//类型
    private String TokenStr;
    private int last;//页数
    private RelativeLayout rl_no_all_records;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ansaction_records, null);
        initView(view);
        return view;
    }

    private void initView(View v) {
        rl_no_all_records = (RelativeLayout) v.findViewById(R.id.rl_no_all_records);
        ansactionTotalList = new ArrayList<AnsactionRecordsEntity>();
        ll_all = (PullToRefreshListView) v.findViewById(R.id.ll_all);
        ll_all.setMode(Mode.BOTH);
        ll_all.setOnRefreshListener(this);
        adapter = new AnsactionRecordsAdapter(getActivity(), ansactionTotalList, "-1");
        ll_all.setAdapter(adapter);
    }

    private void initData(String token, final int pageNo, int pageSize, int from, int type, final int RequestCode) {
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
                        ansactionList = new ArrayList<AnsactionRecordsEntity>();
                        JSONArray array = dataObj.getJSONArray("translist");
                        if (array.length() > 0) {
                            ll_all.setVisibility(View.VISIBLE);
                            rl_no_all_records.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                AnsactionRecordsEntity entity = new AnsactionRecordsEntity();
                                entity.setAmount(obj.getString("amount"));
                                entity.setBalancemoney(obj.getString("balancemoney"));
                                entity.setTranddate(obj.getString("tranddate"));
                                entity.setTransstate(obj.getString("transstate"));
                                entity.setTranstype(obj.getString("transtype"));
                                entity.setName(obj.getString("name"));
                                entity.setType(obj.getString("type"));
                                entity.setRemark(obj.getString("remark"));
                                if (obj.has("projectProductType")) {
                                    entity.setProjectProductType(obj.getString("projectProductType"));
                                }
                                if (obj.has("sn")) {
                                    entity.setSn(obj.getString("sn"));
                                }
                                if (obj.has("inouttype")) {
                                    entity.setInouttype(obj.getString("inouttype"));
                                }
                                ansactionList.add(entity);
                            }
                            setView(ansactionList, RequestCode);
                            if (last == pageNo) {
                                ll_all.onRefreshComplete();
                                ll_all.setMode(Mode.PULL_FROM_START);
                            } else {
                                ll_all.setMode(Mode.BOTH);
                            }
                        } else {
                            ll_all.setVisibility(View.GONE);
                            rl_no_all_records.setVisibility(View.VISIBLE);
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
                    ll_all.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ll_all.onRefreshComplete();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                ll_all.onRefreshComplete();
                //				CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        pageNo = 1;
        if (LoginUserProvider.currentStatus) {
            TokenStr = LoginUserProvider.getUser(getActivity()).getToken();
        }
        initData(TokenStr, pageNo, pageSize, from, type, REFRESH);
        ll_all.setMode(Mode.BOTH);
    }

    protected void setView(List<AnsactionRecordsEntity> ansactionList, int requestCode) {
        if (requestCode == REFRESH) {
            ansactionTotalList.clear();
            if (ansactionList != null && ansactionList.size() > 0) {
                ansactionTotalList.addAll(ansactionList);
                pageNo = 1;
            }
        } else if (requestCode == LOADMORE) {
            ansactionTotalList.addAll(ansactionList);
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
        initData(TokenStr, pageNo + 1, pageSize, from, type, LOADMORE);
    }
}
