package com.ztmg.cicmorgan.investment.adapter;

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
import com.ztmg.cicmorgan.investment.entity.FileGridViewEntity;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.List;

public class FileGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<FileGridViewEntity> imgList = new ArrayList<>();
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    public FileGridViewAdapter(Context context, List<FileGridViewEntity> list) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_investment_detail, false, false, false);
        this.imgList = list;
    }


    public void upData(List<FileGridViewEntity> list) {
        this.imgList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_file_gridview, null);
            holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileGridViewEntity entity = (FileGridViewEntity) getItem(position);
        mImageLoader.displayImage(entity.getUrl(), holder.iv_img, mDisplayImageOptions);
        holder.tv_name.setText(entity.getName());
        return convertView;
    }

    private class ViewHolder {
        private ImageView iv_img;
        private TextView tv_name;
    }
}

