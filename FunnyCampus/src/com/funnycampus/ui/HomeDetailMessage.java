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
	//评论列表
	private PullToRefreshListView commentsListView;	
	//评论数据
	private LinkedList<Map<String, Object>> commentsListViewData;
	//评论适配器
	CommentsListViewAdapter commentsListViewAdapter;
	//点击的条目id
	private int clickPosition;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_detail_message);
		
		//获取点击的条目id
		clickPosition = getIntent().getExtras().getInt("clickPosition");
		
		commentsListView = (PullToRefreshListView) findViewById(R.id.comments_listview);
		commentsListView.setMode(Mode.PULL_FROM_START);	
		
		//数据集合
		commentsListViewData = new LinkedList<Map<String,Object>>();
		
		//评论头部的正文内容
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
		
		//设置评论列表的下拉刷新操作
		commentsListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				//获取当前时间
				SimpleDateFormat sdf = new SimpleDateFormat("最后更新: " + "yy:MM:dd HH:mm");
				Date now = new Date(System.currentTimeMillis());
				String label = sdf.format(now);
				
				//设置上一次刷新时间
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				new GetCommentsOperation().execute();
			}
		});
		
		//新建手势进行下拉刷新
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -200, 0);
		if (commentsListView.isBeingDraggedToTrue()) {
			commentsListView.onTouchEvent(event);
		}
	}
	
	/*
	 * 返回按钮
	 */
	public void BtnBack(View v) {
		HomeDetailMessage.this.finish();
		overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
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
	
	/*
	 * 发送评论
	 */
	public void SendComments(View v) {
		EditText commentEditText = (EditText) findViewById(R.id.home_detail_message_edittext);
		String commentStr = commentEditText.getText().toString().trim();
		if("".equals(commentStr)) {
			Toast.makeText(HomeDetailMessage.this, "内容为空！", Toast.LENGTH_SHORT).show();
		}
		else {
			new SendCommentsOperation().execute(commentStr);
			startActivity(new Intent(HomeDetailMessage.this, LoadingActivity.class));
			overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		}
	}
	/*发送评论的异步类*/
	public class SendCommentsOperation extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... arg0) {
			/*构建评论消息*/
			CommentMSG commentMSG = new CommentMSG();
			/*内容*/
			commentMSG.setComment(arg0[0]);
			/*时间*/
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			commentMSG.setTime(sdf.format(now));
			/*新鲜事id*/
			commentMSG.setID(Integer.parseInt((String) MainPage.clickedItemData.get("id")));
			/*评论人昵称*/
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	 
			commentMSG.setName(name);
			
			/*从服务器返回的结果*/
			String result = null;
			try {
				/*创建连接的socket*/
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				/*构建输入输出流*/
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				/*写数据*/
				out.writeObject(commentMSG);
				out.flush();
				/*读数据*/
				result = (String) in.readObject();
				/*关闭流和套接字*/
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
    			Toast.makeText(HomeDetailMessage.this, "网络错误！", Toast.LENGTH_SHORT).show();
    		}
			else if(result.equals("SUCCESS")) {
				Toast.makeText(HomeDetailMessage.this, "评论成功！", Toast.LENGTH_SHORT).show();
				EditText commentEditText = (EditText) findViewById(R.id.home_detail_message_edittext);
				commentEditText.setText(null);
				
				//更新评论数，加1
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
	 * 下拉刷新的操作
	 */
	public class GetCommentsOperation extends AsyncTask<Void, Void, BackCommentMSGList>
	{

		@Override
		protected BackCommentMSGList doInBackground(Void... arg0) {
			BackCommentMSGList result = null;
			try {
				/*创建Socket，连接到服务器*/
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				ObjectInputStream in = new ObjectInputStream(
						client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(
						client.getOutputStream()); //向服务器写数据
				/*向服务器写数据*/
				out.writeObject("GETCOMMENTS" + MainPage.clickedItemData.get("id"));
				out.flush();
				
				/*读数据*/
				result = (BackCommentMSGList) in.readObject();
				
				/*关闭流和套接字*/
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
        		Toast.makeText(HomeDetailMessage.this, "获取评论失败！", Toast.LENGTH_SHORT).show();
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
