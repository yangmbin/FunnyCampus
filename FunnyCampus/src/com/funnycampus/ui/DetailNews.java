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
	//��������
	private String link = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_news);
		
        
        //��ȡ��������
        Intent intent = this.getIntent();
        link = intent.getStringExtra("link");
        
        startActivity(new Intent(DetailNews.this, LoadingActivity.class));
        overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
        new LoadingNewsOperation().execute();
    }  
    
	//��ȡ��ϸ���ŵ��첽����
	public class LoadingNewsOperation extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... arg0) {
			//���ص�����
			ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
			
			try {
				/**
				 * �����������ȡ����
				 */
				Document doc = Jsoup.connect(link).get();
				Element content = doc.getElementsByClass("cont").first();
				//�������ݣ����ʵ�����
				String news = content.text().toString();
				
				//ȥ��ǰ��ı��ⲿ��
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
				
				//��ȡͼƬ���Ӻ�ͼƬ����
				Elements link2desc = content.getElementsByTag("img");

				ArrayList<String> imgLink = new ArrayList<String>();
				ArrayList<String> imgDesc = new ArrayList<String>();
				for(Element element : link2desc) {
					imgLink.add(element.attr("abs:src"));
					imgDesc.add(element.parent().text());
				}
				
				//���������е�ͼƬ����ȥ��
				for(int i = 0; i < imgLink.size(); i++) {
					//System.out.println(imgLink.get(i) + ", " + imgDesc.get(i));
					news = news.replace(imgDesc.get(i), "");
				}
				//System.out.println(news);
				
				//���������ݷ���result
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("type", "text");
				map1.put("value", news);
				result.add(map1);
				
				if(imgLink.size() > 0) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("type", "text");
					map.put("value", "\n\n���ͼƬ��\n");
					result.add(map);
				}
				
				//��ͼƬ���Ӻ���������result
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
				Toast.makeText(DetailNews.this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			else {			
		        //��ȡ�Զ������������  
		        NewsTextView view = (NewsTextView) findViewById(R.id.news_textview);  
		        //����ConstomTextView�Զ����setText����  
		        view.setText(result);  
		        //Toast.makeText(DetailNews.this, result.get(0).get("value"), Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
	}
	
	//��д���ؼ�
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		if (keyCode == KeyEvent.KEYCODE_BACK) {
  			finish();
  			overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
 		
  			return true;
  		}
  		return super.onKeyDown(keyCode, event);
  	}
  	
    //���ϽǷ��ذ�ť
  	public void BtnBack(View v) {
  		finish();
  		overridePendingTransition(anim.left_to_mid, anim.mid_to_right);
  	}
}
