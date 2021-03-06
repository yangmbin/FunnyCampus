package com.funnycampus.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.funnycampus.XMPPUtils.XMPPUtils;
import com.funnycampus.service.SendLocationInfoService;
import com.yangmbin.funnycampus.R;

public class Exit extends Activity {
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
		
		layout=(LinearLayout)findViewById(R.id.exit_layout);
		layout.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	public void exitbutton1(View v) {  
    	this.finish();    	
    }  
	public void exitbutton0(View v) {  
    	this.finish();
    	MainPage.instance.finish(); //关闭MainPage这个Activity
    	
    	//关闭xmpp连接
    	XMPPUtils.getInstance().closeConnection();
    	
    	//关闭service，停止间歇性向服务器更新位置信息
    	stopService(new Intent("com.funnycampus.service.SendLocationInfoService"));
    	
    	//清空会话信息集合
    	XMPPUtils.username2bitmap.clear();
    	XMPPUtils.username2listItems.clear();
    	XMPPUtils.username2nickname.clear();
    }  
	
}
