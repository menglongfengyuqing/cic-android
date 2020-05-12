package com.ztmg.cicmorgan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 引导页
 */
public class SplashActivity extends BaseActivity {

    private int[] imgArr = {R.drawable.guider1, R.drawable.guider2, R.drawable.guider3, R.drawable.guider4};
    private List<ImageView> imgList;
    private ViewPager viewpager;
    private Button btn_enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_splash);
        //友盟数据统计
        //UMConfigure.setLogEnabled(true);
        //UMConfigure.setEncryptEnabled(true);
        //MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //MobclickAgent.setSessionContinueMillis(1000);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        imgList = new ArrayList<ImageView>();
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        btn_enter = (Button) findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(SplashActivity.this, "100001_skip_splash_click");
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //ToastUtils.show(SplashActivity.this, "点击的对");
            }
        });
        for (int i = 0; i < imgArr.length; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(imgArr[i]);
            imgList.add(image);
        }

    }

    @Override
    protected void initData() {
        SplashAdapter spAdapter = new SplashAdapter(imgList);
        viewpager.setAdapter(spAdapter);
        viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            //选中某页arg0的时候调用的方法
            @Override
            public void onPageSelected(int position) {
                if (position == imgList.size() - 1) {
                    btn_enter.setVisibility(View.VISIBLE);
                } else {
                    btn_enter.setVisibility(View.GONE);
                }
            }

            //滚动完成
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //滚动状态发生改变时候调用的方法
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class SplashAdapter extends PagerAdapter {
        private List<ImageView> pagerList;

        public SplashAdapter(List<ImageView> pagerList) {
            this.pagerList = pagerList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(pagerList.get(position));
            return pagerList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(pagerList.get(position));
        }

        @Override
        public int getCount() {
            return pagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


}
