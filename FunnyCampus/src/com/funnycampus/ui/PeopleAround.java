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
		
		//���̨��ȡ��Χ����
		startActivity(new Intent(this, LoadingActivity.class));
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		new GetPeopleAroundOperation().execute();
		
		//������Ŀ�ļ���
		peopleAroundListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int location,
					long id) {
				//��ȡ�������Ŀ������
				Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(location);
				
				//��ת���������Ӻ��ѶԻ���
				Intent intent = new Intent(PeopleAround.this, AddFriendAndChatDialog.class);
				intent.putExtra("friend", (String) map.get("username"));
				intent.putExtra("nickname", (String) map.get("nickname"));
				Utils.getInstance().friendHeadimg = (Bitmap) map.get("headimg");
				startActivity(intent);
				overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
			}
		});
	}
	
	//��ȡ��Χ���˵��첽����
	public class GetPeopleAroundOperation extends AsyncTask<Void, Void, List<PeopleAroundInfo>> {
		@Override
		protected List<PeopleAroundInfo> doInBackground(Void... arg0) {
			//�ӷ��������ص���Χ����
	    	List<PeopleAroundInfo> result = null;
	    	try {
	    		//����Socket�����ӵ�������
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //�������д����
				
				//�������д����
				out.writeObject("GETPEOPLEAROUND|" + Utils.getInstance().getCurrentUserInfo().get("username"));
				out.flush();
				
				//�ӷ�������������Ϣ
				result = (List<PeopleAroundInfo>) in.readObject();
				
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
		protected void onPostExecute(List<PeopleAroundInfo> result) {
			LoadingActivity.instance.finish();
			if (result == null) {
				Toast.makeText(getApplicationContext(), "��ȡ��Χ����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			else {
				for (int i = 0; i < result.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("username", result.get(i).getUsername());
					map.put("nickname", result.get(i).getNickname());
					map.put("distance", result.get(i).getDistance() + "km");
					
					//��ͼƬ�ִ�תΪBitmap
					byte[] b = Base64Coder.decodeLines(result.get(i).getHeadimg());
					Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
					map.put("headimg", bitmap);
					
					listItems.add(map);
				}
				adapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "��ȡ��Χ���˳ɹ���", Toast.LENGTH_SHORT).show();
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
