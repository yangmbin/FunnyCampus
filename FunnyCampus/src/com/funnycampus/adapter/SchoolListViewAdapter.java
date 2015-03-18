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
	private Context context;                        //����������   
    private LinkedList<Map<String, Object>> listItems;    //��Ϣ����   
    private LayoutInflater listContainer;           //��ͼ����  
    
    public final class ListItemView{                //�Զ���ؼ�����     
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
        
        public TextView id; //�����Ŀ��id����������
        public TextView username;
        
    } 
    
    public SchoolListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems)
    {
    	this.context = context;
    	this.listItems = listItems;
    	listContainer = LayoutInflater.from(context);   //������ͼ����������������   
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
        //�Զ�����ͼ   
        ListItemView  listItemView = null; 
        if (convertView == null) {   
            listItemView = new ListItemView();    
            //��ȡlistview_school_item�����ļ�����ͼ   
            convertView = listContainer.inflate(R.layout.listview_home_item, null); 
            
            //��ȡ�ؼ�����  
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
            
            //���ÿؼ�����convertView   
            convertView.setTag(listItemView);
        }
        else {
        	listItemView = (ListItemView)convertView.getTag();
        }
        
        //�������ֺ�ͼƬ   
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
        
        
        //���û����Ƭ���������ز���
        if (listItems.get(position).get("sharedimg1") == null)
        	listItemView.sharedImgLinearLayout.setVisibility(View.GONE);
        
        //�� ������ ϲ�� ��ϲ�������м���
        listItemView.btnCommentLayout.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainPage.instance, HomeDetailMessage.class);
				//��ȡ�����Ŀ������
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
        
        //��ͷ����м�������ת����Ӻ��Ѻ�˽��ҳ��
        listItemView.img_head.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				//��ȡ��ǰ�ͻ����û���
				SQLiteDatabase infoDB = MainPage.instance.openOrCreateDatabase("info.db", Context.MODE_PRIVATE, null);
		    	Cursor cursor = infoDB.query(true, "user", null, "id=1", null, null, null, null, null);
		    	cursor.moveToFirst();
		    	String username = cursor.getString(1);
		    	cursor.close();
		    	infoDB.close();	
		    	//����Լ�ͷ����ת
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
	 * ��ʾ�޺Ͳȵ��첽��
	 */
	public class LikeAndDislikeOperation extends AsyncTask<LikeAndDislikeMSG, Void, String>
	{
		String str = null;
		int position;
		@Override
		protected String doInBackground(LikeAndDislikeMSG... arg0) {
			str = arg0[0].getLikeORdislike();
			position = arg0[0].getClickPosition();
			
			//��̨���ص�����
			String result = null;
			try {
				/*����Socket�����ӵ�������*/
				Socket client = new Socket(IP_PORT.IP, IP_PORT.PORT);
				ObjectInputStream in = new ObjectInputStream(
						client.getInputStream()); //�ӷ�����������
				ObjectOutputStream out = new ObjectOutputStream(
						client.getOutputStream()); //�������д����
				/*�������д����*/
				out.writeObject(arg0[0]);
				out.flush();
				
				/*������*/
				result = (String) in.readObject();
				
				/*�ر������׽���*/
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
				Toast.makeText(context, "���ӷ�����ʧ�ܣ����޻��ʧЧ��", Toast.LENGTH_SHORT).show();
			}
			else if (result.equals("SUCCESS")) {
				Toast.makeText(context, "�ɹ���", Toast.LENGTH_SHORT).show();
				//�����޻�ȵ�����
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
				Toast.makeText(context, "���޻�ȹ���", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
}
