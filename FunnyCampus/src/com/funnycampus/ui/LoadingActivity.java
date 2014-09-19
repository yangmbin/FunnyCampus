package com.funnycampus.ui;



import com.yangmbin.funnycampus.R;

import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract.Instances;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class LoadingActivity extends Activity {
	public static Activity instance;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		//去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading);
			
		instance = this;
		/*
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Intent intent = new Intent (LoadingActivity.this, MainPage.class);			
				startActivity(intent);			
				LoadingActivity.this.finish();
				Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
			}
		}, 200);
		*/
    }
}