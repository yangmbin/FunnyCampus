package com.funnycampus.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.funnycampus.adapter.MyFriendsListViewAdapter;
import com.funnycampus.reference.Base64Coder;
import com.funnycampus.socket.FriendsInfo;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.utils.Utils;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class MyFriendsListActivity extends Activity {
	private LinkedList<Map<String, Object>> listItems; //数据集合
	private MyFriendsListViewAdapter myFriendsListAdapter; //适配器
	private ListView myFriendsListView; //好友列表
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_myfriendslist);
		
		myFriendsListView = (ListView) findViewById(R.id.listview_myfriendslist_listview);
		listItems = new LinkedList<Map<String,Object>>();
		
		/*
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("headimg", BitmapFactory.decodeResource(getResources(), R.drawable.img_head));
		map1.put("name", "ymb1");
		map1.put("nickname", "昵称");
		listItems.add(map1);
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("headimg", BitmapFactory.decodeResource(getResources(), R.drawable.img_head));
		map2.put("name", "ymb2");
		map2.put("nickname", "昵称");
		listItems.add(map2);
		*/
		
		myFriendsListAdapter = new MyFriendsListViewAdapter(this, listItems);
		myFriendsListView.setAdapter(myFriendsListAdapter);
		
		//设置监听
		myFriendsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int location,
					long id) {
				//获取点击条目的内容，也即是获取好友的id，用于xmpp通信
				Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(location);
				Toast.makeText(getApplicationContext(), ((String) map.get("name")).toLowerCase(), Toast.LENGTH_SHORT).show();
				
				//传递用户id到聊天页面
				Intent intent = new Intent(MyFriendsListActivity.this, ChatListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("username", ((String) map.get("name")).toLowerCase());
				bundle.putString("nickname", (String) map.get("nickname"));
				intent.putExtra("frommessagecenter", "false");
				Utils.getInstance().friendHeadimg = (Bitmap) map.get("headimg");
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
			}
		});
		
		//每次进入此activity使，进行加载好友操作
		startActivity(new Intent(MyFriendsListActivity.this, LoadingActivity.class));
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		new GetMyFriendsListOperation().execute();
		
	}
	
	public class GetMyFriendsListOperation extends AsyncTask<Void, Void, List<FriendsInfo>>
	{
		@Override
		protected List<FriendsInfo> doInBackground(Void... arg0) {
			//获取当前客户端用户名
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	
	    	
	    	//从服务器返回的好友列表
	    	List<FriendsInfo> result = null;
	    	try {
	    		//创建Socket，连接到服务器
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据
				
				//向服务器写数据
				out.writeObject("GETFRIENDSLIST|" + name);
				out.flush();
				
				//从服务器读返回消息
				result = (List<FriendsInfo>) in.readObject();
				
				//关闭输入输出流
				in.close();
				out.close();
				client.close();
	    	} catch (Exception e) {
	    		result = null;
	    	}
	    	
			return result;
		}
		@Override
		protected void onPostExecute(List<FriendsInfo> result) {
			LoadingActivity.instance.finish();
			if (result == null) {
				Toast.makeText(getApplicationContext(), "获取好友列表失败！", Toast.LENGTH_SHORT).show();
			}
			else {
				for (int i = 0; i < result.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", result.get(i).getUsername());
					map.put("nickname", result.get(i).getNickname());
					
					//把图片字串转为Bitmap
					byte[] b = Base64Coder.decodeLines(result.get(i).getHeadimg());
					Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
					map.put("headimg", bitmap);
					
					listItems.add(map);
				}
				myFriendsListAdapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "获取好友列表成功！", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	
	//重写返回键
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		if (keyCode == KeyEvent.KEYCODE_BACK) {
  			finish();
  			overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
 		
  			return true;
  		}
  		return super.onKeyDown(keyCode, event);
  	}
  	
    //左上角返回按钮
  	public void BtnBack(View v) {
  		finish();
  		overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
  	}
}
