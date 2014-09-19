package com.funnycampus.ui;

import com.yangmbin.funnycampus.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

public class AppStart extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.appstart);
		
		//Handler处理子线程更新UI的事件
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				//在这个过程中，创建数据库
				try {
					SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
					infoDB.execSQL("drop table if exists user");
					infoDB.execSQL("create table user(id integer primary key, name text, nickname text, headimg text)");
					infoDB.execSQL("insert into user(id, name, nickname, headimg) values(1, \"NULL\", \"NULL\", \"NULL\")");
					infoDB.close();
					
					Toast.makeText(AppStart.this, "数据库建立成功", Toast.LENGTH_SHORT).show();
				}
				catch(Exception e) {
					Toast.makeText(AppStart.this, "数据库建立异常", Toast.LENGTH_SHORT).show();
				} 
				
				//页面跳转
				Intent intent = new Intent(AppStart.this, Welcome.class);
				startActivity(intent);
				AppStart.this.finish();
			}
		}, 1500);
	}
}
