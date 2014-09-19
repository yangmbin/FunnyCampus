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
import android.widget.TextView;

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
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub 
        //�Զ�����ͼ   
        ListItemView  listItemView = null; 
        if (convertView == null) {   
            listItemView = new ListItemView();    
            //��ȡlistview_school_item�����ļ�����ͼ   
            convertView = listContainer.inflate(R.layout.listview_school_item, null); 
            
            //��ȡ�ؼ�����  
            listItemView.img_head = (ImageView)convertView.findViewById(R.id.listview_school_item_img_head);
            listItemView.name = (TextView)convertView.findViewById(R.id.listview_school_item_name);
            listItemView.content = (TextView)convertView.findViewById(R.id.listview_school_item_content);
            listItemView.time = (TextView)convertView.findViewById(R.id.listview_school_item_time);
            
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
        
        listItemView.name.setText((String) listItems.get(position)   
                .get("name"));   
        listItemView.content.setText((String) listItems.get(position).get("content"));  
        listItemView.time.setText((String) listItems.get(position).get("time")); 
		return convertView;
	}

}
