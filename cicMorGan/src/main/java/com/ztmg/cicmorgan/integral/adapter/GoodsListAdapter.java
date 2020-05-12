package com.ztmg.cicmorgan.integral.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.MainActivity;
import com.ztmg.cicmorgan.integral.activity.OrderConfirmActivity;
import com.ztmg.cicmorgan.integral.entity.GoodsListEntity;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 抽中奖品列表
 *
 * @author pc
 */
public class GoodsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<GoodsListEntity> goodsList;

    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    public GoodsListAdapter(Context context, List<GoodsListEntity> list) {
        this.mContext = context;
        this.goodsList = list;

        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_item_goods_list, null);
            holder = new ViewHolder();
            holder.iv_goods_img = (ImageView) convertView.findViewById(R.id.iv_goods_img);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_details = (TextView) convertView.findViewById(R.id.tv_details);
            holder.tv_get_time = (TextView) convertView.findViewById(R.id.tv_get_time);
            holder.tv_ent_time = (TextView) convertView.findViewById(R.id.tv_ent_time);
            holder.bt_once = (TextView) convertView.findViewById(R.id.bt_once);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //        if (position == goodsList.size() - 1) {
        //            holder.tv_tips.setVisibility(View.VISIBLE);
        //        } else {
        //            holder.tv_tips.setVisibility(View.GONE);
        //        }
        //        holder.tv_tips.setOnClickListener(new OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                Intent riskTipFirstIntent = new Intent(mContext, AgreementActivity.class);
        //                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
        //                riskTipFirstIntent.putExtra("title", "风险提示书");
        //                mContext.startActivity(riskTipFirstIntent);
        //            }
        //        });
        final GoodsListEntity entity = (GoodsListEntity) getItem(position);
        holder.tv_name.setText(entity.getAwardName());
        holder.tv_details.setText(entity.getDocs());
        holder.tv_get_time.setText("兑换时间：" + entity.getAwardDate());
        if (!StringUtils.isEmpty(entity.getDeadline())) {
            holder.tv_ent_time.setText("失效时间：" + entity.getDeadline());
        } else {
            holder.tv_ent_time.setVisibility(View.GONE);
        }
        mImageLoader.displayImage(entity.getAwardimgWeb(), holder.iv_goods_img, mDisplayImageOptions);
        if (entity.getIsTrue().equals("0")) {//是否为虚拟奖品 1-是0-否
            if (entity.getState().equals("0")) {
                holder.bt_once.setText("待下单");//待下单
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_d40f42));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize);
            } else if (entity.getState().equals("1")) {
                holder.bt_once.setText("已下单");
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_d40f42));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize);
                holder.tv_ent_time.setVisibility(View.GONE);
            } else if (entity.getState().equals("2")) {
                holder.bt_once.setText("已发货");
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_d40f42));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize);
                holder.tv_ent_time.setVisibility(View.GONE);
            } else if (entity.getState().equals("3")) {
                holder.bt_once.setText("已结束");
                holder.tv_ent_time.setVisibility(View.GONE);
                holder.bt_once.setEnabled(false);
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize_cccc);
            } else if (entity.getState().equals("4")) {
                holder.bt_once.setText("已兑现");
                holder.tv_ent_time.setVisibility(View.GONE);
                holder.bt_once.setEnabled(false);
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize_cccc);
            } else if (entity.getState().equals("5")) {
                holder.bt_once.setText("已失效");
                holder.tv_ent_time.setVisibility(View.GONE);
                holder.bt_once.setEnabled(false);
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize_cccc);
            }
        } else if (entity.getIsTrue().equals("1")) {

            if (entity.getState().equals("1")) {
                holder.bt_once.setText("待使用");
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_d40f42));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize);
            } else if (entity.getState().equals("2")) {
                holder.bt_once.setText("已使用");
                holder.tv_ent_time.setVisibility(View.GONE);
                holder.bt_once.setEnabled(false);
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize_cccc);
            } else if (entity.getState().equals("3")) {
                holder.bt_once.setText("已过期");
                holder.bt_once.setEnabled(false);
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_details.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_get_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.tv_ent_time.setTextColor(mContext.getResources().getColor(R.color.text_cccccc));
                holder.bt_once.setBackgroundResource(R.drawable.bg_ellipse_prize_cccc);
            }
            //holder.bt_once.setText("立即出借");
        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView iv_goods_img;
        private TextView tv_name, tv_details, tv_get_time, tv_ent_time, bt_once;
    }
}
