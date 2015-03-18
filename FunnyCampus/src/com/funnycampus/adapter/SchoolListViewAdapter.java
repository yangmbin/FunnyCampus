package com.funnycampus.adapter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;

import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.funnycampus.socket.BackCommentMSGList;
import com.funnycampus.socket.IP_PORT;
import com.funnycampus.socket.LikeAndDislikeMSG;
import com.funnycampus.ui.AddFriendAndChatDialog;
import com.funnycampus.ui.HomeDetailMessage;
import com.funnycampus.ui.LoadingActivity;
import com.funnycampus.ui.MainPage;
import com.funnycampus.utils.Utils;
import com.yangmbin.funnycampus.R;

public class SchoolListViewAdapter extends BaseAdapter {
	private Context context;                        //运行上下文   
    private LinkedList<Map<String, Object>> listItems;    //信息集合   
    private LayoutInflater listContainer;           //视图容器  
    
    public final class ListItemView{                //自定义控件集合     
        public ImageView img_head;     
        public TextView name;     
        public TextView time;
        public TextView content;    
        
        public ImageView sharedimg1, sharedimg2, sharedimg3;
        public LinearLayout sharedImgLinearLayout;
        
        public LinearLayout btnCommentLayout;
        public LinearLayout btnDislikeLayout;
        public LinearLayout btnLikeLayout;
        
        public TextView commentNum;
        public TextView likeNum;
        public TextView dislikeNum;
        
        public TextView id; //存放条目的id，隐藏属性
        public TextView username;
        
    } 
    
    public SchoolListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems)
    {
    	this.context = context;
    	this.listItems = listItems;
    	listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub 
        //自定义视图   
        ListItemView  listItemView = null; 
        if (convertView == null) {   
            listItemView = new ListItemView();    
            //获取listview_school_item布局文件的视图   
            convertView = listContainer.inflate(R.layout.listview_home_item, null); 
            
            //获取控件对象  
            listItemView.img_head = (ImageView)convertView.findViewById(R.id.listview_school_item_img_head);
            listItemView.name = (TextView)convertView.findViewById(R.id.listview_school_item_name);
            listItemView.content = (TextView)convertView.findViewById(R.id.listview_school_item_content);
            listItemView.time = (TextView)convertView.findViewById(R.id.listview_school_item_time);
            
            listItemView.sharedimg1 = (ImageView) convertView.findViewById(R.id.listview_school_item_photo0);
            listItemView.sharedimg2 = (ImageView) convertView.findViewById(R.id.listview_school_item_photo1);
            listItemView.sharedimg3 = (ImageView) convertView.findViewById(R.id.listview_school_item_photo2);
            
            listItemView.sharedImgLinearLayout = (LinearLayout) convertView.findViewById(R.id.listview_school_item_photo_linearlayout);
            
            listItemView.btnCommentLayout = (LinearLayout) convertView.findViewById(R.id.listview_school_item_btnComment);
            listItemView.btnDislikeLayout = (LinearLayout) convertView.findViewById(R.id.listview_school_item_btnDislike);
            listItemView.btnLikeLayout = (LinearLayout) convertView.findViewById(R.id.listview_school_item_btnLike);
            
            listItemView.commentNum = (TextView) convertView.findViewById(R.id.listview_school_item_comment_textview);
            listItemView.likeNum = (TextView) convertView.findViewById(R.id.listview_school_item_like_textview);
            listItemView.dislikeNum = (TextView) convertView.findViewById(R.id.listview_school_item_dislike_textview);
            
            listItemView.id = (TextView) convertView.findViewById(R.id.listview_school_item_id);
            listItemView.username = (TextView) convertView.findViewById(R.id.listview_school_item_username);
            
            //设置控件集到convertView   
            convertView.setTag(listItemView);
        }
        else {
        	listItemView = (ListItemView)convertView.getTag();
        }
        
        //设置文字和图片   
        //listItemView.img_head.setBackgroundResource((Integer) listItems.get(   
                //position).get("img_head"));  
        listItemView.img_head.setImageBitmap((Bitmap) listItems.get(position).get("img_head"));   
        listItemView.name.setText((String) listItems.get(position).get("name"));   
        listItemView.content.setText((String) listItems.get(position).get("content"));  
        listItemView.time.setText((String) listItems.get(position).get("time")); 
        
        listItemView.sharedimg1.setImageBitmap((Bitmap) listItems.get(position).get("sharedimg1"));
        listItemView.sharedimg2.setImageBitmap((Bitmap) listItems.get(position).get("sharedimg2"));
        listItemView.sharedimg3.setImageBitmap((Bitmap) listItems.get(position).get("sharedimg3"));
        
        listItemView.commentNum.setText((String) listItems.get(position).get("commentNum"));
        listItemView.likeNum.setText((String) listItems.get(position).get("likeNum"));
        listItemView.dislikeNum.setText((String) listItems.get(position).get("dislikeNum"));
        
        listItemView.id.setText((String) listItems.get(position).get("id"));
        listItemView.username.setText((String) listItems.get(position).get("username"));
        
        
        //如果没有照片分享，则隐藏布局
        if (listItems.get(position).get("sharedimg1") == null)
        	listItemView.sharedImgLinearLayout.setVisibility(View.GONE);
        
        //对 《评论 喜欢 不喜欢》进行监听
        listItemView.btnCommentLayout.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainPage.instance, HomeDetailMessage.class);
				//获取点击条目的数据
				MainPage.clickedItemData = (Map<String, Object>) getItem(position);
				intent.putExtra("clickPosition", position);
				context.startActivity(intent);
			}
		});
        listItemView.btnDislikeLayout.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				LikeAndDislikeMSG dislikeMSG = new LikeAndDislikeMSG();
				dislikeMSG.setFreshNewsID(Integer.parseInt((String) ((Map<String, Object>) getItem(position)).get("id")));
				dislikeMSG.setUsername((String) Utils.getInstance().getCurrentUserInfo().get("username"));
				dislikeMSG.setLikeOrdislike("dislike");
				dislikeMSG.setClickPosition(position);
				
				context.startActivity(new Intent(context, LoadingActivity.class));
				new LikeAndDislikeOperation().execute(dislikeMSG);
			}
		});
        listItemView.btnLikeLayout.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				LikeAndDislikeMSG likeMSG = new LikeAndDislikeMSG();
				likeMSG.setFreshNewsID(Integer.parseInt((String) ((Map<String, Object>) getItem(position)).get("id")));
				likeMSG.setUsername((String) Utils.getInstance().getCurrentUserInfo().get("username"));
				likeMSG.setLikeOrdislike("like");
				likeMSG.setClickPosition(position);
				
				context.startActivity(new Intent(context, LoadingActivity.class));
				new LikeAndDislikeOperation().execute(likeMSG);
			}
		});
        
        //对头像进行监听，跳转到添加好友和私信页面
        listItemView.img_head.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				//获取当前客户端用户名
				SQLiteDatabase infoDB = MainPage.instance.openOrCreateDatabase("info.db", Context.MODE_PRIVATE, null);
		    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
		    	cursor.moveToFirst();
		    	String username = cursor.getString(1);
		    	cursor.close();
		    	infoDB.close();	
		    	//点击自己头像不跳转
		    	if (!username.equals((String) listItems.get(position).get("username"))) {
					Intent intent = new Intent(context, AddFriendAndChatDialog.class);
					intent.putExtra("friend", (String) listItems.get(position).get("username"));
					intent.putExtra("nickname", (String) listItems.get(position).get("name"));
					Utils.getInstance().friendHeadimg = (Bitmap) listItems.get(position).get("img_head");
					context.startActivity(intent);
		    	}
			}
		});
        
        
		return convertView;
	}
	
	/*
	 * 表示赞和踩的异步类
	 */
	public class LikeAndDislikeOperation extends AsyncTask<LikeAndDislikeMSG, Void, String>
	{
		String str = null;
		int position;
		@Override
		protected String doInBackground(LikeAndDislikeMSG... arg0) {
			str = arg0[0].getLikeORdislike();
			position = arg0[0].getClickPosition();
			
			//后台返回的数据
			String result = null;
			try {
				/*创建Socket，连接到服务器*/
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				ObjectInputStream in = new ObjectInputStream(
						client.getInputStream()); //从服务器读数据
				ObjectOutputStream out = new ObjectOutputStream(
						client.getOutputStream()); //向服务器写数据
				/*向服务器写数据*/
				out.writeObject(arg0[0]);
				out.flush();
				
				/*读数据*/
				result = (String) in.readObject();
				
				/*关闭流和套接字*/
				in.close();
				out.close();
				client.close();
			} catch (Exception e) {
				result = null;
			}
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			LoadingActivity.instance.finish();
			if (result == null) {
				Toast.makeText(context, "连接服务器失败，点赞或踩失效！", Toast.LENGTH_SHORT).show();
			}
			else if (result.equals("SUCCESS")) {
				Toast.makeText(context, "成功！", Toast.LENGTH_SHORT).show();
				//更新赞或踩的数量
				if (str.equals("like")) {
					int likeNum = Integer.parseInt((String) MainPage.listItems.get(position).get("likeNum")) + 1;
					MainPage.listItems.get(position).put("likeNum", Integer.toString(likeNum));
					MainPage.schoolListViewAdapter.notifyDataSetChanged();
				}
				else {
					int dislikeNum = Integer.parseInt((String) MainPage.listItems.get(position).get("dislikeNum")) + 1;
					MainPage.listItems.get(position).put("dislikeNum", Integer.toString(dislikeNum));
					MainPage.schoolListViewAdapter.notifyDataSetChanged();
				}
			}
			else if (result.equals("FAILED")) {
				Toast.makeText(context, "已赞或踩过！", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
}
