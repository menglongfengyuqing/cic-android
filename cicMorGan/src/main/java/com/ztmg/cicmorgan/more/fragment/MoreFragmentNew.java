//package com.ztmg.cicmorgan.more.fragment;
//
//import android.annotation.TargetApi;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager.LayoutParams;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.ztmg.cicmorgan.R;
//import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
//import com.ztmg.cicmorgan.base.BaseFragmentCommon;
//import com.ztmg.cicmorgan.home.activity.NoticeListActivity;
//import com.ztmg.cicmorgan.integral.activity.GoodsDetailActivity;
//import com.ztmg.cicmorgan.integral.entity.IntegralShopEntity;
//import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
//import com.ztmg.cicmorgan.login.LoginActivity;
//import com.ztmg.cicmorgan.more.activity.NewIntegralShopActivity;
//import com.ztmg.cicmorgan.more.activity.OnlineContactWeActivity;
//import com.ztmg.cicmorgan.more.adapter.ActionCenterAdapter;
//import com.ztmg.cicmorgan.more.adapter.CoreEnterpriseAgaginAdapter;
//import com.ztmg.cicmorgan.more.adapter.FindIntegralShopAdapter;
//import com.ztmg.cicmorgan.more.entity.ActionCenterEntity;
//import com.ztmg.cicmorgan.more.entity.CoreEnterpriseEntity;
//import com.ztmg.cicmorgan.net.Urls;
//import com.ztmg.cicmorgan.util.DoCacheUtil;
//import com.ztmg.cicmorgan.util.ImageLoaderUtil;
//import com.ztmg.cicmorgan.util.LoginUserProvider;
//import com.ztmg.cicmorgan.util.SystemBarTintManager;
//import com.ztmg.cicmorgan.view.CustomProgress;
//import com.ztmg.cicmorgan.view.FullyGridLayoutManager;
//import com.ztmg.cicmorgan.view.HorizontalListView;
//import com.ztmg.cicmorgan.view.SlowlyProgressBar;
//
//import org.apache.http.Header;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 发现
// *
// * @author pc
// */
//public class MoreFragmentNew extends BaseFragmentCommon implements OnClickListener {
//
//    private HorizontalListView hl_lv;
//    //private ListView lv_action;
//    private IntegralShopEntity entity;
//    private FindIntegralShopAdapter adapter;
//    private List<IntegralShopEntity> shopList;
//    private ActionCenterEntity actionEntity;
//    private CoreEnterpriseEntity coreEnterpriseEntity;
//    private ActionCenterAdapter actionAdapter;
//    private List<ActionCenterEntity> actionList;
//    private List<CoreEnterpriseEntity> coreEnterpriserList;
//    private final int REFRESH = 101;
//    private final int LOADMORE = 102;
//    private static int pageNo = 1;//当前页数
//    private static int pageSize = 20;//当前页面的内容数目
//    private static int CoreEnterprisePageNo = 1;//当前页数
//    private static int CoreEnterprisePageSize = 20;//当前页面的内容数目
//    //private RelativeLayout tv_tips;
//    private ImageView img_pic1, img_pic2, img_pic3, img_pic4, img_pic5, img_pic6;
//    private TextView txt_name1, txt_name2, txt_name3, txt_name4, txt_name5, txt_name6, txt_money1, txt_money2, txt_money3, txt_money4, txt_money5, txt_money6;
//    private RecyclerView hl_core_list;
//    //private HorizontalListView lv_core_enterprise;
//    private ImageView iv_no_data_look_more;
//
//    private LayoutInflater mInflater;
//    private ImageLoader mImageLoader;
//    private DisplayImageOptions mDisplayImageOptions;
//    private SlowlyProgressBar slowlyProgressBar;
//    int mindex;
//    int newProgress = 0;
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            mindex++;
//            if (mindex >= 5) {
//                newProgress += 10;
//                slowlyProgressBar.onProgressChange(newProgress);
//                mHandler.sendEmptyMessage(1);
//
//            } else {
//                newProgress += 5;
//                slowlyProgressBar.onProgressChange(newProgress);
//                mHandler.sendEmptyMessageDelayed(1, 1500);
//            }
//        }
//    };
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
//        }
//
//        mInflater = LayoutInflater.from(getActivity());
//        mImageLoader = ImageLoaderUtil.getImageLoader();
//        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);
//
//        View view = inflater.inflate(R.layout.fragment_more, null);
//        initView(view);
//        initData();
//        return view;
//    }
//
//    @TargetApi(19)
//    private void setTranslucentStatus(boolean on) {
//        Window win = getActivity().getWindow();
//        LayoutParams winParams = win.getAttributes();
//        final int bits = LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        //   getData(3, "2", 1, 7);
//        getActionData(3, "1", pageNo, pageSize);
//        getCoreEnterprise(3, CoreEnterprisePageNo, CoreEnterprisePageSize);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mHandler.removeMessages(1);
//    }
//
//    private void initView(View v) {
//        v.findViewById(R.id.tv_contect_server).setOnClickListener(this);
//
//        v.findViewById(R.id.tv_contect_server).setOnClickListener(this);
//
//
//        //v.findViewById(R.id.tv_tishi).setOnClickListener(this);
//        //lv_action = (ListView) v.findViewById(R.id.lv_action);//活动中心纵向滑动
//        //        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_action_list_header, null);
//        //        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_action_list_title, null);
//        //        lv_action.addHeaderView(headerView);
//        //        lv_action.addFooterView(footerView);
//        v.findViewById(R.id.rl_action_list).setOnClickListener(this);
//        //footerView.findViewById(R.id.tv_tip).setOnClickListener(this);
//        //footerView.findViewById(R.id.ll_inviting_friends).setOnClickListener(this);
//        v.findViewById(R.id.ll_notice).setOnClickListener(this);
//        hl_lv = (HorizontalListView) v.findViewById(R.id.hl_lv);//积分商品横向滑动
//        hl_lv.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Intent intent1 = new Intent(getActivity(), GoodsDetailActivity.class);
//                intent1.putExtra("awardId", shopList.get(position).getAwardId());
//                startActivity(intent1);
//            }
//        });
//        v.findViewById(R.id.rl_goods_more).setOnClickListener(this);//更多
//        v.findViewById(R.id.iv_mth_logo).setOnClickListener(this);//美特好logo
//
//        //lv_core_enterprise = (HorizontalListView) footerView.findViewById(R.id.lv_core_enterprise);
//        hl_core_list = (RecyclerView) v.findViewById(R.id.hl_core_list);
//        //        LinearLayoutManager llManager = new LinearLayoutManager(getActivity());
//        //        llManager.setOrientation(LinearLayoutManager.VERTICAL);
//        //        hl_core_list.setLayoutManager(llManager);
//
//        hl_core_list.setLayoutManager(new FullyGridLayoutManager(getContext(), 2, FullyGridLayoutManager.VERTICAL, false));
//        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
//        hl_core_list.setHasFixedSize(true);
//
//        iv_no_data_look_more = (ImageView) v.findViewById(R.id.iv_no_data_look_more);
//
//        //        footerView.findViewById(R.id.ll_integral_item1).setOnClickListener(this);
//        //        img_pic1 = (ImageView) footerView.findViewById(R.id.img_pic1);
//        //        txt_name1 = (TextView) footerView.findViewById(R.id.txt_name1);
//        //        txt_money1 = (TextView) footerView.findViewById(R.id.txt_money1);
//        //        footerView.findViewById(R.id.ll_integral_item2).setOnClickListener(this);
//        //        img_pic2 = (ImageView) footerView.findViewById(R.id.img_pic2);
//        //        txt_name2 = (TextView) footerView.findViewById(R.id.txt_name2);
//        //        txt_money2 = (TextView) footerView.findViewById(R.id.txt_money2);
//        //        footerView.findViewById(R.id.ll_integral_item3).setOnClickListener(this);
//        //        img_pic3 = (ImageView) footerView.findViewById(R.id.img_pic3);
//        //        txt_name3 = (TextView) footerView.findViewById(R.id.txt_name3);
//        //        txt_money3 = (TextView) footerView.findViewById(R.id.txt_money3);
//        //        footerView.findViewById(R.id.ll_integral_item4).setOnClickListener(this);
//        //        img_pic4 = (ImageView) footerView.findViewById(R.id.img_pic4);
//        //        txt_name4 = (TextView) footerView.findViewById(R.id.txt_name4);
//        //        txt_money4 = (TextView) footerView.findViewById(R.id.txt_money4);
//        //        footerView.findViewById(R.id.ll_integral_item5).setOnClickListener(this);
//        //        img_pic5 = (ImageView) footerView.findViewById(R.id.img_pic5);
//        //        txt_name5 = (TextView) footerView.findViewById(R.id.txt_name5);
//        //        txt_money5 = (TextView) footerView.findViewById(R.id.txt_money5);
//        //        footerView.findViewById(R.id.ll_integral_item6).setOnClickListener(this);
//        //        img_pic6 = (ImageView) footerView.findViewById(R.id.img_pic6);
//        //        txt_name6 = (TextView) footerView.findViewById(R.id.txt_name6);
//        //        txt_money6 = (TextView) footerView.findViewById(R.id.txt_money6);
//
//        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) v.findViewById(R.id.progressBar));
//        slowlyProgressBar.onProgressStart();
//
//    }
//
//    private void initData() {
//
//    }
//
//    //    //获取积分商城数据
//    //    private void getData(int from, String isLottery, final int pageNo, int pageSize) {
//    //        //CustomProgress.show(getActivity());
//    //        newProgress = 0;
//    //        mindex = 0;
//    //        slowlyProgressBar.setProgress(0);
//    //        slowlyProgressBar.onProgressStart();
//    //        mHandler.sendEmptyMessageDelayed(1, 1000);
//    //        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//    //        //AsyncHttpClient client = new AsyncHttpClient();
//    //        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
//    //        String url = Urls.GETAWARDINFOLIST;
//    //        RequestParams params = new RequestParams();
//    //        params.put("from", from);
//    //        params.put("isLottery", isLottery);
//    //        params.put("pageNo", pageNo);
//    //        params.put("pageSize", pageSize);
//    //        client.post(url, params, new AsyncHttpResponseHandler() {
//    //
//    //            @Override
//    //            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//    //                mindex = 5;
//    //                mHandler.sendEmptyMessage(1);
//    //                //CustomProgress.CustomDismis();
//    //                String result = new String(responseBody);
//    //                try {
//    //                    JSONObject jsonObject = new JSONObject(result);
//    //                    if (jsonObject.getString("state").equals("0")) {
//    //                        JSONObject dataObj = jsonObject.getJSONObject("data");
//    //                        String totalCount = dataObj.getString("totalCount");
//    //                        int last = dataObj.getInt("last");//总页数
//    //                        shopList = new ArrayList<IntegralShopEntity>();
//    //                        JSONArray awardlistArray = dataObj.getJSONArray("awardlist");
//    //                        if (awardlistArray.length() > 0) {
//    //                            for (int i = 0; i < awardlistArray.length(); i++) {
//    //                                JSONObject obj = awardlistArray.getJSONObject(i);
//    //                                entity = new IntegralShopEntity();
//    //                                entity.setAwardId(obj.getString("awardId"));//奖品id
//    //                                entity.setName(obj.getString("name"));//奖品名称
//    //                                entity.setNeedAmount(obj.getString("needAmount"));//奖品积分
//    //                                entity.setDocs(obj.getString("docs"));//奖品简介
//    //                                entity.setImgWeb(obj.getString("imgWap"));//奖品图片
//    //                                shopList.add(entity);
//    //                            }
//    //                        }
//    //                        adapter = new FindIntegralShopAdapter(getActivity(), shopList);
//    //                        hl_lv.setAdapter(adapter);
//    //
//    //                        mImageLoader.displayImage(shopList.get(0).getImgWeb(), img_pic1, mDisplayImageOptions);
//    //                        mImageLoader.displayImage(shopList.get(1).getImgWeb(), img_pic2, mDisplayImageOptions);
//    //                        //mImageLoader.displayImage(shopList.get(2).getImgWeb(), img_pic3, mDisplayImageOptions);
//    //                        mImageLoader.displayImage(shopList.get(3).getImgWeb(), img_pic4, mDisplayImageOptions);
//    //                        mImageLoader.displayImage(shopList.get(4).getImgWeb(), img_pic5, mDisplayImageOptions);
//    //                        //mImageLoader.displayImage(shopList.get(5).getImgWeb(), img_pic6, mDisplayImageOptions);
//    //                        txt_name1.setText(shopList.get(0).getName());
//    //                        txt_name2.setText(shopList.get(1).getName());
//    //                        txt_name3.setText(shopList.get(2).getName());
//    //                        txt_name4.setText(shopList.get(3).getName());
//    //                        txt_name5.setText(shopList.get(4).getName());
//    //                        txt_name6.setText(shopList.get(5).getName());
//    //                        txt_money1.setText(shopList.get(0).getNeedAmount());
//    //                        txt_money2.setText(shopList.get(1).getNeedAmount());
//    //                        txt_money3.setText(shopList.get(2).getNeedAmount());
//    //                        txt_money4.setText(shopList.get(3).getNeedAmount());
//    //                        txt_money5.setText(shopList.get(4).getNeedAmount());
//    //                        txt_money6.setText(shopList.get(5).getNeedAmount());
//    //                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//    //                        String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
//    //                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//    //                            //设置手势密码
//    //                            Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
//    //                            intent.putExtra("overtime", "0");
//    //                            startActivity(intent);
//    //                        } else {
//    //                            //未设置手势密码
//    //                            Intent intent = new Intent(getActivity(), LoginActivity.class);
//    //                            intent.putExtra("overtime", "0");
//    //                            startActivity(intent);
//    //                        }
//    //                        //LoginUserProvider.cleanData(getActivity());
//    //                        //LoginUserProvider.cleanDetailData(getActivity());
//    //                        DoCacheUtil util = DoCacheUtil.get(getActivity());
//    //                        util.put("isLogin", "");
//    //                    } else {
//    //                        Toast.makeText(getActivity(), jsonObject.getString("message"), 0).show();
//    //                    }
//    //                } catch (JSONException e) {
//    //                    e.printStackTrace();
//    //                    if (CustomProgress.show(getActivity()).isShowing()) {
//    //                        CustomProgress.CustomDismis();
//    //                    }
//    //                    Toast.makeText(getActivity(), "解析异常", 0).show();
//    //                }
//    //            }
//    //
//    //            @Override
//    //            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//    //                //CustomProgress.CustomDismis();
//    //                Toast.makeText(getActivity(), "请检查网络", 0).show();
//    //            }
//    //        });
//    //    }
//
//    //获取活动列表数据
//    private void getActionData(int from, String type, final int pageNo, int pageSize) {
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //AsyncHttpClient client = new AsyncHttpClient();
//        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
//        String url = Urls.GETCMSLIST;
//        RequestParams params = new RequestParams();
//        params.put("from", from);
//        params.put("type", type);
//        params.put("pageNo", pageNo);
//        params.put("pageSize", pageSize);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                //lv_action.setVisibility(View.VISIBLE);
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        String totalCount = dataObj.getString("totalCount");
//                        int last = dataObj.getInt("pageCount");//总页数
//                        actionList = new ArrayList<ActionCenterEntity>();
//                        JSONArray cmsListArray = dataObj.getJSONArray("cmsList");
//                        if (cmsListArray.length() > 0) {
//                            for (int i = 0; i < cmsListArray.length(); i++) {
//                                JSONObject obj = cmsListArray.getJSONObject(i);
//                                actionEntity = new ActionCenterEntity();
//                                actionEntity.setImgUrl(obj.getString("imgPath"));//图片地址
//                                actionEntity.setType(obj.getString("state"));//0下线 1-上线
//                                actionList.add(actionEntity);
//                            }
//                        }
//                        actionAdapter = new ActionCenterAdapter(getActivity(), actionList);
//                        //lv_action.setAdapter(actionAdapter);
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(getActivity(), LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        //LoginUserProvider.cleanData(getActivity());
//                        //LoginUserProvider.cleanDetailData(getActivity());
//                        DoCacheUtil util = DoCacheUtil.get(getActivity());
//                        util.put("isLogin", "");
//                    } else {
//                        Toast.makeText(getActivity(), jsonObject.getString("message"), 0).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "解析异常", 0).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                //lv_action.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "请检查网络", 0).show();
//            }
//        });
//    }
//
//
//    //获取核心企业列表
//    private void getCoreEnterprise(int from, final int pageNo, int pageSize) {
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        //AsyncHttpClient client = new AsyncHttpClient();
//        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
//        String url = Urls.GETMIDDLEMENLIST;
//        RequestParams params = new RequestParams();
//        params.put("from", from);
//        params.put("pageNo", pageNo);
//        params.put("pageSize", pageSize);
//        client.post(url, params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String result = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("state").equals("0")) {
//                        JSONObject dataObj = jsonObject.getJSONObject("data");
//                        String totalCount = dataObj.getString("totalCount");
//                        int last = dataObj.getInt("pageCount");//总页数
//                        coreEnterpriserList = new ArrayList<CoreEnterpriseEntity>();
//                        JSONArray middlemenListArray = dataObj.getJSONArray("middlemenList");
//                        if (middlemenListArray.length() > 0) {
//                            hl_core_list.setVisibility(View.VISIBLE);
//                            iv_no_data_look_more.setVisibility(View.GONE);
//                            for (int i = 0; i < middlemenListArray.length(); i++) {
//                                JSONObject obj = middlemenListArray.getJSONObject(i);
//                                coreEnterpriseEntity = new CoreEnterpriseEntity();
//                                coreEnterpriseEntity.setName(obj.getString("enterpriseFullName"));//核心企业名字
//                                JSONObject annexFileObj = obj.getJSONObject("annexFile");
//                                coreEnterpriseEntity.setImg(annexFileObj.getString("url"));//图片地址
//                                coreEnterpriseEntity.setRemark(annexFileObj.getString("remark"));//h5链接地址
//                                coreEnterpriserList.add(coreEnterpriseEntity);
//                            }
//                        } else {
//                            hl_core_list.setVisibility(View.GONE);
//                            iv_no_data_look_more.setVisibility(View.VISIBLE);
//                        }
//                        CoreEnterpriseEntity moreEntity = new CoreEnterpriseEntity();
//                        moreEntity.setImg("more");
//                        moreEntity.setRemark("http:1231321.png");
//                        coreEnterpriserList.add(moreEntity);
//                        //CoreEnterpriseAdapter coreEnterpriseAdapter = new CoreEnterpriseAdapter(getActivity(), coreEnterpriserList);
//                        //lv_core_enterprise.setAdapter(coreEnterpriseAdapter);
//
//                        CoreEnterpriseAgaginAdapter coreEnterpriseAgaginAdapter = new CoreEnterpriseAgaginAdapter(getActivity(), coreEnterpriserList);
//                        hl_core_list.setAdapter(coreEnterpriseAgaginAdapter);
//                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
//                        String mGesture = LoginUserProvider.getUser(getActivity()).getGesturePwd();
//                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
//                            //设置手势密码
//                            Intent intent = new Intent(getActivity(), UnlockGesturePasswordActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        } else {
//                            //未设置手势密码
//                            Intent intent = new Intent(getActivity(), LoginActivity.class);
//                            intent.putExtra("overtime", "0");
//                            startActivity(intent);
//                        }
//                        //LoginUserProvider.cleanData(getActivity());
//                        //LoginUserProvider.cleanDetailData(getActivity());
//                        DoCacheUtil util = DoCacheUtil.get(getActivity());
//                        util.put("isLogin", "");
//                    } else {
//                        Toast.makeText(getActivity(), jsonObject.getString("message"), 0).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "解析异常", 0).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Toast.makeText(getActivity(), "请检查网络", 0).show();
//            }
//        });
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.rl_goods_more:
//                Intent intent = new Intent(getActivity(), NewIntegralShopActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.tv_contect_server:
//                ContactServiceDialog dialog = new ContactServiceDialog(getActivity(), R.style.SelectPicDialog);
//                Window dialogWindow = dialog.getWindow();
//                LayoutParams lp = dialogWindow.getAttributes();
//                lp.width = LayoutParams.FILL_PARENT;
//                dialogWindow.setAttributes(lp);
//                dialog.show();
//                break;
//            //		case R.id.ll_inviting_friends:
//            //			if(LoginUserProvider.getUser(getActivity())!=null){
//            //				DoCacheUtil util=DoCacheUtil.get(getActivity());
//            //				String str=util.getAsString("isLogin");
//            //				if(str!=null){
//            //					if (str.equals("isLogin")) {//已登录
//            //						Intent invitIntent = new Intent(getActivity(),RequestFriendsActivity.class);
//            //						startActivity(invitIntent);
//            //					} else {//未登录
//            //						Intent loginIntent = new Intent(getActivity(),LoginActivity.class);
//            //						loginIntent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
//            //						startActivity(loginIntent);
//            //					}
//            //				}else{
//            //					Intent loginIntent1 = new Intent(getActivity(),LoginActivity.class);
//            //					loginIntent1.putExtra("overtime", "6");
//            //					startActivity(loginIntent1);
//            //				}
//            //			}else{
//            //				Intent loginIntent2 = new Intent(getActivity(),LoginActivity.class);
//            //				loginIntent2.putExtra("overtime", "6");
//            //				startActivity(loginIntent2);
//            //			}
//            //			break;
//            case R.id.ll_notice:
//                Intent noticeIntent = new Intent(getActivity(), NoticeListActivity.class);
//                noticeIntent.putExtra("path", "/account_announcement.html?app=1");
//                startActivity(noticeIntent);
//                break;
//            case R.id.iv_mth_logo://美特好
//                String path = "http://www.cicmorgan.com/zt_mth.html";
//                if (!TextUtils.isEmpty(path) && !path.equals("null") && path != null) {
//                    Intent mthLogoIntent = new Intent(getActivity(), AgreementActivity.class);
//                    mthLogoIntent.putExtra("path", path);
//                    mthLogoIntent.putExtra("title", "核心企业美特好");
//                    startActivity(mthLogoIntent);
//                }
//                break;
//            //            case R.id.tv_tip:
//            //                Intent riskTipFirstIntent = new Intent(getActivity(), AgreementActivity.class);
//            //                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
//            //                riskTipFirstIntent.putExtra("title", "风险提示书");
//            //                startActivity(riskTipFirstIntent);
//            //                break;
//            //		case R.id.rl_action_list://历史活动
//            //			Intent actionListIntent = new Intent(getActivity(),ActionListActivity.class);
//            //			startActivity(actionListIntent);
//            //			break;
//            //            case R.id.ll_integral_item1:
//            //                Intent intent1 = new Intent(getActivity(), GoodsDetailActivity.class);
//            //                intent1.putExtra("awardId", shopList.get(0).getAwardId());
//            //                startActivity(intent1);
//            //                break;
//            //            case R.id.ll_integral_item2:
//            //                Intent intent2 = new Intent(getActivity(), GoodsDetailActivity.class);
//            //                intent2.putExtra("awardId", shopList.get(1).getAwardId());
//            //                startActivity(intent2);
//            //                break;
//            //            case R.id.ll_integral_item3:
//            //                Intent intent3 = new Intent(getActivity(), GoodsDetailActivity.class);
//            //                intent3.putExtra("awardId", shopList.get(2).getAwardId());
//            //                startActivity(intent3);
//            //                break;
//            //            case R.id.ll_integral_item4:
//            //                Intent intent4 = new Intent(getActivity(), GoodsDetailActivity.class);
//            //                intent4.putExtra("awardId", shopList.get(3).getAwardId());
//            //                startActivity(intent4);
//            //                break;
//            //            case R.id.ll_integral_item5:
//            //                Intent intent5 = new Intent(getActivity(), GoodsDetailActivity.class);
//            //                intent5.putExtra("awardId", shopList.get(4).getAwardId());
//            //                startActivity(intent5);
//            //                break;
//            //            case R.id.ll_integral_item6:
//            //                Intent intent6 = new Intent(getActivity(), GoodsDetailActivity.class);
//            //                intent6.putExtra("awardId", shopList.get(5).getAwardId());
//            //                startActivity(intent6);
//            //                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 请求返回的
//     *
//     * @param stringId
//     * @param json
//     */
//    @Override
//    protected void responseData(String stringId, String json) {
//        switch (stringId) {
//            case "":
//                try {
//                    JSONObject jsonObject = new JSONObject(json);
//                    JSONObject dataObj = jsonObject.getJSONObject("data");
//                    String totalCount = dataObj.getString("totalCount");
//                    int last = dataObj.getInt("pageCount");//总页数
//                    actionList = new ArrayList<ActionCenterEntity>();
//                    JSONArray cmsListArray = dataObj.getJSONArray("cmsList");
//                    if (cmsListArray.length() > 0) {
//                        for (int i = 0; i < cmsListArray.length(); i++) {
//                            JSONObject obj = cmsListArray.getJSONObject(i);
//                            actionEntity = new ActionCenterEntity();
//                            actionEntity.setImgUrl(obj.getString("imgPath"));//图片地址
//                            actionEntity.setType(obj.getString("state"));//0下线 1-上线
//                            actionList.add(actionEntity);
//                        }
//                    }
//                    actionAdapter = new ActionCenterAdapter(getActivity(), actionList);
//                    //lv_action.setAdapter(actionAdapter);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//
//
//        }
//
//    }
//
//    //联系客服弹框
//    public class ContactServiceDialog extends Dialog {
//        Context context;
//
//        public ContactServiceDialog(Context context) {
//            super(context);
//            this.context = context;
//        }
//
//        public ContactServiceDialog(Context context, int theme) {
//            super(context, theme);
//            this.context = context;
//            this.setContentView(R.layout.dialog_contact_service);
//
//            TextView tv_on_line = (TextView) findViewById(R.id.tv_on_line);//在线客服
//            TextView tv_investment = (TextView) findViewById(R.id.tv_investment);//投资业务
//            TextView tv_loan = (TextView) findViewById(R.id.tv_loan);//借款业务
//            TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
//            tv_on_line.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), OnlineContactWeActivity.class);
//                    startActivity(intent);
//                    dismiss();
//                }
//            });
//            tv_investment.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent mIntent = new Intent(Intent.ACTION_CALL);
//                    mIntent.setData(Uri.parse("tel:4006669068"));
//                    startActivity(mIntent);
//                    dismiss();
//                }
//            });
//            tv_loan.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4006669068"));
//                    startActivity(intent);
//                    dismiss();
//                }
//            });
//            tv_cancel.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    //取消
//                    dismiss();
//                }
//            });
//
//        }
//    }
//}
//
//
//
