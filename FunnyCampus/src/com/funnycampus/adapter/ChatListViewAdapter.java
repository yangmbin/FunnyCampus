package com.funnycampus.adapter;

import java.util.LinkedList;
import java.util.Map;

import com.yangmbin.funnycampus.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import dalvik.system.BaseDexClassLoader;

public class ChatListViewAdapter extends BaseAdapter{
	private Context context; //������
	private LinkedList<Map<String, Object>> listItems; //���ݼ���
	private LayoutInflater layoutInflater; //��ͼ����
	
	//�Զ���ؼ�����
	public final class ViewHolder {
		public TextView content;
		public ImageView headimg;
	}
	
	//���캯��
	public ChatListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems)
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
	public Object getItem(int location) {
		return listItems.get(location);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//��ͼ����
		int type = Integer.parseInt((String) listItems.get(position).get("type")); 
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			if(type == 0) {
				convertView = layoutInflater.inflate(R.layout.listview_chatlist_item1, null);
				
				holder.content = (TextView) convertView.findViewById(R.id.listview_chatlist_item1_content);
				holder.headimg = (ImageView) convertView.findViewById(R.id.listview_chatlist_item1_headimg);
				
				convertView.setTag(holder);
			}
			else if(type == 1) {
				convertView = layoutInflater.inflate(R.layout.listview_chatlist_item2, null);
				
				holder.content = (TextView) convertView.findViewById(R.id.listview_chatlist_item2_content);
				holder.headimg = (ImageView) convertView.findViewById(R.id.listview_chatlist_item2_headimg);
				
				convertView.setTag(holder);
			}
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.content.setText((String) listItems.get(position).get("content"));
		holder.headimg.setImageBitmap((Bitmap) listItems.get(position).get("headimg"));
		
		return convertView;
	}
	
	//������ͬ��ͼ��ListView���ر���Ҫ��д����2������
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
