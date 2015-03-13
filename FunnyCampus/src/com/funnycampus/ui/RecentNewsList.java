package com.funnycampus.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class RecentNewsList extends Activity{
	private ListView recentNewsListView;
	private SimpleAdapter adapter;
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recent_news_list);
		
		adapter = new SimpleAdapter(this, data, R.layout.recent_news_list_item,
				new String[] {"title", "link", "time"},
				new int[] {R.id.recent_news_list_item_title,
						   R.id.recent_news_list_item_link,
						   R.id.recent_news_list_item_time});
		
		recentNewsListView = (ListView)findViewById(R.id.recent_news_listview);
		recentNewsListView.setAdapter(adapter);
		
		startActivity(new Intent(RecentNewsList.this, LoadingActivity.class));
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
		new GetSYSUNewsList().execute();
		
		//对每条新闻item设置监听函数
		recentNewsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HashMap<String, String> item = (HashMap<String, String>)arg0.getItemAtPosition(arg2);
				//Toast.makeText(RecentNewsList.this, ""+arg2+"", Toast.LENGTH_SHORT).show();
				
				//转到详细信息页面，并传递链接
				Intent intent = new Intent(RecentNewsList.this, DetailNews.class);
				intent.putExtra("link", item.get("link"));
				startActivity(intent);
				overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
			}
		});
	}
	
	
	
	 /**
     * 表示用来获取 中大新闻 列表的类
     */
    public class GetSYSUNewsList extends AsyncTask<Void, Void, List<Map<String, String>>> {
		@Override
		protected List<Map<String, String>> doInBackground(Void... arg0) {
			List<Map<String, String>> result = new ArrayList<Map<String, String>>();
			try {
				Document doc = Jsoup.connect("http://news2.sysu.edu.cn/news01/index.htm").get();
				Element content = doc.getElementsByClass("lan5").first();
				Elements links = content.getElementsByTag("a");
				Elements times = content.getElementsByTag("span");
				
				//for(Element e : links)
					//System.out.println(e.text());	
				//for(Element e : links)
					//System.out.println(e.attr("abs:href"));		
				//for(Element e : times)
					//System.out.println(e.text());
				
				
				for(int i = 0; i < links.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("title", links.get(i).text());
					map.put("link", links.get(i).attr("abs:href"));
					map.put("time", times.get(i).text());
					result.add(map);
				}
				
				
			}
			catch(Exception e) {
				result = null;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(List<Map<String, String>> result) {
			LoadingActivity.instance.finish();
			if(result == null) {
				Toast.makeText(RecentNewsList.this, "加载失败！", Toast.LENGTH_SHORT).show();
			}
			else {
				for(int i = 0; i < result.size(); i++) {
					data.add(result.get(i));
				}
				adapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
    	
    }
    
    //重写返回键
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		if (keyCode == KeyEvent.KEYCODE_BACK) {
  			finish();
  			overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
 		
  			return true;
  		}
  		return super.onKeyDown(keyCode, event);
  	}
  	
  	//左上角返回按钮
  	public void BtnBack(View v) {
  		finish();
  		overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
  	}
}
