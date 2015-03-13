package com.funnycampus.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.funnycampus.reference.Base64Coder;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.SchoolInfoMSG;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendMessage extends Activity
{
	private List<Bitmap> bitmaps = new ArrayList(); //���͵�ͼƬ����
    private String content; //����������
    private EditText editText; //�����ı��ı༭��
    private Uri imageUri; //������ʾѡ������ͼƬ���ֻ��е�Uri·��
    private List<String> photos = new ArrayList(); //���͵�ͼƬ���飬ת��Ϊ��String����
    String time = null; //���͵�ʱ��
    private int type = 1; //Ĭ�Ϸ���İ�飬Ϊ��У԰Ȥ�¡�
    
    @Override
    public void onCreate(Bundle paramBundle)
    {
	    super.onCreate(paramBundle);
	    setContentView(R.layout.sendmessage);
    }

    //��Uri�л�ȡBitmap����ͼƬ
    private Bitmap getBitmapFromUri(Uri paramUri)
    {
	    try
	    {
            Bitmap localBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), paramUri);
            return localBitmap;
	    }
	    catch (Exception localException)
	    {
	        localException.printStackTrace();
	    }
	    return null;
    }

    //��ѡ��õ�ͼƬ��ʾ����Ӧλ����ΪԤ��
    private void setPicToView(Intent paramIntent)
    {
    	Bitmap localBitmap;
    	BitmapDrawable localBitmapDrawable;
    	if (paramIntent.getExtras() != null)
    	{
    		localBitmap = getBitmapFromUri(this.imageUri);
    		localBitmapDrawable = new BitmapDrawable(localBitmap);
    		//���ʾѡ��ĵ�һ��ͼƬ����ͼƬ��ʾ����Ӧ��λ��
    		if (this.photos.size() == 0)
            {
    			ImageView localImageView4 = (ImageView)findViewById(R.id.first_photo);
		        localImageView4.setBackgroundDrawable(localBitmapDrawable);
		        //��Bitmap��ʽ��ͼƬת��Ϊ�ֽ�����ѹ���������תΪString����
		        ByteArrayOutputStream localByteArrayOutputStream3 = new ByteArrayOutputStream();
		        localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localByteArrayOutputStream3);
		        String str3 = new String(Base64Coder.encodeLines(localByteArrayOutputStream3.toByteArray()));
		        //�ѵõ���ͼƬ��String���ʹ洢��photos�У��ȴ����͸�������
		        this.photos.add(str3);
		        this.bitmaps.add(localBitmap);
		        //��ʾ�˵�һ��ͼƬ��ʹ�õڶ���Ĭ�����ͼƬ��ʾ����
		        ImageView localImageView5 = (ImageView)findViewById(R.id.second_photo);
		        localImageView5.setClickable(true);
		        localImageView5.setVisibility(0);
		        localImageView4.setClickable(false);
            }
    		//���ʾѡ��ڶ���ͼƬ
    		else if (this.photos.size() == 1)
        	{
        		ImageView localImageView2 = (ImageView)findViewById(R.id.second_photo);
    		    localImageView2.setBackgroundDrawable(localBitmapDrawable);
    		    
    		    ByteArrayOutputStream localByteArrayOutputStream2 = new ByteArrayOutputStream();
    		    localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localByteArrayOutputStream2);
    		    String str2 = new String(Base64Coder.encodeLines(localByteArrayOutputStream2.toByteArray()));
    		    
    		    this.photos.add(str2);
    		    this.bitmaps.add(localBitmap);
    		    
    		    ImageView localImageView3 = (ImageView)findViewById(R.id.third_photo);
    		    localImageView3.setClickable(true);
    		    localImageView3.setVisibility(0);
    		    localImageView2.setClickable(false);
    		    return;
            }
    		//���ʾѡ�������ͼƬ
    		else {  			
    	    	ImageView localImageView1 = (ImageView)findViewById(R.id.third_photo);
    		    localImageView1.setBackgroundDrawable(localBitmapDrawable);
    		    
    		    ByteArrayOutputStream localByteArrayOutputStream1 = new ByteArrayOutputStream();
    		    localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localByteArrayOutputStream1);
    		    String str1 = new String(Base64Coder.encodeLines(localByteArrayOutputStream1.toByteArray()));
    		    
    		    this.photos.add(str1);
    		    this.bitmaps.add(localBitmap);
    		    localImageView1.setClickable(false);
    		}
    	}

    }

    //���ͼƬ��ť
    public void addPhotos(View paramView)
    {
    	new AlertDialog.Builder(this).setTitle("ѡ����Ƭ").setNegativeButton("���", new DialogInterface.OnClickListener()
    	{
    		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
	        {
    			//ѡ������е���Ƭ
		        paramAnonymousDialogInterface.dismiss();
		        Intent localIntent = new Intent("android.intent.action.PICK", null);
		        localIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		        SendMessage.this.startActivityForResult(localIntent, 1);
		        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
	        }
        }).setPositiveButton("����", new DialogInterface.OnClickListener()
        {
        	public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        	{
        		//ͨ�����ջ�ȡ��Ƭ
		        paramAnonymousDialogInterface.dismiss();
		        Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
		        localIntent.putExtra("output", Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photoImg.jpg")));
		        SendMessage.this.startActivityForResult(localIntent, 2);
		        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
        	}
        }).show();
    }

    //��ȡ����Ƭ��Ļص�����
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	//�����ֱ�ӻ�ȡ
    	case 1:
    		if(data != null) {
    			//��Uri��ȡ����е���Ƭ
    			Bitmap localBitmap = getBitmapFromUri(data.getData());
    			//���ڴ濨���½�һ����ѡ����Ƭ�Ŀ���������ԭͼƬ�������޸ĵ�
    	        File localFile = new File(Environment.getExternalStorageDirectory(), "photoImg1.jpg");
    	        try
    	        {
    	          FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
    	          localBitmap.compress(Bitmap.CompressFormat.PNG, 100, localFileOutputStream);
    	          localFileOutputStream.flush();
    	          localFileOutputStream.close();
    	          startPhotoZoom(Uri.fromFile(localFile));
    	        }
    	        catch (Exception localException)
    	        {
    	            localException.printStackTrace();
    	        }
    		}
    		break;
    	// ����ǵ����������ʱ  
    	case 2:
    		File temp = new File(Environment.getExternalStorageDirectory() + "/photoImg.jpg");
    		startPhotoZoom(Uri.fromFile(temp));
    		break;
    	// ȡ�òü����ͼƬ 
    	case 3:
    		//�ǿ��жϴ��һ��Ҫ��֤���������֤�Ļ���  
            //�ڼ���֮��������ֲ����⣬Ҫ���²ü�������  
            //��ǰ����ʱ���ᱨNullException
    		if(data != null) {
    			setPicToView(data);
    		}
    		break;
		default:
			break;
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }    
    
    //�ҷ����Ͱ�ť��������
    public void rightSendMessageBtn(View paramView)
    {
    	new AlertDialog.Builder(this)
    	.setTitle("ѡ�񷢲����")
    	.setIcon(android.R.drawable.ic_dialog_info)
    	.setSingleChoiceItems(
    			new String[] { "У԰Ȥ��", "У԰����", "���Ż", "У԰����" }, 
    			0, 
    			new DialogInterface.OnClickListener()
    			{
    				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    				{
    					SendMessage.this.type = (paramAnonymousInt + 1);
    				}
    			})
    	.setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
    	{
    		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    		{
		        paramAnonymousDialogInterface.dismiss();
		        SendMessage.this.editText = ((EditText)SendMessage.this.findViewById(R.id.send_message_edit));
		        SendMessage.this.content = SendMessage.this.editText.getText().toString();
		        if ("".equals(SendMessage.this.content.trim()))
		        {
		        	Toast.makeText(SendMessage.this, "�������ݲ���Ϊ�գ�", 0).show();
		        	return;
		        }
		        SendMessage.this.startActivity(new Intent(SendMessage.this, LoadingActivity.class));
		        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		        new SendMessage.SendMessageOperation().execute();
    		}
    	})
    	.setNegativeButton("ȡ��", new DialogInterface.OnClickListener()
    	{
    		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    		{
    			paramAnonymousDialogInterface.dismiss();
    		}
    	}).show();
    }

    //��ߵķ��ذ�ť
    public void sendMessage_back(View paramView)
    {
    	finish();
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

    //������Ƭ�Ĳ���
    public void startPhotoZoom(Uri paramUri)
    {
	    Intent localIntent = new Intent("com.android.camera.action.CROP");
	    localIntent.setDataAndType(paramUri, "image/*");
	    localIntent.putExtra("crop", "true");
	    localIntent.putExtra("aspectX", 1);
	    localIntent.putExtra("aspectY", 1);
	    localIntent.putExtra("scale", true);
	    localIntent.putExtra("return-data", false);
	    this.imageUri = paramUri;
	    localIntent.putExtra("output", this.imageUri);
	    
	    startActivityForResult(localIntent, 3);
	    overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }

    //���ͷ������Ϣ���첽����
    public class SendMessageOperation extends AsyncTask<Void, Void, String>
    {

    	protected String doInBackground(Void[] paramArrayOfVoid)
    	{
    		//��ӷ��͵��ı�����
    		SchoolInfoMSG localSchoolInfoMSG = new SchoolInfoMSG();
    		localSchoolInfoMSG.setContent(SendMessage.this.content);
    		
    		SQLiteDatabase localSQLiteDatabase = SendMessage.this.openOrCreateDatabase("info.db", 0, null);
    		Cursor localCursor = localSQLiteDatabase.query(true, "user", null, "id=1", null, null, null, null, null);
    		localCursor.moveToFirst();
    		String str1 = localCursor.getString(1);
    		String str2 = localCursor.getString(2);
    		String str3 = localCursor.getString(3);
    		localCursor.close();
    		localSQLiteDatabase.close();
    		
    		//��ӷ����˵��������ǳƺ�ͷ��
    		localSchoolInfoMSG.setName(str1);
    		localSchoolInfoMSG.setNickname(str2);
    		localSchoolInfoMSG.setHeadIMG(str3);
    		
    		//�������
    		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		Date localDate = new Date();
     		SendMessage.this.time = localSimpleDateFormat.format(localDate);
     		localSchoolInfoMSG.setTime(SendMessage.this.time);
     		
     		int i = SendMessage.this.photos.size();
     		while (i < 3) {
     			SendMessage.this.photos.add("NULL");
     	        i++;
     		}
     		if (i >= 3)
     		{
     			localSchoolInfoMSG.setPhotos(SendMessage.this.photos);
     			localSchoolInfoMSG.setType(SendMessage.this.type);
     		}
     		
     		try
     		{
     			//��������
     			Socket localSocket = new Socket(IP_PORT.IP, IP_PORT.PORT);
     			ObjectInputStream localObjectInputStream = new ObjectInputStream(localSocket.getInputStream());
     			ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localSocket.getOutputStream());
     			
     			//д����
     			localObjectOutputStream.writeObject(localSchoolInfoMSG);
     			localObjectOutputStream.flush();
     			
     			//����������������
     			String str4 = (String)localObjectInputStream.readObject();
     			localObjectInputStream.close();
     			localObjectOutputStream.close();
     			localSocket.close();
     			
     			return str4;
          
     		}
     		catch (Exception localException)
     		{
     		}
     		return "CONNECT_ERROR";
    	}

    	protected void onPostExecute(String paramString)
    	{
    		LoadingActivity.instance.finish();
    		if (paramString.equals("CONNECT_ERROR"))
    			Toast.makeText(SendMessage.this, "���ӷ�����ʧ�ܣ�", 0).show();	
    		else if (paramString.equals("SUCCESS"))
    		{
    			SendMessage.this.finish();
    			Toast.makeText(SendMessage.this, "���ͳɹ���", 0).show();
    		}
    		super.onPostExecute(paramString);
    	}
    }
}