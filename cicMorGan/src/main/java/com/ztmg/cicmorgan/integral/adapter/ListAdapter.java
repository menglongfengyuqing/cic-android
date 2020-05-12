package com.ztmg.cicmorgan.integral.adapter;

import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.integral.entity.AwardEntity;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * * 实现对listView的循环滚动
 */
public class ListAdapter extends BaseAdapter {

    private List<AwardEntity> list;
    private LayoutInflater mInflater;

    public ListAdapter(Context context, List<AwardEntity> list) {
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * 将数据循环展示三遍
     */
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int arg0) {

        return list.get(arg0 % list.size());
    }

    @Override
    public long getItemId(int arg0) {
        return arg0 % list.size();
    }

    @Override
    public View getView(int postition, View convertView, ViewGroup arg2) {
        ViewHoler viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHoler();
            convertView = mInflater.inflate(R.layout.adapter_list_layout, null);
            viewHolder.tvText = (TextView) convertView.findViewById(R.id.adapter_list_layout_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHoler) convertView.getTag();
        }
        String phone = list.get(postition % list.size()).getUserPhone();
        String name = list.get(postition % list.size()).getAwardName();
        String str = "*恭喜　" + phone + "　获得　" + " <font color='#ffff00'>" + name + "</font> ";
        viewHolder.tvText.setText(Html.fromHtml(str));//取余展示数据
        //  String str = "风险承受能力<font color='#A11C3F'>“稳健型”</font>及以上";

        return convertView;
    }

    static class ViewHoler {
        TextView tvText;
    }

}
