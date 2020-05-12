package com.ztmg.cicmorgan.more.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.more.entity.CoreEnterpriseEntity;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.view.RoundImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import static com.umeng.analytics.MobclickAgent.onEvent;

public class CoreEnterpriseAgaginAdapter extends Adapter<CoreEnterpriseAgaginAdapter.MyHolder> {

    private Context mContext;
    private List<CoreEnterpriseEntity> coreList;


    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    public CoreEnterpriseAgaginAdapter(Context context, List<CoreEnterpriseEntity> list) {
        this.mContext = context;
        this.coreList = list;

        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        //pic_investment_detail
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.img_mall_defaultforactivity, false, false, false);

    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return coreList.size();
    }

    @SuppressLint("NewApi")
    @Override
    // 填充onCreateViewHolder方法返回的holder中的控件
    public void onBindViewHolder(MyHolder holder, int position) {
        final CoreEnterpriseEntity entity = (CoreEnterpriseEntity) coreList.get(position);
        if (entity.getImg().equals("more")) {
            holder.iv_core.setVisibility(View.VISIBLE);
            holder.iv_core.setBackground(mContext.getResources().getDrawable(R.drawable.pic_mall_enterprise_moretocome_withshadow));
        } else {
            mImageLoader.displayImage(entity.getImg(), holder.imageView, mDisplayImageOptions);
            holder.imageView.setType(RoundImageView.TYPE_ROUND);
            holder.imageView.setRoundRadius(10);// 圆角大小
            holder.imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    onEvent(mContext, "103005_hxqy_click");
                    Intent mthLogoIntent = new Intent(mContext, AgreementActivity.class);
                    mthLogoIntent.putExtra("path", entity.getRemark());
                    mthLogoIntent.putExtra("title", entity.getName());
                    mContext.startActivity(mthLogoIntent);
                }
            });
        }

    }

    @Override
    // 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    public MyHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        // 填充布局
        View v = View.inflate(mContext, R.layout.item_core_enterprise_list, null);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    // 定义内部类继承ViewHolder
    class MyHolder extends ViewHolder {

        private RoundImageView imageView;
        private ImageView iv_core;

        public MyHolder(View view) {
            super(view);
            imageView = (RoundImageView) view.findViewById(R.id.iv_core_enterprise);
            iv_core = (ImageView) view.findViewById(R.id.iv_core);
        }

    }

}
