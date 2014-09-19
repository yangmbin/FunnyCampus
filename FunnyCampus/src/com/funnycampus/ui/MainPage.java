package com.funnycampus.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.funnycampus.adapter.SchoolListViewAdapter;
import com.funnycampus.reference.Base64Coder;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.SchoolInfoMSG;
import com.funnycampus.socket.SchoolInfoMSGList;
import com.funnycampus.socket.SetUserHeadImgMSG;
import com.funnycampus.socket.SetUserNicknameMSG;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yangmbin.funnycampus.R;

public class MainPage extends Activity {
	public static MainPage instance = null; //��ʾ��ǰ��activity
	
	private ViewPager mTabPager;
	private LayoutInflater inflater;
	
	private ImageView mTabImg; //����ҳ������ͼƬ
	private ImageView mTab1, mTab2, mTab3, mTab4; //�ײ��˵�������ͼƬ
	private LinearLayout mTab1_LinearLayout, mTab2_LinearLayout, mTab3_LinearLayout, mTab4_LinearLayout;
	private int currIndex; //��ǰҳ���ı��
	
	private int zero = 0; //����ͼƬƫ����
	private int one, two, three; //����ˮƽλ����
	
	private PullToRefreshListView schoolListView; //У԰��ҳ���Զ���ListView
	public static SchoolListViewAdapter schoolListViewAdapter;
	public static LinkedList<Map<String, Object>> listItems;
	
	/**
	 * ���ҵ��д�ҳ���е�GridView
	 */
	private GridView mysysuGridView;
	private SimpleAdapter gridAdapter;
	private ArrayList<HashMap<String, Object>> gridItems;
	
	/**
	 * �ü���ͷ����ǳ�
	 */
	Drawable STATIC_DRAWABLE = null;
	Bitmap PHOTO = null;
	String setNickname = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		//ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainpage);
		instance = this;
		
		//����ҳ��Viewװ��������
		LayoutInflater mLi = LayoutInflater.from(this);
		
		
		/**
		 * �ڡ�У԰��ҳ����г�ʼ���Ȳ���
		 */
		View view1 = mLi.inflate(R.layout.main_tab_school, null);
		
		//У԰Ȥ����ҳ��ListView����
		schoolListView = (PullToRefreshListView) view1.findViewById(R.id.listview_school); //��ȡListView
		schoolListView.setMode(Mode.BOTH); //����ˢ��
		
		//����ListView�ļ��ظ��¼���
		schoolListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				//��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat("������:  " + "yyyy.MM.dd  HH:mm");     
            	Date curDate = new Date(System.currentTimeMillis());
            	String label = formatter.format(curDate);   
            	
                //���һ�εĸ���ʱ��
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
   
                //ˢ�²��� 
                new GetDataTask().execute();
			}
		});
		
		////////////////////////////////////////////////////////////////
		listItems = new LinkedList<Map<String, Object>>(); 
		//Map<String, Object> map1 = new HashMap<String, Object>();    
        //map1.put("img_head", BitmapFactory.decodeResource(this.getResources(), R.drawable.img_head));
        //map1.put("name", "����");   
        //map1.put("content", "������������������������������");
        //map1.put("time", "2014-09-23 9:36");
        //listItems.add(map1);   
        ////////////////////////////////////////////////////////////////
        
        schoolListViewAdapter = new SchoolListViewAdapter(this, listItems);
        schoolListView.setAdapter(schoolListViewAdapter);
        
		
        /**
         * �ԡ��ҵ��д�ҳ�����Ԫ��
         */
		View view2 = mLi.inflate(R.layout.main_tab_mysysu, null);
		mysysuGridView = (GridView) view2.findViewById(R.id.mysysu_gridview);
		//����
		gridItems = new ArrayList<HashMap<String, Object>>(); 

		HashMap<String, Object> gridItemMap1 = new HashMap<String, Object>();
		gridItemMap1.put("gridImg", R.drawable.sysu_logo);
		gridItemMap1.put("gridText", "��������");
		gridItems.add(gridItemMap1);
		
		HashMap<String, Object> gridItemMap2 = new HashMap<String, Object>();
		gridItemMap2.put("gridImg", R.drawable.sysu_logo);
		gridItemMap2.put("gridText", "δʵ��");
		gridItems.add(gridItemMap2);

		//������
		gridAdapter = new SimpleAdapter(this, //ûʲô����  
                gridItems, //������Դ   
                R.layout.gridview_mysysu_item, //night_item��XMLʵ��        
                //��̬������ImageItem��Ӧ������          
                new String[] {"gridImg","gridText"},                   
                //ImageItem��XML�ļ������һ��ImageView,����TextView ID  
                new int[] {R.id.gridview_mysysu_item_img,R.id.gridview_mysysu_item_text});
		//���������
		mysysuGridView.setAdapter(gridAdapter);
		//����item����߿�͸��
		mysysuGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		//����GridView�ļ�������
		mysysuGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2 == 0) {
					//��ת�������б����
					startActivity(new Intent(MainPage.this, RecentNewsList.class));
				}
				if(arg2 == 1)
					Toast.makeText(MainPage.this, "δʵ��", Toast.LENGTH_SHORT).show();
			}
		});
		
		View view3 = mLi.inflate(R.layout.main_tab_study, null);
		
		/**
		 * ���ҡ���ҳ�����ʾ
		 */
		View view4 = mLi.inflate(R.layout.main_tab_me, null);
		
		/**
		 * ������ȡ�ڲ����ݿ⣬��ʾ�ǳƺ�ͷ��
		 */
		SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
    	cursor.moveToFirst();
    	String nickname = cursor.getString(2);
    	String headimg = cursor.getString(3);
    	cursor.close();
    	infoDB.close();
    	
    	//��ʾͷ��
    	if(!headimg.equals("NULL")) {
	    	ImageView userHeadImageView = (ImageView)view4.findViewById(R.id.user_head_img_imageView);
	    	byte[] headImgByte = Base64Coder.decodeLines(headimg);
			Bitmap headImgBitmap = BitmapFactory.decodeByteArray(headImgByte, 0, headImgByte.length);
			userHeadImageView.setImageBitmap(headImgBitmap);
    	}
    	//��ʾ�ǳ�
    	if(!nickname.equals("NULL")) {
    		TextView userNicknameTextView = (TextView)view4.findViewById(R.id.user_nickname_textView);
    		userNicknameTextView.setText(nickname);
    	}
    	/*end view4*/
		
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		
		//����ViewPager��������
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return views.size();
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		
		//����������
		mTabPager = (ViewPager)findViewById(R.id.tabpager);
		mTabPager.setAdapter(mPagerAdapter);
		
		
		mTab1 = (ImageView) findViewById(R.id.img_school);
        mTab2 = (ImageView) findViewById(R.id.img_message);
        mTab3 = (ImageView) findViewById(R.id.img_friends);
        mTab4 = (ImageView) findViewById(R.id.img_me);
        mTabImg = (ImageView) findViewById(R.id.img_tab_now);
        
        //����˵�����
        mTab1_LinearLayout = (LinearLayout)findViewById(R.id.img_school_linearlayout);
        mTab2_LinearLayout = (LinearLayout)findViewById(R.id.img_message_linearlayout);
        mTab3_LinearLayout = (LinearLayout)findViewById(R.id.img_friends_linearlayout);
        mTab4_LinearLayout = (LinearLayout)findViewById(R.id.img_me_linearlayout);
        mTab1_LinearLayout.setOnClickListener(new MyOnClickListener(0));
        mTab2_LinearLayout.setOnClickListener(new MyOnClickListener(1));
        mTab3_LinearLayout.setOnClickListener(new MyOnClickListener(2));
        mTab4_LinearLayout.setOnClickListener(new MyOnClickListener(3));
        
        
        Display currDisplay = getWindowManager().getDefaultDisplay();//��ȡ��Ļ��ǰ�ֱ���
        int displayWidth = currDisplay.getWidth();
        int displayHeight = currDisplay.getHeight();
        one = displayWidth / 4; //����ˮƽ����ƽ�ƴ�С
        two = one * 2;
        three = one * 3;
        
        
		//����ҳ�滬��ʱ�ļ���
		mTabPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				Animation animation = null;
				switch(arg0) {
				case 0:
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_school_pressed));
					if (currIndex == 1) {
						animation = new TranslateAnimation(one, 0, 0, 0);
						mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_mysysu_normal));
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, 0, 0, 0);
						mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_study_normal));
					}
					else if (currIndex == 3) {
						animation = new TranslateAnimation(three, 0, 0, 0);
						mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_me_normal));
					}
					break;
				case 1:
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_mysysu_pressed));
					if (currIndex == 0) {
						animation = new TranslateAnimation(zero, one, 0, 0);
						mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_school_normal));
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, one, 0, 0);
						mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_study_normal));
					}
					else if (currIndex == 3) {
						animation = new TranslateAnimation(three, one, 0, 0);
						mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_me_normal));
					}
					break;
				case 2:
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_study_pressed));
					if (currIndex == 0) {
						animation = new TranslateAnimation(zero, two, 0, 0);
						mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_school_normal));
					} else if (currIndex == 1) {
						animation = new TranslateAnimation(one, two, 0, 0);
						mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_mysysu_normal));
					}
					else if (currIndex == 3) {
						animation = new TranslateAnimation(three, two, 0, 0);
						mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_me_normal));
					}
					break;
				case 3:
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_me_pressed));
					if (currIndex == 0) {
						animation = new TranslateAnimation(zero, three, 0, 0);
						mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_school_normal));
					} else if (currIndex == 1) {
						animation = new TranslateAnimation(one, three, 0, 0);
						mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_mysysu_normal));
					}
					else if (currIndex == 2) {
						animation = new TranslateAnimation(two, three, 0, 0);
						mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_study_normal));
					}
					break;	
				}	
				currIndex = arg0;
				animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
				animation.setDuration(150);
				mTabImg.startAnimation(animation);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	} //onCreate����
	
	//�����·��˵��������
    public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;
		public MyOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	}
    
    //Listview���ص��첽����
    private class GetDataTask extends AsyncTask<Void, Void, SchoolInfoMSGList> {  
		  
        //��̨������  
        @Override  
        protected SchoolInfoMSGList doInBackground(Void... params) {  
            // Simulates a background job. 
        	//�ӷ��������ص���Ϣ
        	SchoolInfoMSGList result = null;
        	
            try {  
                Thread.sleep(1000);  
 	
                //ͷ��ˢ������
                if(schoolListView.isHeaderShown())
                {
                	//����
                	String headRefreshMSG = "HEAD_REFRESH";
         	
                	//����Socket�����ӵ�������
    				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
    				
    				ObjectInputStream in = new ObjectInputStream(
    						client.getInputStream()); //�ӷ�����������
    				ObjectOutputStream out = new ObjectOutputStream(
    						client.getOutputStream()); //�������д����
    				
    				//�������д����ˢ��
    				out.writeObject(headRefreshMSG);
    				out.flush();

    				//��ȡ���������ص���Ϣ
    				result = (SchoolInfoMSGList)in.readObject();

    				//�ر����������
    				in.close();
    				out.close();
    				client.close();
                }
            } 
            catch (Exception e) {  
            	result = null;
            }   
            return result;  
        }  
  
        //�����Ƕ�ˢ�µ���Ӧ����������addFirst������addLast()�������¼ӵ����ݼӵ�LISTView��  
        //����AsyncTask��ԭ��onPostExecute���result��ֵ����doInBackground()�ķ���ֵ  
        @Override  
        protected void onPostExecute(SchoolInfoMSGList result) {  
        	//����
        	List<SchoolInfoMSG> list;
        	if(result == null) {
        		Toast.makeText(MainPage.this, "ˢ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
        	}
        	else {
        		list = result.getList();
        		if(schoolListView.isHeaderShown()) {
    	            //��ͷ��������������     
        			listItems.clear();
            		for(int i = list.size() - 1; i >= 0; i--)
            		{
    	    			Map<String, Object> map = new HashMap<String, Object>(); 
    	    			if(list.get(i).getHeadIMG().equals("NULL"))
    	    				map.put("img_head", BitmapFactory.decodeResource(MainPage.this.getResources(), R.drawable.img_head));
    	    			else {
    	    				byte[] tempb = Base64Coder.decodeLines(list.get(i).getHeadIMG());
    	    				Bitmap dBitmap = BitmapFactory.decodeByteArray(tempb, 0, tempb.length);
    	    				//Bitmap dBitmap = BitmapFactory.decodeFile(list.get(i).getHeadIMG());  
    	    	            //Drawable drawable = new BitmapDrawable(dBitmap); 
    	    	            map.put("img_head", dBitmap);
    	    	            
    	    			}
    	    	        map.put("name", list.get(i).getNickname());   
    	    	        map.put("content", list.get(i).getContent());
    	    	        map.put("time", list.get(i).getTime());
    	    	        listItems.addFirst(map);
            		}

            	}
            	else if(schoolListView.isFooterShown()) {
            		//��β��������������  
            		;
            	}
        	}
        	
              
            //֪ͨ�������ݼ��Ѿ��ı䣬�������֪ͨ����ô������ˢ��mListItems�ļ���  
            schoolListViewAdapter.notifyDataSetChanged();  
            // Call onRefreshComplete when the list has been refreshed.  
            schoolListView.onRefreshComplete();  
  
            super.onPostExecute(result);  
        }  
    }
    
    //��ʾ���°����ļ�������
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //��ȡ back��
    		Intent intent = new Intent();
        	intent.setClass(MainPage.this,Exit.class);
        	startActivity(intent);
    	}
    	return false;
    }
    
    //��ʾ��ҳ���еġ������¡���ť��������
    public void sendMessage(View v) { 
    	startActivity(new Intent(MainPage.this, SendMessage.class));
    }
    
   /**
    * ��ʾ���������ļ����¼�
    */
    public void GetTodayWeather(View v) {
    	//Toast.makeText(MainPage.this, "����˽�������", Toast.LENGTH_SHORT).show();
    	startActivity(new Intent(MainPage.this, ChooseCity.class));
    }
    
    /**
     * ��������ͼ��ҳ��
     */
    public void GoSearchView(View v) { 
    	startActivity(new Intent(MainPage.this, SearchBook.class));
    }
    
    /**
     * �����û���ͷ��
     */
    public void SetUserHeadImg(View v) {
    	//Toast.makeText(MainPage.this, "����ͷ��", Toast.LENGTH_SHORT).show();
    	new AlertDialog.Builder(this)
    			.setTitle("����ͷ��")
    			.setNegativeButton("���", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, 1);
					}
				})
				.setPositiveButton("����", new DialogInterface.OnClickListener() {		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						//�������ָ������������պ����Ƭ�洢��·��
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "userHeadImg.jpg")));
						startActivityForResult(intent, 2);
					}
				})
				.show();
    }
    /*����ͷ����غ���*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	//�����ֱ�ӻ�ȡ
    	case 1:
    		if(data != null)
    			startPhotoZoom(data.getData());
    		break;
    	// ����ǵ����������ʱ  
    	case 2:
    		File temp = new File(Environment.getExternalStorageDirectory() + "/userHeadImg.jpg");
    		startPhotoZoom(Uri.fromFile(temp));
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
    /*�ü�ͼƬ����ʵ��*/
    public void startPhotoZoom(Uri uri) {  
        Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, "image/*");  
        //�������crop=true�������ڿ�����Intent��������ʾ��VIEW�ɲü�  
        intent.putExtra("crop", "true");  
        // aspectX aspectY �ǿ�ߵı���  
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        // outputX outputY �ǲü�ͼƬ���  
        intent.putExtra("outputX", 150);  
        intent.putExtra("outputY", 150);  
        intent.putExtra("return-data", true);  
        startActivityForResult(intent, 3);  
    }
    /*����ü�֮���ͼƬ����*/
    private void setPicToView(Intent picdata) {  
        Bundle extras = picdata.getExtras();  
        if (extras != null) {  
            PHOTO = extras.getParcelable("data");  
            STATIC_DRAWABLE = new BitmapDrawable(PHOTO);  
              
            /**  
             * ����ע�͵ķ����ǽ��ü�֮���ͼƬ��Base64Coder���ַ���ʽ��  
             * ������������QQͷ���ϴ����õķ������������  
             */ 
              
            ByteArrayOutputStream stream = new ByteArrayOutputStream();  
            PHOTO.compress(Bitmap.CompressFormat.JPEG, 60, stream);  
            byte[] b = stream.toByteArray();  
            // ��ͼƬ�����ַ�����ʽ�洢����  
              
            String tp = new String(Base64Coder.encodeLines(b));  
            /*
                        ����ط�����д�¸��������ϴ�ͼƬ��ʵ�֣�ֱ�Ӱ�tpֱ���ϴ��Ϳ����ˣ�  
       	        ����������ķ����Ƿ������Ǳߵ�����
              
         	������ص��ķ����������ݻ�����Base64Coder����ʽ�Ļ������������·�ʽת��  
                        Ϊ���ǿ����õ�ͼƬ���;�OK��
            Bitmap dBitmap = BitmapFactory.decodeFile(tp);  
            Drawable drawable = new BitmapDrawable(dBitmap);  
            */ 

            //ImageView userHeadImgImageView = (ImageView)findViewById(R.id.user_head_img_imageView);
            //userHeadImgImageView.setBackgroundDrawable(drawable);
            //����ͷ��
            //userHeadImgImageView.setImageDrawable(drawable);
            
            //�Ѹ��ĵ�ͼƬ���͵���̨�����º�̨���ݿ�
            new SetUserHeadImgOperation().execute(tp);
        }  
    } 
    //����ͷ�����
    public class SetUserHeadImgOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	    	
	    	
	    	//�������ݵ�����������Ϣ
	    	SetUserHeadImgMSG msg = new SetUserHeadImgMSG();
	    	msg.setName(name);
	    	msg.setHeadimg(arg0[0]);
	    	
	    	//�ӷ��������صĽ��
	    	String result = null;
	    	try {
				//����Socket�����ӵ�������
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //�������д����
				
				//�������д��¼����
				out.writeObject(msg);
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
			return result;
		}
    	@Override
    	protected void onPostExecute(String result) {
    		if(result.equals("CONNECT_ERROR")) {
    			Toast.makeText(MainPage.this, "�������", Toast.LENGTH_SHORT).show();
    		}
    		else if(result.equals("SUCCESS")) {
    			Toast.makeText(MainPage.this, "����ͷ��ɹ���", Toast.LENGTH_SHORT).show();
    			ImageView userHeadImgImageView = (ImageView)findViewById(R.id.user_head_img_imageView);
                userHeadImgImageView.setImageDrawable(STATIC_DRAWABLE);
                
                //�����ڲ����ݿ��ͷ��
                ByteArrayOutputStream stream = new ByteArrayOutputStream();  
                PHOTO.compress(Bitmap.CompressFormat.JPEG, 60, stream);  
                byte[] b = stream.toByteArray();  
                // ��ͼƬ�����ַ�����ʽ�洢����                  
                String tp = new String(Base64Coder.encodeLines(b));
                
                //���ݿ���²���
                SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
                infoDB.execSQL("update user set headimg=" + "'" + tp + "'" + "where id=1");
    	    	infoDB.close();	 
    		}
    		super.onPostExecute(result);
    	}
    }
    
    /**
     * �����û����ǳ�
     */
    public void SetUserNickname(View v) { 
    	/*
    	SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
    	cursor.moveToFirst();
    	String name = cursor.getString(1);
    	String nickname = cursor.getString(2);
    	String headimg = cursor.getString(3);
    	cursor.close();
    	infoDB.close();
    	Toast.makeText(MainPage.this, name + "," + nickname + "," + headimg, Toast.LENGTH_SHORT).show();
    	*/
    	final EditText nicknameEditText = new EditText(this);
    	new AlertDialog.Builder(this)
    		.setTitle("�����ǳ�")
    		.setView(nicknameEditText)
    		.setPositiveButton("ȷ��", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					setNickname = nicknameEditText.getText().toString();
					setNickname = setNickname.trim();
					if(setNickname.equals("")) {
						Toast.makeText(MainPage.this, "���벻��Ϊ�գ�", Toast.LENGTH_SHORT).show();
					}
					else {
						new SetUserNicknameOperation().execute(setNickname);
					}
				}
			})
    		.setNegativeButton("ȡ��", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			})
    		.show();
    }
    
    //�����ǳƵ���
    public class SetUserNicknameOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	    	
	    	
	    	//�������ݵ�����������Ϣ
	    	SetUserNicknameMSG msg = new SetUserNicknameMSG();
	    	msg.setName(name);
	    	msg.setNickname(arg0[0]);
	    	
	    	//�ӷ��������صĽ��
	    	String result = null;
	    	try {
				//����Socket�����ӵ�������
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //�������д����
				
				//�������д��¼����
				out.writeObject(msg);
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
			return result;
		}
    	@Override
    	protected void onPostExecute(String result) {
    		if(result.equals("CONNECT_ERROR")) {
    			Toast.makeText(MainPage.this, "�������", Toast.LENGTH_SHORT).show();
    		}
    		else if(result.equals("SUCCESS")) {
    			Toast.makeText(MainPage.this, "�����ǳƳɹ���", Toast.LENGTH_SHORT).show();
                TextView userNicknameTextView = (TextView)findViewById(R.id.user_nickname_textView);
                userNicknameTextView.setText(setNickname);
                
                //�����ڲ����ݿ���ǳ�       
                //���ݿ���²���
                SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
                infoDB.execSQL("update user set nickname=" + "'" + setNickname + "'" + "where id=1");
    	    	infoDB.close();	 
    		}
    		super.onPostExecute(result);
    	}
    }
    
}
