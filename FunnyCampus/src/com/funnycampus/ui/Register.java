package com.funnycampus.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.funnycampus.XMPPUtils.XMPPUtils;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.RegisterMSG;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class Register extends Activity {
	private EditText nameEditText;
	private EditText pass1EditText;
	private EditText pass2EditText;
	private Button registerButton;
	
	private boolean Register_xmpp_state = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		
		nameEditText = (EditText)findViewById(R.id.register_user_edit);
		pass1EditText = (EditText)findViewById(R.id.register_passwd_edit1);
		pass2EditText = (EditText)findViewById(R.id.register_passwd_edit2);
		registerButton = (Button)findViewById(R.id.register_register_btn);
		
		//注册按钮的监听
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				//进度条
		    	startActivity(new Intent(Register.this, LoadingActivity.class));
		    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
				//注册操作
				RegisterOperation registerOperation = new RegisterOperation();
				registerOperation.execute();
			}
		});
		
	}
	
	//发送注册信息的异步类
	public class RegisterOperation extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String name = nameEditText.getText().toString();
			String pass1 = pass1EditText.getText().toString();
			String pass2 = pass2EditText.getText().toString();
			
			//构建传递到服务器的对象
			RegisterMSG registerMSG = new RegisterMSG();
			registerMSG.setName(name);
			registerMSG.setPassword1(pass1);
			registerMSG.setPassword2(pass2);
			
			//从服务器返回的消息
			String result = null;
			try {
				//创建Socket，连接到服务器
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据
				
				//向服务器写注册数据
				out.writeObject(registerMSG);
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
			
			//向openfire注册
			if (result.equals("SUCCESS"))
				Register_xmpp_state = XMPPUtils.getInstance().register(name, pass1);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			LoadingActivity.instance.finish();
			if(result.equals("EMPTY_ERROR")) {
				Toast.makeText(Register.this, "账号密码不能为空！", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("SPACE_ERROR")) {
				Toast.makeText(Register.this, "账号密码不能包含空格！", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("DIFFERENT_ERROR")) {
				Toast.makeText(Register.this, "前后密码不一致！", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("REPEAT_ERROR")) {
				Toast.makeText(Register.this, "此账号已被注册！", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("CONNECT_ERROR")) {
				Toast.makeText(Register.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("SUCCESS") && Register_xmpp_state) {
				Toast.makeText(Register.this, "注册成功！", Toast.LENGTH_SHORT).show();
				Register.this.finish();
			}
			else {
				Toast.makeText(Register.this, "注册失败！", Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(result);
		}
	}
	
	//返回按钮
	public void register_back(View v) {
		this.finish();
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
}
