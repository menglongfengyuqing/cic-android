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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 交易记录—充值
 *
 * @author pc
 */
public class RechargeFragment extends Fragment implements OnRefreshListener2<ListView> {

    private PullToRefreshListView lv_recharge;
    private List<AnsactionRecordsEntity> rechargeList;
    private List<AnsactionRecordsEntity> rechargeTotalList;
    private AnsactionRecordsAdapter adapter;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private static int from = 3;//来源
    private static int type = 0;//类型
    private String TokenStr;

    private int last;
    private RelativeLayout rl_no_recharge_records;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recharge, null);
        initView(view);
        return view;
    }

    private void initView(View v) {
        rl_no_recharge_records = (RelativeLayout) v.findViewById(R.id.rl_no_recharge_records);
        rechargeTotalList = new ArrayList<>();
        lv_recharge = (PullToRefreshListView) v.findViewById(R.id.lv_recharge);
        lv_recharge.setMode(Mode.BOTH);
        lv_recharge.setOnRefreshListener(this);
        adapter = new AnsactionRecordsAdapter(getActivity(), rechargeTotalList, "0");
        lv_recharge.setAdapter(adapter);
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
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        last = dataObj.getInt("last");
                        rechargeList = new ArrayList<AnsactionRecordsEntity>();
                        JSONArray array = dataObj.getJSONArray("translist");
                        if (array.length() > 0) {
                            lv_recharge.setVisibility(View.VISIBLE);
                            rl_no_recharge_records.setVisibility(View.GONE);
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
                                if (obj.has("projectProductType")) {
                                    entity.setProjectProductType(obj.getString("projectProductType"));
                                }
                                if (obj.has("sn")) {
                                    entity.setSn(obj.getString("sn"));
                                }
                                if (obj.has("inouttype")) {
                                    entity.setInouttype(obj.getString("inouttype"));
                                }
                                rechargeList.add(entity);
                            }
                            setView(rechargeList, RequestCode);
                            if (last == pageNo) {
                                lv_recharge.onRefreshComplete();
                                lv_recharge.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_recharge.setMode(Mode.BOTH);
                            }
                        } else {
                            lv_recharge.setVisibility(View.GONE);
                            rl_no_recharge_records.setVisibility(View.VISIBLE);
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
                    lv_recharge.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    lv_recharge.onRefreshComplete();
                    Toast.makeText(getActivity(), "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //				CustomProgress.CustomDismis();
                lv_recharge.onRefreshComplete();
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
        lv_recharge.setMode(Mode.BOTH);
    }

    protected void setView(List<AnsactionRecordsEntity> rechargeList,
                           int requestCode) {
        if (requestCode == REFRESH) {
            rechargeTotalList.clear();
            if (rechargeList != null && rechargeList.size() > 0) {
                rechargeTotalList.addAll(rechargeList);
                pageNo = 1;
            }
        } else if (requestCode == LOADMORE) {
            rechargeTotalList.addAll(rechargeList);
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
