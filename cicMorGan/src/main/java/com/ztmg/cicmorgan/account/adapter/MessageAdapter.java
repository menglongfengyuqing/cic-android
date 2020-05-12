package com.ztmg.cicmorgan.account.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.MessageNoticeEntity;

import java.util.List;

/**
 * 消息和公告
 *
 * @author pc
 */
public class MessageAdapter extends BaseAdapter {
    private Context mContext;
    private List<MessageNoticeEntity> messageList;

    public MessageAdapter(Context context, List<MessageNoticeEntity> list) {
        this.mContext = context;
        this.messageList = list;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
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
            convertView = View.inflate(mContext, R.layout.fragment_message_item, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            //holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            //holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MessageNoticeEntity entity = (MessageNoticeEntity) getItem(position);
        if (entity != null) {
            if (entity.getLetterType().equals("1")) {
                holder.tv_name.setText("出借消息  " + entity.getTitle() + entity.getBody());
            } else if (entity.getLetterType().equals("2")) {
                holder.tv_name.setText("还款消息" + entity.getTitle() + entity.getBody());
            } else if (entity.getLetterType().equals("3")) {
                holder.tv_name.setText("充值消息  " + entity.getTitle() + entity.getBody());
            } else if (entity.getLetterType().equals("4")) {
                holder.tv_name.setText("提现消息  " + entity.getTitle() + entity.getBody());
            } else {
                holder.tv_name.setText(entity.getTitle() + entity.getBody());
            }
            holder.tv_time.setText(entity.getSendTime());
            //holder.tv_title.setText(entity.getTitle());
            //holder.tv_content.setText(entity.getBody());
            if (entity.getState().equals("2")) {//已读 信件状态（1、未读，2、已读）
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                //holder.tv_content.setTextColor(mContext.getResources().getColor(R.color.investment_detail_text));
                //holder.iv_img.setBackgroundResource(R.drawable.pic_message_gray);
            } else {//未读
                holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                //holder.tv_content.setTextColor(mContext.getResources().getColor(R.color.investment_detail_text));
                //holder.iv_img.setBackgroundResource(R.drawable.pic_message_red);
            }
        }


        return convertView;
    }

    private class ViewHolder {
        TextView tv_name, tv_time/*, tv_title, tv_content*/;
        ImageView iv_img;
    }

}
