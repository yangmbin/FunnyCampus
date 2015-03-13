package com.funnycampus.ui;

import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去掉标题栏
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
	}

	//登录跳转
	public void welcome_login(View v) {  
      	Intent intent = new Intent();
		intent.setClass(Welcome.this,Login.class);
		startActivity(intent);
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }  
	
	//注册跳转
    public void welcome_register(View v) {  
      	Intent intent = new Intent();
		intent.setClass(Welcome.this, Register.class);
		startActivity(intent);
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	*/

}
