package com.funnycampus.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.funnycampus.adapter.CommentsListViewAdapter;
import com.funnycampus.reference.Base64Coder;
import com.funnycampus.socket.BackCommentMSG;
import com.funnycampus.socket.BackCommentMSGList;
import com.funnycampus.socket.CommentMSG;
import com.funnycampus.socket.IP_PORT;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class HomeDetailMessage extends Activity{
	//�����б�
	private PullToRefreshListView commentsListView;	
	//��������
	private LinkedList<Map<String, Object>> commentsListViewData;
	//����������
	CommentsListViewAdapter commentsListViewAdapter;
	//�������Ŀid
	private int clickPosition;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_detail_message);
		
		//��ȡ�������Ŀid
		clickPosition = getIntent().getExtras().getInt("clickPosition");
		
		commentsListView = (PullToRefreshListView) findViewById(R.id.comments_listview);
		commentsListView.setMode(Mode.PULL_FROM_START);	
		
		//���ݼ���
		commentsListViewData = new LinkedList<Map<String,Object>>();
		
		//����ͷ������������
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("type", "0");
		map1.put("name", MainPage.clickedItemData.get("name"));
		map1.put("time", MainPage.clickedItemData.get("time"));
		map1.put("content", MainPage.clickedItemData.get("content"));
		map1.put("img_head", MainPage.clickedItemData.get("img_head"));
		map1.put("sharedimg1", MainPage.clickedItemData.get("sharedimg1"));
		map1.put("sharedimg2", MainPage.clickedItemData.get("sharedimg2"));
		map1.put("sharedimg3", MainPage.clickedItemData.get("sharedimg3"));
		map1.put("commentNum", MainPage.clickedItemData.get("commentNum"));
		map1.put("likeNum", MainPage.clickedItemData.get("likeNum"));
		map1.put("dislikeNum", MainPage.clickedItemData.get("dislikeNum"));
		commentsListViewData.add(map1);
		
		/*
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("type", "1");
		commentsListViewData.add(map2);
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("type", "1");
		commentsListViewData.add(map3);
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("type", "1");
		commentsListViewData.add(map4);
		*/
		
		commentsListViewAdapter = new CommentsListViewAdapter(this, commentsListViewData);
		commentsListView.setAdapter(commentsListViewAdapter);
		
		//���������б������ˢ�²���
		commentsListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				//��ȡ��ǰʱ��
				SimpleDateFormat sdf = new SimpleDateFormat("������: " + "yy:MM:dd HH:mm");
				Date now = new Date(System.currentTimeMillis());
				String label = sdf.format(now);
				
				//������һ��ˢ��ʱ��
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				new GetCommentsOperation().execute();
			}
		});
		
		//�½����ƽ�������ˢ��
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -200, 0);
		if (commentsListView.isBeingDraggedToTrue()) {
			commentsListView.onTouchEvent(event);
		}
	}
	
	/*
	 * ���ذ�ť
	 */
	public void BtnBack(View v) {
		HomeDetailMessage.this.finish();
		overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
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
	
	/*
	 * ��������
	 */
	public void SendComments(View v) {
		EditText commentEditText = (EditText) findViewById(R.id.home_detail_message_edittext);
		String commentStr = commentEditText.getText().toString().trim();
		if("".equals(commentStr)) {
			Toast.makeText(HomeDetailMessage.this, "����Ϊ�գ�", Toast.LENGTH_SHORT).show();
		}
		else {
			new SendCommentsOperation().execute(commentStr);
			startActivity(new Intent(HomeDetailMessage.this, LoadingActivity.class));
			overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		}
	}
	/*�������۵��첽��*/
	public class SendCommentsOperation extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... arg0) {
			/*����������Ϣ*/
			CommentMSG commentMSG = new CommentMSG();
			/*����*/
			commentMSG.setComment(arg0[0]);
			/*ʱ��*/
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			commentMSG.setTime(sdf.format(now));
			/*������id*/
			commentMSG.setID(Integer.parseInt((String) MainPage.clickedItemData.get("id")));
			/*�������ǳ�*/
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	 
			commentMSG.setName(name);
			
			/*�ӷ��������صĽ��*/
			String result = null;
			try {
				/*�������ӵ�socket*/
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				/*�������������*/
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				/*д����*/
				out.writeObject(commentMSG);
				out.flush();
				/*������*/
				result = (String) in.readObject();
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
			} catch(Exception e) {
				result = "CONNECT_ERROR";
			}
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			LoadingActivity.instance.finish();
			if(result.equals("CONNECT_ERROR")) {
    			Toast.makeText(HomeDetailMessage.this, "�������", Toast.LENGTH_SHORT).show();
    		}
			else if(result.equals("SUCCESS")) {
				Toast.makeText(HomeDetailMessage.this, "���۳ɹ���", Toast.LENGTH_SHORT).show();
				EditText commentEditText = (EditText) findViewById(R.id.home_detail_message_edittext);
				commentEditText.setText(null);
				
				//��������������1
				int count = Integer.parseInt((String) commentsListViewData.get(0).get("commentNum")) + 1;
				commentsListViewData.get(0).put("commentNum", Integer.toString(count));
				commentsListViewAdapter.notifyDataSetChanged();
				
				MainPage.listItems.get(clickPosition).put("commentNum", Integer.toString(count));
				MainPage.schoolListViewAdapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
	}
	
	/*
	 * ����ˢ�µĲ���
	 */
	public class GetCommentsOperation extends AsyncTask<Void, Void, BackCommentMSGList>
	{

		@Override
		protected BackCommentMSGList doInBackground(Void... arg0) {
			BackCommentMSGList result = null;
			try {
				/*����Socket�����ӵ�������*/
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				ObjectInputStream in = new ObjectInputStream(
						client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(
						client.getOutputStream()); //�������д����
				/*�������д����*/
				out.writeObject("GETCOMMENTS" + MainPage.clickedItemData.get("id"));
				out.flush();
				
				/*������*/
				result = (BackCommentMSGList) in.readObject();
				
				/*�ر������׽���*/
				in.close();
				out.close();
				client.close();
				
			} catch(Exception e) {
				result = null;
			}
			
			return result;
		}
		@Override
		protected void onPostExecute(BackCommentMSGList result) {
			List<BackCommentMSG> list;
        	if(result == null) {
        		Toast.makeText(HomeDetailMessage.this, "��ȡ����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
        	}
        	else {
        		list = result.getList();
        		while(commentsListViewData.size() > 1) {
        			commentsListViewData.remove(1);
        		}
        		
        		for(int i = 0; i < list.size(); i++) {
        			Map<String, Object> map = new HashMap<String, Object>();
        			if(list.get(i).getHeadIMG().equals("NULL")) {
        				map.put("head_img", BitmapFactory.decodeResource(getResources(), R.drawable.img_head));
        			}
        			else {
        				byte[] tempb = Base64Coder.decodeLines(list.get(i).getHeadIMG());
        				Bitmap bitmap = BitmapFactory.decodeByteArray(tempb, 0, tempb.length);
        				map.put("head_img", bitmap);
        			}
        			map.put("nickname", list.get(i).getNickname());
        			map.put("time", list.get(i).getTime());
        			map.put("comment", list.get(i).getComment());
        			map.put("type", "1");
        			
        			commentsListViewData.addLast(map);
        		}
        	}
        	commentsListViewAdapter.notifyDataSetChanged();
        	commentsListView.onRefreshComplete();
        	
			super.onPostExecute(result);
		}
	}
}
