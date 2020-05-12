package com.ztmg.cicmorgan.home;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.BindBankCardActivity;
import com.ztmg.cicmorgan.account.activity.NewMessageActivity;
import com.ztmg.cicmorgan.account.activity.RequestFriendsActivity;
import com.ztmg.cicmorgan.activity.MyEvent;
import com.ztmg.cicmorgan.activity.RollViewActivity;
import com.ztmg.cicmorgan.activity.SplashActivity;
import com.ztmg.cicmorgan.activity.StartUpActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseFragment;
import com.ztmg.cicmorgan.entity.PagerBean;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.home.activity.HomeDataH5Activity;
import com.ztmg.cicmorgan.home.activity.HomeYearReportH5Activity;
import com.ztmg.cicmorgan.home.adapter.HomeProjectAdapter;
import com.ztmg.cicmorgan.home.entity.HomeProjectEntity;
import com.ztmg.cicmorgan.home.entity.HomeTjProjectEntity;
import com.ztmg.cicmorgan.integral.activity.GoodsDetailActivity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.InvestmentDetailBorrowActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.login.RegisterActivity;
import com.ztmg.cicmorgan.more.activity.AboutWeActivity;
import com.ztmg.cicmorgan.more.activity.IntegralShopActivity;
import com.ztmg.cicmorgan.more.activity.OnlineContactWeActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.test.TestQuestionFirstActivity;
import com.ztmg.cicmorgan.util.ACache;
import com.ztmg.cicmorgan.util.CommonUtil;
import com.ztmg.cicmorgan.util.Constant;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.PermissionUtil;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.TimeUtil;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.MyScrollViewScroll;
import com.ztmg.cicmorgan.view.RollViewPager;
import com.ztmg.cicmorgan.view.VerticalScrollTextView;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 首页
 *
 * @author pc
 */
public class HomeFragment extends BaseFragment implements OnClickListener {

    private View view;
    private LinearLayout ll_top_view_pager;//用于存放viewpager
    private TextView tv_title;//轮播对应描述信息title
    private LinearLayout ll_dot;//用于存放点
    private PagerBean pagerBean;//轮播数据
    private List<String> imgUrls;
    private List<String> textUrls;//点击轮播图跳转界面
    private List<String> titleList;//公告消息
    private String name;
    private String projectid;
    private VerticalScrollTextView tv_tips;
    private TextView tv_sign;
    private String continuousTime;//连续签到天数
    private String integral;//签到积分
    private String integralCount;
    private TextView tv_sign_days;//签到天数
    private TextView tv_sign_total_integral;//当前总积分
    private TextView tv_integral;//签到积分
    private TextView tv_total_integral;//当前积分
    private SignDialog dialog;
    private AlreadySignDialog alreadySignDialog;
    private HomeProjectAdapter adapter;
    private HomeProjectEntity entity;
    private List<HomeProjectEntity> projectList;
    private final int REFRESH = 101;
    private final int LOADMORE = 102;
    private static int pageNo = 1;//当前页数
    private static int pageSize = 15;//当前页面的内容数目

    private View v_header;
    private RelativeLayout ll_header;
    private TextView tv_header;
    private MyScrollViewScroll sv_scroll_view;
    //推荐产品
    private RelativeLayout rl_hot_project;
    private TextView tv_hot_project_name, tv_hot_project_rate_first, tv_hot_project_rate_second, tv_hot_project_span;
    //优选产品一
    private TextView tv_good_project_name, tv_good_project_rate_first, tv_good_project_rate_second, tv_good_project_span;
    //优选产品二
    private TextView tv_good_second_project_name, tv_good_second_project_rate_first, tv_good_second_project_rate_second, tv_good_second_project_span;
    //积分精选一
    private ImageView iv_integral_first;
    private TextView tv_integral_first_name, tv_integral_first_use;
    //积分精选二
    private ImageView iv_integral_second;
    private TextView tv_integral_second_name, tv_integral_second_use;

    //轮播图上
    private ImageView iv_home_message;//消息
    private ImageView iv_message;//签到
    //页面上
    private ImageView iv_black_message, iv_white_message, iv_black_sign, iv_white_sign;
    private View v_head_line;
    private ImageView iv_unread_message, iv_unsign;

    private NewHandGuideDialog newHandGuideDialog;
    private YearDialog yearDialog;
    private TextView tv_new_hand_guide;//新手引导

    private String hotProjectId, hotProjectLoandata, hotProjectType;
    private Button bt_once_enjoy;

    private String goodProjectFristId, goodProjectFristLoanData, goodProjectSecondId, goodProjectSecondLoanData;

    private boolean yearReportIsOpen;// 只打开一次
    private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(Color.argb(0, 0, 0, 0));//通知栏所需颜色
        }

        view = inflater.inflate(R.layout.fragment_home, null);
        initView(view);
        //getCmsListByType("1","10","2","3");
        //0 - 安心投  2-新手标 3- 推荐标
        pageNo = 1;
        //lv_home_project.setMode(Mode.BOTH);
        //getDate("3","2",pageNo,pageSize,REFRESH);
        //获取年报是否上线
        requestVersionNum("3");
        return view;

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
        getProjectListWap(3, 1, 0);
        getCmsListByType("1", "10", "0", "3");
        //		getSupplyChainProjectDate(3,1,5,"2",0);//2供应链 1安心投
        //是否签到 3：未签到，2：已签到
        if (LoginUserProvider.getUser(getActivity()) != null) {
            DoCacheUtil util = DoCacheUtil.get(getActivity());
            String str = util.getAsString("isLogin");
            if (str != null) {
                if (str.equals("isLogin")) {//已登录
                    //是否签到 3：未签到，2：已签到
                    if (LoginUserProvider.getUser(getActivity()).getSigned() != null && LoginUserProvider.getUser(getActivity()).getSigned().equals("3")) {
                        tv_sign.setText("签到");
                        iv_unsign.setVisibility(View.VISIBLE);
                        //iv_message.setBackgroundResource(R.drawable.pic_new_sign_nosign);
                        //iv_black_sign.setBackgroundResource(R.drawable.nav_icon_register_unregister_gray);
                    } else if (LoginUserProvider.getUser(getActivity()).getSigned() != null && LoginUserProvider.getUser(getActivity()).getSigned().equals("2")) {
                        tv_sign.setText("已签到");
                        iv_unsign.setVisibility(View.GONE);
                        //iv_message.setBackgroundResource(R.drawable.pic_new_sign);
                        //iv_black_sign.setBackgroundResource(R.drawable.nav_icon_register_nor_gray);
                    }
                }
            }
            //判断用户是否注册，开户，评测  int from,String token,int type
            //0没有点击,1点击
            newHandGuide(3, LoginUserProvider.getUser(getActivity()).getToken(), 0);
            isReadLetter(LoginUserProvider.getUser(getActivity()).getToken(), "3");
        } else {
            isReadLetter("", "3");
        }
    }

    private void initView(View v) {
        ll_top_view_pager = (LinearLayout) v.findViewById(R.id.ll_top_view_pager);
        iv_unread_message = (ImageView) v.findViewById(R.id.iv_unread_message);//未读消息红点
        iv_unsign = (ImageView) v.findViewById(R.id.iv_unsign);
        v.findViewById(R.id.ll_sign).setOnClickListener(this);//签到
        v_head_line = v.findViewById(R.id.v_head_line);
        iv_white_message = (ImageView) v.findViewById(R.id.iv_white_message);
        iv_white_message.setOnClickListener(this);
        iv_white_message.setVisibility(View.VISIBLE);
        iv_black_message = (ImageView) v.findViewById(R.id.iv_black_message);//首页上面消息
        iv_black_message.setVisibility(View.GONE);
        iv_black_message.setOnClickListener(this);
        iv_white_sign = (ImageView) v.findViewById(R.id.iv_white_sign);
        iv_white_sign.setOnClickListener(this);
        iv_white_sign.setVisibility(View.VISIBLE);
        iv_black_sign = (ImageView) v.findViewById(R.id.iv_black_sign);//首页上面签到
        iv_black_sign.setVisibility(View.GONE);
        iv_black_sign.setOnClickListener(this);
        iv_home_message = (ImageView) v.findViewById(R.id.iv_home_message);//轮播图消息
        iv_home_message.setOnClickListener(this);//消息
        iv_message = (ImageView) v.findViewById(R.id.iv_message);//轮播图签到图片
        tv_sign = (TextView) v.findViewById(R.id.tv_sign);//签到字


        //是否签到 3：未签到，2：已签到
        if (LoginUserProvider.getUser(getActivity()) != null) {
            DoCacheUtil util = DoCacheUtil.get(getActivity());
            String str = util.getAsString("isLogin");
            if (str != null) {
                if (str.equals("isLogin")) {//已登录
                    //是否签到 3：未签到，2：已签到
                    if (LoginUserProvider.getUser(getActivity()).getSigned() != null && LoginUserProvider.getUser(getActivity()).getSigned().equals("3")) {
                        tv_sign.setText("签到");
                        iv_unsign.setVisibility(View.VISIBLE);
                        //						iv_message.setBackgroundResource(R.drawable.pic_new_sign_nosign);
                        //						iv_black_sign.setBackgroundResource(R.drawable.nav_icon_register_unregister_gray);
                    } else if (LoginUserProvider.getUser(getActivity()).getSigned() != null && LoginUserProvider.getUser(getActivity()).getSigned().equals("2")) {
                        tv_sign.setText("已签到");
                        iv_unsign.setVisibility(View.GONE);
                        //						iv_message.setBackgroundResource(R.drawable.pic_new_sign);
                        //						iv_black_sign.setBackgroundResource(R.drawable.nav_icon_register_nor_gray);
                    }
                }
            }
        }

        tv_title = (TextView) v.findViewById(R.id.tv_title);
        ll_dot = (LinearLayout) v.findViewById(R.id.ll_dot);
        //新改版
        tv_new_hand_guide = (TextView) v.findViewById(R.id.tv_new_hand_guide);
        tv_new_hand_guide.setOnClickListener(this);//新手引导
        //tv_new_hand_guide.bringToFront();//让这个控件在所有的控件之上
        v.findViewById(R.id.home_gift).setOnClickListener(this);
        v.findViewById(R.id.home_adduser).setOnClickListener(this);

        //推荐产品
        rl_hot_project = (RelativeLayout) v.findViewById(R.id.rl_hot_project);
        tv_hot_project_name = (TextView) v.findViewById(R.id.tv_hot_project_name);//推荐项目名称
        tv_hot_project_name.setOnClickListener(this);
        tv_hot_project_rate_first = (TextView) v.findViewById(R.id.tv_hot_project_rate_first);//预期出借利率 点之前（8）
        tv_hot_project_rate_second = (TextView) v.findViewById(R.id.tv_hot_project_rate_second);//预期出借利率 点之前（.00%）
        tv_hot_project_span = (TextView) v.findViewById(R.id.tv_hot_project_span);//项目期限
        bt_once_enjoy = (Button) v.findViewById(R.id.bt_once_enjoy);
        bt_once_enjoy.setOnClickListener(this);//立即加入
        //优选产品1
        v.findViewById(R.id.rl_good_project_more).setOnClickListener(this);//更多
        tv_good_project_name = (TextView) v.findViewById(R.id.tv_good_project_name);//优选产品名称
        tv_good_project_name.setOnClickListener(this);
        tv_good_project_rate_first = (TextView) v.findViewById(R.id.tv_good_project_rate_first);//预期出借利率 点之前
        tv_good_project_rate_second = (TextView) v.findViewById(R.id.tv_good_project_rate_second);//预期出借利率 点之后
        tv_good_project_span = (TextView) v.findViewById(R.id.tv_good_project_span);//项目期限
        v.findViewById(R.id.ll_good_project_first).setOnClickListener(this);

        //优选产品2
        tv_good_second_project_name = (TextView) v.findViewById(R.id.tv_good_second_project_name);//产品名称
        tv_good_second_project_name.setOnClickListener(this);
        tv_good_second_project_rate_first = (TextView) v.findViewById(R.id.tv_good_second_project_rate_first);//预期出借利率 点之前
        tv_good_second_project_rate_second = (TextView) v.findViewById(R.id.tv_good_second_project_rate_second);//预期出借利率 点之后
        tv_good_second_project_span = (TextView) v.findViewById(R.id.tv_good_second_project_span);//项目期限
        v.findViewById(R.id.ll_good_project_second).setOnClickListener(this);

        //积分精选第一个
        v.findViewById(R.id.rl_integral_more).setOnClickListener(this);
        v.findViewById(R.id.ll_integral_first).setOnClickListener(this);//积分精选第一个
        iv_integral_first = (ImageView) v.findViewById(R.id.iv_integral_first);//图片
        tv_integral_first_name = (TextView) v.findViewById(R.id.tv_integral_first_name);//商品名字
        tv_integral_first_use = (TextView) v.findViewById(R.id.tv_integral_first_use);//商品所需积分
        //积分精选第二个
        v.findViewById(R.id.ll_integral_second).setOnClickListener(this);
        iv_integral_second = (ImageView) v.findViewById(R.id.iv_integral_second);
        tv_integral_second_name = (TextView) v.findViewById(R.id.tv_integral_second_name);
        tv_integral_second_use = (TextView) v.findViewById(R.id.tv_integral_second_use);

        //联系客服
        v.findViewById(R.id.ll_phone).setOnClickListener(this);
        v.findViewById(R.id.ll_online).setOnClickListener(this);

        //出借人风险教育
        v.findViewById(R.id.iv_risk_education).setOnClickListener(this);

        //信息披露 关于我们 运营数据
        v.findViewById(R.id.home_order).setOnClickListener(this);
        //v.findViewById(R.id.ll_aboutus_nor).setOnClickListener(this);
        //v.findViewById(R.id.ll_report_nor).setOnClickListener(this);
        v.findViewById(R.id.tv_risk_hints).setOnClickListener(this);

        v_header = v.findViewById(R.id.v_header);
        ll_header = (RelativeLayout) v.findViewById(R.id.ll_header);
        tv_header = (TextView) v.findViewById(R.id.tv_header);
        tv_header.setVisibility(View.GONE);
        v_header.setVisibility(View.INVISIBLE);
        v_head_line.setVisibility(View.GONE);
        ll_header.getBackground().mutate().setAlpha(0);
        sv_scroll_view = (MyScrollViewScroll) v.findViewById(R.id.sv_scroll_view);
        sv_scroll_view.setOnScrollListener(new MyScrollViewScroll.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                int i = CommonUtil.dip2px(getActivity(), scrollY);
                int dp = CommonUtil.px2dip(getActivity(), i);
                if (dp <= 0) {
                    v_header.getBackground().mutate().setAlpha(0);
                    ll_header.getBackground().mutate().setAlpha(0);
                    tv_header.setTextColor(Color.argb(0, 52, 57, 60));
                    iv_white_message.setVisibility(View.VISIBLE);
                    iv_white_sign.setVisibility(View.VISIBLE);
                    iv_white_message.getBackground().mutate().setAlpha(Color.argb(0, 255, 255, 255));
                    iv_black_message.getBackground().mutate().setAlpha(0);
                    iv_black_sign.getBackground().mutate().setAlpha(0);
                    iv_white_sign.getBackground().mutate().setAlpha(Color.argb(0, 255, 255, 255));
                    v_head_line.getBackground().mutate().setAlpha(0);
                } else if (dp > 0 && dp <= 200) {
                    tv_header.setVisibility(View.VISIBLE);
                    v_header.setVisibility(View.VISIBLE);
                    v_head_line.setVisibility(View.VISIBLE);
                    float scale = (float) dp / 200;
                    int alpha = (int) (255 * scale);
                    iv_white_message.getBackground().mutate().setAlpha(dp);
                    iv_white_sign.getBackground().mutate().setAlpha(dp);
                    iv_black_message.setVisibility(View.VISIBLE);
                    iv_black_sign.setVisibility(View.VISIBLE);
                    v_header.getBackground().mutate().setAlpha((int) alpha);
                    ll_header.getBackground().mutate().setAlpha((int) alpha);
                    tv_header.setTextColor(Color.argb(alpha, 52, 57, 60));
                    iv_black_message.getBackground().mutate().setAlpha((int) alpha);
                    iv_black_sign.getBackground().mutate().setAlpha((int) alpha);
                    v_head_line.getBackground().mutate().setAlpha((int) alpha);
                } else {
                    iv_white_message.setVisibility(View.GONE);
                    iv_white_sign.setVisibility(View.GONE);
                    v_header.getBackground().mutate().setAlpha(255);
                    ll_header.getBackground().mutate().setAlpha(255);
                    tv_header.setTextColor(Color.argb(255, 52, 57, 60));
                    iv_black_message.getBackground().mutate().setAlpha(255);
                    iv_black_sign.getBackground().mutate().setAlpha(255);
                    v_head_line.getBackground().mutate().setAlpha(255);
                }
            }
        });
    }


    private void initData() {
        //	//给View传递数据
        //		tv_tips.setList(lst);
        //		//更新View
        //		tv_tips.updateUI();
        initPagerBean();
        //viewpager你放到那里去都能用，组装思想
        RollViewPager rollViewPager = new RollViewPager(getActivity(), ll_dot, tv_title, pagerBean,
                //回调相关
                new RollViewPager.OnPageClick() {
                    @Override
                    public void onClick(String url,String title) {
                        onEvent(getActivity(), "101004_banner_click");
                        System.out.println("url:" + url);
                        if (title != null && !TextUtils.isEmpty(title)) {
                            Intent intent;
                            if(title.contains("年度报告")){
                                intent = new Intent(getActivity(), HomeYearReportH5Activity.class);
                            }else{
                                intent = new Intent(getActivity(), RollViewActivity.class);
                            }
                            DoCacheUtil util = DoCacheUtil.get(getActivity());
                            String str = util.getAsString("isLogin");
                            String token = "";
                            if (str != null) {
                                if (str.equals("isLogin")) {//已登录
                                    token = LoginUserProvider.getUser(getActivity()).getToken();
                                } else {
                                    token = "";
                                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                    loginIntent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                                    startActivity(loginIntent);
                                    return;
                                }
                            } else {
                                token = "";
                                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                loginIntent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                                startActivity(loginIntent);
                                return;
                            }
if(!TextUtils.isEmpty(url)&&url!=null&&url!=""){
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
                    }
                }
        );
        //滚动自定义viewpager方法
        rollViewPager.startRoll();

        ll_top_view_pager.removeAllViews();
        ll_top_view_pager.addView(rollViewPager);
    }

    private void initPagerBean() {
        List<String> titles = new ArrayList<String>();
        titles.add("title1");
        titles.add("title2");
        titles.add("title3");
        titles.add("title4");


        pagerBean = new PagerBean(imgUrls, titleList, textUrls);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_once_enjoy://立即投资
                onEvent(getActivity(), "101009_recommended_products_click");
                if (hotProjectType.equals("1")) {//安心投
                    //Intent intent = new Intent(getActivity(), SafeInvestmentDetailActivity.class);
                    Intent intent = new Intent(getActivity(), InvestmentDetailBorrowActivity.class);
                    intent.putExtra("projectid", hotProjectId);
                    intent.putExtra("loanDate", hotProjectLoandata);
                    startActivity(intent);
                    ACache.get(getActivity()).put(Constant.Investment_Detail_SupplyChain_Relieved_Key, "0");//安心投，不要回款计划
                } else {//供应链
                    ACache.get(getActivity()).put(Constant.Investment_Detail_SupplyChain_Relieved_Key, "0");//供应链，不要回款计划
                    //Intent intent = new Intent(getActivity(), InvestmentDetailActivity.class);
                    Intent intent = new Intent(getActivity(), InvestmentDetailBorrowActivity.class);
                    intent.putExtra("projectid", hotProjectId);
                    intent.putExtra("loanDate", hotProjectLoandata);
                    startActivity(intent);
                }

                break;
            //case R.id.ll_notice:
            //	Intent noticeIntent = new Intent(getActivity(),NoticeListActivity.class);
            //	startActivity(noticeIntent);
            //	break;
            case R.id.iv_white_message://首页白底的图片消息
            case R.id.iv_black_message://首页消息
                onEvent(getActivity(), "101002_news_click");
                Intent messageIntent = new Intent(getActivity(), NewMessageActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.iv_white_sign://首页白底的图片签到
            case R.id.iv_black_sign://首页签到
                onEvent(getActivity(), "101003_sign_click");
                if (LoginUserProvider.getUser(getActivity()) != null) {
                    userSigned(LoginUserProvider.getUser(getActivity()).getToken(), "3");
                    //				DoCacheUtil util=DoCacheUtil.get(getActivity());
                    //				String str=util.getAsString("isLogin");
                    //				if(str!=null){
                    //					if (str.equals("isLogin")) {//已登录
                    ////						userSigned(LoginUserProvider.getUser(getActivity()).getToken(),"3");
                    //						//是否签到 3：未签到，2：已签到
                    //						if(LoginUserProvider.getUser(getActivity()).getSigned()!=null&&LoginUserProvider.getUser(getActivity()).getSigned().equals("3")){
                    //							userSigned(LoginUserProvider.getUser(getActivity()).getToken(),"3");
                    //						}else if(LoginUserProvider.getUser(getActivity()).getSigned()!=null&&LoginUserProvider.getUser(getActivity()).getSigned().equals("2")){
                    //							Toast.makeText(getActivity(), "您已签到，请明天再来", 0).show();
                    //						}
                    //					}else {//未登录
                    //						Intent intent1 = new Intent(getActivity(),LoginActivity.class);
                    //						intent1.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                    //						startActivity(intent1);
                    //					}
                    //				}else{
                    //					Intent intent2 = new Intent(getActivity(),LoginActivity.class);
                    //					intent2.putExtra("overtime", "6");
                    //					startActivity(intent2);
                    //				}
                } else {
                    Intent intent3 = new Intent(getActivity(), LoginActivity.class);
                    intent3.putExtra("overtime", "6");
                    startActivity(intent3);
                }
                break;
            case R.id.home_gift://注册有礼
                onEvent(getActivity(), "101005_registered_courtesy_click");
                Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                registerIntent.putExtra("overtime", "0");
                startActivity(registerIntent);
                break;
            case R.id.home_order://信息披露
                onEvent(getActivity(), "101017_information_disclosure_click");
                Intent safeNoDataIntent = new Intent(getActivity(), HomeDataH5Activity.class);
                safeNoDataIntent.putExtra("url", Urls.DISCLOSUREHOME);
                safeNoDataIntent.putExtra("name", "信息披露");
                startActivity(safeNoDataIntent);
                break;

            //            case R.id.ll_aboutus_nor://关于我们
            //                onEvent(getActivity(), "101018_about_click");
            //                Intent aboutWeNoDataIntent = new Intent(getActivity(), AboutWeActivity.class);
            //                startActivity(aboutWeNoDataIntent);
            //                break;
            //            case R.id.ll_report_nor://运营数据
            //                onEvent(getActivity(), "101019_operation_click");
            //                Intent operationReportIntent = new Intent(getActivity(), HomeDataH5Activity.class);
            //                operationReportIntent.putExtra("url", Urls.MOREDATA);
            //                operationReportIntent.putExtra("name", "运营数据");
            //                startActivity(operationReportIntent);
            //                break;
            case R.id.home_adduser://邀请好友
                onEvent(getActivity(), "101006_invitation_friends_click");
                if (LoginUserProvider.getUser(getActivity()) != null) {
                    DoCacheUtil util = DoCacheUtil.get(getActivity());
                    String str = util.getAsString("isLogin");
                    if (str != null) {
                        if (str.equals("isLogin")) {//已登录
                            Intent invitIntent = new Intent(getActivity(), RequestFriendsActivity.class);
                            startActivity(invitIntent);
                        } else {//未登录
                            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                            loginIntent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                            startActivity(loginIntent);
                        }
                    } else {
                        Intent loginIntent1 = new Intent(getActivity(), LoginActivity.class);
                        loginIntent1.putExtra("overtime", "6");
                        startActivity(loginIntent1);
                    }
                } else {
                    Intent loginIntent2 = new Intent(getActivity(), LoginActivity.class);
                    loginIntent2.putExtra("overtime", "6");
                    startActivity(loginIntent2);
                }
                break;
            case R.id.iv_risk_education://风险人教育
                onEvent(getActivity(), "101016_education_click");
                Intent riskEducationNoDataIntent = new Intent(getActivity(), HomeDataH5Activity.class);
                riskEducationNoDataIntent.putExtra("url", Urls.MOREEDUCATION);
                riskEducationNoDataIntent.putExtra("name", "风险教育");
                startActivity(riskEducationNoDataIntent);
                break;
            case R.id.tv_risk_hints://提示
                Intent riskTipFirstIntent = new Intent(getActivity(), AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                startActivity(riskTipFirstIntent);
                break;
            case R.id.rl_integral_more://积分精选更多
                onEvent(getActivity(), "101013_integral_selection_more_click");
                Intent integralMoreIntent = new Intent(getActivity(), IntegralShopActivity.class);
                startActivity(integralMoreIntent);
                break;
            case R.id.ll_good_project_first:
                onEvent(getActivity(), "101010_preferred_products_list_click");
                //供应链
                ACache.get(getActivity()).put(Constant.Investment_Detail_SupplyChain_Relieved_Key, "0");//供应链，不要回款计划
                //Intent project_firstIntent = new Intent(getActivity(), InvestmentDetailActivity.class);
                Intent project_firstIntent = new Intent(getActivity(), InvestmentDetailBorrowActivity.class);
                project_firstIntent.putExtra("projectid", goodProjectFristId);
                project_firstIntent.putExtra("loanDate", goodProjectFristLoanData);
                startActivity(project_firstIntent);
                break;
            case R.id.ll_good_project_second:
                //安心投
                ACache.get(getActivity()).put(Constant.Investment_Detail_SupplyChain_Relieved_Key, "0");//安心投，不要回款计划
                onEvent(getActivity(), "101010_preferred_products_list_click");
                //Intent project_secondIntent = new Intent(getActivity(), InvestmentDetailActivity.class);
                Intent project_secondIntent = new Intent(getActivity(), InvestmentDetailBorrowActivity.class);
                project_secondIntent.putExtra("projectid", goodProjectSecondId);
                project_secondIntent.putExtra("loanDate", goodProjectSecondLoanData);
                startActivity(project_secondIntent);
                break;
            case R.id.rl_good_project_more://更多优选产品
                onEvent(getActivity(), "101011_preferred_products_more_click");
                MyEvent event = new MyEvent();
                event.setType("UX");
                EventBus.getDefault().post(event);
                break;
            case R.id.ll_integral_first://小米移动电源
                onEvent(getActivity(), "101012_integral_selection_list_click");
                Intent integralFirstIntent = new Intent(getActivity(), GoodsDetailActivity.class);
                integralFirstIntent.putExtra("awardId", "6be4b6d1ea3e4a6cabe96e625136b386");
                startActivity(integralFirstIntent);
                break;
            case R.id.ll_integral_second://小米便携鼠标
                onEvent(getActivity(), "101012_integral_selection_list_click");
                Intent integralSecondIntent = new Intent(getActivity(), GoodsDetailActivity.class);
                integralSecondIntent.putExtra("awardId", "b911ae765f72468fb9d333fdc36e50f0");
                startActivity(integralSecondIntent);
                break;
            case R.id.ll_phone:
                if (!PermissionUtil.requestCallPhone(mActivity))
                    return;
                onEvent(getActivity(), "101014_telephone_consultation_click");
                ContactServiceDialog dialog = new ContactServiceDialog(getActivity(), R.style.SelectPicDialog, "0");
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                dialog.show();
                break;
            case R.id.ll_online:
                //				ContactServiceDialog dialogOnline = new ContactServiceDialog(getActivity(),R.style.SelectPicDialog,"1");
                //				Window dialogWindowOnline = dialogOnline.getWindow();
                //				WindowManager.LayoutParams lpOnline = dialogWindowOnline.getAttributes();
                //				lpOnline.width = WindowManager.LayoutParams.FILL_PARENT;
                //				dialogWindowOnline.setAttributes(lpOnline);
                //				dialogOnline.show();
                onEvent(getActivity(), "101015_on_line_consultation_click");
                Intent intent = new Intent(getActivity(), OnlineContactWeActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_new_hand_guide:
                onEvent(getActivity(), "101020_guide_click");
                //if(LoginUserProvider.getUser(getActivity())!=null){
                //	newHandGuide(3,LoginUserProvider.getUser(getActivity()).getToken(),0);
                //}
                if (LoginUserProvider.getUser(getActivity()) != null) {
                    newHandGuide(3, LoginUserProvider.getUser(getActivity()).getToken(), 1);
                } else {
                    newHandGuideDialog = new NewHandGuideDialog(getActivity(), R.style.MyDialogDeletAddress, "0");//默认注册
                    newHandGuideDialog.show();
                    newHandGuideDialog.setCancelable(false);
                }
                break;
            default:
                break;
        }
    }

    //获取轮播图
    private void getCmsListByType(String pageNo, String pageSize, final String type, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETCMSLISTBYTYPE;
        RequestParams params = new RequestParams();
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("type", type);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    imgUrls = new ArrayList<String>();
                    textUrls = new ArrayList<String>();
                    titleList = new ArrayList<String>();
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray cmsArray = dataObject.getJSONArray("cmsList");
                        for (int i = 0; i < cmsArray.length(); i++) {
                            JSONObject obj = cmsArray.getJSONObject(i);
                            String img = obj.getString("imgPath");
                            String text = obj.getString("text");
                            String title = obj.getString("title");
                            titleList.add(title);
                            imgUrls.add(img);
                            textUrls.add(text);
                        }
                        DoCacheUtil util = DoCacheUtil.get(getActivity());
                        util.put("titleList", (Serializable) titleList);
                        initData();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //签到弹出框
    public class SignDialog extends Dialog {
        Context context;

        public SignDialog(Context context) {
            super(context);
            this.context = context;
        }

        public SignDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_sign);
            initView1();
        }

        private void initView1() {
            RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
            tv_integral = (TextView) findViewById(R.id.tv_integral);//签到的积分
            tv_sign_days = (TextView) findViewById(R.id.tv_sign_days);//签到天数
            tv_integral.setText(integral);
            tv_sign_days.setText(continuousTime);

            parent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    //已签到弹出框
    public class AlreadySignDialog extends Dialog {
        Context context;

        public AlreadySignDialog(Context context) {
            super(context);
            this.context = context;
        }

        public AlreadySignDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_already_sign);
            initView2();
        }

        private void initView2() {
            RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
            tv_sign_days = (TextView) findViewById(R.id.tv_sign_days);//签到天数
            tv_total_integral = (TextView) findViewById(R.id.tv_total_integral);//签到的积分
            tv_sign_days.setText(continuousTime);
            tv_total_integral.setText(integralCount);
            parent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alreadySignDialog.dismiss();
                }
            });
        }
    }


    //获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETUSERINFO;
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
                        UserInfo info = new UserInfo();
                        info.setToken(token);
                        info.setPhone(dataObj.getString("name"));
                        info.setBankPas(dataObj.getString("businessPwd"));//交易密码
                        info.setEmail(dataObj.getString("email"));//邮箱
                        String cgbBindBankCardState = dataObj.getString("cgbBindBankCardState");
                        if(cgbBindBankCardState.equals("null")){
                            info.setIsBindBank("1");//是否绑定银行卡 1未绑定 2已绑定
                        }else{
                            info.setIsBindBank(cgbBindBankCardState);
                        }
                        String certificateChecked = dataObj.getString("certificateChecked");
                        if(certificateChecked.equals("null")){
                            info.setCertificateChecked("1");
                        }else{
                            info.setCertificateChecked(dataObj.getString("certificateChecked"));//是否注册 1未开户  2已开户
                        }
                        info.setGesturePwd(dataObj.getString("gesturePwd"));//是否设置过手势密码，0设置1已设置
                        info.setAddress(dataObj.getString("address"));//地址
                        info.setEmergencyUser(dataObj.getString("emergencyUser"));//紧急联系人
                        info.setEmergencyTel(dataObj.getString("emergencyTel"));//紧急联系电话
                        info.setRealName(dataObj.getString("realName"));//真实姓名
                        info.setIdCard(dataObj.getString("IdCard"));//身份证号
                        info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));//银行卡号
                        String isSigned = dataObj.getString("signed");
                        info.setSigned(isSigned);//是否签到 3：未签到，2：已签到
                        if (!TextUtils.isEmpty(isSigned) && isSigned.equals("3")) {//3：未签到，2：已签到
                            tv_sign.setText("签到");
                            iv_unsign.setVisibility(View.VISIBLE);
                            //							iv_message.setBackgroundResource(R.drawable.pic_new_sign_nosign);
                            //							iv_black_sign.setBackgroundResource(R.drawable.nav_icon_register_unregister_gray);
                        } else if (!TextUtils.isEmpty(isSigned) && isSigned.equals("2")) {
                            tv_sign.setText("已签到");
                            iv_unsign.setVisibility(View.GONE);
                            //							iv_message.setBackgroundResource(R.drawable.pic_new_sign);
                            //							iv_black_sign.setBackgroundResource(R.drawable.nav_icon_register_nor_gray);
                        }
                        dialog = new SignDialog(getActivity(), R.style.MyDialogDeletAddress);
                        dialog.show();
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //用户签到
    private void userSigned(final String token, String from) {
        //CustomProgress.show(getActivity());
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.USERSIGNED;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        continuousTime = dataObj.getString("continuousTime");//连续签到的次数
                        integral = dataObj.getString("integral");//每次签到的积分
                        integralCount = dataObj.getString("integralCount");//当前总积分
                        String signed = dataObj.getString("signed");//签到返回信息｛1：签到成功，2：已签到，请明天再来｝
                        if (!TextUtils.isEmpty(signed) && signed.equals("1")) {
                            getUserInfo(token, "3");
                        } else if (!TextUtils.isEmpty(signed) && signed.equals("2")) {
                            tv_sign.setText("已签到");
                            alreadySignDialog = new AlreadySignDialog(getActivity(), R.style.MyDialogDeletAddress);
                            alreadySignDialog.show();
                            //							Toast.makeText(getActivity(), "您已签到，请明天再来", 0).show();
                        }
                        //  						getUserInfo(LoginUserProvider.getUser(getActivity()).getToken(),"3");
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

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //是否有未读消息
    private void isReadLetter(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.LETTERSTATE;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        //letterState    1 有未读消息  0  没有未读消息
                        String letterState = dataObj.getString("letterState");
                        if (letterState.equals("1")) {
                            iv_unread_message.setVisibility(View.VISIBLE);
                            //							iv_home_message.setBackgroundResource(R.drawable.pic_new_message_unread);
                            //							iv_black_message.setBackgroundResource(R.drawable.icon_msg_unread_gray);
                        } else if (letterState.equals("0")) {
                            iv_unread_message.setVisibility(View.GONE);
                            //							iv_home_message.setBackgroundResource(R.drawable.pic_new_message);
                            //							iv_black_message.setBackgroundResource(R.drawable.icon_msg_gray);
                        }
                    } else {
                        iv_unread_message.setVisibility(View.GONE);
                        //						iv_home_message.setBackgroundResource(R.drawable.pic_new_message);
                        //						iv_black_message.setBackgroundResource(R.drawable.icon_msg_gray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //联系客服弹框
    public class ContactServiceDialog extends Dialog {
        Context context;
        String type;

        public ContactServiceDialog(Context context) {
            super(context);
            this.context = context;
        }

        public ContactServiceDialog(Context context, int theme, String type) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_contact_service);
            this.type = type;

            TextView tv_on_line = (TextView) findViewById(R.id.tv_on_line);//在线客服
            TextView tv_investment = (TextView) findViewById(R.id.tv_investment);//投资业务
            TextView tv_loan = (TextView) findViewById(R.id.tv_loan);//借款业务
            if (type.equals("0")) {//电话咨询
                tv_loan.setVisibility(View.VISIBLE);
                tv_on_line.setVisibility(View.GONE);
            } else if (type.equals("1")) {
                tv_loan.setVisibility(View.GONE);
                tv_on_line.setVisibility(View.VISIBLE);
            }
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

    //新手引导弹出框
    public class NewHandGuideDialog extends Dialog {
        Context context;
        String step;

        public NewHandGuideDialog(Context context) {
            super(context);
            this.context = context;
        }

        public NewHandGuideDialog(Context context, int theme, String step) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_new_hand_guide);
            this.step = step;
            newHandGuideInitView(step);
        }

        private void newHandGuideInitView(final String step) {
            RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
            ImageView iv_close = (ImageView) findViewById(R.id.iv_close);
            TextView tv_register_text = (TextView) findViewById(R.id.tv_register_text);
            TextView tv_register_content = (TextView) findViewById(R.id.tv_register_content);
            Button bt_execution = (Button) findViewById(R.id.bt_execution);
            ImageView iv_write_triangle_first = (ImageView) findViewById(R.id.iv_write_triangle_first);
            ImageView iv_triangle_second = (ImageView) findViewById(R.id.iv_triangle_second);
            ImageView iv_triangle_third = (ImageView) findViewById(R.id.iv_triangle_third);
            ImageView iv_triangle_fourth = (ImageView) findViewById(R.id.iv_triangle_fourth);
            ImageView iv_triangle_fifth = (ImageView) findViewById(R.id.iv_triangle_fifth);

            ImageView iv_finish_frist = (ImageView) findViewById(R.id.iv_finish_frist);
            ImageView iv_finish_second = (ImageView) findViewById(R.id.iv_finish_second);
            ImageView iv_finish_third = (ImageView) findViewById(R.id.iv_finish_third);
            ImageView tv_finish_fourth = (ImageView) findViewById(R.id.tv_finish_fourth);
            ImageView iv_finish_fifth = (ImageView) findViewById(R.id.iv_finish_fifth);

            TextView tv_bank_first = (TextView) findViewById(R.id.tv_bank_first);
            TextView tv_tv_bank_second = (TextView) findViewById(R.id.tv_tv_bank_second);
            TextView tv_test_first = (TextView) findViewById(R.id.tv_test_first);
            TextView tv_test_second = (TextView) findViewById(R.id.tv_test_second);

            if (step.equals("3")) {//开通银行存管
                tv_register_text.setText("开通银行存管");
                tv_register_content.setText("开通银行存管，以便于帮您建立海口联合农商银行存管账户，您的资金会由海口联合农商银行负责存管。");
                bt_execution.setText("前往开通");
                iv_write_triangle_first.setVisibility(View.INVISIBLE);
                iv_triangle_second.setVisibility(View.INVISIBLE);
                iv_triangle_third.setVisibility(View.VISIBLE);
                iv_triangle_fourth.setVisibility(View.INVISIBLE);
                iv_triangle_fifth.setVisibility(View.INVISIBLE);

                iv_finish_frist.setBackgroundResource(R.drawable.icon_newuser_done);
                iv_finish_second.setBackgroundResource(R.drawable.icon_newuser_done);
                iv_finish_third.setBackgroundResource(R.drawable.icon_newuser_inprocess);
                tv_finish_fourth.setBackgroundResource(R.drawable.icon_newuser_undo);
                iv_finish_fifth.setBackgroundResource(R.drawable.icon_newuser_undo);

                tv_bank_first.setTextColor(getResources().getColor(R.color.text_cbb693));
                tv_tv_bank_second.setTextColor(getResources().getColor(R.color.text_cbb693));

            } else if (step.equals("4")) {//风险测评
                tv_register_text.setText("风险测评");
                tv_register_content.setText("请您进行风险测评，以便我们对您有更加全面的了解，为您推荐更加适合的产品。");
                bt_execution.setText("前往测评");

                iv_write_triangle_first.setVisibility(View.INVISIBLE);
                iv_triangle_second.setVisibility(View.INVISIBLE);
                iv_triangle_third.setVisibility(View.INVISIBLE);
                iv_triangle_fourth.setVisibility(View.VISIBLE);
                iv_triangle_fifth.setVisibility(View.INVISIBLE);

                iv_finish_frist.setBackgroundResource(R.drawable.icon_newuser_done);
                iv_finish_second.setBackgroundResource(R.drawable.icon_newuser_done);
                iv_finish_third.setBackgroundResource(R.drawable.icon_newuser_done);
                tv_finish_fourth.setBackgroundResource(R.drawable.icon_newuser_inprocess);
                iv_finish_fifth.setBackgroundResource(R.drawable.icon_newuser_undo);

                tv_test_first.setTextColor(getResources().getColor(R.color.text_cbb693));
                tv_test_second.setTextColor(getResources().getColor(R.color.text_cbb693));
            }
            //            parent.setOnClickListener(new View.OnClickListener() {
            //
            //                @Override
            //                public void onClick(View v) {
            //                    newHandGuideDialog.dismiss();
            //                }
            //            });
            bt_execution.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEvent(getActivity(), "101022_guide_button_click");
                    if (step.equals("3")) {//开通银行存管
                        //				tv_register_text
                        //				tv_register_content
                        Intent intent = new Intent(getActivity(), BindBankCardActivity.class);
                        intent.putExtra("isBackAccount", "3");
                        startActivity(intent);
                        newHandGuideDialog.dismiss();
                    } else if (step.equals("4")) {//风险测评
                        Intent testIntent = new Intent(getActivity(), TestQuestionFirstActivity.class);
                        testIntent.putExtra("isInvestment", "0");
                        startActivity(testIntent);
                        newHandGuideDialog.dismiss();
                    } else {
                        Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                        registerIntent.putExtra("overtime", "6");
                        startActivity(registerIntent);
                        newHandGuideDialog.dismiss();
                    }
                }
            });
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEvent(getActivity(), "101021_guide_close_click");
                    newHandGuideDialog.dismiss();
                }
            });
        }
    }


    //年报弹出框
    public class YearDialog extends Dialog {
        Context context;
        String step;

        public YearDialog(Context context) {
            super(context);
            this.context = context;
        }

        public YearDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_year_report);
            this.step = step;
            yearReportInitView();
        }

        private void yearReportInitView() {
            View v_close = findViewById(R.id.v_close);
            View v_check = findViewById(R.id.v_check);
            v_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = Urls.ANNUALREPORT;
                    Intent intent = new Intent(getActivity(), HomeYearReportH5Activity.class);
                    DoCacheUtil util = DoCacheUtil.get(getActivity());
                    String str = util.getAsString("isLogin");
                    String token = "";
                    if (str != null) {
                        if (str.equals("isLogin")) {//已登录
                            token = LoginUserProvider.getUser(getActivity()).getToken();
                        } else {
                            token = "";
                            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                            loginIntent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                            startActivity(loginIntent);
                            return;
                        }
                    } else {
                        token = "";
                        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                        loginIntent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                        startActivity(loginIntent);
                        return;
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
                    yearDialog.dismiss();

//                    Intent yearReportIntent = new Intent(getActivity(),HomeYearReportH5Activity.class);
//                    yearReportIntent.putExtra("url","");
//                    startActivity(yearReportIntent);
                }
            });
            v_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yearDialog.dismiss();
                }
            });
//            iv_close.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onEvent(getActivity(), "101021_guide_close_click");
//                    yearDialog.dismiss();
//                }
//            });
        }
    }



    //新手引导  type 0 自动加载    type 1 点击请求
    private void newHandGuide(int from, String token, final int type) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.USERGUIDANCE;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("type", type);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataobj = jsonObject.getJSONObject("data");
                        String step = dataobj.getString("step");
                        String cgbBindBankCardState = dataobj.getString("cgbBindBankCardState");//是否绑定银行卡 1未绑定 2已绑定
                        String certificateChecked = dataobj.getString("certificateChecked");//是否注册 1未注册  2已注册
                        if (step.equals("3")) {
                            if(certificateChecked.equals("1")){
                                //弹出存管银行
                                newHandGuideDialog = new NewHandGuideDialog(getActivity(), R.style.MyDialogDeletAddress, step);
                                if (!newHandGuideDialog.isShowing()) {
                                    newHandGuideDialog.show();
                                    newHandGuideDialog.setCancelable(false);
                                }
                            }

                        } else if (step.equals("4")) {
                            //弹出风险测评
                            //TODO dong  有bug
                            newHandGuideDialog = new NewHandGuideDialog(getActivity(), R.style.MyDialogDeletAddress, step);
                            if (!newHandGuideDialog.isShowing()) {
                                newHandGuideDialog.show();
                                newHandGuideDialog.setCancelable(false);
                            }
                        } else if (step.equals("5")) {
                            //全部完成
                            //							newHandGuideDialog = new NewHandGuideDialog(getActivity(),R.style.MyDialogDeletAddress,step);
                            //							newHandGuideDialog.show();
                            //							newHandGuideDialog.setCancelable(false);
                            tv_new_hand_guide.setVisibility(View.GONE);
                        }
//                        else if(step.equals("6")){
//                            //已开户未绑卡
//                            tv_new_hand_guide.setVisibility(View.GONE);
//                        }
                    } else if (jsonObject.getString("state").equals("4")) {
                        if (type == 1) {
                            //                            if (newHandGuideDialog!=null&&!newHandGuideDialog.isShowing()) {
                            //
                            //                            }
                            newHandGuideDialog = new NewHandGuideDialog(getActivity(), R.style.MyDialogDeletAddress, "0");//默认的注册
                            newHandGuideDialog.show();
                            newHandGuideDialog.setCancelable(false);
                        }
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "解析异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //请求推荐列表
    private void getProjectListWap(int from, final int pageNo, int pageSize) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETPROJECTLISTWAP;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getGoodProjectListWap(3, 1, 10, 2);
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    HomeTjProjectEntity homeTjProjectEntity = new Gson().fromJson(result, HomeTjProjectEntity.class);
                    if (homeTjProjectEntity != null) {
                        String state = homeTjProjectEntity.getState();
                        if (state.equals("0")) {
                            String totalCount = homeTjProjectEntity.getTotalCount();
                            if (homeTjProjectEntity.getData() != null) {
                                List<HomeTjProjectEntity.DataBean.ListTJBean> listTJ = homeTjProjectEntity.getData().getListTJ();
                                if (listTJ != null && listTJ.size() > 0) {
                                    rl_hot_project.setVisibility(View.VISIBLE);
                                    HomeTjProjectEntity.DataBean.ListTJBean listTJBean = listTJ.get(0);
                                    if (listTJBean.getProjectProductType().equals("2")) {
                                        tv_hot_project_name.setText(listTJBean.getName() + "(" + listTJBean.getSn() + ")");
                                    } else {
                                        tv_hot_project_name.setText(listTJBean.getName());
                                    }
                                    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    if (!decimalFormat.format(Double.parseDouble(listTJBean.getInterestRateIncrease())).equals("0.00")) {
                                        //得到基本利率 =  总利率-加息利率
                                        Double basic = Double.parseDouble(listTJBean.getAnnualRate()) - Double.parseDouble(listTJBean.getInterestRateIncrease());
                                        String rateDot = decimalFormat.format(basic);
                                        tv_hot_project_rate_first.setText(rateDot);
                                        String interestRateIncrease = decimalFormat.format(Double.parseDouble(listTJBean.getInterestRateIncrease()));
                                        tv_hot_project_rate_second.setText("+" + interestRateIncrease + "%");
                                    } else {
                                        String rateDot = decimalFormat.format(Double.parseDouble(listTJBean.getAnnualRate()));
                                        tv_hot_project_rate_first.setText(rateDot);
                                        tv_hot_project_rate_second.setVisibility(View.GONE);
                                    }
                                    tv_hot_project_span.setText(listTJBean.getSpan() + "");
                                    hotProjectId = listTJBean.getId();
                                    hotProjectLoandata = listTJBean.getLoanDate();
                                    hotProjectType = listTJBean.getProjectProductType();
                                    if (listTJBean.getState().equals("4")) {//4是立即投资
                                        if (!TextUtils.isEmpty(listTJBean.getLoanDate()) && !listTJBean.getLoanDate().equals("null")) {
                                            String loanTime = listTJBean.getLoanDate();
                                            boolean isBidders = TimeUtil.compareInverstmentListNowTime(loanTime);
                                            if (isBidders) {
                                                bt_once_enjoy.setText("立即加入");
                                                bt_once_enjoy.setBackgroundResource(R.drawable.bt_red);
                                            } else {
                                                bt_once_enjoy.setText("已到期");
                                                bt_once_enjoy.setBackgroundResource(R.drawable.bt_gray);
                                            }
                                        }
                                    } else if (listTJBean.getState().equals("3")) {
                                        bt_once_enjoy.setText("即将上线");
                                        bt_once_enjoy.setBackgroundResource(R.drawable.bt_red);
                                    }
                                    //for (HomeTjProjectEntity.DataBean.ListTJBean bean : listTJ) {
                                    //    if (bean.getProjectProductType().equals("2")) {
                                    //        tv_hot_project_name.setText(bean.getName() + "(" + bean.getSn() + ")");
                                    //        //DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    //        //String rateDot = decimalFormat.format(Double.parseDouble(bean.getAnnualRate()));
                                    //        //tv_hot_project_rate_first.setText(rateDot.substring(0, rateDot.indexOf(".")));
                                    //
                                    //        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    //        if (!decimalFormat.format(Double.parseDouble(bean.getInterestRateIncrease())).equals("0.00")) {
                                    //            //得到基本利率 =  总利率-加息利率
                                    //            Double basic = Double.parseDouble(bean.getAnnualRate()) - Double.parseDouble(bean.getInterestRateIncrease());
                                    //            String rateDot = decimalFormat.format(basic);
                                    //            tv_hot_project_rate_first.setText(rateDot);
                                    //            String interestRateIncrease = decimalFormat.format(Double.parseDouble(bean.getInterestRateIncrease()));
                                    //            tv_hot_project_rate_second.setText("+" + interestRateIncrease + "%");
                                    //        } else {
                                    //            String rateDot = decimalFormat.format(Double.parseDouble(bean.getAnnualRate()));
                                    //            tv_hot_project_rate_first.setText(rateDot);
                                    //            tv_hot_project_rate_second.setVisibility(View.GONE);
                                    //        }
                                    //        tv_hot_project_span.setText(bean.getSpan() + "");
                                    //        hotProjectId = bean.getId();
                                    //        hotProjectLoandata = bean.getLoanDate();
                                    //        hotProjectType = bean.getProjectProductType();
                                    //
                                    //        if (bean.getState().equals("4")) {//4是立即投资
                                    //            if (!TextUtils.isEmpty(bean.getLoanDate()) && !bean.getLoanDate().equals("null")) {
                                    //                String loanTime = bean.getLoanDate();
                                    //                boolean isBidders = TimeUtil.compareInverstmentListNowTime(loanTime);
                                    //                if (isBidders) {
                                    //                    bt_once_enjoy.setText("立即加入");
                                    //                    bt_once_enjoy.setBackgroundResource(R.drawable.bt_red);
                                    //                } else {
                                    //                    bt_once_enjoy.setText("已到期");
                                    //                    bt_once_enjoy.setBackgroundResource(R.drawable.bt_gray);
                                    //                }
                                    //            }
                                    //        } else if (bean.getState().equals("3")) {
                                    //            bt_once_enjoy.setText("即将上线");
                                    //            bt_once_enjoy.setBackgroundResource(R.drawable.bt_red);
                                    //        }
                                    //        return;
                                    //
                                    //    } else {
                                    //        tv_hot_project_name.setText(listTJ.get(0).getName());
                                    //        //DecimalFormat decimalFormat1 = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    //        //String rateDot1 = decimalFormat1.format(Double.parseDouble(listTJ.get(0).getAnnualRate()));
                                    //        //tv_hot_project_rate_first.setText(rateDot1);
                                    //        //tv_hot_project_rate_second.setText(rateDot.substring(rateDot.indexOf("."), rateDot.length()) + "%");
                                    //
                                    //        tv_hot_project_span.setText(listTJ.get(0).getSpan() + "");
                                    //        hotProjectId = listTJ.get(0).getId();
                                    //        hotProjectLoandata = listTJ.get(0).getLoanDate();
                                    //        hotProjectType = bean.getProjectProductType();
                                    //        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    //        if (!decimalFormat.format(Double.parseDouble(bean.getInterestRateIncrease())).equals("0.00")) {
                                    //            //得到基本利率 =  总利率-加息利率
                                    //            Double basic = Double.parseDouble(bean.getAnnualRate()) - Double.parseDouble(bean.getInterestRateIncrease());
                                    //            String rateDot = decimalFormat.format(basic);
                                    //            tv_hot_project_rate_first.setText(rateDot);
                                    //            String interestRateIncrease = decimalFormat.format(Double.parseDouble(bean.getInterestRateIncrease()));
                                    //            tv_hot_project_rate_second.setText("+" + interestRateIncrease + "%");
                                    //        } else {
                                    //            String rateDot = decimalFormat.format(Double.parseDouble(bean.getAnnualRate()));
                                    //            tv_hot_project_rate_first.setText(rateDot);
                                    //            tv_hot_project_rate_second.setVisibility(View.GONE);
                                    //        }
                                    //
                                    //
                                    //        if (listTJ.get(0).getState().equals("4")) {//4是立即投资
                                    //            if (!TextUtils.isEmpty(listTJ.get(0).getLoanDate()) && !listTJ.get(0).getLoanDate().equals("null")) {
                                    //                String loanTime = listTJ.get(0).getLoanDate();
                                    //                boolean isBidders = TimeUtil.compareInverstmentListNowTime(loanTime);
                                    //                if (isBidders) {
                                    //                    bt_once_enjoy.setText("立即加入");
                                    //                    bt_once_enjoy.setBackgroundResource(R.drawable.bt_red);
                                    //                } else {
                                    //                    bt_once_enjoy.setText("已到期");
                                    //                    bt_once_enjoy.setBackgroundResource(R.drawable.bt_gray);
                                    //                }
                                    //            }
                                    //        } else if (listTJ.get(0).getState().equals("3")) {
                                    //            bt_once_enjoy.setText("即将上线");
                                    //            bt_once_enjoy.setBackgroundResource(R.drawable.bt_red);
                                    //        }
                                    //    }
                                    //}
                                } else {
                                    rl_hot_project.setVisibility(View.GONE);
                                }
                            }
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
                            //LoginUserProvider.cleanData(getActivity());
                            //LoginUserProvider.cleanDetailData(getActivity());
                            DoCacheUtil util = DoCacheUtil.get(getActivity());
                            util.put("isLogin", "");
                        } else {
                            CustomProgress.CustomDismis();
                            Toast.makeText(getActivity(), homeTjProjectEntity.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
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

    //请求优选列表
    private void getGoodProjectListWap(int from, final int pageNo, int pageSize, int projectProductType) {
        //CustomProgress.show(getActivity());
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(getActivity()));
        String url = Urls.GETNEWPROJECTLISTWAP;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        //params.put("projectProductType", projectProductType);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    HomeTjProjectEntity homeTjProjectEntity = new Gson().fromJson(result, HomeTjProjectEntity.class);
                    if (homeTjProjectEntity != null) {
                        String state = homeTjProjectEntity.getState();
                        if (state.equals("0")) {
                            String totalCount = homeTjProjectEntity.getTotalCount();
                            if (homeTjProjectEntity.getData() != null) {
                                List<HomeTjProjectEntity.DataBean.ListYXBean> listYX = homeTjProjectEntity.getData().getListYX();

                                if (listYX != null && listYX.size() >= 2) {
                                    //优选产品1
                                    tv_good_project_name.setText(listYX.get(0).getName() + "(" + listYX.get(0).getSn() + ")");
                                    //DecimalFormat decimalFormat3 = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    //String rateDot3 = decimalFormat3.format(Double.parseDouble(listZC.get(0).getAnnualRate()));
                                    //tv_good_project_rate_first.setText(rateDot3.substring(0, rateDot3.indexOf(".")));
                                    //tv_good_project_rate_second.setText(rateDot3.substring(rateDot3.indexOf("."), rateDot3.length()) + "%");
                                    // DecimalFormat decimalFormat1 = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    //String rateDot1 = decimalFormat1.format(Double.parseDouble(listYX.get(0).getAnnualRate()));
                                    //tv_good_project_rate_first.setText(rateDot1);
                                    // tv_good_project_rate_second.setText(listZC.get(0).getAnnualRate()+ "%");


                                    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    if (!decimalFormat.format(Double.parseDouble(listYX.get(0).getInterestRateIncrease())).equals("0.00")) {
                                        //得到基本利率 =  总利率-加息利率
                                        Double basicYX1 = Double.parseDouble(listYX.get(0).getAnnualRate()) - Double.parseDouble(listYX.get(0).getInterestRateIncrease());
                                        String rateDot = decimalFormat.format(basicYX1);
                                        tv_good_project_rate_first.setText(rateDot);
                                        String interestRateIncrease = decimalFormat.format(Double.parseDouble(listYX.get(0).getInterestRateIncrease()));
                                        tv_good_project_rate_second.setText("+" + interestRateIncrease + "%");
                                    } else {
                                        String rateDot = decimalFormat.format(Double.parseDouble(listYX.get(0).getAnnualRate()));
                                        tv_good_project_rate_first.setText(rateDot);
                                        tv_good_project_rate_second.setVisibility(View.GONE);
                                    }


                                    tv_good_project_span.setText(listYX.get(0).getSpan() + "");
                                    goodProjectFristId = listYX.get(0).getId();
                                    goodProjectFristLoanData = listYX.get(0).getLoanDate();

                                    //优选产品二
                                    tv_good_second_project_name.setText(listYX.get(1).getName() + "(" + listYX.get(1).getSn() + ")");
                                    //DecimalFormat decimalFormat4 = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    //String rateDot4 = decimalFormat4.format(Double.parseDouble(listZC.get(1).getAnnualRate()));
                                    //tv_good_second_project_rate_first.setText(rateDot4.substring(0, rateDot4.indexOf(".")));
                                    //tv_good_second_project_rate_second.setText(rateDot4.substring(rateDot4.indexOf("."), rateDot4.length()) + "%");

                                    //DecimalFormat decimalFormat2 = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
                                    //String rateDot2 = decimalFormat2.format(Double.parseDouble(listYX.get(1).getAnnualRate()));
                                    //tv_good_second_project_rate_first.setText(rateDot2);
                                    //tv_good_second_project_rate_second.setText(listZC.get(1).getAnnualRate() + "%");

                                    if (!decimalFormat.format(Double.parseDouble(listYX.get(1).getInterestRateIncrease())).equals("0.00")) {
                                        //得到基本利率 =  总利率-加息利率
                                        Double basicYX2 = Double.parseDouble(listYX.get(1).getAnnualRate()) - Double.parseDouble(listYX.get(1).getInterestRateIncrease());
                                        String rateDot2 = decimalFormat.format(basicYX2);
                                        tv_good_second_project_rate_first.setText(rateDot2);
                                        String interestRateIncrease2 = decimalFormat.format(Double.parseDouble(listYX.get(1).getInterestRateIncrease()));
                                        tv_good_second_project_rate_second.setText("+" + interestRateIncrease2 + "%");
                                    } else {
                                        String rateDot = decimalFormat.format(Double.parseDouble(listYX.get(1).getAnnualRate()));
                                        tv_good_second_project_rate_first.setText(rateDot);
                                        tv_good_second_project_rate_second.setVisibility(View.GONE);
                                    }
                                    tv_good_second_project_span.setText(listYX.get(1).getSpan() + "");

                                    goodProjectSecondId = listYX.get(1).getId();
                                    goodProjectSecondLoanData = listYX.get(1).getLoanDate();

                                }
                            }
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
                            Toast.makeText(getActivity(), homeTjProjectEntity.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
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

    //获取版本号
    private void requestVersionNum(String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MainActivity.this));
        String url = Urls.APPVERSION;
        RequestParams params = new RequestParams();
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String isAnnualReportOnline = dataObj.getString("isAnnualReportOnline");//年度报告是否上线，1-年度报告上线，2-年度报告下线
                        if(!TextUtils.isEmpty(isAnnualReportOnline)||isAnnualReportOnline!=null){
                            if(isAnnualReportOnline.equals("1")){
                                sp = getContext().getSharedPreferences("tet", MODE_PRIVATE);
                                yearReportIsOpen = sp.getBoolean("yearReportIsOpen", true);
                                if (yearReportIsOpen) {
                                    sp.edit().putBoolean("yearReportIsOpen", false).commit();
                                    yearDialog = new YearDialog(getActivity(), R.style.MyDialogDeletAddress);//默认注册
                                    yearDialog.show();
                                    yearDialog.setCancelable(false);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "请检查网络", 0).show();
            }
        });
    }

}
