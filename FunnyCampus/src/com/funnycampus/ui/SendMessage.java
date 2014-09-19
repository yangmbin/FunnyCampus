package com.funnycampus.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.funnycampus.reference.Base64Coder;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.SchoolInfoMSG;
import com.yangmbin.funnycampus.R;

public class SendMessage extends Activity {
	private EditText editText; //发送的信息的编辑框
	private String content; //编辑的信息
	
	String time = null; //发送信息的时间
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmessage);
	}
	
	//返回按钮
	public void sendMessage_back(View v) {
		this.finish();
	}
	
	//发送按钮
	public void rightSendMessageBtn(View v) {
		editText = (EditText)findViewById(R.id.send_message_edit);
		content = editText.getText().toString();
		//Toast.makeText(SendMessage.this, content, Toast.LENGTH_SHORT).show();
		String temp = content.trim();
		if("".equals(temp)) { 
			Toast.makeText(SendMessage.this, "输入内容不能为空！", Toast.LENGTH_SHORT).show();
		}
		else {
			startActivity(new Intent(SendMessage.this, LoadingActivity.class));
			new SendMessageOperation().execute();
		}
	}
	
	//发送信息的异步函数
	public class SendMessageOperation extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			//传输到服务器的对象
			SchoolInfoMSG msg = new SchoolInfoMSG();
			//msg.setName(CurrUser.name);
			msg.setContent(content);
			
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	String nickname = cursor.getString(2);
	    	String headimg = cursor.getString(3);
	    	cursor.close();
	    	infoDB.close();
	    	
	    	msg.setName(name);
	    	msg.setNickname(nickname);
	    	msg.setHeadIMG(headimg);
	    	
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date date=new Date();  
			time = sdf.format(date);
			
			msg.setTime(time);
			
			//从服务器返回的消息
			String result = null;
			try {
				//创建Socket，连接到服务器
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据

				//向服务器写数据
				out.writeObject(msg);
				out.flush();
				
				//从服务器读返回消息
				result = (String)in.readObject();
				
				//关闭输入输出流
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
			//结束进度条
			LoadingActivity.instance.finish();
			if(result.equals("CONNECT_ERROR")) {
				Toast.makeText(SendMessage.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("SUCCESS")) {				
				finish();
				
				SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
		    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
		    	cursor.moveToFirst();
		    	String nickname = cursor.getString(2);
		    	String headimg = cursor.getString(3);
		    	cursor.close();
		    	infoDB.close();
		    	//把字串转为图片
		    	Bitmap dBitmap = null;
		    	//Drawable drawable = null;
		    	if(!headimg.equals("NULL")) {
		    		byte[] tempb = Base64Coder.decodeLines(headimg);
    				dBitmap = BitmapFactory.decodeByteArray(tempb, 0, tempb.length);
		    		//dBitmap = BitmapFactory.decodeFile(headimg);  
		            //drawable = new BitmapDrawable(dBitmap);
		    	}
				
				//主页信息集合发生了改变
				Map<String, Object> map = new HashMap<String, Object>();
				if(headimg.equals("NULL"))
					map.put("img_head", BitmapFactory.decodeResource(SendMessage.this.getResources(), R.drawable.img_head));
				else {
					map.put("img_head", dBitmap);
				}
				map.put("name", nickname);
				map.put("content", content);
				map.put("time", time);
				MainPage.listItems.addFirst(map);
				
				MainPage.schoolListViewAdapter.notifyDataSetChanged();
				Toast.makeText(SendMessage.this, "发送成功！", Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(result);
		}
	}
}
