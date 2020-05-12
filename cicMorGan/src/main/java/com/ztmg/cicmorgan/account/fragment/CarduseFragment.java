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
import com.ztmg.cicmorgan.account.adapter.CardAdapter;
import com.ztmg.cicmorgan.account.entity.CardEntity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.view.CustomProgress;

import android.annotation.SuppressLint;
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
 * 可使用
 *
 * @author pc
 */
@SuppressLint("ValidFragment")
public class CarduseFragment extends Fragment implements OnRefreshListener2<ListView> {
    private String type;//"2"加息券，"1"抵用券
    private CardAdapter adapter;
    private List<CardEntity> cardUseList;
    private List<CardEntity> cardUseTotalList;
    private PullToRefreshListView lv_card_use;
    private RelativeLayout rl_no_card;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;// 当前页数
    private static int pageSize = 10;// 当前页面的内容数目
    private String token;

    public CarduseFragment(String type) {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_use, null);
        initView(view);
        //initData(LoginUserProvider.getUser(getActivity()).getToken(),"3","1");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //		initData(LoginUserProvider.getUser(getActivity()).getToken(),"3","1");
        pageNo = 1;
        token = LoginUserProvider.getUser(getActivity()).getToken();
        if (type.equals("1")) {//抵用券
            initServeCardData(token, pageNo, pageSize, "3", "1", REFRESH);
        } else {//加息券
            initRaisingRatesCardData(token, pageNo, pageSize, "3", "1", REFRESH);
        }
        lv_card_use.setMode(Mode.BOTH);
    }

    private void initView(View v) {
        lv_card_use = (PullToRefreshListView) v.findViewById(R.id.lv_card_use);
        //		lv_card_use.setOnItemClickListener(new OnItemClickListener() {
        //
        //			@Override
        //			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        //					long arg3) {
        //				Intent intent = new Intent(getActivity(),ValueVoucherDetailActivity.class);
        //				startActivity(intent);
        //			}
        //		});
        rl_no_card = (RelativeLayout) v.findViewById(R.id.rl_no_card);
        cardUseTotalList = new ArrayList<CardEntity>();
        lv_card_use.setMode(Mode.BOTH);
        lv_card_use.setOnRefreshListener(this);
        adapter = new CardAdapter(getActivity(), cardUseTotalList, "1", type);
        lv_card_use.setAdapter(adapter);
    }

    //	//获取数据可使用的
    //		private void initData(String token,String from,String state){
    //			CustomProgress.show(getActivity());
    //			AsyncHttpClient client = new AsyncHttpClient();
    //			String url = Urls.GETUSERAWARDSHISTORYLIST;
    //			RequestParams params = new RequestParams();
    //			params.put("token", token);
    //			params.put("from", from);
    //			params.put("state", state);
    //			client.post(url, params, new AsyncHttpResponseHandler() {
    //
    //				@Override
    //				public void onSuccess(int statusCode, Header[] headers,
    //						byte[] responseBody) {
    //					CustomProgress.CustomDismis();
    //					String result = new String(responseBody);
    //					try {
    //						JSONObject jsonObject = new JSONObject(result);
    //						if (jsonObject.getString("state").equals("0")) {
    //							cardUseList = new ArrayList<CardEntity>();
    //							JSONObject dataObj = jsonObject.getJSONObject("data");
    //							JSONArray array = dataObj.getJSONArray("awardsList");
    //							if(array.length()>0){
    //								for(int i=0;i<array.length();i++){
    //									CardEntity entity = new CardEntity();
    //									JSONObject obj = array.getJSONObject(i);
    //									entity.setId(obj.getString("id"));
    //									entity.setGetDate(obj.getString("getDate"));
    //									entity.setLimitAmount(obj.getString("limitAmount"));
    //									entity.setOverdueDate(obj.getString("overdueDate"));
    //									entity.setState(obj.getString("state"));
    //									entity.setType(obj.getString("type"));
    //									entity.setValue(obj.getString("value"));
    //									cardUseList.add(entity);
    //								}
    //							}else{
    //								tv_tips.setVisibility(View.VISIBLE);
    //							}
    //							adapter = new CardAdapter(getActivity(), cardUseList,"1",type);
    //							lv_card_use.setAdapter(adapter);
    //						}else if(jsonObject.getString("state").equals("4")){//系统超时
    //							String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
    //							if(mGesture.equals("1")&&!mGesture.equals("")&&mGesture!=null){// 判断是否设置手势密码
    //								//设置手势密码
    //								Intent intent = new Intent(getActivity(),UnlockGesturePasswordActivity.class);
    //								intent.putExtra("overtime", "0");
    //								startActivity(intent);
    //							}else{
    //								//未设置手势密码
    //								Intent intent = new Intent(getActivity(),LoginActivity.class);
    //								intent.putExtra("overtime", "0");
    //								startActivity(intent);
    //							}
    ////							LoginUserProvider.cleanData(getActivity());
    ////							LoginUserProvider.cleanDetailData(getActivity());
    //							DoCacheUtil util=DoCacheUtil.get(getActivity());
    //							util.put("isLogin", "");
    //						}else{
    //							Toast.makeText(getActivity(), jsonObject.getString("message"), 0).show();
    //						}
    //					} catch (JSONException e) {
    //						e.printStackTrace();
    //						Toast.makeText(getActivity(), "解析异常", 0).show();
    //					}
    //				}
    //
    //				@Override
    //				public void onFailure(int statusCode,org.apache.http.Header[] headers, byte[] responseBody,
    //						Throwable error) {
    //					CustomProgress.CustomDismis();
    //					Toast.makeText(getActivity(), "请检查网络", 0).show();
    //				}
    //			});
    //		}

    // 抵用券获取数据可使用的
    private void initServeCardData(String token, final int pageNo, int pageSize, String from, String state, final int RequestCode) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String url = Urls.GETUSERVOUCHERSLIST;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("from", from);
        params.put("state", state);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        int totalCount = dataObj.getInt("totalCount");//总条数
                        cardUseList = new ArrayList<CardEntity>();
                        JSONArray vouchersListArray = dataObj.getJSONArray("vouchersList");
                        if (vouchersListArray.length() > 0) {
                            lv_card_use.setVisibility(View.VISIBLE);
                            rl_no_card.setVisibility(View.GONE);
                            for (int i = 0; i < vouchersListArray.length(); i++) {
                                CardEntity entity = new CardEntity();
                                JSONObject obj = vouchersListArray.getJSONObject(i);
                                entity.setId(obj.getString("id"));
                                entity.setGetDate(obj.getString("getDate"));
                                entity.setLimitAmount(obj
                                        .getString("limitAmount"));
                                entity.setOverdueDate(obj
                                        .getString("overdueDate"));
                                entity.setState(obj.getString("state"));
                                entity.setType(obj.getString("type"));
                                entity.setValue(obj.getString("amount"));
                                entity.setSpans(obj.getString("spans"));
                                cardUseList.add(entity);
                            }
                            setView(cardUseList, RequestCode);
                            int num = 0;
                            //1%10=1     10%10=0    11%10=1
                            if (totalCount < 10) {
                                num = 1;
                            } else {
                                //10/10=1   100/10=10  11/10=1
                                if (totalCount % 10 == 0) {
                                    num = totalCount / 10;
                                } else {
                                    num = totalCount / 10 + 1;
                                }
                            }
                            if (num == pageNo) {
                                lv_card_use.onRefreshComplete();
                                lv_card_use.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_card_use.setMode(Mode.BOTH);
                            }
                        } else {
                            lv_card_use.setVisibility(View.GONE);
                            rl_no_card.setVisibility(View.VISIBLE);
                        }
                        //							adapter = new CardAdapter(getActivity(), cardList, "3",
                        //									type);// 1抵用券2加息券
                        //							ll_card_out.setAdapter(adapter);
                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
                        String mGesture = LoginUserProvider.getUser(
                                getActivity()).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("")
                                && mGesture != null) {// 判断是否设置手势密码
                            // 设置手势密码
                            Intent intent = new Intent(getActivity(),
                                    UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            // 未设置手势密码
                            Intent intent = new Intent(getActivity(),
                                    LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        // LoginUserProvider.cleanData(getActivity());
                        // LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(getActivity(),
                                jsonObject.getString("message"), 0).show();
                    }
                    lv_card_use.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    lv_card_use.onRefreshComplete();
                    Toast.makeText(getActivity(), "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                lv_card_use.onRefreshComplete();
                //				CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", 0).show();
            }
        });
    }

    // 加息券获取数据过期的
    private void initRaisingRatesCardData(String token, final int pageNo, int pageSize, String from, String state, final int RequestCode) {
        CustomProgress.show(getActivity());
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETUSERRATECOUPONLIST;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("from", from);
        params.put("state", state);
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
                        int totalCount = dataObj.getInt("totalCount");//总条数
                        cardUseList = new ArrayList<CardEntity>();
                        JSONArray rateCouponListArray = dataObj.getJSONArray("rateCouponList");
                        if (rateCouponListArray.length() > 0) {
                            lv_card_use.setVisibility(View.VISIBLE);
                            rl_no_card.setVisibility(View.GONE);
                            for (int i = 0; i < rateCouponListArray.length(); i++) {
                                CardEntity entity = new CardEntity();
                                JSONObject obj = rateCouponListArray.getJSONObject(i);
                                entity.setId(obj.getString("id"));
                                entity.setGetDate(obj.getString("getDate"));
                                entity.setLimitAmount(obj
                                        .getString("limitAmount"));
                                entity.setOverdueDate(obj
                                        .getString("overdueDate"));
                                entity.setState(obj.getString("state"));
                                entity.setType(obj.getString("type"));
                                entity.setValue(obj.getString("rate"));
                                cardUseList.add(entity);
                            }
                            setView(cardUseList, RequestCode);
                            int num = 0;
                            //1%10=1     10%10=0    11%10=1
                            if (totalCount < 10) {
                                num = 1;
                            } else {
                                //10/10=1   100/10=10  11/10=1
                                if (totalCount % 10 == 0) {
                                    num = totalCount / 10;
                                } else {
                                    num = totalCount / 10 + 1;
                                }
                            }
                            if (num == pageNo) {
                                lv_card_use.onRefreshComplete();
                                lv_card_use.setMode(Mode.PULL_FROM_START);
                            } else {
                                lv_card_use.setMode(Mode.BOTH);
                            }
                        } else {
                            lv_card_use.setVisibility(View.GONE);
                            rl_no_card.setVisibility(View.VISIBLE);
                        }
                        //									adapter = new CardAdapter(getActivity(), cardList, "3",
                        //											type);// 1抵用券2加息券
                        //									ll_card_out.setAdapter(adapter);
                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
                        String mGesture = LoginUserProvider.getUser(
                                getActivity()).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("")
                                && mGesture != null) {// 判断是否设置手势密码
                            // 设置手势密码
                            Intent intent = new Intent(getActivity(),
                                    UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            // 未设置手势密码
                            Intent intent = new Intent(getActivity(),
                                    LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        // LoginUserProvider.cleanData(getActivity());
                        // LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(getActivity(),
                                jsonObject.getString("message"), 0).show();
                    }
                    lv_card_use.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    lv_card_use.onRefreshComplete();
                    Toast.makeText(getActivity(), "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                lv_card_use.onRefreshComplete();
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", 0).show();
            }
        });
    }

    protected void setView(List<CardEntity> list,
                           int requestCode) {
        if (requestCode == REFRESH) {
            cardUseTotalList.clear();
            if (list != null && list.size() > 0) {
                cardUseTotalList.addAll(list);
                pageNo = 1;
            }

        } else if (requestCode == LOADMORE) {
            cardUseTotalList.addAll(list);
            pageNo += 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        if (type.equals("1")) {//抵用券
            initServeCardData(token, pageNo, pageSize, "3", "1", REFRESH);
        } else {//加息券
            initRaisingRatesCardData(token, pageNo, pageSize, "3", "1", REFRESH);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (type.equals("1")) {//抵用券
            initServeCardData(token, pageNo + 1, pageSize, "3", "1", LOADMORE);
        } else {//加息券
            initRaisingRatesCardData(token, pageNo + 1, pageSize, "3", "1", LOADMORE);
        }
    }
}
