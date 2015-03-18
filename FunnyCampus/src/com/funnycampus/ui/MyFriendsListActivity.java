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
	private LinkedList<Map<String, Object>> listItems; //���ݼ���
	private MyFriendsListViewAdapter myFriendsListAdapter; //������
	private ListView myFriendsListView; //�����б�
	
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
		map1.put("nickname", "�ǳ�");
		listItems.add(map1);
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("headimg", BitmapFactory.decodeResource(getResources(), R.drawable.img_head));
		map2.put("name", "ymb2");
		map2.put("nickname", "�ǳ�");
		listItems.add(map2);
		*/
		
		myFriendsListAdapter = new MyFriendsListViewAdapter(this, listItems);
		myFriendsListView.setAdapter(myFriendsListAdapter);
		
		//���ü���
		myFriendsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int location,
					long id) {
				//��ȡ�����Ŀ�����ݣ�Ҳ���ǻ�ȡ���ѵ�id������xmppͨ��
				Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(location);
				Toast.makeText(getApplicationContext(), ((String) map.get("name")).toLowerCase(), Toast.LENGTH_SHORT).show();
				
				//�����û�id������ҳ��
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
		
		//ÿ�ν����activityʹ�����м��غ��Ѳ���
		startActivity(new Intent(MyFriendsListActivity.this, LoadingActivity.class));
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		new GetMyFriendsListOperation().execute();
		
	}
	
	public class GetMyFriendsListOperation extends AsyncTask<Void, Void, List<FriendsInfo>>
	{
		@Override
		protected List<FriendsInfo> doInBackground(Void... arg0) {
			//��ȡ��ǰ�ͻ����û���
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	
	    	
	    	//�ӷ��������صĺ����б�
	    	List<FriendsInfo> result = null;
	    	try {
	    		//����Socket�����ӵ�������
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //�������д����
				
				//�������д����
				out.writeObject("GETFRIENDSLIST|" + name);
				out.flush();
				
				//�ӷ�������������Ϣ
				result = (List<FriendsInfo>) in.readObject();
				
				//�ر����������
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
				Toast.makeText(getApplicationContext(), "��ȡ�����б�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			else {
				for (int i = 0; i < result.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", result.get(i).getUsername());
					map.put("nickname", result.get(i).getNickname());
					
					//��ͼƬ�ִ�תΪBitmap
					byte[] b = Base64Coder.decodeLines(result.get(i).getHeadimg());
					Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
					map.put("headimg", bitmap);
					
					listItems.add(map);
				}
				myFriendsListAdapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "��ȡ�����б�ɹ���", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
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
