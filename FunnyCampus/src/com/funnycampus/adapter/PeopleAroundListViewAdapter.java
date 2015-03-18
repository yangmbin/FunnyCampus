package com.funnycampus.adapter;

import java.util.LinkedList;
import java.util.Map;

import com.funnycampus.adapter.MyFriendsListViewAdapter.ViewHolder;
import com.yangmbin.funnycampus.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleAroundListViewAdapter extends BaseAdapter {
	private Context context;
	private LinkedList<Map<String, Object>> listItems;
	private LayoutInflater layoutInflater;
	
	public final class ViewHolder {
		public ImageView headimg;
		public TextView username;
		public TextView nickname;
		public TextView distance;
	}
	
	public PeopleAroundListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems) {
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
			convertView = layoutInflater.inflate(R.layout.listview_people_around_item, null);
			
			holder.headimg = (ImageView) convertView.findViewById(R.id.listview_people_around_item_headimg);
			holder.username = (TextView) convertView.findViewById(R.id.listview_people_around_item_username);
			holder.nickname = (TextView) convertView.findViewById(R.id.listview_people_around_item_nickname);
			holder.distance = (TextView) convertView.findViewById(R.id.listview_people_around_item_distance);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.headimg.setImageBitmap((Bitmap) listItems.get(location).get("headimg"));
		holder.username.setText((String) listItems.get(location).get("username"));
		holder.nickname.setText((String) listItems.get(location).get("nickname"));
		holder.distance.setText((String) listItems.get(location).get("distance"));
		
		return convertView;
	}

}
