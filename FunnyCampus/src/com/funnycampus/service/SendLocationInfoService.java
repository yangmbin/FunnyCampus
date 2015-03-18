package com.funnycampus.service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.LocationInfoMSG;
import com.funnycampus.utils.Utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class SendLocationInfoService extends Service {
	//��ʱ������ʱ����λ����Ϣ
	private Timer timer = null;
	//��ȡ��ǰ��λ����Ϣ
	LocationManager locationManager = null;
	
	LocationListener myLocationListener = new LocationListener() {	
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
		
		@Override
		public void onProviderEnabled(String arg0) {
		}
		
		@Override
		public void onProviderDisabled(String arg0) {
		}
		
		@Override
		public void onLocationChanged(Location arg0) {
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {		
		super.onCreate();
	}
	
	@Override 
	public void onStart(Intent intent, int startid) {
		timer = new Timer();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//ע��һ�������Ե�λ�ø��£�ÿ2�����һ�γ���2�׷�Χ�ƶ�����Ϣ
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, myLocationListener);
		timer.schedule(new TimerTask() {		
			@Override
			public void run() {		
				//��ȡλ��
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				//location������һ�ξͶ�λ�ɹ�������
				while (location == null) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, myLocationListener);
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
				//����λ����Ϣ��
				LocationInfoMSG locationInfoMSG = new LocationInfoMSG();
				locationInfoMSG.setUsername((String) Utils.getInstance().getCurrentUserInfo().get("username"));
				locationInfoMSG.setLatitude(location.getLatitude());
				locationInfoMSG.setLongitude(location.getLongitude());
				//�ӷ��������ص���Ϣ
				String result = null;
				try {
					//����Socket�����ӵ�������
					Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
					
					ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //�ӷ�����������
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //�������д����
					
					//�������д��¼����
					out.writeObject(locationInfoMSG);
					out.flush();
					
					//�ӷ�������������Ϣ
					result = (String) in.readObject();
					
					//�ر����������
					in.close();
					out.close();
					client.close();
				} catch(Exception e) {
					result = null;
				}
				
				if (result == null) {
					//Toast.makeText(SendLocationInfoService.this, "����λ����Ϣʧ�ܣ�", Toast.LENGTH_SHORT).show();
				}
				else if (result.equals("SUCCESS")) {
					//Toast.makeText(SendLocationInfoService.this, "����λ����Ϣ�ɹ���", Toast.LENGTH_SHORT).show();
				}
			}
		}, 0, 300 * 1000); //300s����һ��λ����Ϣ
		
		super.onStart(intent, startid);
	}
	
	@Override
	public void onDestroy() {
		if (timer != null)
			timer.cancel();
		super.onDestroy();
	}
}
