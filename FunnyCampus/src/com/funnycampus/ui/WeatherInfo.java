package com.funnycampus.ui;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.funnycampus.static_data.CityCode;
import com.yangmbin.funnycampus.R;



public class WeatherInfo extends Activity {
	private int province, city;
	private TextView textView1, textView2, textView3, textView4, textView5;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_info);
		
		Bundle bundle = this.getIntent().getExtras();
		province = bundle.getInt("province");
		city = bundle.getInt("city");
		
		//后台加载
		startActivity(new Intent(WeatherInfo.this, LoadingActivity.class));
		new GetWeatherInfo().execute();
	}
	
	//获取网络数据的异步操作
	public class GetWeatherInfo extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... arg0) {
			String cityCodeString = CityCode.CDDE_CITY[province][city];
			String cityCode = cityCodeString.substring(0, cityCodeString.indexOf("="));
			
			String result[] = new String[2];
			try {
				//HttpGet对象
				HttpGet httpRequest0 = 
						new HttpGet("http://www.weather.com.cn/data/sk/" + cityCode + ".html");
				//HttpClient对象
				HttpClient httpClient0 = new DefaultHttpClient();
				//获得HttpResponse对象
				HttpResponse httpResponse0 = httpClient0.execute(httpRequest0);
				if(httpResponse0.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					//取得返回数据
					result[0] = EntityUtils.toString(httpResponse0.getEntity());
				}
				
				
				//另一个请求
				//HttpGet对象
				HttpGet httpRequest1 = 
						new HttpGet("http://www.weather.com.cn/data/cityinfo/" + cityCode + ".html");
				//HttpClient对象
				HttpClient httpClient1 = new DefaultHttpClient();
				//获得HttpResponse对象
				HttpResponse httpResponse1 = httpClient1.execute(httpRequest1);
				if(httpResponse1.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					//取得返回数据
					result[1] = EntityUtils.toString(httpResponse1.getEntity());
				}
			}
			catch(Exception e) { 
				result = null;
			}
			
			return result;
		}
		@Override
		protected void onPostExecute(String[] result) { 
			LoadingActivity.instance.finish();
			if(result == null) {
				Toast.makeText(WeatherInfo.this, "加载天气失败！", Toast.LENGTH_SHORT).show();
			}
			else {
				//Toast.makeText(WeatherInfo.this, "jjjj" + result[0] + result[1], Toast.LENGTH_SHORT).show();
				textView1 = (TextView)findViewById(R.id.weather_info_text1);
				textView2 = (TextView)findViewById(R.id.weather_info_text2);
				textView3 = (TextView)findViewById(R.id.weather_info_text3);
				textView4 = (TextView)findViewById(R.id.weather_info_text4);
				textView5 = (TextView)findViewById(R.id.weather_info_text5);
				
				//解析JSON对象
				JSONTokener jsonParser0 = new JSONTokener(result[0]);
				JSONTokener jsonParser1 = new JSONTokener(result[1]);
				try {
					JSONObject obj0 = (JSONObject)jsonParser0.nextValue();
					JSONObject obj1 = (JSONObject)jsonParser1.nextValue();
					
					JSONObject obj00 = obj0.getJSONObject("weatherinfo");
					JSONObject obj11 = obj1.getJSONObject("weatherinfo");
					
					textView1.setText(CityCode.PROVINCE[province] + "   " + obj11.getString("city"));
					textView2.setText(obj11.getString("temp1") + " ~ " + obj11.getString("temp2"));
					textView3.setText(obj11.getString("weather"));
					
					textView4.setText(obj00.getString("WD") + "   " + obj00.getString("WS"));
					textView5.setText("相对湿度" + "   " +  obj00.getString("SD"));
					
					LinearLayout linearLayout = (LinearLayout)findViewById(R.id.weather_info_linearLayout);
					linearLayout.setVisibility(0);
				} catch (JSONException e) {
					Toast.makeText(WeatherInfo.this, "加载天气失败！", Toast.LENGTH_SHORT).show();
				}
			}
			super.onPostExecute(result);
		}
	}
}
