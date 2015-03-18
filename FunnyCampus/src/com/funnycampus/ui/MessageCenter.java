package com.funnycampus.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.funnycampus.XMPPUtils.XMPPUtils;
import com.funnycampus.adapter.MessageCenterAdapter;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class MessageCenter extends Activity {
	private ListView messageCenterListView;
	private MessageCenterAdapter adapter;
	private LinkedList<Map<String, Object>> listItems = new LinkedList<Map<String,Object>>();;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_message_center);
		
		//�������ݼ���
		for (String str : XMPPUtils.getInstance().username2listItems.keySet()) {
			LinkedList<Map<String, Object>> ll = (LinkedList<Map<String, Object>>) XMPPUtils.username2listItems.get(str);
			//�ж����һ��������Ϣ�Ƿ���ڣ�������������ʾ
			if (ll.size() > 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("headimg", XMPPUtils.username2bitmap.get(str));
				map.put("nickname", XMPPUtils.username2nickname.get(str));
				map.put("lastmessage", ll.get(ll.size() - 1).get("content"));
				map.put("username", str);

				listItems.addFirst(map);
			}
		}
		messageCenterListView = (ListView) findViewById(R.id.listview_message_center_listview);
		adapter = new MessageCenterAdapter(getApplicationContext(), listItems);
		messageCenterListView.setAdapter(adapter);
		
		//��Ŀ���ü���
		messageCenterListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//��ȡ�������Ŀ
				Map<String, Object> map = (Map<String, Object>) arg0.getAdapter().getItem(arg2);
				
				Intent intent = new Intent(MessageCenter.this, ChatListActivity.class);
				intent.putExtra("username", ((String) map.get("username")).toLowerCase());
				intent.putExtra("nickname", (String) map.get("nickname"));
				intent.putExtra("frommessagecenter", "true");
				startActivity(intent);
				overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
				
				//�رմ�ҳ�棬������ҳ�淵��ʱ���½��룬����item��������Ϣ
				finish();
			}
		});
	}
	
	//��д���ؼ�
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		if (keyCode == KeyEvent.KEYCODE_BACK) {
  			finish();
  			overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
 		
  			return true;
  		}
  		return super.onKeyDown(keyCode, event);
  	}
  	
  	//���ϽǷ��ذ�ť
  	public void BtnBack(View v) {
  		finish();
  		overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
  	}
}
