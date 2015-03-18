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
	//定时器，定时发送位置信息
	private Timer timer = null;
	//获取当前的位置信息
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
		//注册一个周期性的位置更新，每2秒更新一次超过2米范围移动的信息
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, myLocationListener);
		timer.schedule(new TimerTask() {		
			@Override
			public void run() {		
				//获取位置
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				//location不可能一次就定位成功，需多次
				while (location == null) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, myLocationListener);
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
				//构建位置信息类
				LocationInfoMSG locationInfoMSG = new LocationInfoMSG();
				locationInfoMSG.setUsername((String) Utils.getInstance().getCurrentUserInfo().get("username"));
				locationInfoMSG.setLatitude(location.getLatitude());
				locationInfoMSG.setLongitude(location.getLongitude());
				//从服务器返回的消息
				String result = null;
				try {
					//创建Socket，连接到服务器
					Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
					
					ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据
					
					//向服务器写登录数据
					out.writeObject(locationInfoMSG);
					out.flush();
					
					//从服务器读返回消息
					result = (String) in.readObject();
					
					//关闭输入输出流
					in.close();
					out.close();
					client.close();
				} catch(Exception e) {
					result = null;
				}
				
				if (result == null) {
					//Toast.makeText(SendLocationInfoService.this, "发送位置信息失败！", Toast.LENGTH_SHORT).show();
				}
				else if (result.equals("SUCCESS")) {
					//Toast.makeText(SendLocationInfoService.this, "发送位置信息成功！", Toast.LENGTH_SHORT).show();
				}
			}
		}, 0, 300 * 1000); //300s更新一次位置信息
		
		super.onStart(intent, startid);
	}
	
	@Override
	public void onDestroy() {
		if (timer != null)
			timer.cancel();
		super.onDestroy();
	}
}
