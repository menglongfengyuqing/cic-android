package com.ztmg.cicmorgan.investment.adapter;

import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.entity.ValueVoucherEntity;
import com.ztmg.cicmorgan.net.Urls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 优惠券
 *
 * @author pc
 */
public class ValueVoucherAdapter extends BaseAdapter {

    private Context mContext;
    private List<ValueVoucherEntity> valueVoucherList;

    public ValueVoucherAdapter(Context context, List<ValueVoucherEntity> list) {
        this.mContext = context;
        this.valueVoucherList = list;
    }

    @Override
    public int getCount() {
        return valueVoucherList.size();
    }

    @Override
    public Object getItem(int position) {
        return valueVoucherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_value_voucher_item, null);
            holder = new ViewHolder();
            holder.tv_value_voucher_money = (TextView) convertView.findViewById(R.id.tv_value_voucher_money);
            holder.tv_range_availability_voucher = (TextView) convertView.findViewById(R.id.tv_range_availability_voucher);
            holder.tv_value_voucher_num = (TextView) convertView.findViewById(R.id.tv_value_voucher_num);
            holder.tv_value_voucher_time = (TextView) convertView.findViewById(R.id.tv_value_voucher_time);
            holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            holder.ll_allbg = (LinearLayout) convertView.findViewById(R.id.ll_allbg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == valueVoucherList.size() - 1) {
            holder.tv_tips.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tips.setVisibility(View.GONE);
        }
        holder.tv_tips.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent riskTipFirstIntent = new Intent(mContext, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                mContext.startActivity(riskTipFirstIntent);
            }
        });
        ValueVoucherEntity entity = (ValueVoucherEntity) getItem(position);
        if (entity != null) {
            String type = entity.getType();//1抵用券
            String state = entity.getState();//1可用2已使用3过期
            if (type.equals("1")) {
                if (state.equals("1")) {
                    holder.tv_value_voucher_money.setText("¥" + entity.getValue());
                    holder.tv_range_availability_voucher.setText(entity.getSpans());
                    holder.tv_value_voucher_num.setText("出借满" + entity.getLimitAmount() + "元可用");
                    String getDate = entity.getGetDate().substring(0, entity.getGetDate().length() - 9);
                    String OverdueDate = entity.getOverdueDate().substring(0, entity.getOverdueDate().length() - 8);
                    holder.tv_value_voucher_time.setText(getDate + "至" + OverdueDate);
                }
            }

            if (entity.isCheck()) {
                holder.ll_allbg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_blue_line));
            } else {
                holder.ll_allbg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_no_blue_line));
            }

        }
        return convertView;
    }

    public class ViewHolder {
        LinearLayout ll_allbg;
        TextView tv_value_voucher_money, tv_value_voucher_num, tv_value_voucher_time, tv_tips, tv_range_availability_voucher;
    }
}
