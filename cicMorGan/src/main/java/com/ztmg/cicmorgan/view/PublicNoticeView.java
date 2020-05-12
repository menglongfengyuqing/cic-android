package com.ztmg.cicmorgan.view;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.home.activity.NoticeListActivity;
import com.ztmg.cicmorgan.home.entity.NoticeEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

public class PublicNoticeView extends LinearLayout {

    private static final String TAG = "PUBLICNOTICEVIEW";
    public Context mContext;
    private ViewFlipper mViewFlipper;
    private View mScrollTitleView;
    private List<NoticeEntity> titleList;
    private static PublicNoticeView mPublicNoticeView;
    //	private boolean isFresh;

    public PublicNoticeView(Context context) {
        super(context);
        mContext = context;
        mPublicNoticeView = this;
        init();
    }

    public PublicNoticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPublicNoticeView = this;
        init();
    }

    public static PublicNoticeView getInstance() {
        return mPublicNoticeView;
    }

    public void refresh(boolean isflag) {
        if (isflag) {

        } else {

        }
    }

    public void refresh() {
        getCmsListByType("1", "15", "2", "3");
    }

    private void init() {
        getCmsListByType("1", "15", "2", "3");
        bindLinearLayout();
    }

    /**
     * 初始化自定义的布局
     */
    private void bindLinearLayout() {
        mScrollTitleView = LayoutInflater.from(mContext).inflate(R.layout.scrollnoticebar, null);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        addView(mScrollTitleView, params);

        mViewFlipper = (ViewFlipper) mScrollTitleView.findViewById(R.id.id_scrollNoticeTitle);

        //是否自动开始滚动
        mViewFlipper.setAutoStart(true);
        mViewFlipper.setFlipInterval(2000);
        mViewFlipper.startFlipping();

        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_top));
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom));

        mViewFlipper.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onEvent(mContext, "101007_notice_click");
                Intent noticeIntent = new Intent(mContext, NoticeListActivity.class);
                noticeIntent.putExtra("Url", "/account_announcement_details.html?noticeid=" + titleList.get(mViewFlipper.getDisplayedChild()).getId() + "&app=1");
                mContext.startActivity(noticeIntent);
            }
        });

        mScrollTitleView.findViewById(R.id.rl_notice_list).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onEvent(mContext, "101008_notice_more_click");
                Intent noticeListIntent = new Intent(mContext, NoticeListActivity.class);
                noticeListIntent.putExtra("Url", "/account_announcement.html?app=1");
                mContext.startActivity(noticeListIntent);
            }
        });
    }

    /**
     * 网络请求内容后进行适配
     */
    //	DoCacheUtil util=DoCacheUtil.get(this);
    //	util.put("titleList", (Serializable)titleList);
    protected void bindNotices() {
        mViewFlipper.removeAllViews();
        //		int i = 0;
        //		while (i < 5) {
        if (titleList.size() > 0) {
            for (int i = 0; i < titleList.size(); i++) {
                String text = titleList.get(i).getTitle();
                //				id = titleList.get(i).getId();
                TextView textView = new TextView(mContext);
                textView.setText(text);
                textView.setMaxEms(19);
                textView.setSingleLine();
                textView.setEllipsize(TruncateAt.END);
                textView.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                textView.setTextSize(15);
                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mViewFlipper.addView(textView, layoutParams);
            }
        }
        //			i++;
        //		}
    }

    private void getCmsListByType(String pageNo, String pageSize, String type, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(mContext));
        String url = Urls.GETCMSLISTBYTYPE;
        RequestParams params = new RequestParams();
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("type", type);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    //					imgUrls = new ArrayList<String>();
                    //					textUrls = new ArrayList<String>();
                    titleList = new ArrayList<NoticeEntity>();
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray cmsArray = dataObject.getJSONArray("cmsList");
                        for (int i = 0; i < cmsArray.length(); i++) {
                            JSONObject obj = cmsArray.getJSONObject(i);
                            NoticeEntity entity = new NoticeEntity();
                            //							String img = obj.getString("imgPath");
                            //							String text = obj.getString("text");
                            //							String title = obj.getString("title");
                            //							imgUrls.add(img);
                            //							textUrls.add(text);
                            entity.setTitle(obj.getString("title"));
                            entity.setId(obj.getString("id"));
                            titleList.add(entity);
                        }
                        bindNotices();
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(mContext).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(mContext, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            mContext.startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            mContext.startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(mContext);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Toast.makeText(mContext, "请检查网络", 0).show();
            }
        });
    }

}
