package com.funnycampus.adapter;

import java.util.LinkedList;
import java.util.Map;

import com.yangmbin.funnycampus.R;

import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyFriendsListViewAdapter extends BaseAdapter {
	private Context context;
	private LinkedList<Map<String, Object>> listItems;
	private LayoutInflater layoutInflater;
	
	public final class ViewHolder {
		public ImageView headimg;
		public TextView name;
		public TextView nickname;
	}
	
	public MyFriendsListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems) {
		this.context = context;
		this.listItems = listItems;
		this.layoutInflater = LayoutInflater.from(context);
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
	public long getItemId(int location) {
		return 0;
	}

	@Override
	public View getView(int location, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.listview_myfriendslist_item, null);
			
			holder.headimg = (ImageView) convertView.findViewById(R.id.listview_myfriendslist_item_headimg);
			holder.name = (TextView) convertView.findViewById(R.id.listview_myfriendslist_item_name);
			holder.nickname = (TextView) convertView.findViewById(R.id.listview_myfriendslist_item_nickname);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.headimg.setImageBitmap((Bitmap) listItems.get(location).get("headimg"));
		holder.name.setText((String) listItems.get(location).get("name"));
		holder.nickname.setText((String) listItems.get(location).get("nickname"));
		
		return convertView;
	}

}
