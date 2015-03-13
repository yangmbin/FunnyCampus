package com.funnycampus.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.funnycampus.userDefined.NewsTextView;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DetailNews extends Activity {
	//新闻链接
	private String link = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_news);
		
        
        //获取新闻链接
        Intent intent = this.getIntent();
        link = intent.getStringExtra("link");
        
        startActivity(new Intent(DetailNews.this, LoadingActivity.class));
        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
        new LoadingNewsOperation().execute();
    }  
    
	//获取详细新闻的异步操作
	public class LoadingNewsOperation extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... arg0) {
			//返回的数据
			ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
			
			try {
				/**
				 * 网络操作，获取内容
				 */
				Document doc = Jsoup.connect(link).get();
				Element content = doc.getElementsByClass("cont").first();
				//正文内容，做适当处理
				String news = content.text().toString();
				
				//去掉前面的标题部分
				int index = 0, count = 0;
				for(index = 0; index < news.length(); index++) {
					if(news.charAt(index) == ' ')
						count++;
					if(count == 10)
						break;
				}
				news = news.substring(index + 1);
				news.trim();
				news = "\t\t" + news;
				news = news.replace(" ", "\n\t\t");
				
				//System.out.println(news);
				//System.out.println("success");
				
				//获取图片链接和图片描述
				Elements link2desc = content.getElementsByTag("img");

				ArrayList<String> imgLink = new ArrayList<String>();
				ArrayList<String> imgDesc = new ArrayList<String>();
				for(Element element : link2desc) {
					imgLink.add(element.attr("abs:src"));
					imgDesc.add(element.parent().text());
				}
				
				//把主内容中的图片描述去掉
				for(int i = 0; i < imgLink.size(); i++) {
					//System.out.println(imgLink.get(i) + ", " + imgDesc.get(i));
					news = news.replace(imgDesc.get(i), "");
				}
				//System.out.println(news);
				
				//把正文内容放入result
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("type", "text");
				map1.put("value", news);
				result.add(map1);
				
				if(imgLink.size() > 0) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("type", "text");
					map.put("value", "\n\n相关图片：\n");
					result.add(map);
				}
				
				//把图片链接和描述放入result
				for(int i = 0; i < imgLink.size(); i++) {
					HashMap<String, String> map2 = new HashMap<String, String>();
					map2.put("type", "image");
					map2.put("value", imgLink.get(i));
					result.add(map2);
					
					HashMap<String, String> map3 = new HashMap<String, String>();
					map3.put("type", "imgText");
					map3.put("value", imgDesc.get(i));
					result.add(map3);
				}
			}
			catch(Exception e) {
				result = null;
			}
			return result;
		}
		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			LoadingActivity.instance.finish();
			if(result == null) {
				Toast.makeText(DetailNews.this, "加载失败！", Toast.LENGTH_SHORT).show();
			}
			else {			
		        //获取自定义组件的引用  
		        NewsTextView view = (NewsTextView) findViewById(R.id.news_textview);  
		        //调用ConstomTextView自定义的setText方法  
		        view.setText(result);  
		        //Toast.makeText(DetailNews.this, result.get(0).get("value"), Toast.LENGTH_LONG).show();
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
