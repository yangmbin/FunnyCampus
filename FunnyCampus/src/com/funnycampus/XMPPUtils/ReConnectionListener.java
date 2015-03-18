package com.funnycampus.XMPPUtils;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;

import com.funnycampus.ui.MainPage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReConnectionListener implements ConnectionListener{
	private Timer timer;
	private String username, password;
	private int reconnectTime = 2000;
	@Override
	public void connectionClosed() {
		//�ر�����
		XMPPUtils.getInstance().closeConnection();
		//����������
		timer = new Timer();
		timer.schedule(new TimeTask(), reconnectTime);
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		//���ʺ��Ѿ�����¼
		boolean error = e.getMessage().equals("stream:error (conflict)");
		if (!error) {
			//�ر�����
			XMPPUtils.getInstance().closeConnection();
			//����������
			timer = new Timer();
			timer.schedule(new TimeTask(), reconnectTime);
		}
	}

	@Override
	public void reconnectingIn(int arg0) {
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
	}

	@Override
	public void reconnectionSuccessful() {
	}
	
	class TimeTask extends TimerTask {
		@Override
		public void run() {
			SQLiteDatabase infoDB = MainPage.instance.openOrCreateDatabase("info.db", 0, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	username = cursor.getString(1);

	    	cursor = infoDB.query(true, "user", null, "id=2", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	password = cursor.getString(1);
	    	
	    	cursor.close();
	    	infoDB.close();	 
	    	
			//���ӷ�����
			if (XMPPUtils.getInstance().login(username, password))
				;
			else
				timer.schedule(new TimeTask(), reconnectTime);
		}
		
	}

}
