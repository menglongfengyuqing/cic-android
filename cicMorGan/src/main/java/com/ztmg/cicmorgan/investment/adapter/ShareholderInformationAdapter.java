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
import com.ztmg.cicmorgan.investment.entity.ShareholderInformationEntity;
import com.ztmg.cicmorgan.investment.entity.ValueVoucherEntity;

import java.util.List;

/**
 * Created by dell on 2018/5/21.
 */

public class ShareholderInformationAdapter extends BaseAdapter{
    private Context mContext;
    private List<ShareholderInformationEntity> shareholderInformationList;
    public ShareholderInformationAdapter(Context context, List<ShareholderInformationEntity> list) {
        this.mContext = context;
        this.shareholderInformationList = list;
    }

    @Override
    public int getCount() {
        return shareholderInformationList.size();
    }

    @Override
    public Object getItem(int position) {
        return shareholderInformationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_shareholder_information, null);
           holder.tv_shareholder_type = (TextView) convertView.findViewById(R.id.tv_shareholder_type);//股东类型
            holder.tv_document_type = (TextView) convertView.findViewById(R.id.tv_document_type);//证件类型
            holder.tv_shareholder_name = (TextView) convertView.findViewById(R.id.tv_shareholder_name);//股东名称

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ShareholderInformationEntity entity = (ShareholderInformationEntity) getItem(position);
        holder.tv_shareholder_type.setText(entity.getShareholdersType());
        holder.tv_document_type.setText(entity.getShareholdersCertType());
        holder.tv_shareholder_name.setText(entity.getShareholdersName());
        return convertView;
    }
    private class ViewHolder{
        private TextView tv_shareholder_type,tv_document_type,tv_shareholder_name;
    }
}
