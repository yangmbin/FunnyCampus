package com.funnycampus.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yangmbin.funnycampus.R;

public class SearchBook extends Activity {
	private String inputString; //输入的搜索内容
	private EditText inputEditText;
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_book);
		
		inputEditText = (EditText)findViewById(R.id.search_book_editText);
	}
	
	/**
	 * 搜索按钮
	 */
	public void SearchBookInfoBtn(View v) {
		inputString = inputEditText.getText().toString();
		inputString = inputString.trim();
		if("".equals(inputString)) {
			Toast.makeText(SearchBook.this, "输入不能为空！", Toast.LENGTH_SHORT).show();
		}
		else {
			startActivity(new Intent(SearchBook.this, LoadingActivity.class));
			new GetBookInfoFromDOUBAN().execute();
		}
	}
	
	/**
	 * 获取豆瓣数据的异步网络操作
	 */
	public class GetBookInfoFromDOUBAN extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			//返回的结果，JSON字串
			String result = null;
			try {
				HttpGet httpRequest = 
						new HttpGet("http://api.douban.com/v2/book/search?q=" + inputString + "&count=1");
				
				BasicHttpParams httpParams = new BasicHttpParams();
		        SchemeRegistry schemeRegistry = new SchemeRegistry();
		        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
				HttpClient httpClient = new DefaultHttpClient(cm, httpParams);
				
				HttpResponse httpResponse = httpClient.execute(httpRequest);
				/*
				StringBuffer sb = new StringBuffer();
				String line;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(httpResponse.getEntity().getContent()));
				while(null != (line = reader.readLine())) {
					sb.append(line);
				}
				
				result = sb.toString();
				*/
				result = EntityUtils.toString(httpResponse.getEntity());
			}
			catch(Exception e) {
				result = null;
			}
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			LoadingActivity.instance.finish();
			//Toast.makeText(SearchBook.this, result, Toast.LENGTH_LONG).show();
			if(result == null) {
				Toast.makeText(SearchBook.this, "网络错误！", Toast.LENGTH_SHORT).show();
			}
			else {
				JSONTokener jsonTokener = new JSONTokener(result);
				try {
					JSONObject bookInfo = (JSONObject)jsonTokener.nextValue();
					
					//判断搜索是否有结果
					if(bookInfo.getInt("total") == 0) {
						Toast.makeText(SearchBook.this, "无搜索结果返回！", Toast.LENGTH_SHORT).show();
					}
					else {
						JSONObject book = bookInfo.getJSONArray("books").getJSONObject(0);
						
						//图书名
						TextView title = (TextView)findViewById(R.id.book_name_textView);
						title.setText("《" + book.getString("title") + "》");
						
						//图书详情
						TextView detail = (TextView)findViewById(R.id.book_detail_textView);
						int length = book.getJSONArray("author").length();
						StringBuffer authors = new StringBuffer();
						for(int i = 0; i < length; i++)
							authors.append(book.getJSONArray("author").getString(i) + " ");
						detail.setText("评分：" + book.getJSONObject("rating").getString("average") + "/10\n" 
								+ "作者：" + authors.toString() + "\n"
								+ "页数：" + book.getString("pages") + "\n"
								+ "出版社：" + book.getString("publisher") + "\n"
								+ "出版时间：" + book.getString("pubdate") + "\n");
						
						//图书Tag
						StringBuffer tags = new StringBuffer();
						length = book.getJSONArray("tags").length();
						for(int i = 0; i < length; i++) 
							tags.append(book.getJSONArray("tags").getJSONObject(i).getString("name") + " ");
						TextView bookTags = (TextView)findViewById(R.id.book_tags_textView);
						bookTags.setText(tags.toString() + "\n");
						
						//作者
						TextView authorInfo = (TextView)findViewById(R.id.author_info_textView);
						authorInfo.setText(book.getString("author_intro") + "\n");
						
						//内容概要
						TextView contentIntro = (TextView)findViewById(R.id.content_summary_textView);
						contentIntro.setText(book.getString("summary"));
						
						//设置可见
						LinearLayout bookInfoLinearLayout = (LinearLayout)findViewById(R.id.book_info_linearlayout);
						bookInfoLinearLayout.setVisibility(0);
						
						//异步加载图片
						ImageView imageView = (ImageView)findViewById(R.id.book_pic_imageView);
						String Url = book.getString("image");
						Url.replace("\\", "");
						new DownloadPicThread(imageView, Url).start();
					}
				} catch (JSONException e) {
					Toast.makeText(SearchBook.this, "解析错误！", Toast.LENGTH_SHORT).show();
				}
			}
			super.onPostExecute(result);
		}
	}
	
	/**
	 * 异步加载图书封面的线程
	 */
	private class DownloadPicThread extends Thread {
		private ImageView imageView;
		private String Url;
		public DownloadPicThread(ImageView imageView, String Url) {
			super();
			this.imageView = imageView;
			this.Url = Url;
		}
		@Override 
		public void run() {
			Drawable drawable = null;
			try {
				drawable = Drawable.createFromStream(new URL(Url).openStream(), null);
			}
			catch(Exception e) {
			}
			//handler更新UI
			Message msg = handler.obtainMessage();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", drawable);
			map.put("imageview", imageView);
			msg.obj = map;
			handler.sendMessage(msg);
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			HashMap<String, Object> map = (HashMap<String, Object>)msg.obj;
			ImageView imageView = (ImageView)map.get("imageview");
			Drawable drawable = (Drawable)map.get("image");
			imageView.setImageDrawable(drawable);
		}
	};
}
