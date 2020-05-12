package com.ztmg.cicmorgan.integral.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivityCom;
import com.ztmg.cicmorgan.integral.entity.AwardEntity;
import com.ztmg.cicmorgan.integral.entity.IntegralShopEntity;
import com.ztmg.cicmorgan.integral.entity.NumberEntity;
import com.ztmg.cicmorgan.investment.activity.MyGridView;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.OkUtil;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.AndroidUtil;
import com.ztmg.cicmorgan.util.CustomToastUtils;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.GsonManager;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.TimeTaskScroll;
import com.ztmg.cicmorgan.view.CustomProgress;
import com.ztmg.cicmorgan.view.SlowlyProgressBar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.umeng.analytics.MobclickAgent.onEvent;


/**
 * 我的积分
 *
 * @author pc
 */
public class MyIntegralActivity extends BaseActivityCom implements OnClickListener {
    private TextView tv_my_integral;
    private LinearLayout tv_look_rule, me_prize;
    private ListView listView;
    private List<AwardEntity> list;
    //	private ImageView iv_frist,iv_second,iv_third;
    //	private TextView tv_frist_goods_name,tv_second_goods_name,tv_third_goods_name;
    //	private TextView tv_frist_goods_integral,tv_second_goods_integral,tv_third_goods_integral;
    //private ImageView pic_draw_tablelight_1, pic_draw_tablelight_2;
    private MyGridView gridView;
    // 未开始抽奖时的图片
    // 	private int[] imgs1 = { R.drawable.m1, R.drawable.m2, R.drawable.m3,
    // 			R.drawable.m4, R.drawable.ic_launcher, R.drawable.m5,
    // 			R.drawable.m6, R.drawable.m7, R.drawable.m8 };
    //    private int[] imgs1 = {R.drawable.pic_draw_awardsample, R.drawable.pic_draw_awardsample, R.drawable.pic_draw_awardsample,
    //            R.drawable.pic_draw_awardsample, R.drawable.pic_draw_awardsample, R.drawable.pic_draw_awardsample,
    //            R.drawable.pic_draw_awardsample, R.drawable.pic_draw_awardsample, R.drawable.pic_draw_awardsample};
    // 开始抽奖时的图片
    //	private int[] imgs2 = { R.drawable.n1, R.drawable.n2, R.drawable.n3,
    //			R.drawable.n4, R.drawable.ic_launcher, R.drawable.n5,
    //			R.drawable.n6, R.drawable.n7, R.drawable.n8 };
    // 对应转盘id的数组
    private int[] array = {0, 1, 2, 5, 8, 7, 6, 3};
    // Runnable接口
    private MyRunnable mMyRunnable;
    // 代表从0到8的9个图片序号
    private int num;

    // 开始的时间
    private int startTime;
    // 结束的时间
    private int stopTime;
    private Adpter adpter;
    private Context context;
    //开始按钮
    private ImageView iv_draw_bt;
    //抽中奖品弹框
    private SignDialog dialog;
    private String score;//用户积分
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private List<IntegralShopEntity> goodsList;
    private List<IntegralShopEntity> goodsThreeList;//底部三张图片
    private String isDrawnPrize;
    private View v_head_line;
    private ScrollView sv_content;
    private SlowlyProgressBar slowlyProgressBar;
    int mindex;
    int newProgress = 0;
    private String isTrue;
    private String deadline = "0";

    @BindView(R.id.tv_number)
    TextView tv_number;


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
            SystemBarTintManager tintManager = new SystemBarTintManager(MyIntegralActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        super.setContentView(R.layout.activity_my_integral);
        ButterKnife.bind(this);
        initView();
        initData();
        context = this;
        getAwardInfoList("3", "1", "1", "8");
        //getAwardInfoListBottom("3","2","1","3");
        getUserBounsList("3");
        mInflater = LayoutInflater.from(MyIntegralActivity.this);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.img_mall_defaultforgoods, false, false, false);

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(MyIntegralActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        userBouns(LoginUserProvider.getUser(MyIntegralActivity.this).getToken(), "3");
        getDataNumber();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    protected void initView() {
        setTitle("积分抽奖");
        setBack(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(MyIntegralActivity.this, "309001_jfcj_back_click");
                finish();
            }
        });
        tv_my_integral = (TextView) findViewById(R.id.tv_my_integral);
        tv_look_rule = (LinearLayout) findViewById(R.id.tv_look_rule);
        me_prize = (LinearLayout) findViewById(R.id.me_prize);
        me_prize.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listView1);
        //pic_draw_tablelight_1 = (ImageView) findViewById(R.id.pic_draw_tablelight_1);
        //pic_draw_tablelight_2 = (ImageView) findViewById(R.id.pic_draw_tablelight_2);
        //        list = getList();


        //		iv_frist = (ImageView) findViewById(R.id.iv_frist);
        //		iv_second = (ImageView) findViewById(R.id.iv_second);
        //		iv_third = (ImageView) findViewById(R.id.iv_third);
        //		tv_frist_goods_name = (TextView) findViewById(R.id.tv_frist_goods_name);
        //		tv_second_goods_name = (TextView) findViewById(R.id.tv_second_goods_name);
        //		tv_third_goods_name = (TextView) findViewById(R.id.tv_third_goods_name);
        //		tv_frist_goods_integral = (TextView) findViewById(R.id.tv_frist_goods_integral);
        //		tv_second_goods_integral = (TextView) findViewById(R.id.tv_second_goods_integral);
        //		tv_third_goods_integral = (TextView) findViewById(R.id.tv_third_goods_integral);

        //		findViewById(R.id.ll_first).setOnClickListener(this);
        //		findViewById(R.id.ll_second).setOnClickListener(this);
        //		findViewById(R.id.ll_third).setOnClickListener(this);
        //		findViewById(R.id.bt_exchange).setOnClickListener(this);

        tv_look_rule.setOnClickListener(this);
        //抽奖表盘
        gridView = (MyGridView) findViewById(R.id.gridview);
        goodsList = new ArrayList<IntegralShopEntity>();
        //		adpter = new Adpter();
        //		gridView.setAdapter(adpter);
        findViewById(R.id.tv_tip).setOnClickListener(this);

        v_head_line = (View) findViewById(R.id.v_head_line);
        sv_content = (ScrollView) findViewById(R.id.sv_content);
        slowlyProgressBar = new SlowlyProgressBar((ProgressBar) findViewById(R.id.progressBar));
        slowlyProgressBar.onProgressStart();
    }

    //	/**
    //     * 获取数据
    //     * @return
    //     */
    //    public List<String> getList(){
    //    	List<String> list =  new ArrayList<String>();
    //    	for (int i = 0; i < 10; i++) {
    //			list.add(String.valueOf(i));
    //		}
    //    	return list;
    //    }


    @Override
    protected void initData() {
    }

    @Override
    protected void responseData(String stringId, String json) {
        switch (stringId) {
            case "USERDRAWLOTTERYNUM":
                NumberEntity numberEntity = GsonManager.fromJson(json, NumberEntity.class);
                tv_number.setText("今日剩余次数：" + numberEntity.getNum());
                break;
        }
    }

    /**
     * 剩余次数
     */
    public void getDataNumber() {
        HashMap<String, String> params = new HashMap();
        params.put("from", "3");
        params.put("token", LoginUserProvider.getUser(MyIntegralActivity.this).getToken());//0 实体  1虚拟
        OkUtil.post("USERDRAWLOTTERYNUM", Urls.USERDRAWLOTTERYNUM, params, MyIntegralActivity.this, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_look_rule://积分规则
                onEvent(MyIntegralActivity.this, "309004_jfcj_jfgz_click");
                Intent intent = new Intent(MyIntegralActivity.this, IntegralRuleActivity.class);
                startActivity(intent);
                break;
            case R.id.me_prize://我的奖品
                onEvent(MyIntegralActivity.this, "309003_jfcj_ckwdjp_click");
                Intent intent1 = new Intent(MyIntegralActivity.this, GoodsListActivity.class);
                startActivity(intent1);
            default:
                break;
        }
    }

    private String drawImgUrls;

    //抽奖线程
    class MyRunnable implements Runnable {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);  //发送消息
            //如果到达指定的时间,则停止
            if (startTime >= stopTime) {
                handler.removeCallbacks(mMyRunnable);
                //提示中奖消息
                dialog = new SignDialog(MyIntegralActivity.this, R.style.MyDialogDeletAddress);//没中奖
                dialog.show();
                iv_draw_bt.setClickable(true);
                iv_draw_bt.setBackgroundResource(R.drawable.pic_draw_table_btn);
                startTime = 0;
                stopTime = 0;
                return;
            }
            if (startTime >= 3400) {
                //最后的时候每隔200毫秒运行一次
                handler.postDelayed(mMyRunnable, 200);
            } else {
                //每隔100毫秒运行一次
                handler.postDelayed(mMyRunnable, 100);
            }
            startTime += 100;
        }
    }

    //抽奖发送消息
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Change(array[num]);  //改变背景色
            num++;               //依次下一个
            //如果到了最后一个item，则循环
            if (num >= 8) {
                num = 0;
            }
        }
    };

    //改变背景颜色
    private void Change(int id) {
        for (int i = 0; i < gridView.getChildCount(); i++) {
            // pic_draw_tablelight_2.setVisibility(View.GONE);
            if (i == id) {
                //如果是选中的，则改变图片为数组2中的图片
                ((ImageView) (gridView.getChildAt(i).findViewById(R.id.iv_mask))).setVisibility(View.VISIBLE);
                //pic_draw_tablelight_2.setVisibility(View.GONE);//灯
                //pic_draw_tablelight_2.setVisibility(View.VISIBLE);
            } else if (i == 4) {
                //如果是到了中间那个，则跳出
                continue;
            } else {
                //未选中的就设置为数组1中的图片
                (gridView.getChildAt(i).findViewById(R.id.iv_mask)).setVisibility(View.GONE);
                //pic_draw_tablelight_2.setVisibility(View.VISIBLE);
            }
        }
    }


    class Adpter extends BaseAdapter {


        @Override
        public int getCount() {
            //return imgs1.length;
            return goodsList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            //return imgs1[position];
            return goodsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MyIntegralActivity.this, R.layout.item, null);
            final RelativeLayout rl_disk = (RelativeLayout) view.findViewById(R.id.rl_disk);//圆盘
            RelativeLayout rl_draw = (RelativeLayout) view.findViewById(R.id.rl_draw);//中间抽奖
            ImageView img = (ImageView) view.findViewById(R.id.img);//奖品的图片
            TextView text_view = (TextView) view.findViewById(R.id.text_view);//如果是谢谢惠顾就显示
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);//奖品的名字
            //img.setBackgroundResource(imgs1[position]);
            //奖品的显示
            if (position == 0) {
                IntegralShopEntity entity = (IntegralShopEntity) getItem(0);
                String needAmount = entity.getNeedAmount();
                if (needAmount.equals("1")) {//谢谢惠顾  默认积分是1
                    tv_name.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                    text_view.setVisibility(View.VISIBLE);
                } else {
                    tv_name.setVisibility(View.VISIBLE);
                    img.setVisibility(View.VISIBLE);
                    text_view.setVisibility(View.GONE);
                    mImageLoader.displayImage(entity.getImgWeb(), img, mDisplayImageOptions);
                    tv_name.setText(entity.getName());
                }
                //img.setBackgroundResource(imgs1[position]);
                //tv_name.setText("10元" + position);

            } else if (position == 1) {
                IntegralShopEntity entity = (IntegralShopEntity) getItem(1);
                mImageLoader.displayImage(entity.getImgWeb(), img, mDisplayImageOptions);
                tv_name.setText(entity.getName());
                //img.setBackgroundResource(imgs1[position]);
                //tv_name.setText("10元" + position);
            } else if (position == 2) {
                IntegralShopEntity entity = (IntegralShopEntity) getItem(2);
                mImageLoader.displayImage(entity.getImgWeb(), img, mDisplayImageOptions);
                tv_name.setText(entity.getName());
                //img.setBackgroundResource(imgs1[position]);
                //tv_name.setText("10元" + position);
            } else if (position == 3) {
                IntegralShopEntity entity = (IntegralShopEntity) getItem(7);
                mImageLoader.displayImage(entity.getImgWeb(), img, mDisplayImageOptions);
                tv_name.setText(entity.getName());
                //img.setBackgroundResource(imgs1[position]);
                //tv_name.setText("10元" + position);
            } else if (position == 5) {
                IntegralShopEntity entity = (IntegralShopEntity) getItem(3);
                mImageLoader.displayImage(entity.getImgWeb(), img, mDisplayImageOptions);
                tv_name.setText(entity.getName());
                //img.setBackgroundResource(imgs1[position]);
                //tv_name.setText("10元" + position);
            } else if (position == 6) {
                IntegralShopEntity entity = (IntegralShopEntity) getItem(6);
                mImageLoader.displayImage(entity.getImgWeb(), img, mDisplayImageOptions);
                tv_name.setText(entity.getName());
                //img.setBackgroundResource(imgs1[position]);
                //tv_name.setText("10元" + position);
            } else if (position == 7) {
                IntegralShopEntity entity = (IntegralShopEntity) getItem(5);
                mImageLoader.displayImage(entity.getImgWeb(), img, mDisplayImageOptions);
                tv_name.setText(entity.getName());
                //img.setBackgroundResource(imgs1[position]);
                //tv_name.setText("10元" + position);
            } else if (position == 8) {
                IntegralShopEntity entity = (IntegralShopEntity) getItem(4);
                mImageLoader.displayImage(entity.getImgWeb(), img, mDisplayImageOptions);
                tv_name.setText(entity.getName());
                //img.setBackgroundResource(imgs1[position]);
                //tv_name.setText("10元" + position);
            }
            ImageView iv_mask = (ImageView) view.findViewById(R.id.iv_mask);
            //rl_draw.setVisibility(View.GONE);
            iv_mask.setVisibility(View.GONE);

            if (position == 4) {
                //view = View.inflate(MyIntegralActivity.this, R.layout.item_draw,null);
                rl_draw.setVisibility(View.VISIBLE);
                // iv_mask.setVisibility(View.VISIBLE);

                iv_draw_bt = (ImageView) view.findViewById(R.id.iv_draw_bt);
                iv_draw_bt.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onEvent(MyIntegralActivity.this, "309002_jfcj_ljcj_click");
                        if (AndroidUtil.isFastDoubleClick()) {
                            return;
                        }
                        drawLottery("3", LoginUserProvider.getUser(MyIntegralActivity.this).getToken());
                    }
                });
            }
            return view;
        }
    }

    //抽中奖品弹出框
    public class SignDialog extends Dialog {
        Context context;

        public SignDialog(Context context) {
            super(context);
            this.context = context;
        }

        public SignDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_draw_win_1);
            initView1();
        }

        private void initView1() {

            RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
            TextView tv_hint_text = (TextView) findViewById(R.id.tv_hint_text);
            TextView remind = (TextView) findViewById(R.id.remind);
            ImageView iv_draw_img = (ImageView) findViewById(R.id.iv_draw_img);
            //ImageView iv_close = (ImageView) findViewById(R.id.iv_close);
            RelativeLayout yes_prize = (RelativeLayout) findViewById(R.id.yes_prize);
            ImageView no_prize = (ImageView) findViewById(R.id.no_prize);
            //Button bt_submit = (Button) findViewById(R.id.bt_submit);
            //			if (array[num] < 4) {
            //				text = array[num] + 1 + "";
            //				//						Toast.makeText(context, "恭喜你中了" + text, 0).show();
            //				tv_draw_win.setText("恭喜您抽中"+text);
            //			} else {
            //				tv_draw_win.setText("恭喜您抽中"+array[num]);
            //				//						Toast.makeText(context, "恭喜你中了" + array[num], 0).show();
            //			}
            // 判断是否中奖（1-未中奖，0-中奖）
            if (isDrawnPrize.equals("1")) {
                //tv_hint_text.setText("您未抽中任何商品");
                //iv_draw_img.setBackgroundResource(R.drawable.pic_no_draw);
                no_prize.setVisibility(View.VISIBLE);
                yes_prize.setVisibility(View.GONE);
            } else if (isDrawnPrize.equals("0")) {
                no_prize.setVisibility(View.GONE);
                yes_prize.setVisibility(View.VISIBLE);
                drawImgUrls = goodsList.get(num).getImgWeb();
                remind.setText(goodsList.get(num).getName());
                mImageLoader.displayImage(drawImgUrls, iv_draw_img, mDisplayImageOptions);
                if (isTrue.equals("0")) {
                    tv_hint_text.setText("温馨提示：请在" + Integer.parseInt(deadline) * 24 + "小时内进行去“我的奖品”下单，否则奖品将会失效。");
                    tv_hint_text.setVisibility(View.VISIBLE);
                } else if (isTrue.equals("1")) {
                    tv_hint_text.setVisibility(View.GONE);
                }
            }
            //			LinearLayout ll_check_prize = (LinearLayout) findViewById(R.id.ll_check_prize);
            //			ll_check_prize.setOnClickListener(new View.OnClickListener() {
            //
            //				@Override
            //				public void onClick(View v) {
            //					Intent intent = new Intent(MyIntegralActivity.this,GoodsListActivity.class);
            //					startActivity(intent);
            //				}
            //			});
            //            iv_close.setOnClickListener(new View.OnClickListener() {
            //
            //                @Override
            //                public void onClick(View v) {
            //                    dialog.dismiss();
            //                    for (int i = 0; i < gridView.getChildCount(); i++) {
            //                        (gridView.getChildAt(i).findViewById(R.id.iv_mask)).setVisibility(View.GONE);
            //                    }
            //                }
            //            });
            no_prize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    for (int i = 0; i < gridView.getChildCount(); i++) {
                        (gridView.getChildAt(i).findViewById(R.id.iv_mask)).setVisibility(View.GONE);
                    }
                }
            });
            yes_prize.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getUserBounsList("3");
                    dialog.dismiss();
                    for (int i = 0; i < gridView.getChildCount(); i++) {
                        (gridView.getChildAt(i).findViewById(R.id.iv_mask)).setVisibility(View.GONE);
                    }
                }
            });
        }
    }


    //    public class IntegralNotEnough extends Dialog {
    //        Context context;
    //
    //        public IntegralNotEnough(Context context) {
    //            super(context);
    //            this.context = context;
    //        }
    //
    //        public IntegralNotEnough(Context context, int theme) {
    //            super(context, theme);
    //            this.context = context;
    //            this.setContentView(R.layout.dialog_integral_no_enough);
    //            RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
    //            parent.setOnClickListener(new View.OnClickListener() {
    //
    //                @Override
    //                public void onClick(View v) {
    //                    dismiss();
    //                }
    //            });
    //        }
    //    }

    //用户积分
    private void userBouns(String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MyIntegralActivity.this));
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
                        score = dataObj.getString("score");
                        String str = "我的积分：" + " <font color='#ffff00'>" + score + "</font> " + "分";
                        tv_my_integral.setText(Html.fromHtml(str));
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyIntegralActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyIntegralActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyIntegralActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(getActivity());
                        //LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(MyIntegralActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyIntegralActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyIntegralActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MyIntegralActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //奖品
    private void getAwardInfoList(String from, String isLottery, String pageNo, String pageSize) {
        //CustomProgress.show(MyIntegralActivity.this);
        newProgress = 0;
        mindex = 0;
        slowlyProgressBar.setProgress(0);
        slowlyProgressBar.onProgressStart();
        mHandler.sendEmptyMessageDelayed(1, 1000);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MyIntegralActivity.this));
        String url = Urls.GETAWARDINFOLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("isLottery", isLottery);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mindex = 5;
                mHandler.sendEmptyMessage(1);
                sv_content.setVisibility(View.VISIBLE);
                v_head_line.setVisibility(View.GONE);
                //CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String pageCount = dataObj.getString("pageCount");
                        String last = dataObj.getString("last");
                        String totalCount = dataObj.getString("totalCount");
                        String pageNo = dataObj.getString("pageNo");
                        String pageSize = dataObj.getString("pageSize");
                        goodsList = new ArrayList<IntegralShopEntity>();
                        JSONArray awardlistArray = dataObj.getJSONArray("awardlist");
                        if (awardlistArray.length() > 0) {
                            for (int i = 0; i < awardlistArray.length(); i++) {
                                JSONObject obj = awardlistArray.getJSONObject(i);
                                IntegralShopEntity entity = new IntegralShopEntity();
                                entity.setAwardId(obj.getString("awardId"));//奖品id
                                entity.setName(obj.getString("name"));//奖品名称
                                entity.setNeedAmount(obj.getString("needAmount"));//奖品积分
                                entity.setDocs(obj.getString("docs"));//奖品简介
                                entity.setImgWeb(obj.getString("imgWap"));//奖品图片
                                goodsList.add(entity);
                            }
                            adpter = new Adpter();
                            gridView.setAdapter(adpter);
                        }
                        //mImageLoader.displayImage(imgWeb,iv_frist, mDisplayImageOptions);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyIntegralActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyIntegralActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyIntegralActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(getActivity());
                        //LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(MyIntegralActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyIntegralActivity.this, jsonObject.getString("message"), 0).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyIntegralActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                //CustomProgress.CustomDismis();
                Toast.makeText(MyIntegralActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //具体的抽中哪一个抽奖
    private void drawLottery(String from, String token) {
        CustomProgress.show(MyIntegralActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //AsyncHttpClient client = new AsyncHttpClient();
        //client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MyIntegralActivity.this));
        String url = Urls.DRAWLOTTERY;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject obj = jsonObject.getJSONObject("data");
                        isDrawnPrize = obj.getString("isDrawnPrize");
                        String drawLotteryNum = obj.getString("drawLotteryNum");
                        tv_number.setText("今日剩余次数：" + drawLotteryNum);
                        String awardId = obj.getString("awardId");
                        if (isDrawnPrize.equals("0")) {//中奖
                            isTrue = obj.getString("isTrue");
                            deadline = obj.getString("deadline");
                        }
                        String score = obj.getString("score");
                        String str = "我的积分：" + " <font color='#ffff00'>" + score + "</font> " + "分";
                        tv_my_integral.setText(Html.fromHtml(str));
                        String awardName = obj.getString("awardName");
                        if (awardId.equals(goodsList.get(0).getAwardId())) {
                            stopTime = 4000;
                            //							winPrizeImgUrl = goodsList.get(0).getImgWeb();
                        } else if (awardId.equals(goodsList.get(1).getAwardId())) {
                            stopTime = 4100;
                            //							winPrizeImgUrl = goodsList.get(1).getImgWeb();
                        } else if (awardId.equals(goodsList.get(2).getAwardId())) {
                            stopTime = 4200;
                            //							winPrizeImgUrl = goodsList.get(2).getImgWeb();
                        } else if (awardId.equals(goodsList.get(3).getAwardId())) {
                            stopTime = 4300;
                            //							winPrizeImgUrl = goodsList.get(3).getImgWeb();
                        } else if (awardId.equals(goodsList.get(4).getAwardId())) {
                            stopTime = 4400;
                            //							winPrizeImgUrl = goodsList.get(4).getImgWeb();
                        } else if (awardId.equals(goodsList.get(5).getAwardId())) {
                            stopTime = 4500;
                            //							winPrizeImgUrl = goodsList.get(5).getImgWeb();
                        } else if (awardId.equals(goodsList.get(6).getAwardId())) {
                            stopTime = 4600;
                            //							winPrizeImgUrl = goodsList.get(6).getImgWeb();
                        } else if (awardId.equals(goodsList.get(7).getAwardId())) {
                            stopTime = 4700;
                            //							winPrizeImgUrl = goodsList.get(7).getImgWeb();
                        }
                        //开启线程
                        num = 0;
                        mMyRunnable = new MyRunnable();
                        new Thread(mMyRunnable).start();
                        iv_draw_bt.setBackgroundResource(R.drawable.pic_draw_table_btn);
                        iv_draw_bt.setClickable(false);
                    } else if (jsonObject.getString("state").equals("3")) {
                        //noIntegralDialog = new IntegralNotEnough(MyIntegralActivity.this, R.style.MyDialogDeletAddress);
                        //noIntegralDialog.show();
                        new CustomToastUtils(MyIntegralActivity.this, jsonObject.getString("message")).show();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(MyIntegralActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(MyIntegralActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(MyIntegralActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        DoCacheUtil util = DoCacheUtil.get(MyIntegralActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(MyIntegralActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(MyIntegralActivity.this, "请检查网络", 0).show();
            }
        });
    }

    //获取抽奖名单
    private void getUserBounsList(String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(MyIntegralActivity.this));
        String url = Urls.USERBOUNSLIST;
        RequestParams params = new RequestParams();
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject obj = jsonObject.getJSONObject("data");
                        JSONArray awardlistArray = obj.getJSONArray("awardlist");
                        list = new ArrayList<AwardEntity>();
                        if (awardlistArray.length() > 0) {
                            for (int i = 0; i < awardlistArray.length(); i++) {
                                AwardEntity entity = new AwardEntity();
                                JSONObject awardObj = awardlistArray.getJSONObject(i);
                                entity.setAwardName(awardObj.getString("awardName"));
                                entity.setUserPhone(awardObj.getString("userPhone"));
                                list.add(entity);
                            }
                            new Timer().schedule(new TimeTaskScroll(MyIntegralActivity.this, listView, list), 20, 20);
                        }

                    } else {
                        Toast.makeText(MyIntegralActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(MyIntegralActivity.this, "请检查网络", 0).show();
            }
        });
    }

}

