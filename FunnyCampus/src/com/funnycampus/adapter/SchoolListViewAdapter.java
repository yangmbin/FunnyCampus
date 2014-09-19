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
	private Context context;                        //运行上下文   
    private LinkedList<Map<String, Object>> listItems;    //信息集合   
    private LayoutInflater listContainer;           //视图容器  
    
    public final class ListItemView{                //自定义控件集合     
        public ImageView img_head;     
        public TextView name;     
        public TextView time;
        public TextView content;    
        
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
        //自定义视图   
        ListItemView  listItemView = null; 
        if (convertView == null) {   
            listItemView = new ListItemView();    
            //获取listview_school_item布局文件的视图   
            convertView = listContainer.inflate(R.layout.listview_school_item, null); 
            
            //获取控件对象  
            listItemView.img_head = (ImageView)convertView.findViewById(R.id.listview_school_item_img_head);
            listItemView.name = (TextView)convertView.findViewById(R.id.listview_school_item_name);
            listItemView.content = (TextView)convertView.findViewById(R.id.listview_school_item_content);
            listItemView.time = (TextView)convertView.findViewById(R.id.listview_school_item_time);
            
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
        
        listItemView.name.setText((String) listItems.get(position)   
                .get("name"));   
        listItemView.content.setText((String) listItems.get(position).get("content"));  
        listItemView.time.setText((String) listItems.get(position).get("time")); 
		return convertView;
	}

}
