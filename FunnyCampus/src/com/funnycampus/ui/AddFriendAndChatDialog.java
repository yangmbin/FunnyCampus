package com.funnycampus.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.funnycampus.socket.IP_PORT;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class AddFriendAndChatDialog extends Activity {
	//点击图片传递过来的friend名
	private String friend;
	private String nickname;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addfriend_and_chat_dialog);
		
		//获取点击头像所属用户名
		friend = getIntent().getExtras().getString("friend");
		nickname = getIntent().getExtras().getString("nickname");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	
	/*
	 * 私信
	 */
	public void privateChat(View v) {
		//传递用户id到聊天页面
		Intent intent = new Intent(this, ChatListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("username", friend.toLowerCase());
		bundle.putString("nickname", nickname);
		intent.putExtra("frommessagecenter", "false");
		intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		
		finish();
	}
	
	/*
	 * 添加好友
	 */
	public void addFriend(View v) {
		startActivity(new Intent(this, LoadingActivity.class));
		new AddFriendOperation().execute();
	}
	
	/*
	 * 添加好友异步操作
	 */
	class AddFriendOperation extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... arg0) {
			//获取当前客户端用户名
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String username = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	
	    	
	    	//从服务器返回的结果
	    	String result = null;
	    	try {
				//创建Socket，连接到服务器
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据
				
				//向服务器写登录数据
				out.writeObject("ADDFRIENDS" + username + "|" + friend);
				out.flush();
				
				//从服务器读返回消息
				result = (String) in.readObject();
				
				//关闭输入输出流
				in.close();
				out.close();
				client.close();
			} catch(Exception e) {
				result = null;
			}
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			LoadingActivity.instance.finish();
			if (result == null) {
				Toast.makeText(getApplicationContext(), "网络错误！", Toast.LENGTH_SHORT).show();
			}
			else if (result.equals("SUCCESS")) {
				Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_SHORT).show();
				Button addfriend = (Button) findViewById(R.id.addfriend_and_chat_dialog_addfriend);
				addfriend.setText("已添加");
				addfriend.setClickable(false);
			}
			super.onPostExecute(result);
		}
	}
}
