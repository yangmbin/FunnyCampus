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
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
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
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.funnycampus.XMPPUtils.XMPPUtils;
import com.funnycampus.adapter.SchoolListViewAdapter;
import com.funnycampus.reference.Base64Coder;
import com.funnycampus.service.SendLocationInfoService;
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
import com.yangmbin.funnycampus.R.anim;

public class MainPage extends Activity {
	public static MainPage instance = null; //表示当前的activity
	
	private ViewPager mTabPager;
	private LayoutInflater inflater;
	
	private ImageView mTabImg; //随着页滑动的图片
	private ImageView mTab1, mTab2, mTab3, mTab4; //底部菜单的四张图片
	private LinearLayout mTab1_LinearLayout, mTab2_LinearLayout, mTab3_LinearLayout, mTab4_LinearLayout;
	private TextView mTab1_TextView, mTab2_TextView, mTab3_TextView, mTab4_TextView;
	private int currIndex; //当前页卡的编号
	
	private int zero = 0; //动画图片偏移量
	private int one, two, three; //动画水平位移量
	
	private PullToRefreshListView schoolListView; //校园主页面自定义ListView
	public static SchoolListViewAdapter schoolListViewAdapter;
	public static LinkedList<Map<String, Object>> listItems;
	
	/**
	 * “我的中大”页面中的GridView
	 */
	private GridView mysysuGridView;
	private SimpleAdapter gridAdapter;
	private ArrayList<HashMap<String, Object>> gridItems;
	
	/**
	 * 裁剪的头像和昵称
	 */
	Drawable STATIC_DRAWABLE = null;
	Bitmap PHOTO = null;
	String setNickname = null;
	
	//保存点击条目后获取的条目内容
	public static Map<String, Object> clickedItemData;
	
	//点击进行刷新
	private boolean clickToRefresh = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		//去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainpage);
		instance = this;
		
		//将分页的View装入数组中
		LayoutInflater mLi = LayoutInflater.from(this);
		
		
		/**
		 * 在“主页”页面进行初始化等操作
		 */
		View view1 = mLi.inflate(R.layout.main_tab_home, null);
		
		//校园趣事主页面ListView数据
		schoolListView = (PullToRefreshListView) view1.findViewById(R.id.listview_school); //获取ListView
		schoolListView.setMode(Mode.BOTH); //两端刷新
		
		//设置ListView的加载更新监听
		schoolListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				//获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat("最后更新: " + "yyyy.MM.dd HH:mm");     
            	Date curDate = new Date(System.currentTimeMillis());
            	String label = formatter.format(curDate);   
            	
                //最近一次的更新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
   
                //刷新操作 
                new GetDataTask().execute();
			}
		});
		
		////////////////////////////////////////////////////////////////
		listItems = new LinkedList<Map<String, Object>>(); 
		//Map<String, Object> map1 = new HashMap<String, Object>();    
        //map1.put("img_head", BitmapFactory.decodeResource(this.getResources(), R.drawable.img_head));
        //map1.put("name", "名字");   
        //map1.put("content", "容内容内容内容内容内容内容内容");
        //map1.put("time", "2014-09-23 9:36");
        //listItems.add(map1);   
        ////////////////////////////////////////////////////////////////
        
        schoolListViewAdapter = new SchoolListViewAdapter(this, listItems);
        schoolListView.setAdapter(schoolListViewAdapter);
        
        //设置主页条目的监听
        schoolListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Intent intent = new Intent(MainPage.this, HomeDetailMessage.class);
				//获取点击条目的数据
				clickedItemData = (Map<String, Object>) parent.getAdapter().getItem(position);
				intent.putExtra("clickPosition", position - 1);
				startActivity(intent);
				overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
			}
		});
        
		
        /**
         * 对“资讯”页面加入元素
         */
		View view2 = mLi.inflate(R.layout.main_tab_information, null);
		mysysuGridView = (GridView) view2.findViewById(R.id.mysysu_gridview);
		//数据
		gridItems = new ArrayList<HashMap<String, Object>>(); 
		
		HashMap<String, Object> localHashMap1 = new HashMap<String, Object>();
	    localHashMap1.put("gridImg", R.drawable.sysu_logo);
	    localHashMap1.put("gridText", "中大新闻");
	    this.gridItems.add(localHashMap1);
	    
	    HashMap<String, Object> localHashMap2 = new HashMap<String, Object>();
	    localHashMap2.put("gridImg", R.drawable.duanzi_logo);
	    localHashMap2.put("gridText", "段子");
	    this.gridItems.add(localHashMap2);
	    
	    HashMap<String, Object> localHashMap3 = new HashMap<String, Object>();
	    localHashMap3.put("gridImg", R.drawable.keji_logo);
	    localHashMap3.put("gridText", "科技");
	    this.gridItems.add(localHashMap3);
	    
	    HashMap<String, Object> localHashMap4 = new HashMap<String, Object>();
	    localHashMap4.put("gridImg", R.drawable.tiyu_logo);
	    localHashMap4.put("gridText", "体育");
	    this.gridItems.add(localHashMap4);
	    
	    HashMap<String, Object> localHashMap5 = new HashMap<String, Object>();
	    localHashMap5.put("gridImg", R.drawable.youxi_logo);
	    localHashMap5.put("gridText", "游戏");
	    this.gridItems.add(localHashMap5);

		//适配器
		gridAdapter = new SimpleAdapter(this, //没什么解释  
                gridItems, //数据来源   
                R.layout.gridview_information_item, //night_item的XML实现        
                //动态数组与ImageItem对应的子项          
                new String[] {"gridImg","gridText"},                   
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] {R.id.gridview_mysysu_item_img,R.id.gridview_mysysu_item_text});
		//添加适配器
		mysysuGridView.setAdapter(gridAdapter);
		//设置item点击边框透明
		mysysuGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		//设置GridView的监听函数
		mysysuGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2 == 0) {
					//跳转到新闻列表界面
					startActivity(new Intent(MainPage.this, RecentNewsList.class));
					overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
				}
				if(arg2 == 1)
					Toast.makeText(MainPage.this, "未实现", Toast.LENGTH_SHORT).show();
			}
		});
		
		View view3 = mLi.inflate(R.layout.main_tab_discovery, null);
		
		/**
		 * “我”的页面的显示
		 */
		View view4 = mLi.inflate(R.layout.main_tab_me, null);
		
		/**
		 * 进入后读取内部数据库，显示昵称和头像
		 */
		SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
    	cursor.moveToFirst();
    	String nickname = cursor.getString(2);
    	String headimg = cursor.getString(3);
    	cursor.close();
    	infoDB.close();
    	
    	//显示头像
    	if(!headimg.equals("NULL")) {
	    	ImageView userHeadImageView = (ImageView)view4.findViewById(R.id.user_head_img_imageView);
	    	byte[] headImgByte = Base64Coder.decodeLines(headimg);
			Bitmap headImgBitmap = BitmapFactory.decodeByteArray(headImgByte, 0, headImgByte.length);
			userHeadImageView.setImageBitmap(headImgBitmap);
    	}
    	//显示昵称
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
		
		//创建ViewPager的适配器
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
		
		//设置适配器
		mTabPager = (ViewPager)findViewById(R.id.tabpager);
		mTabPager.setAdapter(mPagerAdapter);
		
		
		mTab1 = (ImageView) findViewById(R.id.img_school);
        mTab2 = (ImageView) findViewById(R.id.img_message);
        mTab3 = (ImageView) findViewById(R.id.img_friends);
        mTab4 = (ImageView) findViewById(R.id.img_me);
        mTabImg = (ImageView) findViewById(R.id.img_tab_now);
        
        //点击菜单监听
        mTab1_LinearLayout = (LinearLayout)findViewById(R.id.img_school_linearlayout);
        mTab2_LinearLayout = (LinearLayout)findViewById(R.id.img_message_linearlayout);
        mTab3_LinearLayout = (LinearLayout)findViewById(R.id.img_friends_linearlayout);
        mTab4_LinearLayout = (LinearLayout)findViewById(R.id.img_me_linearlayout);
        mTab1_LinearLayout.setOnClickListener(new MyOnClickListener(0));
        mTab2_LinearLayout.setOnClickListener(new MyOnClickListener(1));
        mTab3_LinearLayout.setOnClickListener(new MyOnClickListener(2));
        mTab4_LinearLayout.setOnClickListener(new MyOnClickListener(3));
        
        mTab1 = (ImageView)findViewById(R.id.img_school);
        mTab2 = (ImageView)findViewById(R.id.img_message);
        mTab3 = (ImageView)findViewById(R.id.img_friends);
        mTab4 = (ImageView)findViewById(R.id.img_me);
        mTab1.setOnClickListener(new MyOnClickListener(0));
        mTab2.setOnClickListener(new MyOnClickListener(1));
        mTab3.setOnClickListener(new MyOnClickListener(2));
        mTab4.setOnClickListener(new MyOnClickListener(3));
        
        mTab1_TextView = (TextView)findViewById(R.id.text_school);
        mTab2_TextView = (TextView)findViewById(R.id.text_message);
        mTab3_TextView = (TextView)findViewById(R.id.text_friends);
        mTab4_TextView = (TextView)findViewById(R.id.text_me);
        mTab1_TextView.setOnClickListener(new MyOnClickListener(0));
        mTab2_TextView.setOnClickListener(new MyOnClickListener(1));
        mTab3_TextView.setOnClickListener(new MyOnClickListener(2));
        mTab4_TextView.setOnClickListener(new MyOnClickListener(3));
        
        
        Display currDisplay = getWindowManager().getDefaultDisplay();//获取屏幕当前分辨率
        int displayWidth = currDisplay.getWidth();
        int displayHeight = currDisplay.getHeight();
        one = displayWidth / 4; //设置水平动画平移大小
        two = one * 2;
        three = one * 3;
        
        
		//设置页面滑动时的监听
		mTabPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				Animation animation = null;
				switch(arg0) {
				case 0:
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_home_pressed));
					if (currIndex == 1) {
						animation = new TranslateAnimation(one, 0, 0, 0);
						mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_information_normal));
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, 0, 0, 0);
						mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_discovery_normal));
					}
					else if (currIndex == 3) {
						animation = new TranslateAnimation(three, 0, 0, 0);
						mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_me_normal));
					}
					break;
				case 1:
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_information_pressed));
					if (currIndex == 0) {
						animation = new TranslateAnimation(zero, one, 0, 0);
						mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_home_normal));
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, one, 0, 0);
						mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_discovery_normal));
					}
					else if (currIndex == 3) {
						animation = new TranslateAnimation(three, one, 0, 0);
						mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_me_normal));
					}
					break;
				case 2:
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_discovery_pressed));
					if (currIndex == 0) {
						animation = new TranslateAnimation(zero, two, 0, 0);
						mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_home_normal));
					} else if (currIndex == 1) {
						animation = new TranslateAnimation(one, two, 0, 0);
						mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_information_normal));
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
						mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_home_normal));
					} else if (currIndex == 1) {
						animation = new TranslateAnimation(one, three, 0, 0);
						mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_information_normal));
					}
					else if (currIndex == 2) {
						animation = new TranslateAnimation(two, three, 0, 0);
						mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_discovery_normal));
					}
					break;	
				}	
				currIndex = arg0;
				animation.setFillAfter(true);// True:图片停在动画结束位置
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
		
		//启动service，间歇性向服务器更新位置信息
		startService(new Intent("com.funnycampus.service.SendLocationInfoService"));
		
	} //onCreate结束
	
	//设置下方菜单点击监听
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
    
    /**
     * 主页Listview下拉刷新的异步操作
     * @author Administrator
     *
     */
    private class GetDataTask extends AsyncTask<Void, Void, SchoolInfoMSGList> {  
		  
        //后台处理部分  
        @Override  
        protected SchoolInfoMSGList doInBackground(Void... params) {  
        	//从服务器返回的消息
        	SchoolInfoMSGList result = null;
        	
            try {  
                //Thread.sleep(1000);  
 	
                //头部刷新请求
                if(schoolListView.isHeaderShown() || clickToRefresh)
                {     	
                	//表示现在的topTitle的String
                	String topTitle = ((TextView) findViewById(R.id.top_title_4)).getText().toString();
                	//表示现在的topTitle的number
                	int i = 1;
                	
                	if (topTitle.equals("校园趣事"))
                		i = 1;
                	else if (topTitle.equals("校园新闻"))
                		i = 2;
                	else if (topTitle.equals("社团活动"))
                		i = 3;
                	else if (topTitle.equals("校园讲座"))
                		i = 4;
                	
                	//请求
                	String headRefreshMSG = "HEAD_REFRESH" + i;
         	
                	//创建Socket，连接到服务器
    				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
    				
    				ObjectInputStream in = new ObjectInputStream(
    						client.getInputStream()); //从服务器读数据
    				ObjectOutputStream out = new ObjectOutputStream(
    						client.getOutputStream()); //向服务器写数据
    				
    				//向服务器写下拉刷新
    				out.writeObject(headRefreshMSG);
    				out.flush();

    				//读取服务器返回的信息
    				result = (SchoolInfoMSGList)in.readObject();

    				//关闭输入输出流
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
  
        //这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中  
        //根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值  
        @Override  
        protected void onPostExecute(SchoolInfoMSGList result) {  
        	//数据
        	List<SchoolInfoMSG> list;
        	if(result == null) {
        		Toast.makeText(MainPage.this, "刷新失败！", Toast.LENGTH_SHORT).show();
        	}
        	else {
        		list = result.getList();
        		if(schoolListView.isHeaderShown() || clickToRefresh) {
    	            //在头部增加新添内容     
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
    	    	        
    	    	        //添加三张分享的图片
    	    	        List<String> photos = list.get(i).getPhotos();
    	    	        for(int j = 0; j < photos.size(); j++) {
    	    	        	if(photos.get(j).equals("NULL")) {
    	    	        		map.put("sharedimg" + (j + 1), null);
    	    	        	}
    	    	        	else {
    	    	        		byte[] tempb = Base64Coder.decodeLines(photos.get(j));
    	    	        		Bitmap dBitmap = BitmapFactory.decodeByteArray(tempb, 0, tempb.length);
    	    	        		map.put("sharedimg" + (j + 1), dBitmap);
    	    	        	}
    	    	        }
    	    	        
    	    	        //添加新鲜事id
    	    	        map.put("id", Integer.toString(list.get(i).getID()));
    	    	        //姓名而非昵称
    	    	        map.put("username", list.get(i).getName());
    	    	        
    	    	        //评论 赞 踩
    	    	        map.put("commentNum", Integer.toString(list.get(i).getCommnetNum()));
    	    	        map.put("likeNum", Integer.toString(list.get(i).getLikeNum()));
    	    	        map.put("dislikeNum", Integer.toString(list.get(i).getDislikeNum()));
    	    	        
    	    	        
    	    	        listItems.addFirst(map);
            		}

            	}
            	else if(schoolListView.isFooterShown()) {
            		//在尾部增加新添内容  
            		;
            	}
        	}
        	
              
            //通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合  
            schoolListViewAdapter.notifyDataSetChanged();  
            // Call onRefreshComplete when the list has been refreshed.  
            schoolListView.onRefreshComplete();  
            
            //完成刷新后把这个变量reset
            clickToRefresh = false;
  
            super.onPostExecute(result);  
        }  
    }
    
    //表示按下按键的监听函数
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		Intent intent = new Intent();
        	intent.setClass(MainPage.this,Exit.class);
        	startActivity(intent);
        	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    	}
    	return false;
    }
    
    //表示主页面中的“新鲜事”按钮监听函数
    public void sendMessage(View v) { 
    	startActivity(new Intent(MainPage.this, SendMessage.class));
    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    
   /**
    * 表示今日天气的监听事件
    */
    public void GetTodayWeather(View v) {
    	//Toast.makeText(MainPage.this, "点击了今日天气", Toast.LENGTH_SHORT).show();
    	startActivity(new Intent(MainPage.this, ChooseCity.class));
    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    
    /**
     * 进入搜索图书页面
     */
    public void GoSearchView(View v) { 
    	startActivity(new Intent(MainPage.this, SearchBook.class));
    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    
    /**
     * 设置用户的头像
     */
    public void SetUserHeadImg(View v) {
    	//Toast.makeText(MainPage.this, "设置头像", Toast.LENGTH_SHORT).show();
    	new AlertDialog.Builder(this)
    			.setTitle("更换头像")
    			.setNegativeButton("相册", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, 1);
						overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
					}
				})
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						//下面这句指定调用相机拍照后的照片存储的路径
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "userHeadImg.jpg")));
						startActivityForResult(intent, 2);
						overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
					}
				})
				.show();
    }
    /*设置头像相关函数*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	//从相册直接获取
    	case 1:
    		if(data != null)
    			startPhotoZoom(data.getData());
    		break;
    	// 如果是调用相机拍照时  
    	case 2:
    		File temp = new File(Environment.getExternalStorageDirectory() + "/userHeadImg.jpg");
    		startPhotoZoom(Uri.fromFile(temp));
    	// 取得裁剪后的图片 
    	case 3:
    		//非空判断大家一定要验证，如果不验证的话，  
            //在剪裁之后如果发现不满意，要重新裁剪，丢弃  
            //当前功能时，会报NullException
    		if(data != null) {
    			setPicToView(data);
    		}
    		break;
    	//从HomePopDialog传递过来的参数，表示点击了哪一个板块
    	case 100:
    		if(resultCode == 101) {
    			TextView topTitle4 = (TextView) findViewById(R.id.top_title_4);
    			topTitle4.setText(data.getExtras().getString("title"));
    			
    			//新建手势进行下拉刷新
    			clickToRefresh = true;
    			MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
    					SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -200, 0);
    			if (schoolListView.isBeingDraggedToTrue()) {
    				schoolListView.onTouchEvent(event);			
    			}		
    		}
		default:
			break;
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    /*裁剪图片方法实现*/
    public void startPhotoZoom(Uri uri) {  
        Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, "image/*");  
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪  
        intent.putExtra("crop", "true");  
        // aspectX aspectY 是宽高的比例  
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        // outputX outputY 是裁剪图片宽高  
        intent.putExtra("outputX", 150);  
        intent.putExtra("outputY", 150);  
        intent.putExtra("return-data", true);  
        startActivityForResult(intent, 3);  
        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    /*保存裁剪之后的图片数据*/
    private void setPicToView(Intent picdata) {  
        Bundle extras = picdata.getExtras();  
        if (extras != null) {  
            PHOTO = extras.getParcelable("data");  
            STATIC_DRAWABLE = new BitmapDrawable(PHOTO);  
              
            /**  
             * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上  
             * 传到服务器，QQ头像上传采用的方法跟这个类似  
             */ 
              
            ByteArrayOutputStream stream = new ByteArrayOutputStream();  
            PHOTO.compress(Bitmap.CompressFormat.JPEG, 60, stream);  
            byte[] b = stream.toByteArray();  
            // 将图片流以字符串形式存储下来  
              
            String tp = new String(Base64Coder.encodeLines(b));  
            /*
                        这个地方可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，  
       	        服务器处理的方法是服务器那边的事了
              
         	如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换  
                        为我们可以用的图片类型就OK啦
            Bitmap dBitmap = BitmapFactory.decodeFile(tp);  
            Drawable drawable = new BitmapDrawable(dBitmap);  
            */ 

            //ImageView userHeadImgImageView = (ImageView)findViewById(R.id.user_head_img_imageView);
            //userHeadImgImageView.setBackgroundDrawable(drawable);
            //更改头像
            //userHeadImgImageView.setImageDrawable(drawable);
            
            //把更改的图片发送到后台，更新后台数据库
            new SetUserHeadImgOperation().execute(tp);
        }  
    } 
    //更改头像的类
    public class SetUserHeadImgOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	    	
	    	
	    	//构建传递到服务器的消息
	    	SetUserHeadImgMSG msg = new SetUserHeadImgMSG();
	    	msg.setName(name);
	    	msg.setHeadimg(arg0[0]);
	    	
	    	//从服务器返回的结果
	    	String result = null;
	    	try {
				//创建Socket，连接到服务器
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据
				
				//向服务器写登录数据
				out.writeObject(msg);
				out.flush();
				
				//从服务器读返回消息
				result = (String)in.readObject();
				
				//关闭输入输出流
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
    			Toast.makeText(MainPage.this, "网络错误！", Toast.LENGTH_SHORT).show();
    		}
    		else if(result.equals("SUCCESS")) {
    			Toast.makeText(MainPage.this, "设置头像成功！", Toast.LENGTH_SHORT).show();
    			ImageView userHeadImgImageView = (ImageView)findViewById(R.id.user_head_img_imageView);
                userHeadImgImageView.setImageDrawable(STATIC_DRAWABLE);
                
                //更改内部数据库的头像
                ByteArrayOutputStream stream = new ByteArrayOutputStream();  
                PHOTO.compress(Bitmap.CompressFormat.JPEG, 60, stream);  
                byte[] b = stream.toByteArray();  
                // 将图片流以字符串形式存储下来                  
                String tp = new String(Base64Coder.encodeLines(b));
                
                //数据库更新操作
                SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
                infoDB.execSQL("update user set headimg=" + "'" + tp + "'" + "where id=1");
    	    	infoDB.close();	 
    		}
    		super.onPostExecute(result);
    	}
    }
    
    /**
     * 设置用户的昵称
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
    		.setTitle("更改昵称")
    		.setView(nicknameEditText)
    		.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					setNickname = nicknameEditText.getText().toString();
					setNickname = setNickname.trim();
					if(setNickname.equals("")) {
						Toast.makeText(MainPage.this, "输入不能为空！", Toast.LENGTH_SHORT).show();
					}
					else {
						new SetUserNicknameOperation().execute(setNickname);
					}
				}
			})
    		.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			})
    		.show();
    }
    
    //更改昵称的类
    public class SetUserNicknameOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
	    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
	    	cursor.moveToFirst();
	    	String name = cursor.getString(1);
	    	cursor.close();
	    	infoDB.close();	    	
	    	
	    	//构建传递到服务器的消息
	    	SetUserNicknameMSG msg = new SetUserNicknameMSG();
	    	msg.setName(name);
	    	msg.setNickname(arg0[0]);
	    	
	    	//从服务器返回的结果
	    	String result = null;
	    	try {
				//创建Socket，连接到服务器
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); //向服务器写数据
				
				//向服务器写登录数据
				out.writeObject(msg);
				out.flush();
				
				//从服务器读返回消息
				result = (String)in.readObject();
				
				//关闭输入输出流
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
    			Toast.makeText(MainPage.this, "网络错误！", Toast.LENGTH_SHORT).show();
    		}
    		else if(result.equals("SUCCESS")) {
    			Toast.makeText(MainPage.this, "设置昵称成功！", Toast.LENGTH_SHORT).show();
                TextView userNicknameTextView = (TextView)findViewById(R.id.user_nickname_textView);
                userNicknameTextView.setText(setNickname);
                
                //更改内部数据库的昵称       
                //数据库更新操作
                SQLiteDatabase infoDB = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
                infoDB.execSQL("update user set nickname=" + "'" + setNickname + "'" + "where id=1");
    	    	infoDB.close();	 
    		}
    		super.onPostExecute(result);
    	}
    }
    
    /**
     * 主页上部分类按钮菜单
     * @param paramView
     */
    public void HomeTopMenu(View paramView)
    {
    	startActivityForResult(new Intent(this, HomePopDialog.class), 100);
    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    
    /**
     * 我的好友列表
     */
    public void GetMyFriendsListBtn(View v) {
    	startActivity(new Intent(MainPage.this, MyFriendsListActivity.class));
    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    
    /**
     * 消息中心按钮
     */
    public void messageCenterBtn(View v) {
    	startActivity(new Intent(MainPage.this, MessageCenter.class));
    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    
    /**
     * 周围的人按钮
     */
    public void peopleAroundBtn(View v) {
    	startActivity(new Intent(MainPage.this, PeopleAround.class));
    	overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
    }
    
    /**
     * 更新UI的handler，用于处理外界发送来的聊天消息
     */
    public static Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		if(msg.what == 0) {	
    			//如果此页面没有进入过，那么更新数据集合就会出错，应该判断当
    			//前处于哪一个activity然后再通知适配器数据集合发生了改变
    			ActivityManager activityManager = (ActivityManager) MainPage.instance.getSystemService(Context.ACTIVITY_SERVICE);  
    	        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName(); 
    			if (runningActivity.equals("com.funnycampus.ui.ChatListActivity"))
    				ChatListActivity.chatListAdapter.notifyDataSetChanged();
    			
    			Toast.makeText(MainPage.instance, "收到消息！", Toast.LENGTH_SHORT).show();
    		}
    		else if (msg.what == 1) {
    			Toast.makeText(MainPage.instance, "获取聊天用户信息失败！", Toast.LENGTH_SHORT).show();
    		}
    	}
    };
}
