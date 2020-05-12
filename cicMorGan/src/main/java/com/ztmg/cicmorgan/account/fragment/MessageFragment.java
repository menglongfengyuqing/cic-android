package com.ztmg.cicmorgan.account.fragment;

import java.util.ArrayList;
import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.adapter.MessageAdapter;
import com.ztmg.cicmorgan.account.entity.MessageNoticeEntity;
import com.ztmg.cicmorgan.util.DoCacheUtil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
/**
 * 消息
 * @author pc
 *
 */
public class MessageFragment extends Fragment{
	private ListView lv_message;
	private MessageAdapter adapter;
	private List<MessageNoticeEntity> messsageList; 
	private SharedPreferences sp;
	private DoCacheUtil doUtil;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_message, null);
		initView(view);
		return view;
	}
	private void initView(View v){
		
		lv_message = (ListView) v.findViewById(R.id.lv_message);
		messsageList = new ArrayList<MessageNoticeEntity>();
		for(int i=0;i<10;i++){
			MessageNoticeEntity entity = new MessageNoticeEntity();
			entity.setTitle("中投摩根祝您出借愉快！");
			entity.setBody("中投摩根"+i);
			entity.setSendTime("2016-5-11");
			messsageList.add(entity);
		}
		
		adapter = new MessageAdapter(getActivity(), messsageList);
		lv_message.setAdapter(adapter);
		doUtil = DoCacheUtil.get(getActivity());
		String flag=doUtil.getAsString("isReadMsg");
		if(flag!=null&&flag.equals("isReadMsg")){
			changeColor(true);
		}
	}
	public void changeColor(boolean flag){
		if(messsageList!=null&&messsageList.size()>0){
		if(flag){
			doUtil.put("isReadMsg","isReadMsg");
			for (MessageNoticeEntity entity:messsageList) {
				entity.setState("2");
			}
		}else{
			for (MessageNoticeEntity entity:messsageList) {
				entity.setState("1");
			}
		}
		
		adapter.notifyDataSetChanged();
		}
	}
}
