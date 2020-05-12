package com.ztmg.cicmorgan.more.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.integral.entity.IntegralShopEntity;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;

import java.util.List;

/**
 * 发现滑动商品列表
 *
 * @author pc
 */
public class FindIntegralShopAdapter extends BaseAdapter {
    private Context mContext;
    private List<IntegralShopEntity> integralShopList;


    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    public FindIntegralShopAdapter(Context context, List<IntegralShopEntity> list) {
        this.mContext = context;
        this.integralShopList = list;

        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);

    }

    @Override
    public int getCount() {
        return integralShopList.size();
    }

    @Override
    public Object getItem(int position) {
        return integralShopList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_find_score, null);
            holder = new ViewHolder();
            holder.img_pic = (ImageView) convertView.findViewById(R.id.img_pic);
            holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txt_money = (TextView) convertView.findViewById(R.id.txt_money);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        IntegralShopEntity entity = (IntegralShopEntity) getItem(position);
        mImageLoader.displayImage(entity.getImgWeb(), holder.img_pic, mDisplayImageOptions);
        holder.txt_name.setText(entity.getName());
        holder.txt_money.setText(entity.getNeedAmount());

        return convertView;
    }

    public class ViewHolder {
        private ImageView img_pic;
        private TextView txt_name, txt_money;
    }
}
