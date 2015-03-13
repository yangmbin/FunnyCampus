package com.funnycampus.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.funnycampus.XMPPUtils.XMPPUtils;
import com.funnycampus.socket.CurrUser;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.LoginMSG;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private EditText mUser; // �ʺű༭��
	private EditText mPassword; // ����༭��

	private boolean Login_xmpp_state = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ȥ��������
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);     
    }

    
    public void login_mainpage(View v) {
    	//������
    	startActivity(new Intent(Login.this, LoadingActivity.class));
    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    	//��¼��ť
      	LoginOperation loginOperation = new LoginOperation();
      	loginOperation.execute();
      	
      	
    }  
    
    public class LoginOperation extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String name = mUser.getText().toString();
			String pass = mPassword.getText().toString();
			
			//�������ݵ��������Ķ���
			LoginMSG loginMSG = new LoginMSG();
			loginMSG.setName(name);
			loginMSG.setPassword(pass);
			
			//�ӷ��������ص���Ϣ
			String result = null;
			try {
				//����Socket�����ӵ�������
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //�������д����
				
				//�������д��¼����
				out.writeObject(loginMSG);
				out.flush();
				
				//�ӷ�������������Ϣ
				result = (String)in.readObject();
				
				//�ر����������
				in.close();
				out.close();
				client.close();
			} catch(Exception e) {
				result = "CONNECT_ERROR";
			}
			
			//��openfire��¼
			if (result.substring(0, 7).equals("SUCCESS"))
				Login_xmpp_state = XMPPUtils.getInstance().login(name, pass);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			//����������
			LoadingActivity.instance.finish();
			if(result.equals("EMPTY_ERROR")) {
				Toast.makeText(Login.this, "�˺����벻��Ϊ�գ�", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("WRONG_ERROR")) {
				Toast.makeText(Login.this, "�˺��������", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("CONNECT_ERROR")) {
				Toast.makeText(Login.this, "���ӷ�����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			else if(result.substring(0, 7).equals("SUCCESS") && Login_xmpp_state){											
				//��ס��ǰ�û�
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
					infoDB.execSQL("update user set name=" + "'" + mPassword.getText().toString() + "'" + "where id=2");
					infoDB.close();
					//Toast.makeText(Login.this, "���³ɹ�", Toast.LENGTH_SHORT).show();
				}
				catch(Exception e) {} 
		
				
				Toast.makeText(Login.this, "��¼�ɹ���", Toast.LENGTH_SHORT).show();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(new Intent(Login.this, MainPage.class));
				overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
				finish();
				
			}
			else {
				Toast.makeText(Login.this, "��¼ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(result);
		}
    	
    }
    
    public void login_back(View v) {     //������ ���ذ�ť
      	this.finish();
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
    
}
