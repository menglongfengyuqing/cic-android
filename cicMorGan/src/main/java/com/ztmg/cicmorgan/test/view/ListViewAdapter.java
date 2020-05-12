package com.ztmg.cicmorgan.test.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.test.entity.OptionEntity;

import java.util.List;

/**
 * 作者：bcq on 2017/4/19 09:40 If they throw stones at you, don’t throw back, use
 * them to build your own foundation instead.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context ctx;
    private List<OptionEntity> list;
    //	private List<OptionEntity> templist;
    private LayoutInflater mInflater;
    private OnChildClickListening mOnChildClickListening;
    private int parentPos;

    public OnChildClickListening getmOnChildClickListening() {
        return mOnChildClickListening;
    }

    public void setmOnChildClickListening(
            OnChildClickListening mOnChildClickListening) {
        this.mOnChildClickListening = mOnChildClickListening;
    }

    public ListViewAdapter(Context ctx, int parentPos, List<OptionEntity> list) {
        this.ctx = ctx;
        this.list = list;
        this.parentPos = parentPos;
        mInflater = LayoutInflater.from(ctx);
        //		templist = new ArrayList<OptionEntity>();
    }

    @Override
    public int getCount() {
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_viewpager_listview, null);
            convertView.setTag(holder);
            holder.txt_content = (TextView) convertView.findViewById(R.id.txt_content);
            holder.rl_item = (RelativeLayout) convertView.findViewById(R.id.ll_item);
            holder.checkedView = (ImageView) convertView.findViewById(R.id.checkedView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OptionEntity entity = list.get(position);
        if (entity != null) {
            holder.txt_content.setText(entity.getName());
            if (entity.isFlag()) {
                holder.checkedView.setBackgroundResource(R.drawable.pic_riskevaluation_checkbox_seleceed);
            } else {
                holder.checkedView.setBackgroundResource(R.drawable.pic_riskevaluation_checkbox_normal);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entity.setFlag(true);
                holder.checkedView.setBackgroundResource(R.drawable.pic_riskevaluation_checkbox_seleceed);
                mOnChildClickListening.onChildClick(parentPos, position, true);
                //notifyDataSetChanged();

                //				if (entity.isFlag()) {
                //					templist.clear();
                //					entity.setFlag(true);
                //					templist.add(entity);
                //					holder.checkedView.setBackgroundResource(R.drawable.pic_test_chose_y);
                //					 mOnChildClickListening.onChildClick(parentPos,position,
                //					 true);
                //				} else {
                //					if (templist.size() > 0) {
                //						templist.get(0).setFlag(false);
                //					}
                //					templist.clear();
                //					entity.setFlag(true);
                //					holder.checkedView.setBackgroundResource(R.drawable.pic_test_chose_y);
                //					templist.add(entity);
                //					 mOnChildClickListening.onChildClick(parentPos,position,
                //					 true);
                //				}
                //				ToastUtils.show(ctx, list.get(position).getName()+"::::点击的item+：：" + position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView txt_content;
        RelativeLayout rl_item;
        ImageView checkedView;
    }

    public interface OnChildClickListening {
        void onChildClick(int parentPos, int childPos, boolean flag);
    }
}
