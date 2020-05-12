package com.ztmg.cicmorgan.more.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.RollViewActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseFragmentCommon;
import com.ztmg.cicmorgan.integral.activity.GoodsDetailActivity;
import com.ztmg.cicmorgan.integral.entity.IntegralShopEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.activity.IntegralShopActivity;
import com.ztmg.cicmorgan.more.activity.OnlineContactWeActivity;
import com.ztmg.cicmorgan.more.adapter.ActionCenterAdapter;
import com.ztmg.cicmorgan.more.adapter.BaseVPAdapter;
import com.ztmg.cicmorgan.more.adapter.CoreEnterpriseAgaginAdapter;
import com.ztmg.cicmorgan.more.adapter.FindIntegralShopAdapter;
import com.ztmg.cicmorgan.more.adapter.VpTranform;
import com.ztmg.cicmorgan.more.entity.ActionCenterEntity;
import com.ztmg.cicmorgan.more.entity.CoreEnterpriseEntity;
import com.ztmg.cicmorgan.net.OkUtil;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.view.FullyGridLayoutManager;
import com.ztmg.cicmorgan.view.RoundImageView;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 发现
 *
 * @author pc
 */
public class MoreFragment extends BaseFragmentCommon implements OnClickListener {

    //private HorizontalListView hl_lv;
    //private ListView lv_action;
    private IntegralShopEntity entity;
    private FindIntegralShopAdapter adapter;
    private List<IntegralShopEntity> shopList;
    private ActionCenterEntity actionEntity;
    private CoreEnterpriseEntity coreEnterpriseEntity;
    private ActionCenterAdapter actionAdapter;
    private List<ActionCenterEntity> actionList;
    private List<CoreEnterpriseEntity> coreEnterpriserList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 20;//当前页面的内容数目
    private static int CoreEnterprisePageNo = 1;//当前页数
    private static int CoreEnterprisePageSize = 20;//当前页面的内容数目
    //private RelativeLayout tv_tips;
    private ImageView img_pic1, img_pic2, img_pic3, img_pic4, img_pic5, img_pic6;
    private TextView txt_name1, txt_name2, txt_name3, txt_name4, txt_name5, txt_name6, txt_money1, txt_money2, txt_money3, txt_money4, txt_money5, txt_money6;
    private RecyclerView hl_core_list;
    //private HorizontalListView lv_core_enterprise;
    private ImageView iv_no_data_look_more;

    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private SlowlyProgressBar slowlyProgressBar;
    int mindex;
    int newProgress = 0;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.ll_integral_second10)
    LinearLayout ll_integral_second10;
    @BindView(R.id.ll_integral_second20)
    LinearLayout ll_integral_second20;
    @BindView(R.id.ll__mi_mobile)
    LinearLayout ll__mi_mobile;
    @BindView(R.id.ll_mi_mouse)
    LinearLayout ll_mi_mouse;
    Unbinder bind;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mindex++;
            if (mindex >= 5) {
                newProgress += 10;
                slowlyProgressBar.onProgressChange(newProgress);
                mHandler.sendEmptyMessage(1);

            } else {
                newProgress += 5;
                slowlyProgressBar.onProgressChange(newProgress);
                mHandler.sendEmptyMessageDelayed(1, 1500);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        mInflater = LayoutInflater.from(getActivity());
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.img_mall_defaultforactivity, false, false, false);
        View view = inflater.inflate(R.layout.fragment_more, null);
        bind = ButterKnife.bind(this, view);
        initView(view);
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(mActivity, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
        return view;
    }

    /**
     * onDestroyView中进行解绑操作
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getActivity().getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mActivity); //统计时长
        //getData(3, "2", 1, 7);
        //getActionData(3, "1", pageNo, pageSize);
        getAction(3 + "", "1", pageNo + "", "" + pageSize);
        getCoreEnterprise(3, CoreEnterprisePageNo, CoreEnterprisePageSize);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(mActivity); //统计时长
    }

    private void initView(View v) {
        v.findViewById(R.id.tv_contect_server).setOnClickListener(this);
        v.findViewById(R.id.tv_contect_server).setOnClickListener(this);
        v.findViewById(R.id.ll_notice).setOnClickListener(this);
        v.findViewById(R.id.rl_goods_more).setOnClickListener(this);//更多
        v.findViewById(R.id.iv_mth_logo).setOnClickListener(this);//美特好logo

        hl_core_list = (RecyclerView) v.findViewById(R.id.hl_core_list);

        hl_core_list.setLayoutManager(new FullyGridLayoutManager(getContext(), 2, FullyGridLayoutManager.VERTICAL, false));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        hl_core_list.setHasFixedSize(true);
        hl_core_list.setNestedScrollingEnabled(false);
        iv_no_data_look_more = (ImageView) v.findViewById(R.id.iv_no_data_look_more);


        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) v.findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();


    }

    private void initData() {

    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    /**
     * 活动列表
     *
     * @param from
     * @param type
     * @param pageNo
     * @param pageSize
     */

    private void getAction(String from, String type, final String pageNo, String pageSize) {
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        HashMap<String, String> params = new HashMap();
        params.put("from", from);
        params.put("type", type);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        OkUtil.post("GETCMSLIST", Urls.GETCMSLIST, params, mActivity, this);
    }


    //获取活动列表数据
    private void getActionData(int from, String type, final int pageNo, int pageSize) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETCMSLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("type", type);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //lv_action.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String totalCount = dataObj.getString("totalCount");
                        int last = dataObj.getInt("pageCount");//总页数
                        actionList = new ArrayList<ActionCenterEntity>();
                        JSONArray cmsListArray = dataObj.getJSONArray("cmsList");
                        if (cmsListArray.length() > 0) {
                            for (int i = 0; i < cmsListArray.length(); i++) {
                                JSONObject obj = cmsListArray.getJSONObject(i);
                                actionEntity = new ActionCenterEntity();
                                actionEntity.setImgUrl(obj.getString("imgPath"));//图片地址
                                actionEntity.setType(obj.getString("state"));//0下线 1-上线
                                actionList.add(actionEntity);
                            }
                        }
                        actionAdapter = new ActionCenterAdapter(getActivity(), actionList);
                        //lv_action.setAdapter(actionAdapter);
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
                        //LoginUserProvider.cleanData(getActivity());
                        //LoginUserProvider.cleanDetailData(getActivity());
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
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                //lv_action.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "请检查网络", 0).show();
            }
        });
    }


    //获取核心企业列表
    private void getCoreEnterprise(int from, final int pageNo, int pageSize) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETMIDDLEMENLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String totalCount = dataObj.getString("totalCount");
                        int last = dataObj.getInt("pageCount");//总页数
                        coreEnterpriserList = new ArrayList<CoreEnterpriseEntity>();
                        JSONArray middlemenListArray = dataObj.getJSONArray("middlemenList");
                        if (middlemenListArray.length() > 0) {
                            hl_core_list.setVisibility(View.VISIBLE);
                            iv_no_data_look_more.setVisibility(View.GONE);
                            for (int i = 0; i < middlemenListArray.length(); i++) {
                                JSONObject obj = middlemenListArray.getJSONObject(i);
                                coreEnterpriseEntity = new CoreEnterpriseEntity();
                                coreEnterpriseEntity.setName(obj.getString("enterpriseFullName"));//核心企业名字
                                JSONObject annexFileObj = obj.getJSONObject("annexFile");
                                coreEnterpriseEntity.setImg(annexFileObj.getString("url"));//图片地址
                                coreEnterpriseEntity.setRemark(annexFileObj.getString("remark"));//h5链接地址
                                coreEnterpriserList.add(coreEnterpriseEntity);
                            }
                        } else {
                            hl_core_list.setVisibility(View.GONE);
                            iv_no_data_look_more.setVisibility(View.VISIBLE);
                        }
                        CoreEnterpriseEntity moreEntity = new CoreEnterpriseEntity();
                        moreEntity.setImg("more");
                        moreEntity.setRemark("");
                        coreEnterpriserList.add(moreEntity);
                        //CoreEnterpriseAdapter coreEnterpriseAdapter = new CoreEnterpriseAdapter(getActivity(), coreEnterpriserList);
                        //lv_core_enterprise.setAdapter(coreEnterpriseAdapter);

                        CoreEnterpriseAgaginAdapter coreEnterpriseAgaginAdapter = new CoreEnterpriseAgaginAdapter(getActivity(), coreEnterpriserList);
                        hl_core_list.setAdapter(coreEnterpriseAgaginAdapter);
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
                        //LoginUserProvider.cleanData(getActivity());
                        //LoginUserProvider.cleanDetailData(getActivity());
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
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "请检查网络", 0).show();
            }
        });
    }

    @OnClick({R.id.ll_integral_second10, R.id.ll_integral_second20, R.id.ll__mi_mobile, R.id.ll_mi_mouse})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_goods_more://积分商城
                onEvent(mActivity, "103003_more_click");
                Intent intent = new Intent(getActivity(), IntegralShopActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_integral_second10:
                onEvent(mActivity, "103004_tjsp_click");
                Intent second10 = new Intent(getActivity(), GoodsDetailActivity.class);
                second10.putExtra("awardId", "6e7f33650b7446e49b15054790b73230");
                startActivity(second10);
                break;
            case R.id.ll_integral_second20:
                onEvent(mActivity, "103004_tjsp_click");
                Intent second20 = new Intent(getActivity(), GoodsDetailActivity.class);
                second20.putExtra("awardId", "94e59f64c9c34957addc08c4749ce757");
                startActivity(second20);
                break;
            case R.id.ll__mi_mobile:
                onEvent(mActivity, "103004_tjsp_click");
                Intent integralFirstIntent = new Intent(getActivity(), GoodsDetailActivity.class);
                integralFirstIntent.putExtra("awardId", "6be4b6d1ea3e4a6cabe96e625136b386");
                startActivity(integralFirstIntent);
                break;
            case R.id.ll_mi_mouse:
                onEvent(mActivity, "103004_tjsp_click");
                Intent integralSecondIntent = new Intent(getActivity(), GoodsDetailActivity.class);
                integralSecondIntent.putExtra("awardId", "b911ae765f72468fb9d333fdc36e50f0");
                startActivity(integralSecondIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 请求返回的
     *
     * @param stringId
     * @param json
     */
    @Override
    protected void responseData(String stringId, String json) {
        switch (stringId) {
            case "GETCMSLIST":
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    String totalCount = dataObj.getString("totalCount");
                    int last = dataObj.getInt("pageCount");//总页数
                    actionList = new ArrayList<ActionCenterEntity>();
                    JSONArray cmsListArray = dataObj.getJSONArray("cmsList");
                    if (cmsListArray.length() > 0) {
                        for (int i = 0; i < cmsListArray.length(); i++) {
                            JSONObject obj = cmsListArray.getJSONObject(i);
                            actionEntity = new ActionCenterEntity();
                            actionEntity.setImgUrl(obj.getString("imgPath"));//图片地址
                            actionEntity.setType(obj.getString("state"));//0下线 1-上线
                            actionEntity.setText(obj.getString("text"));//活动地址
                            actionList.add(actionEntity);
                        }
                    }
                    //BaseVPAdapter 万能适配器
                    BaseVPAdapter<ActionCenterEntity> baseVPAdapter = new BaseVPAdapter<ActionCenterEntity>(mActivity, R.layout.item_action, actionList) {
                        @Override
                        public void bindView(View view, final ActionCenterEntity data) {
                            //RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mRelativeLayout);
                            //mRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
                            RoundImageView imageView = (RoundImageView) view.findViewById(R.id.iv_action);
                            TextView textView = (TextView) view.findViewById(R.id.txt_tips);
                            mImageLoader.displayImage(data.getImgUrl(), imageView, mDisplayImageOptions);
                            imageView.setType(RoundImageView.TYPE_ROUND);
                            imageView.setRoundRadius(30);//矩形圆角大小

                            if (data.getType().equals("0")) {//0下线 1-上线
                                textView.setText("已结束");
                            } else if (data.getType().equals("1")) {
                                textView.setText("进行中");
                            }
                            imageView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onEvent(mActivity, "103002_rmhd_click");
                                    String url = data.getText();
                                    System.out.println("url:" + url);
                                    if (url != null && !TextUtils.isEmpty(url)) {
                                        Intent intent = new Intent(getActivity(), RollViewActivity.class);
                                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                                        String str = util.getAsString("isLogin");
                                        String token = "";
                                        if (str != null) {
                                            if (str.equals("isLogin")) {//已登录
                                                token = LoginUserProvider.getUser(getActivity()).getToken();
                                            } else {
                                                token = "";
                                            }
                                        } else {
                                            token = "";
                                        }
                                        if (token.equals("") || TextUtils.isEmpty(token)) {
                                            if (url.contains("?")) {
                                                intent.putExtra("Url", url + "&app=1" + "&token=" + "");
                                            } else {
                                                intent.putExtra("Url", url + "?app=1" + "&token=" + "");
                                            }
                                        } else {
                                            if (url.contains("?")) {
                                                intent.putExtra("Url", url + "&app=1" + "&token=" + token);
                                            } else {
                                                intent.putExtra("Url", url + "?app=1" + "&token=" + token);
                                            }
                                        }
                                        startActivity(intent);
                                    }
                                }
                            });

                        }
                    };
                    mViewPager.setOffscreenPageLimit(2);
                    mViewPager.setPageTransformer(false, new VpTranform());
                    mViewPager.setAdapter(baseVPAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    //联系客服弹框
    public class ContactServiceDialog extends Dialog {
        Context context;

        public ContactServiceDialog(Context context) {
            super(context);
            this.context = context;
        }

        public ContactServiceDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_contact_service);

            TextView tv_on_line = (TextView) findViewById(R.id.tv_on_line);//在线客服
            TextView tv_investment = (TextView) findViewById(R.id.tv_investment);//投资业务
            TextView tv_loan = (TextView) findViewById(R.id.tv_loan);//借款业务
            TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_on_line.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), OnlineContactWeActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            tv_investment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(Intent.ACTION_CALL);
                    mIntent.setData(Uri.parse("tel:4006669068"));
                    startActivity(mIntent);
                    dismiss();
                }
            });
            tv_loan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4006669068"));
                    startActivity(intent);
                    dismiss();
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //取消
                    dismiss();
                }
            });

        }
    }
}



