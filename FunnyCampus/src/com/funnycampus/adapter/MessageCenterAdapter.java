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

public class MessageCenterAdapter extends BaseAdapter {

	private Context context;
	private LinkedList<Map<String, Object>> listItems;
	private LayoutInflater layoutInflater;
	
	//自定义视图
	public final class ViewHolder {
		ImageView headimgImageView;
		TextView nicknameTextView, lastmessageTextView, usernameTextView;
	}
	//构造函数
	public MessageCenterAdapter(Context context, LinkedList<Map<String, Object>> listItems) {
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
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int location, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.listview_message_center_item, null);
			
			holder.headimgImageView = (ImageView) convertView.findViewById(R.id.listview_message_center_item_headimg);
			holder.nicknameTextView = (TextView) convertView.findViewById(R.id.listview_message_center_item_nickname);
			holder.lastmessageTextView = (TextView) convertView.findViewById(R.id.listview_message_center_item_lastmessage);
			holder.usernameTextView = (TextView) convertView.findViewById(R.id.listview_message_center_item_username);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.headimgImageView.setImageBitmap((Bitmap) listItems.get(location).get("headimg"));
		holder.nicknameTextView.setText((String) listItems.get(location).get("nickname"));
		holder.lastmessageTextView.setText((String) listItems.get(location).get("lastmessage"));
		holder.usernameTextView.setText((String) listItems.get(location).get("username"));
		
		return convertView;
	}

}
