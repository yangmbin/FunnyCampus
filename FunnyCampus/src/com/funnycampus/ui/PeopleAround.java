package com.funnycampus.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.LauncherActivity.ListItem;
import android.content.Intent;
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

import com.funnycampus.adapter.PeopleAroundListViewAdapter;
import com.funnycampus.reference.Base64Coder;
import com.funnycampus.socket.FriendsInfo;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.PeopleAroundInfo;
import com.funnycampus.utils.Utils;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class PeopleAround extends Activity {
	private ListView peopleAroundListView = null;
	private LinkedList<Map<String, Object>> listItems = null;
	private PeopleAroundListViewAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_people_around);
		
		peopleAroundListView = (ListView) findViewById(R.id.listview_people_around_listview);
		listItems = new LinkedList<Map<String, Object>>();
		
		/*
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("headimg", BitmapFactory.decodeResource(getResources(), R.drawable.img_head));
		map.put("username", "good man");
		map.put("nickname", "bad man");
		map.put("distance", "111km");
		listItems.add(map);
		*/
		
		adapter = new PeopleAroundListViewAdapter(getApplicationContext(), listItems);
		peopleAroundListView.setAdapter(adapter);
		
		//向后台获取周围的人
		startActivity(new Intent(this, LoadingActivity.class));
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		new GetPeopleAroundOperation().execute();
		
		//设置条目的监听
		peopleAroundListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int location,
					long id) {
				//获取点击的条目的内容
				Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(location);
				
				//跳转到聊天和添加好友对话框
				Intent intent = new Intent(PeopleAround.this, AddFriendAndChatDialog.class);
				intent.putExtra("friend", (String) map.get("username"));
				intent.putExtra("nickname", (String) map.get("nickname"));
				Utils.getInstance().friendHeadimg = (Bitmap) map.get("headimg");
				startActivity(intent);
				overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
			}
		});
	}
	
	//获取周围的人的异步操作
	public class GetPeopleAroundOperation extends AsyncTask<Void, Void, List<PeopleAroundInfo>> {
		@Override
		protected List<PeopleAroundInfo> doInBackground(Void... arg0) {
			//从服务器返回的周围的人
	    	List<PeopleAroundInfo> result = null;
	    	try {
	    		//创建Socket，连接到服务器
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据
				
				//向服务器写数据
				out.writeObject("GETPEOPLEAROUND|" + Utils.getInstance().getCurrentUserInfo().get("username"));
				out.flush();
				
				//从服务器读返回消息
				result = (List<PeopleAroundInfo>) in.readObject();
				
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
		protected void onPostExecute(List<PeopleAroundInfo> result) {
			LoadingActivity.instance.finish();
			if (result == null) {
				Toast.makeText(getApplicationContext(), "获取周围的人失败！", Toast.LENGTH_SHORT).show();
			}
			else {
				for (int i = 0; i < result.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("username", result.get(i).getUsername());
					map.put("nickname", result.get(i).getNickname());
					map.put("distance", result.get(i).getDistance() + "km");
					
					//把图片字串转为Bitmap
					byte[] b = Base64Coder.decodeLines(result.get(i).getHeadimg());
					Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
					map.put("headimg", bitmap);
					
					listItems.add(map);
				}
				adapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "获取周围的人成功！", Toast.LENGTH_SHORT).show();
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
