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
		//ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		
		nameEditText = (EditText)findViewById(R.id.register_user_edit);
		pass1EditText = (EditText)findViewById(R.id.register_passwd_edit1);
		pass2EditText = (EditText)findViewById(R.id.register_passwd_edit2);
		registerButton = (Button)findViewById(R.id.register_register_btn);
		
		//ע�ᰴť�ļ���
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				//������
		    	startActivity(new Intent(Register.this, LoadingActivity.class));
		    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
				//ע�����
				RegisterOperation registerOperation = new RegisterOperation();
				registerOperation.execute();
			}
		});
		
	}
	
	//����ע����Ϣ���첽��
	public class RegisterOperation extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String name = nameEditText.getText().toString();
			String pass1 = pass1EditText.getText().toString();
			String pass2 = pass2EditText.getText().toString();
			
			//�������ݵ��������Ķ���
			RegisterMSG registerMSG = new RegisterMSG();
			registerMSG.setName(name);
			registerMSG.setPassword1(pass1);
			registerMSG.setPassword2(pass2);
			
			//�ӷ��������ص���Ϣ
			String result = null;
			try {
				//����Socket�����ӵ�������
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //�������д����
				
				//�������дע������
				out.writeObject(registerMSG);
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
			
			//��openfireע��
			if (result.equals("SUCCESS"))
				Register_xmpp_state = XMPPUtils.getInstance().register(name, pass1);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			LoadingActivity.instance.finish();
			if(result.equals("EMPTY_ERROR")) {
				Toast.makeText(Register.this, "�˺����벻��Ϊ�գ�", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("SPACE_ERROR")) {
				Toast.makeText(Register.this, "�˺����벻�ܰ����ո�", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("DIFFERENT_ERROR")) {
				Toast.makeText(Register.this, "ǰ�����벻һ�£�", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("REPEAT_ERROR")) {
				Toast.makeText(Register.this, "���˺��ѱ�ע�ᣡ", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("CONNECT_ERROR")) {
				Toast.makeText(Register.this, "���ӷ�����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("SUCCESS") && Register_xmpp_state) {
				Toast.makeText(Register.this, "ע��ɹ���", Toast.LENGTH_SHORT).show();
				Register.this.finish();
			}
			else {
				Toast.makeText(Register.this, "ע��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(result);
		}
	}
	
	//���ذ�ť
	public void register_back(View v) {
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
