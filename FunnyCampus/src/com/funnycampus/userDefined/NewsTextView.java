package com.funnycampus.userDefined;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangmbin.funnycampus.R;

//�Զ������ŵ���ʾTextView
public class NewsTextView extends LinearLayout {
	//�����Ķ���
	private Context mContext;
	//����TypedArray������
	private TypedArray mTypedArray;
	//���ֲ���
	private LayoutParams params;

	public NewsTextView(Context context) {
		super(context);
	}
	
	public NewsTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.setOrientation(VERTICAL);
		//��attrs.xml�ļ��л���Զ�������
		mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.newsTextView);
	}
	
	public void setText(ArrayList<HashMap<String, String>> datas) {
		//����ArrayList
		for(HashMap<String, String> hashMap : datas) 
		{
			String type = hashMap.get("type");
			//�����ͼƬ
			if(type.equals("image")) {
				//��ȡ�Զ�������
				int imagewidth = mTypedArray.getDimensionPixelOffset(R.styleable.newsTextView_image_width, 200);  
                int imageheight = mTypedArray.getDimensionPixelOffset(R.styleable.newsTextView_image_height, 200);  
                ImageView imageView = new ImageView(mContext);  
                params = new LayoutParams(imagewidth, imageheight);  
                params.gravity = Gravity.CENTER_HORIZONTAL; //����  
                imageView.setLayoutParams(params);  
                //��ʾͼƬ  
                imageView.setImageResource(R.drawable.default_img);  
                //��imageView��ӵ�LinearLayout����  
                addView(imageView);  
                //�����첽�̸߳����첽��ʾͼƬ��Ϣ  
                new DownloadPicThread(imageView, hashMap.get("value")).start();
			}
			//�������������
			else if(type.equals("text")) {
				float textSize = mTypedArray.getDimension(R.styleable.newsTextView_textSize, 18);  
                int textColor = mTypedArray.getColor(R.styleable.newsTextView_textColor, 0xFF000000);  
                TextView textView = new TextView(mContext);  
                textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));  
                //textView.setText(Html.fromHtml(hashMap.get("value")));  
                textView.setText(hashMap.get("value"));  
                textView.setTextSize(textSize);     //���������С  
                textView.setTextColor(textColor);   //����������ɫ  
                textView.setLineSpacing(5.5f, 1f);  //�����м��
                addView(textView);
			}
			//�����ͼƬ����
			else if(type.equals("imgText")) {  
                TextView textView = new TextView(mContext);  
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                textView.setLayoutParams(params);  
                //textView.setText(Html.fromHtml(hashMap.get("value")));  
                textView.setText(hashMap.get("value"));  
                textView.setTextSize(10.0f);     //���������С  
                textView.setTextColor(Color.rgb(88, 182, 45));   //����������ɫ  
                textView.setLineSpacing(5.5f, 1f);  //�����м��
                addView(textView);
			}
		}
	}
	
	private Handler handler = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
            @SuppressWarnings("unchecked")  
            HashMap<String, Object> hashMap = (HashMap<String, Object>) msg.obj;  
            ImageView imageView = (ImageView) hashMap.get("imageView");  
            LayoutParams params = new LayoutParams(msg.arg1, msg.arg2);  
            params.gravity = Gravity.CENTER_HORIZONTAL; //����  
            imageView.setLayoutParams(params);  
            Drawable drawable = (Drawable) hashMap.get("drawable");  
            imageView.setImageDrawable(drawable);       //��ʾͼƬ  
        };  
    };  
      
    /** 
     * ����һ���߳��࣬�첽����ͼƬ 
     */  
    private class DownloadPicThread extends Thread {  
        private ImageView imageView;  
        private String mUrl;  
          
        public DownloadPicThread(ImageView imageView, String mUrl) {  
            super();  
            this.imageView = imageView;  
            this.mUrl = mUrl;  
        }  
  
  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
            Drawable drawable = null;  
            int newImgWidth = 0;  
            int newImgHeight = 0;  
            try {  
                drawable = Drawable.createFromStream(new URL(mUrl).openStream(), "image");  
                //��ͼƬ��������  
                newImgWidth = drawable.getIntrinsicWidth();  
                newImgHeight = drawable.getIntrinsicHeight();  
            } catch (Exception e) {  
                // TODO: handle exception  
                e.printStackTrace();  
            }  
            //���߳�����2��  
            SystemClock.sleep(2000);  
            
            //ʹ��Handler����UI  
            Message msg = handler.obtainMessage();  
            HashMap<String, Object> hashMap = new HashMap<String, Object>();  
            hashMap.put("imageView", imageView);  
            hashMap.put("drawable", drawable);  
            msg.obj = hashMap;  
            msg.arg1 = newImgWidth;  
            msg.arg2 = newImgHeight;  
            handler.sendMessage(msg);  
        }  
    } 
	
}
