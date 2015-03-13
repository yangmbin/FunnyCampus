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
	private List<Bitmap> bitmaps = new ArrayList(); //发送的图片数组
    private String content; //新鲜事内容
    private EditText editText; //分享文本的编辑框
    private Uri imageUri; //用来表示选择分享的图片在手机中的Uri路径
    private List<String> photos = new ArrayList(); //发送的图片数组，转换为了String类型
    String time = null; //发送的时间
    private int type = 1; //默认分享的板块，为“校园趣事”
    
    @Override
    public void onCreate(Bundle paramBundle)
    {
	    super.onCreate(paramBundle);
	    setContentView(R.layout.sendmessage);
    }

    //从Uri中获取Bitmap类型图片
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

    //把选择好的图片显示在相应位置作为预览
    private void setPicToView(Intent paramIntent)
    {
    	Bitmap localBitmap;
    	BitmapDrawable localBitmapDrawable;
    	if (paramIntent.getExtras() != null)
    	{
    		localBitmap = getBitmapFromUri(this.imageUri);
    		localBitmapDrawable = new BitmapDrawable(localBitmap);
    		//这表示选择的第一张图片，把图片显示在相应的位置
    		if (this.photos.size() == 0)
            {
    			ImageView localImageView4 = (ImageView)findViewById(R.id.first_photo);
		        localImageView4.setBackgroundDrawable(localBitmapDrawable);
		        //把Bitmap格式的图片转换为字节流并压缩，编码后转为String类型
		        ByteArrayOutputStream localByteArrayOutputStream3 = new ByteArrayOutputStream();
		        localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localByteArrayOutputStream3);
		        String str3 = new String(Base64Coder.encodeLines(localByteArrayOutputStream3.toByteArray()));
		        //把得到的图片的String类型存储在photos中，等待发送给服务器
		        this.photos.add(str3);
		        this.bitmaps.add(localBitmap);
		        //显示了第一张图片后使得第二张默认添加图片显示出来
		        ImageView localImageView5 = (ImageView)findViewById(R.id.second_photo);
		        localImageView5.setClickable(true);
		        localImageView5.setVisibility(0);
		        localImageView4.setClickable(false);
            }
    		//这表示选择第二章图片
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
    		//这表示选择第三张图片
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

    //添加图片按钮
    public void addPhotos(View paramView)
    {
    	new AlertDialog.Builder(this).setTitle("选择照片").setNegativeButton("相册", new DialogInterface.OnClickListener()
    	{
    		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
	        {
    			//选择相册中的照片
		        paramAnonymousDialogInterface.dismiss();
		        Intent localIntent = new Intent("android.intent.action.PICK", null);
		        localIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		        SendMessage.this.startActivityForResult(localIntent, 1);
		        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
	        }
        }).setPositiveButton("拍照", new DialogInterface.OnClickListener()
        {
        	public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        	{
        		//通过拍照获取照片
		        paramAnonymousDialogInterface.dismiss();
		        Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
		        localIntent.putExtra("output", Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photoImg.jpg")));
		        SendMessage.this.startActivityForResult(localIntent, 2);
		        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
        	}
        }).show();
    }

    //获取到照片后的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	//从相册直接获取
    	case 1:
    		if(data != null) {
    			//从Uri获取相册中的照片
    			Bitmap localBitmap = getBitmapFromUri(data.getData());
    			//在内存卡中新建一个被选择照片的拷贝，避免原图片被剪裁修改掉
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
    	// 如果是调用相机拍照时  
    	case 2:
    		File temp = new File(Environment.getExternalStorageDirectory() + "/photoImg.jpg");
    		startPhotoZoom(Uri.fromFile(temp));
    		break;
    	// 取得裁剪后的图片 
    	case 3:
    		//非空判断大家一定要验证，如果不验证的话，  
            //在剪裁之后如果发现不满意，要重新裁剪，丢弃  
            //当前功能时，会报NullException
    		if(data != null) {
    			setPicToView(data);
    		}
    		break;
		default:
			break;
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }    
    
    //右方发送按钮监听函数
    public void rightSendMessageBtn(View paramView)
    {
    	new AlertDialog.Builder(this)
    	.setTitle("选择发布板块")
    	.setIcon(android.R.drawable.ic_dialog_info)
    	.setSingleChoiceItems(
    			new String[] { "校园趣事", "校园新闻", "社团活动", "校园讲座" }, 
    			0, 
    			new DialogInterface.OnClickListener()
    			{
    				public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    				{
    					SendMessage.this.type = (paramAnonymousInt + 1);
    				}
    			})
    	.setPositiveButton("确定", new DialogInterface.OnClickListener()
    	{
    		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    		{
		        paramAnonymousDialogInterface.dismiss();
		        SendMessage.this.editText = ((EditText)SendMessage.this.findViewById(R.id.send_message_edit));
		        SendMessage.this.content = SendMessage.this.editText.getText().toString();
		        if ("".equals(SendMessage.this.content.trim()))
		        {
		        	Toast.makeText(SendMessage.this, "输入内容不能为空！", 0).show();
		        	return;
		        }
		        SendMessage.this.startActivity(new Intent(SendMessage.this, LoadingActivity.class));
		        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		        new SendMessage.SendMessageOperation().execute();
    		}
    	})
    	.setNegativeButton("取消", new DialogInterface.OnClickListener()
    	{
    		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    		{
    			paramAnonymousDialogInterface.dismiss();
    		}
    	}).show();
    }

    //左边的返回按钮
    public void sendMessage_back(View paramView)
    {
    	finish();
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

    //剪裁照片的操作
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

    //发送分享的信息的异步操作
    public class SendMessageOperation extends AsyncTask<Void, Void, String>
    {

    	protected String doInBackground(Void[] paramArrayOfVoid)
    	{
    		//添加发送的文本内容
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
    		
    		//添加发送人的姓名、昵称和头像
    		localSchoolInfoMSG.setName(str1);
    		localSchoolInfoMSG.setNickname(str2);
    		localSchoolInfoMSG.setHeadIMG(str3);
    		
    		//添加日期
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
     			//建立连接
     			Socket localSocket = new Socket(IP_PORT.IP, IP_PORT.PORT);
     			ObjectInputStream localObjectInputStream = new ObjectInputStream(localSocket.getInputStream());
     			ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localSocket.getOutputStream());
     			
     			//写数据
     			localObjectOutputStream.writeObject(localSchoolInfoMSG);
     			localObjectOutputStream.flush();
     			
     			//读服务器来的数据
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
    			Toast.makeText(SendMessage.this, "连接服务器失败！", 0).show();	
    		else if (paramString.equals("SUCCESS"))
    		{
    			SendMessage.this.finish();
    			Toast.makeText(SendMessage.this, "发送成功！", 0).show();
    		}
    		super.onPostExecute(paramString);
    	}
    }
}