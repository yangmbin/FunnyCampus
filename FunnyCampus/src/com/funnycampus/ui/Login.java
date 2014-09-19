package com.funnycampus.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.funnycampus.socket.CurrUser;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.LoginMSG;
import com.yangmbin.funnycampus.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
        
    }

    
    public void login_mainpage(View v) {
    	/*
    	if("buaa".equals(mUser.getText().toString()) && "123".equals(mPassword.getText().toString()))   //判断 帐号和密码
        {
             Intent intent = new Intent();
             intent.setClass(Login.this,LoadingActivity.class);
             startActivity(intent);
             //Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
          }
        else if("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString()))   //判断 帐号和密码
        {
        	new AlertDialog.Builder(Login.this)
			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
			.setTitle("登录错误")
			.setMessage("微信帐号或者密码不能为空，\n请输入后再登录！")
			.create().show();
         }
        else{
           
        	new AlertDialog.Builder(Login.this)
			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
			.setTitle("登录失败")
			.setMessage("微信帐号或者密码不正确，\n请检查后重新输入！")
			.create().show();
        }
        */
    	
    	//进度条
    	startActivity(new Intent(Login.this, LoadingActivity.class));
    	//登录按钮
      	LoginOperation loginOperation = new LoginOperation();
      	loginOperation.execute();
      	
      	
    }  
    
    public class LoginOperation extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String name = mUser.getText().toString();
			String pass = mPassword.getText().toString();
			
			//构建传递到服务器的对象
			LoginMSG loginMSG = new LoginMSG();
			loginMSG.setName(name);
			loginMSG.setPassword(pass);
			
			//从服务器返回的消息
			String result = null;
			try {
				//创建Socket，连接到服务器
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据
				
				//向服务器写登录数据
				out.writeObject(loginMSG);
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
			if(result.equals("EMPTY_ERROR")) {
				Toast.makeText(Login.this, "账号密码不能为空！", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("WRONG_ERROR")) {
				Toast.makeText(Login.this, "账号密码错误！", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("CONNECT_ERROR")) {
				Toast.makeText(Login.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
			}
			else {			
				startActivity(new Intent(Login.this, MainPage.class));
				//记住当前用户
				//CurrUser.name = mUser.getText().toString();
				try {
					String name = mUser.getText().toString();
					String temp = result.substring(result.indexOf("$") + 1);
					String nickname = temp.substring(0, temp.indexOf("$"));
					String headimg = temp.substring(temp.indexOf("$") + 1);
					
					SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
					infoDB.execSQL("update user set name=" + "'" + name + "'" + "where id=1");
					infoDB.execSQL("update user set nickname=" + "'" + nickname + "'" + "where id=1");
					infoDB.execSQL("update user set headimg=" + "'" + headimg + "'" + "where id=1");
					infoDB.close();
					Toast.makeText(Login.this, "更新成功", Toast.LENGTH_SHORT).show();
				}
				catch(Exception e) {} 
				
				finish();
				Toast.makeText(Login.this, "登录成功！", Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(result);
		}
    	
    }
    
    public void login_back(View v) {     //标题栏 返回按钮
      	this.finish();
    }  
    
}
