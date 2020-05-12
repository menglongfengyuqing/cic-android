package com.ztmg.cicmorgan.account.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.RequestFriendsEntity;

import java.util.List;

public class RequestFriendsAdapter extends BaseAdapter {
    private Context mContext;
    private List<RequestFriendsEntity> requestFriendsList;
    private String type;//0 已获得 积分 1邀请好友详情

    public RequestFriendsAdapter(Context context, List<RequestFriendsEntity> list, String type) {
        this.mContext = context;
        this.requestFriendsList = list;
        this.type = type;
    }

    @Override
    public int getCount() {
        return requestFriendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return requestFriendsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_item_request_friends, null);
            holder = new ViewHolder();
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_get_date = (TextView) convertView.findViewById(R.id.tv_get_date);
            holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //        if (position == requestFriendsList.size() - 1) {
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
        RequestFriendsEntity entity = (RequestFriendsEntity) getItem(position);
        holder.tv_phone.setText(entity.getMobilePhone());

        if (type.equals("0")) {//邀请好友积分详情
            //			DecimalFormat decimalFormat =new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
            //			String amountDot = decimalFormat.format(Double.parseDouble(entity.getAmount()));

            holder.tv_money.setText((int) Double.parseDouble(entity.getAmount()) + "分");
            holder.tv_get_date.setText(date(entity.getCreateDate()));

        } else {

            if (entity.getRealName().equals("") || entity.getRealName().equals("null")) {
                holder.tv_money.setText("***");
            } else {
                holder.tv_money.setText(entity.getRealName());
            }
            holder.tv_get_date.setText(date(entity.getRegisterDate()));
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_phone, tv_money, tv_get_date, tv_tips;
    }

    public String date(String str) {
        str = str.substring(0, str.length() - 9);
        return str;
    }
}
