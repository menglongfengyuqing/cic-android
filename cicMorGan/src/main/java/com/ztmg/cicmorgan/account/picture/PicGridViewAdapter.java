package com.ztmg.cicmorgan.account.picture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Entity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.util.ImageloaderUtils;
import com.ztmg.cicmorgan.util.ToastUtils;

/**
 * 作者：bcq on 2017/3/17 17:33
 */

public class PicGridViewAdapter extends BaseAdapter {
    private Context ctx;
    private List<ImgEntity> list, backList;
    private LayoutInflater mInflater;
    private List<ImgEntity> tempMapList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private int imgSize;
    private HashMap<Integer, View> viewMap;

    public PicGridViewAdapter(Context ctx, List<ImgEntity> list, int imgSize,
                              List<ImgEntity> backList) {
        this.ctx = ctx;
        this.list = list;
        mInflater = LayoutInflater.from(ctx);
        this.backList = backList;
        tempMapList = new ArrayList<ImgEntity>();
        viewMap = new HashMap<Integer, View>();
        this.imgSize = imgSize;
        mImageLoader = ImageloaderUtils.getImageLoader();
        mDisplayImageOptions = ImageloaderUtils.getSimpleDisplayImageOptions(0,
                true);
        if (backList != null && backList.size() > 0) {
            //			for (int i = 0; i < list.size(); i++) {
            for (ImgEntity entity : backList) {
                //					if (list.get(i).getName().equals(entity.getName())) {
                tempMapList.add(entity);
                //					}
            }
            //			}

        }
    }

    @Override
    public int getCount() {
        if (tempMapList != null) {

            mOnPicListening.setPicNum(tempMapList.size());
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (!viewMap.containsKey(position) || viewMap.get(position) == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_grid, null);
            holder.txt_name = (TextView) convertView
                    .findViewById(R.id.txt_name);
            holder.img = (MyImageView) convertView.findViewById(R.id.img);

            convertView.setTag(holder);
            viewMap.put(position, convertView);
        } else {
            convertView = viewMap.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        final ImgEntity bean = list.get(position);
        if (bean != null) {
            if (bean.getPath() != null) {
                if (bean.getPath().equals("default_path")) {
                    holder.img.setBackgroundResource(R.drawable.pic_add);
                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnPicListening.startAct();
                        }
                    });

                } else {
                    if (bean.isBln()) {
                        holder.img.setClick(true);
                    } else {
                        holder.img.setClick(false);
                    }
                    mImageLoader.displayImage(bean.getUri() + "", holder.img,
                            mDisplayImageOptions);

                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bean.isBln()) {
                                holder.img.setClick(false);
                                bean.setBln(false);
                                if (tempMapList.contains(bean)) {
                                    tempMapList.remove(bean);
                                }
                                mOnPicListening.setPicNum(tempMapList.size());
                                mOnPicListening.getImgList(tempMapList);

                            } else {
                                if (tempMapList.size() < imgSize) {
                                    holder.img.setClick(true);
                                    bean.setBln(true);
                                    tempMapList.add(bean);
                                } else {
                                    holder.img.setClick(false);
                                    ToastUtils.show(ctx, "选择的照片不可以超过" + imgSize
                                            + "张！");
                                }
                                mOnPicListening.setPicNum(tempMapList.size());
                                mOnPicListening.getImgList(tempMapList);

                            }
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView txt_name;
        MyImageView img;
    }

    OnPicListening mOnPicListening;

    public OnPicListening getOnPicListening() {
        return mOnPicListening;
    }

    public void setOnPicListening(OnPicListening onPicListening) {
        mOnPicListening = onPicListening;
    }

    public interface OnPicListening {
        void setPicNum(int size);

        void getImgList(List<ImgEntity> map);

        void startAct();
    }
}
