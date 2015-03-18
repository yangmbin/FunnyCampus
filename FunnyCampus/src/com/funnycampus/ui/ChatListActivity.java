package com.funnycampus.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.funnycampus.XMPPUtils.XMPPUtils;
import com.funnycampus.adapter.ChatListViewAdapter;
import com.funnycampus.utils.Utils;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class ChatListActivity extends Activity {
	private static ListView chatListView;
	public static ChatListViewAdapter chatListAdapter;
	
	//传递过来的好友id
	private String username;
	private String nickname;
	private String frommessagecenter;
	//判断输入法是否弹出
	private int heightDiff = 0;
	//用来表示会话的数据集合
	//private LinkedList<Map<String, Object>> listItems = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_chatlist);
		
		//获取好友id
		username = getIntent().getExtras().getString("username");
		nickname = getIntent().getExtras().getString("nickname");
		frommessagecenter = getIntent().getExtras().getString("frommessagecenter");
		
		TextView friendname = (TextView) findViewById(R.id.listview_chatlist_friendname);
		friendname.setText(nickname);
		
		//判断是否有过聊天记录，获取数据集合
		int flag = 0;
		for (String str : XMPPUtils.username2listItems.keySet()) {
			if (username.equals(str)) {
				flag = 1;
				break;
			}
		}
		if (flag == 0) {
			LinkedList<Map<String, Object>> temp = new LinkedList<Map<String,Object>>();
			XMPPUtils.username2listItems.put(username, temp);
			XMPPUtils.username2nickname.put(username, nickname);
			XMPPUtils.username2bitmap.put(username, Utils.getInstance().friendHeadimg);
		}
		
		//获取xml控件
		chatListView = (ListView) findViewById(R.id.listview_chatlist_listview);
		
		/*
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("content", "解放开绿飞洒独孤独孤独发给豆腐干豆腐豆腐干豆腐干的阿斯蒂芬阿斯蒂芬灯机");
		map1.put("headimg", BitmapFactory.decodeResource(getResources(), R.drawable.img_head));
		map1.put("type", "0");
		listItems.add(map1);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("content", "飞洒发送发生独孤给豆腐干豆腐大幅撒旦法机");
		map2.put("headimg", BitmapFactory.decodeResource(getResources(), R.drawable.img_head));
		map2.put("type", "1");
		listItems.add(map2);
		*/
		
		//构建并设置适配器
		chatListAdapter = new ChatListViewAdapter(getApplicationContext(), (LinkedList<Map<String, Object>>) XMPPUtils.username2listItems.get(username));
		chatListView.setAdapter(chatListAdapter);
		
		//ListView信息显示最底部
		chatListView.setSelection(((LinkedList<Map<String, Object>>) XMPPUtils.username2listItems.get(username)).size() - 1);
		
		//对根布局进行监听，判断软键盘是否弹出，显示ListView的底部
		final View rootView = findViewById(R.id.listview_chatlist_rootRelativeLayout);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {	
			@Override
			public void onGlobalLayout() {
				//比较activity根布局和当前布局的大小
				heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
				if (heightDiff > 100) {
					chatListView.setSelection(chatListView.getBottom());
				}
			}
		});
		
		//对ListView触摸监听，隐藏软键盘
		chatListView.setOnTouchListener(new OnTouchListener() {	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					//判断软键盘是否弹出
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
					if (heightDiff > 100) {
						imm.hideSoftInputFromWindow(findViewById(R.id.listview_chatlist_edittext).getWindowToken(), 0);
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * 发送聊天信息按钮
	 */
	public void sendChatMessageBtn(View v) {
		EditText chatMessageEditText = (EditText) findViewById(R.id.listview_chatlist_edittext);
		final String msg = chatMessageEditText.getText().toString().trim();
		if ("".equals(msg)) {
			Toast.makeText(getApplicationContext(), "发送内容为空！", Toast.LENGTH_SHORT).show();
		}
		else {
			//发送聊天消息的线程
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (XMPPUtils.getInstance().sendChatMessage(username, null, msg)) {
						Message message = new Message();
						message.obj = msg;
						message.what = 0;
						handler.sendMessage(message);
					}
					else
						handler.sendEmptyMessage(1);
				}
				
			}).start();

		}
	}
	
	/**
	 * 更新UI的handler
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//发送聊天消息成功
			if (msg.what == 0) {
				Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
				//清空编辑框
				EditText chatMessageEditText = (EditText) findViewById(R.id.listview_chatlist_edittext);
				chatMessageEditText.setText(null);
				//添加内容到数据集合显示出来
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("headimg", Utils.getInstance().getCurrentUserInfo().get("headimg"));
				map.put("content", msg.obj);
				map.put("type", "1");
				((LinkedList<Map<String, Object>>) XMPPUtils.username2listItems.get(username)).add(map);
				chatListAdapter.notifyDataSetChanged();		
				//ListView信息显示最底部
				chatListView.setSelection(chatListView.getBottom());
				
			}
			else if (msg.what == 1) {
				Toast.makeText(getApplicationContext(), "发送失败！", Toast.LENGTH_SHORT).show();
			}
		}
	};

	
	//重写返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && frommessagecenter.equals("true")) {
			startActivity(new Intent(this, MessageCenter.class));
			overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
			finish();
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_BACK && frommessagecenter.equals("false")) {
			finish();
  			overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
 		
  			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//左上角返回按钮
  	public void BtnBack(View v) {
  		if (frommessagecenter.equals("true")) {
			startActivity(new Intent(this, MessageCenter.class));
			overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
			finish();
		}
		else if (frommessagecenter.equals("false")) {
			finish();
  			overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
		}
  	}
}
