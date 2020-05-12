package com.ztmg.cicmorgan.account.fragment;

import java.util.ArrayList;
import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.MessageAdapter;
import com.ztmg.cicmorgan.account.entity.MessageNoticeEntity;
import com.ztmg.cicmorgan.util.DoCacheUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 公告
 *
 * @author pc
 */
public class NoticeFragment extends Fragment {
    private ListView lv_notice;
    private MessageAdapter adapter;
    private List<MessageNoticeEntity> noticeList;
    private DoCacheUtil doUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, null);
        initView(view);
        return view;
    }

    private void initView(View v) {
        lv_notice = (ListView) v.findViewById(R.id.lv_notice);

        noticeList = new ArrayList<MessageNoticeEntity>();
        for (int i = 0; i < 10; i++) {
            MessageNoticeEntity entity = new MessageNoticeEntity();
            entity.setTitle("中投摩根祝您出借愉快！");
            entity.setBody("中投摩根" + i);
            entity.setSendTime("2016-5-11");
            noticeList.add(entity);
        }

        adapter = new MessageAdapter(getActivity(), noticeList);
        lv_notice.setAdapter(adapter);
        doUtil = DoCacheUtil.get(getActivity());
        String flag = doUtil.getAsString("isReadNo");
        if (flag != null && flag.equals("isReadNo")) {
            changeColor(true);
        }
    }

    public void changeColor(boolean flag) {
        if (noticeList != null && noticeList.size() > 0) {
            if (flag) {
                doUtil.put("isReadNo", "isReadNo");
                for (MessageNoticeEntity entity : noticeList) {
                    entity.setState("2");
                }
            } else {
                for (MessageNoticeEntity entity : noticeList) {
                    entity.setState("1");
                }
            }

            adapter.notifyDataSetChanged();
        }
    }
}
