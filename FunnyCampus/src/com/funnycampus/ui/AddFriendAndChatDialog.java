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
	//���ͼƬ���ݹ�����friend��
	private String friend;
	private String nickname;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addfriend_and_chat_dialog);
		
		//��ȡ���ͷ�������û���
		friend = getIntent().getExtras().getString("friend");
		nickname = getIntent().getExtras().getString("nickname");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	
	/*
	 * ˽��
	 */
	public void privateChat(View v) {
		//�����û�id������ҳ��
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
	 * ��Ӻ���
	 */
	public void addFriend(View v) {
		startActivity(new Intent(this, LoadingActivity.class));
		new AddFriendOperation().execute();
	}
	
	/*
	 * ��Ӻ����첽����
	 */
	class AddFriendOperation extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... arg0) {
			//��ȡ��ǰ�ͻ����û���
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String username = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	
	    	
	    	//�ӷ��������صĽ��
	    	String result = null;
	    	try {
				//����Socket�����ӵ�������
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //�������д����
				
				//�������д��¼����
				out.writeObject("ADDFRIENDS" + username + "|" + friend);
				out.flush();
				
				//�ӷ�������������Ϣ
				result = (String) in.readObject();
				
				//�ر����������
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
				Toast.makeText(getApplicationContext(), "�������", Toast.LENGTH_SHORT).show();
			}
			else if (result.equals("SUCCESS")) {
				Toast.makeText(getApplicationContext(), "��ӳɹ���", Toast.LENGTH_SHORT).show();
				Button addfriend = (Button) findViewById(R.id.addfriend_and_chat_dialog_addfriend);
				addfriend.setText("�����");
				addfriend.setClickable(false);
			}
			super.onPostExecute(result);
		}
	}
}
