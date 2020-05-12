package com.ztmg.cicmorgan.more.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivityCom;
import com.ztmg.cicmorgan.base.recyclerview.BaseRecyclerAdapter;
import com.ztmg.cicmorgan.base.recyclerview.RecyclerViewHolder;
import com.ztmg.cicmorgan.integral.activity.GoodsDetailActivity;
import com.ztmg.cicmorgan.integral.activity.GoodsListActivity;
import com.ztmg.cicmorgan.integral.activity.MyIntegralActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.entity.IntegralShop;
import com.ztmg.cicmorgan.net.OkUtil;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.Lists;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.FullyGridLayoutManager;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 积分商城   dong dong
 * 2018年7月20日 14:04:00
 */
public class IntegralShopActivity extends BaseActivityCom implements OnRefreshLoadMoreListener {

    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 10;//当前页面的内容数目
    private static int type = 3;//类型
    private int last;//总页数
    private TextView tv_integral;
    public static IntegralShopActivity mContext;
    private SlowlyProgressBar slowlyProgressBar;
    private int mindex;
    private int newProgress = 0;
    private BaseRecyclerAdapter RecyclerAdapter;
    private BaseRecyclerAdapter RecyclerAdapterShop;
    @BindView(R.id.ll_integral)
    public RelativeLayout ll_integral;
    @BindView(R.id.mRecyclerView_envelope)
    public RecyclerView mRecyclerView_envelope;
    @BindView(R.id.mRecyclerView_shop)
    public RecyclerView mRecyclerView_shop;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private List<IntegralShop.DataBean.AwardlistBean> awardlistBeen = new ArrayList<>();
    private boolean b;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(IntegralShopActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_integral_shop);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        pageNo = 1;
        awardlistBeen.clear();
        initData();
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(IntegralShopActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
    }

    public static IntegralShopActivity getInstance() {
        return mContext;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
        if (LoginUserProvider.getUser(IntegralShopActivity.this) != null) {
            DoCacheUtil util = DoCacheUtil.get(this);
            String str = util.getAsString("isLogin");
            if (str != null) {
                if (str.equals("isLogin")) {//已登录
                    userBouns(LoginUserProvider.getUser(IntegralShopActivity.this).getToken(), "3");
                } else {//未登录
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("overtime", "5");//无论登录界面返回还是登录成功，都是finish当前界面
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("overtime", "5");
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("overtime", "5");
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void initView() {
        setTitle("积分商城");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(IntegralShopActivity.this, "213001_jfsc_back_click");
                finish();
            }
        });
        setRightText("积分明细", new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEvent(IntegralShopActivity.this, "213006_jfsc_jfmx_click");
                Intent intent = new Intent(IntegralShopActivity.this, IntegralDetailActivity.class);
                //intent.putExtra("valueVoucherList", (Serializable) valueVoucherList);
                startActivity(intent);
            }
        });
        mRecyclerView_envelope.setLayoutManager(new FullyGridLayoutManager(this, 2, FullyGridLayoutManager.VERTICAL, false));
        mRecyclerView_shop.setLayoutManager(new FullyGridLayoutManager(this, 2, FullyGridLayoutManager.VERTICAL, false));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView_envelope.setHasFixedSize(true);
        mRecyclerView_shop.setHasFixedSize(true);

        tv_integral = (TextView) findViewById(R.id.tv_integral);
        findViewById(R.id.ll_integral).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(IntegralShopActivity.this, "213002_jfsc_jfcj_click");
                if (b) {
                    Intent integralIntent = new Intent(IntegralShopActivity.this, MyIntegralActivity.class);
                    startActivity(integralIntent);
                } else {
                    ToastUtils.show(IntegralShopActivity.this, "积分抽奖维护中，暂时停止使用");
                }
            }
        });
        findViewById(R.id.ll_exchange_record).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(IntegralShopActivity.this, "213003_jfsc_wdjp_click");
                Intent exIntent = new Intent(IntegralShopActivity.this, GoodsListActivity.class);
                startActivity(exIntent);
            }
        });
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
        refreshLayout.setOnRefreshLoadMoreListener(this);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
    }

    @Override
    protected void initData() {
        getDataTrue(pageNo);
        getDataFalse(pageNo);
    }

    /**
     * 数据返回
     *
     * @param stringId
     * @param json
     */
    @Override
    protected void responseData(String stringId, String json) {
        switch (stringId) {
            case "GETNEWAWARDINFOLIST_TRUE"://1虚拟
                refreshLayout.finishRefresh();//结束刷新
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                //String json1 = "{\"message\":\"奖品信息查询成功\",\"data\":{\"pageCount\":1,\"last\":1,\"totalCount\":4,\"pageNo\":1,\"pageSize\":10,\"awardlist\":[{\"needAmount\":\"200\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/10.png\",\"awardId\":\"6e7f33650b7446e49b15054790b73230\",\"docs\":\"红包仅限抵扣30天以上（不含30天）项目使用，立即发放\",\"name\":\"10元抵用券\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/10.png\"},{\"needAmount\":\"380\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/20.png\",\"awardId\":\"94e59f64c9c34957addc08c4749ce757\",\"docs\":\"红包仅限抵扣30天以上（不含30天）项目使用，立即发放\",\"name\":\"20元抵用券\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/20.png\"},{\"needAmount\":\"900\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/50.png\",\"awardId\":\"797418516c1a4ac8956858d2ff884997\",\"docs\":\"红包仅限抵扣30天以上（不含30天）项目使用，立即发放\",\"name\":\"50元抵用券\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/50.png\"},{\"needAmount\":\"1800\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/100.png\",\"awardId\":\"3f8e57aedfa245768c3ce55fbfe9aaa5\",\"docs\":\"红包仅限抵扣30天以上（不含30天）项目使用，立即发放\",\"name\":\"100元抵用券\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/100.png\"}]},\"state\":\"0\"}";
                IntegralShop integralShop = GsonManager.fromJson(json, IntegralShop.class);
                if ("0".equals(integralShop.getIslock())) {
                    //0积分抽奖按钮可点
                    b = true;
                } else if ("1".equals(integralShop.getIslock())) {
                    // 1积分抽奖按钮不可点
                    b = false;
                }
                if (!Lists.isEmpty(integralShop.getData().getAwardlist())) {
                    // bidList = data.getBidList();
                    RecyclerViewAdapter(integralShop.getData().getAwardlist());
                } else {
                    //mRelativeLayout.setVisibility(View.VISIBLE);
                    //mRecyclerView.setVisibility(View.GONE);
                }
                break;
            case "GETNEWAWARDINFOLIST_FALSE"://0 实体
                refreshLayout.finishLoadMore();//结束加载
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                //String json2 = "{\"message\":\"奖品信息查询成功\",\"data\":{\"pageCount\":7,\"last\":7,\"totalCount\":62,\"pageNo\":1,\"pageSize\":10,\"awardlist\":[{\"needAmount\":\"1000\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20180307144817.png\",\"awardId\":\"72bef1daeed14d09986acbb777643016\",\"docs\":\"&nbsp;金龙鱼 黄金产地长粒香大米5kg\",\"name\":\"金龙鱼大米\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20180307144817.png\"},{\"needAmount\":\"1100\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/%E7%BB%B4%E8%BE%BE%E5%8D%AB%E7%94%9F%E7%BA%B8.png\",\"awardId\":\"99ad212d3b7142c5a94598746f7103c4\",\"docs\":\"维达(Vinda) 无芯卷纸 超韧3层100g卫生纸*40卷\",\"name\":\"维达无芯卷纸\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/%E7%BB%B4%E8%BE%BE%E5%8D%AB%E7%94%9F%E7%BA%B8.png\"},{\"needAmount\":\"1300\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/%E7%8E%8B%E8%80%81%E5%90%89.png\",\"awardId\":\"644b9c3e79b4456ca27f31e1c8d6fa20\",\"docs\":\"王老吉凉茶310ml*24罐 整箱\",\"name\":\"王老吉凉茶\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/%E7%8E%8B%E8%80%81%E5%90%89.png\"},{\"needAmount\":\"1500\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/430b1964b12e4da3883c5c59091fd126/images/photo/2017/4%E6%9C%88%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87%E7%B4%A0%E6%9D%90/timg.jpg\",\"awardId\":\"0fa213eec7ee482084bb7d32486ecb3c\",\"docs\":\"小米（MI）小米蓝牙耳机青春版\",\"name\":\"小米青春版蓝牙耳机\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/430b1964b12e4da3883c5c59091fd126/images/photo/2017/4%E6%9C%88%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87%E7%B4%A0%E6%9D%90/timg.jpg\"},{\"needAmount\":\"1500\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/430b1964b12e4da3883c5c59091fd126/images/photo/2017/8%E6%9C%88%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87%E7%B4%A0%E6%9D%90/%E9%9B%80%E5%B7%A2%E5%92%96%E5%95%A1.jpg\",\"awardId\":\"19f61fcb247f4402a594b6437c58e236\",\"docs\":\"Nestle雀巢咖啡醇品黑咖啡罐装 500g 可冲277杯\",\"name\":\"Nestle雀巢咖啡\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/430b1964b12e4da3883c5c59091fd126/images/photo/2017/8%E6%9C%88%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87%E7%B4%A0%E6%9D%90/%E9%9B%80%E5%B7%A2%E5%92%96%E5%95%A1.jpg\"},{\"needAmount\":\"1500\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/430b1964b12e4da3883c5c59091fd126/images/photo/2017/5%E6%9C%88%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87%E7%B4%A0%E6%9D%90/U%E7%9B%98.jpg\",\"awardId\":\"35e6e1935ae44b34b8fc5b0ad754818b\",\"docs\":\"金士顿（Kingston）16GB U盘 USB3.0 DTSE9G2金属银色亮薄读速100MB/s\",\"name\":\"金士顿（Kingston）16GB U盘\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/430b1964b12e4da3883c5c59091fd126/images/photo/2017/5%E6%9C%88%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87%E7%B4%A0%E6%9D%90/U%E7%9B%98.jpg\"},{\"needAmount\":\"1500\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/58049090N8e686e8d.jpg\",\"awardId\":\"6be4b6d1ea3e4a6cabe96e625136b386\",\"docs\":\"小米（MI）小米移动电源2（10000mAh）\",\"name\":\"小米（MI）小米移动电源2（10000mAh）\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/58049090N8e686e8d.jpg\"},{\"needAmount\":\"1500\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/57fda056N361459eb.jpg\",\"awardId\":\"ecdabf86da5245b18b81855fccfde768\",\"docs\":\"笑脸多士炉 吐司机 家用烤面包机烤馒头片机\",\"name\":\"北欧欧慕烤面包机\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/1/images/photo/2017/01/57fda056N361459eb.jpg\"},{\"needAmount\":\"1600\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/%E5%86%9C%E5%A4%AB%E5%B1%B1%E6%B3%89.png\",\"awardId\":\"07d9a80bafc64a6c9eac0133e92d658a\",\"docs\":\"农夫山泉 饮用天然矿泉水 535ml*24瓶 整箱\",\"name\":\"农夫山泉天然矿泉水\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/%E5%86%9C%E5%A4%AB%E5%B1%B1%E6%B3%89.png\"},{\"needAmount\":\"1700\",\"imgWeb\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/86_90%E7%8E%89%E7%B1%B3%E6%B2%B9.png\",\"awardId\":\"2b5f765531f64b1986e62841f60a28cc\",\"docs\":\"金龙鱼 食用油 非转基因 压榨植物甾醇玉米油5L\",\"name\":\"&nbsp;金龙鱼玉米油\",\"imgWap\":\"http://182.92.114.130:8051/erp/userfiles/sharedFiles/images/2017-%E7%A7%AF%E5%88%86%E5%95%86%E5%9F%8E%E5%9B%BE%E7%89%87/20180307/86_90%E7%8E%89%E7%B1%B3%E6%B2%B9.png\"}]},\"state\":\"0\"}";
                IntegralShop fromJson = GsonManager.fromJson(json, IntegralShop.class);
                if ("0".equals(fromJson.getIslock())) {
                    //0积分抽奖按钮可点
                    b = true;
                } else if ("1".equals(fromJson.getIslock())) {
                    // 1积分抽奖按钮不可点
                    b = false;
                }
                if (!Lists.isEmpty(fromJson.getData().getAwardlist())) {
                    String last = fromJson.getData().getLast();
                    if (pageNo == 1) {
                        if (last.equals(pageNo + "")) {//总页数
                            awardlistBeen.addAll(fromJson.getData().getAwardlist());
                            RecyclerViewAdapterShop(awardlistBeen);
                            //mXRefreshView.setPullLoadEnable(false);
                            refreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
                        } else {
                            awardlistBeen.addAll(fromJson.getData().getAwardlist());
                            RecyclerViewAdapterShop(awardlistBeen);
                            refreshLayout.setNoMoreData(false);
                        }
                    } else if (pageNo > 1) {
                        if (last.equals(pageNo + "")) {//总页数
                            awardlistBeen.addAll(fromJson.getData().getAwardlist());
                            RecyclerViewAdapterShop(awardlistBeen);
                            //mXRefreshView.setPullLoadEnable(false);
                            refreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
                        } else {
                            awardlistBeen.addAll(fromJson.getData().getAwardlist());
                            RecyclerViewAdapterShop(awardlistBeen);
                            refreshLayout.setNoMoreData(false);
                        }
                    }
                } else {
                    //mXRefreshView.setPullLoadEnable(false);
                }
                break;
        }
    }


    /**
     * RecyclerViewAdapter  优惠券
     *
     * @param awardlistBeen
     */

    private void RecyclerViewAdapter(final List<IntegralShop.DataBean.AwardlistBean> awardlistBeen) {
        if (RecyclerAdapter == null) {
            RecyclerAdapter = new BaseRecyclerAdapter<IntegralShop.DataBean.AwardlistBean>(this, awardlistBeen) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.activity_item_integral_shop;
                }

                @Override
                public void bindData(RecyclerViewHolder holder, int position, IntegralShop.DataBean.AwardlistBean item) {
                    holder.getTextView(R.id.tv_frist_goods_name).setText(item.getName());
                    holder.getTextView(R.id.tv_frist_goods_integral).setText(item.getNeedAmount());

                    //RoundImageView imageView = (RoundImageView) holder.getImageView(R.id.white_bg);
                    ImageView holderImageView = holder.getImageView(R.id.iv_frist);

                    ImageLoader mImageLoader = ImageLoaderUtil.getImageLoader();
                    DisplayImageOptions mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.img_mall_defaultforgoods, false, false, false);
                    mImageLoader.displayImage(item.getImgWeb(), holderImageView, mDisplayImageOptions);

                    //imageView.setType(RoundImageView.TYPE_ROUND);
                    //imageView.setRoundRadius(10);// 圆角大小
                }
            };
            mRecyclerView_envelope.setAdapter(RecyclerAdapter);
            RecyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int pos) {
                    onEvent(IntegralShopActivity.this, "213004_jfsc_cjhb_click");
                    Intent intent2 = new Intent(mContext, GoodsDetailActivity.class);
                    intent2.putExtra("awardId", awardlistBeen.get(pos).getAwardId());
                    startActivity(intent2);
                }
            });
        } else {
            if (mRecyclerView_envelope.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView_envelope.isComputingLayout()) {
                RecyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * RecyclerViewAdapterShop  商品
     *
     * @param awardlistBeen
     */

    private void RecyclerViewAdapterShop(final List<IntegralShop.DataBean.AwardlistBean> awardlistBeen) {
        if (RecyclerAdapterShop == null) {
            RecyclerAdapterShop = new BaseRecyclerAdapter<IntegralShop.DataBean.AwardlistBean>(this, awardlistBeen) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.activity_item_integral_shop;
                }

                @Override
                public void bindData(RecyclerViewHolder holder, int position, IntegralShop.DataBean.AwardlistBean item) {
                    holder.getTextView(R.id.tv_frist_goods_name).setText(item.getName());
                    holder.getTextView(R.id.tv_frist_goods_integral).setText(item.getNeedAmount());

                    //RoundImageView imageView = (RoundImageView) holder.getImageView(R.id.white_bg);
                    ImageView holderImageView = holder.getImageView(R.id.iv_frist);

                    ImageLoader mImageLoader = ImageLoaderUtil.getImageLoader();
                    DisplayImageOptions mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.img_mall_defaultforgoods, false, false, false);
                    mImageLoader.displayImage(item.getImgWeb(), holderImageView, mDisplayImageOptions);

                    //imageView.setType(RoundImageView.TYPE_ROUND);
                    //imageView.setRoundRadius(10);// 圆角大小

                }
            };

            mRecyclerView_shop.setAdapter(RecyclerAdapterShop);
            RecyclerAdapterShop.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int pos) {
                    onEvent(IntegralShopActivity.this, "213005_jfsc_rmsp_click");
                    Intent intent2 = new Intent(mContext, GoodsDetailActivity.class);
                    intent2.putExtra("awardId", awardlistBeen.get(pos).getAwardId());
                    startActivity(intent2);
                }
            });
        } else {
            if (mRecyclerView_shop.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView_shop.isComputingLayout()) {
                RecyclerAdapterShop.notifyDataSetChanged();
            }
        }
    }


    /**
     * 虚拟商品
     */
    private void getDataTrue(int pageNo) {
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        HashMap<String, String> params = new HashMap();
        params.put("from", "3");
        params.put("pageNo", "1");
        params.put("pageSize", "10");
        params.put("isTrue", "1");//0 实体  1虚拟
        OkUtil.post("GETNEWAWARDINFOLIST_TRUE", Urls.GETNEWAWARDINFOLIST, params, IntegralShopActivity.this, this);
    }

    /**
     * 实体商品
     */
    private void getDataFalse(int pageNo) {
        //        newProgress = 0;
        //        mindex = 0;
        //        slowlyProgressBar.setProgress(0);
        //        slowlyProgressBar.onProgressStart();
        //        mHandler.sendEmptyMessageDelayed(1, 1000);
        HashMap<String, String> params = new HashMap();
        params.put("from", "3");
        params.put("pageNo", pageNo + "");
        params.put("pageSize", "10");
        params.put("isTrue", "0");//0 实体  1虚拟
        OkUtil.post("GETNEWAWARDINFOLIST_FALSE", Urls.GETNEWAWARDINFOLIST, params, IntegralShopActivity.this, this);
    }

    //用户积分
    private void userBouns(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(NewIntegralShopActivity.this));
        String url = Urls.USERBOUNS;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        //score = dataObj.getString("score");
                        tv_integral.setText(dataObj.getString("score") + "分");
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(IntegralShopActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(IntegralShopActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(IntegralShopActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(getActivity());
                        //LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(IntegralShopActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(IntegralShopActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(IntegralShopActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(IntegralShopActivity.this, "请检查网络", 0).show();
            }
        });
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        ++pageNo;
        getDataFalse(pageNo);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        pageNo = 1;
        awardlistBeen.clear();
        getDataTrue(pageNo);
        getDataFalse(pageNo);
    }
}

