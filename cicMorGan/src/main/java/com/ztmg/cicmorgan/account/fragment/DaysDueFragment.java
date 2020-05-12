package com.ztmg.cicmorgan.account.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d3rich.pulltorefresh.library.PullToRefreshBase;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.Mode;
import com.d3rich.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.d3rich.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.MyInvestmentAdapter;
import com.ztmg.cicmorgan.account.entity.MyInvestmentEntity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.InvestmentDetailBorrowActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 回款中
 *
 * @author pc
 */
@SuppressLint({"NewApi", "ValidFragment"})
public class DaysDueFragment extends Fragment implements OnRefreshListener2<ListView>, OnItemClickListener {
    private MyInvestmentAdapter adapter;
    private List<MyInvestmentEntity> daysDueList;
    private List<MyInvestmentEntity> daysDueTotalList;
    private PullToRefreshListView lv_daysdue;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private static String state = "2";//类型
    private String token;
    private RelativeLayout rl_no_investment_message;

    private String type;
    private String stateType = "1";
    //	public DaysDueFragment(String type){
    //		this.type = type;
    //	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daysdue, null);
        Bundle typeBundle = getArguments();
        type = typeBundle.getString("type");
        initView(view);
        //		initData(token,type,"12",pageNo,pageSize,REFRESH);
        token = LoginUserProvider.getUser(getActivity()).getToken().toString();
        pageNo = 1;
        //String token,String from,String projectProductType,String state,final int pageNo,final int pageSize
        initData(token, "3", type, state, pageNo, pageSize, REFRESH);
        lv_daysdue.setMode(Mode.BOTH);
        return view;
    }

    private void initView(View view) {
        rl_no_investment_message = (RelativeLayout) view.findViewById(R.id.rl_no_investment_message);
        daysDueTotalList = new ArrayList<MyInvestmentEntity>();
        lv_daysdue = (PullToRefreshListView) view.findViewById(R.id.lv_daysdue);
        lv_daysdue.setMode(Mode.BOTH);
        lv_daysdue.setOnRefreshListener(this);
        lv_daysdue.setOnItemClickListener(this);
        adapter = new MyInvestmentAdapter(getActivity(), daysDueTotalList, type, stateType);
        lv_daysdue.setAdapter(adapter);

    }


    //获取数据
    private void initData(String token, String from, String projectProductType, String state, final int pageNo, final int pageSize, final int RequestCode) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETNEWMYBIDSDETAILH5;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        params.put("projectProductType", projectProductType);
        params.put("state", state);
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
                        int totalCount = dataObj.getInt("totalCount");
                        int pageCount = dataObj.getInt("pageCount");//总页数
                        daysDueList = new ArrayList<MyInvestmentEntity>();
                        JSONArray array = dataObj.getJSONArray("userBidHistoryList");
                        if (array.length() > 0) {
                            lv_daysdue.setVisibility(View.VISIBLE);
                            rl_no_investment_message.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                MyInvestmentEntity entity = new MyInvestmentEntity();
                                entity.setAmount(obj.getString("amount"));//出借金额
                                entity.setInterest(obj.getString("interest"));//预期收益
                                entity.setState(obj.getString("state"));//4回款中5已满标6已结束
                                entity.setEndDate(obj.getString("endDate"));//到期时间
                                entity.setProjectName(obj.getString("projectName"));//项目名字
                                entity.setSn(obj.getString("sn"));//供应链项目编号
                                entity.setProjectId(obj.getString("projectId"));//项目id
                                entity.setBidId(obj.getString("bidId"));
                                entity.setRate(obj.getString("rate"));
                                entity.setBid_signature(obj.getString("bid_signature"));
                                entity.setDtime(obj.getString("dtime"));
                                entity.setSpan(obj.getString("span"));
                                if (obj.has("creditName")) {
                                    entity.setCreditName(obj.getString("creditName"));
                                }
                                if (obj.has("creditUrl")) {
                                    entity.setCreditUrl(obj.getString("creditUrl"));
                                }
                                daysDueList.add(entity);
                            }
                            setView(daysDueList, RequestCode);
                            if (pageCount == pageNo) {
                                lv_daysdue.onRefreshComplete();
                                lv_daysdue.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_daysdue.setMode(Mode.BOTH);
                            }
                        } else {
                            lv_daysdue.setVisibility(View.GONE);
                            rl_no_investment_message.setVisibility(View.VISIBLE);
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
                    lv_daysdue.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", 0).show();
                    lv_daysdue.onRefreshComplete();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                //				CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", 0).show();
                lv_daysdue.onRefreshComplete();
            }
        });
    }

    //	@Override
    //	public void onResume() {
    //		super.onResume();
    //
    //	}
    protected void setView(List<MyInvestmentEntity> daysDueList, int requestCode) {
        if (requestCode == REFRESH) {
            daysDueTotalList.clear();
            if (daysDueList != null && daysDueList.size() > 0) {
                daysDueTotalList.addAll(daysDueList);
                pageNo = 1;
            }
        } else if (requestCode == LOADMORE) {

            daysDueTotalList.addAll(daysDueList);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        initData(token, "3", type, state, pageNo, pageSize, REFRESH);

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        initData(token, "3", type, state, pageNo + 1, pageSize, LOADMORE);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        onEvent(getActivity(), "207002_wdcj_list_click");
        //		Intent intent = new Intent(getActivity(),MyInvestmentDetailActivity.class);
        //		intent.putExtra("name", daysDueTotalList.get(position-1).getProjectName());
        //		intent.putExtra("state", daysDueTotalList.get(position-1).getState());
        //		intent.putExtra("span", daysDueTotalList.get(position-1).getSpan());
        //		intent.putExtra("money", daysDueTotalList.get(position-1).getAmount());
        //		intent.putExtra("time", daysDueTotalList.get(position-1).getDtime());
        //		intent.putExtra("rate", daysDueTotalList.get(position-1).getRate());
        //		intent.putExtra("income", daysDueTotalList.get(position-1).getInterest());
        //		intent.putExtra("endtime", daysDueTotalList.get(position-1).getEndDate());
        //		startActivity(intent);
        if (type.equals("1")) {//1安心投2供应链
            //			Intent intent = new Intent(getActivity(),SafeInvestmentDetailActivity.class);
            //			intent.putExtra("projectid", daysDueTotalList.get(position-1).getProjectId());
            //			intent.putExtra("loanDate", "");
            //			startActivity(intent);

            //Intent intent = new Intent(getActivity(), MySafeInvestmentPaymentDetailActivity.class);
            Intent intent = new Intent(getActivity(), InvestmentDetailBorrowActivity.class);
            ACache.get(getActivity()).put(Constant.Investment_Detail_SupplyChain_Relieved_Key, "3");//我的出借 详情
            intent.putExtra("projectid", daysDueTotalList.get(position - 1).getProjectId());
            intent.putExtra("bidid", daysDueTotalList.get(position - 1).getBidId());
            startActivity(intent);
        } else if (type.equals("2")) {
            //			Intent intent = new Intent(getActivity(),InvestmentDetailActivity.class);
            //			intent.putExtra("projectid", daysDueTotalList.get(position-1).getProjectId());
            //			intent.putExtra("loanDate", "");
            //			startActivity(intent);

            //            Intent intent = new Intent(getActivity(), MyInvestmentPaymentDetailActivity.class);
            Intent intent = new Intent(getActivity(), InvestmentDetailBorrowActivity.class);
            ACache.get(getActivity()).put(Constant.Investment_Detail_SupplyChain_Relieved_Key, "3");//我的出借 详情
            intent.putExtra("projectid", daysDueTotalList.get(position - 1).getProjectId());
            intent.putExtra("bidid", daysDueTotalList.get(position - 1).getBidId());
            startActivity(intent);
        }
    }
}
