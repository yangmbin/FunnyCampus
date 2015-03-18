package com.funnycampus.adapter;

import java.util.LinkedList;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangmbin.funnycampus.R;

public class CommentsListViewAdapter extends BaseAdapter {
	
	private Context context; //上下文
	private LinkedList<Map<String, Object>> listItems; //数据集合
	private LayoutInflater layoutInflater; //视图容器
	
	//自定义控件集合
	public final class ViewHolder1 {
		public TextView name, time, content;
		public ImageView img_head, sharedimg1, sharedimg2, sharedimg3;
		public LinearLayout sharedImgLinearLayout;
		public TextView commentNum, likeNum, dislikeNum;
	}
	//自定义控件集合
	public final class ViewHolder2 {
		public TextView nickname, time, comment;
		public ImageView head_img;
	}
	
	//构造函数
	public CommentsListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems)
	{
		this.context = context;
		this.listItems = listItems;
		layoutInflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//视图类型
		int type = Integer.parseInt((String) listItems.get(position).get("type")); 
		
		ViewHolder1 viewHolder1 = null;
		ViewHolder2 viewHolder2 = null;
		if(convertView == null) {
			viewHolder1 = new ViewHolder1();
			viewHolder2 = new ViewHolder2();
			if(type == 0) {
				convertView = layoutInflater.inflate(R.layout.listview_comments_item1, null);
				//获取控件对象
				viewHolder1.name = (TextView) convertView.findViewById(R.id.listview_comments_item1_name);
				viewHolder1.time = (TextView) convertView.findViewById(R.id.listview_comments_item1_time);
				viewHolder1.content = (TextView) convertView.findViewById(R.id.listview_comments_item1_content);
				viewHolder1.img_head = (ImageView) convertView.findViewById(R.id.listview_comments_item1_img_head);
				viewHolder1.sharedimg1 = (ImageView) convertView.findViewById(R.id.listview_comments_item1_photo0);
				viewHolder1.sharedimg2 = (ImageView) convertView.findViewById(R.id.listview_comments_item1_photo1);
				viewHolder1.sharedimg3 = (ImageView) convertView.findViewById(R.id.listview_comments_item1_photo2);	
				viewHolder1.sharedImgLinearLayout = (LinearLayout) convertView.findViewById(R.id.listview_comments_item1_photo_linearlayout);
				viewHolder1.commentNum = (TextView) convertView.findViewById(R.id.listview_comments_item1_commentNum_textview);
				viewHolder1.likeNum = (TextView) convertView.findViewById(R.id.listview_comments_item1_likeNum_textview);
				viewHolder1.dislikeNum = (TextView) convertView.findViewById(R.id.listview_comments_item1_dislike_textview);
				
				convertView.setTag(viewHolder1);
			}
			else if(type == 1) {
				convertView = layoutInflater.inflate(R.layout.listview_comments_item2, null);
				//获取控件对象
				viewHolder2.head_img = (ImageView) convertView.findViewById(R.id.listview_comments_item2_head_img);
				viewHolder2.nickname = (TextView) convertView.findViewById(R.id.listview_comments_item2_nickname);
				viewHolder2.time = (TextView) convertView.findViewById(R.id.listview_comments_item2_time);
				viewHolder2.comment = (TextView) convertView.findViewById(R.id.listview_comments_item2_comment);
				
				convertView.setTag(viewHolder2);
			}		
		}
		else {
			if(type == 0)
				viewHolder1 = (ViewHolder1)convertView.getTag();
			else if(type == 1) 
				viewHolder2 = (ViewHolder2)convertView.getTag();
		}
		
		if(type == 0) {
			viewHolder1.name.setText((String) listItems.get(position).get("name"));
			viewHolder1.time.setText((String) listItems.get(position).get("time"));
			viewHolder1.content.setText((String) listItems.get(position).get("content"));
			viewHolder1.img_head.setImageBitmap((Bitmap) listItems.get(position).get("img_head"));
			viewHolder1.sharedimg1.setImageBitmap((Bitmap) listItems.get(position).get("sharedimg1"));
			viewHolder1.sharedimg2.setImageBitmap((Bitmap) listItems.get(position).get("sharedimg2"));
			viewHolder1.sharedimg3.setImageBitmap((Bitmap) listItems.get(position).get("sharedimg3"));
			viewHolder1.commentNum.setText((String) listItems.get(position).get("commentNum"));
			viewHolder1.likeNum.setText((String) listItems.get(position).get("likeNum"));
			viewHolder1.dislikeNum.setText((String) listItems.get(position).get("dislikeNum"));
			
			//如果没有照片分享，则隐藏布局
	        if (listItems.get(position).get("sharedimg1") == null)
	        	viewHolder1.sharedImgLinearLayout.setVisibility(View.GONE);
		}
		else if(type == 1) {
			viewHolder2.head_img.setImageBitmap((Bitmap) listItems.get(position).get("head_img"));
			viewHolder2.nickname.setText((String) listItems.get(position).get("nickname"));
			viewHolder2.time.setText((String) listItems.get(position).get("time"));
			viewHolder2.comment.setText((String) listItems.get(position).get("comment"));
		}
		
		
		return convertView;
	}
	
	//创建不同视图的ListView，特别需要重写下面2个函数
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	@Override
	public int getItemViewType(int position) {
		int type = super.getItemViewType(position);
		type = Integer.parseInt((String) listItems.get(position).get("type"));
		
		return type;
	}
}
