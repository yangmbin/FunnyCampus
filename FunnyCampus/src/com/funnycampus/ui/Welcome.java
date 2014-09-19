package com.funnycampus.ui;

import com.yangmbin.funnycampus.R;

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
		//ȥ��������
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
	}

	//��¼��ת
	public void welcome_login(View v) {  
      	Intent intent = new Intent();
		intent.setClass(Welcome.this,Login.class);
		startActivity(intent);
		//this.finish();
    }  
	
	//ע����ת
    public void welcome_register(View v) {  
      	Intent intent = new Intent();
		intent.setClass(Welcome.this, Register.class);
		startActivity(intent);
		//this.finish();
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
