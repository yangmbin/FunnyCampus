package com.funnycampus.utils;

import java.util.HashMap;
import java.util.Map;

import com.funnycampus.reference.Base64Coder;
import com.funnycampus.ui.MainPage;
import com.yangmbin.funnycampus.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * ������
 * @author yangmbin
 *
 */
public class Utils {
	/*����û����������ListView����Ŀ�󣬱��������ͼƬ��Ҳ����ͷ��*/
	public static Bitmap friendHeadimg = null;
	
	/*��ȡ��ǰ�ͻ����û������ǳơ�ͷ�������*/
	public Map<String, Object> getCurrentUserInfo() {
		/*��ȡ�û���Ϣ*/
		SQLiteDatabase infoDB = MainPage.instance.openOrCreateDatabase("info.db", Context.MODE_PRIVATE, null);
    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
    	cursor.moveToFirst();
    	String username = cursor.getString(1);
    	String nickname = cursor.getString(2);
    	String headimgStr = cursor.getString(3);
    	cursor = infoDB.query(true, "user", null, "id=2", null, null, null, null, null);
    	cursor.moveToFirst();
    	String password = cursor.getString(1);
    	cursor.close();
    	infoDB.close();
    	
    	Bitmap headimg;
    	if (headimgStr.equals("NULL")) {
    		headimg = BitmapFactory.decodeResource(MainPage.instance.getResources(), R.drawable.img_head);
    	}
    	else {
    		headimg = stringToBitmap(headimgStr);
    	}
    	
    	/*�������ض���*/
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("username", username);
    	map.put("nickname", nickname);
    	map.put("headimg", headimg);
    	map.put("password", password);
    	
    	return map;
	}
	
	/*StringתBitmap*/
	public Bitmap stringToBitmap(String str) {
		byte[] tempb = Base64Coder.decodeLines(str);
		Bitmap dBitmap = BitmapFactory.decodeByteArray(tempb, 0, tempb.length);
		return dBitmap;
	}
	
	
	/*����*/
	private static Utils utils = new Utils();
	synchronized public static Utils getInstance() {
		return utils;
	}
}
