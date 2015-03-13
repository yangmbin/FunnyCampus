package com.funnycampus.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.funnycampus.static_data.CityCode;
import com.yangmbin.funnycampus.R;
import com.yangmbin.funnycampus.R.anim;

public class ChooseCity extends Activity {
	private Spinner province;
	private Spinner city;
	private ArrayAdapter<String> provinceAdapter;
	private ArrayAdapter<String> cityAdapter;
	
	//保存选的省份和城市
	private int choosedProvince = 0;
	private int choosedCity = 0;
	
	//随着省份变化的城市数据集
	private String[] cityItemSet = getRealCity(CityCode.CDDE_CITY[0]);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_city);
		
		//获取视图
		province = (Spinner)findViewById(R.id.province_spinner);
		city = (Spinner)findViewById(R.id.city_spinner);
		
		/**
		 * 处理省的显示
		 */
		provinceAdapter = 
				new ArrayAdapter<String>(this, R.layout.spinner_display_style, R.id.txtvwSpinner, CityCode.PROVINCE);
		//设置下拉列表的风格
		provinceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
		//将数据捆绑到Spinner视图上
		province.setAdapter(provinceAdapter);
		
		/**
		 * 处理城市的显示
		 */
		
		cityAdapter = 
				new ArrayAdapter<String>(this, R.layout.spinner_display_style, R.id.txtvwSpinner, cityItemSet);
		cityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
		city.setAdapter(cityAdapter);
		
		
		//province为条目添加监听器
		province.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//parent是province对象
				choosedProvince = position;
				//Toast.makeText(ChooseCity.this, position + ",", Toast.LENGTH_SHORT).show();
				//如果改变了省份，城市需改变
				cityItemSet = getRealCity(CityCode.CDDE_CITY[position]);
				cityAdapter = 
						new ArrayAdapter<String>(ChooseCity.this, R.layout.spinner_display_style, R.id.txtvwSpinner, cityItemSet);
				cityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
				city.setAdapter(cityAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		//city添加条目监听函数
		city.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				choosedCity = position;
				//Toast.makeText(ChooseCity.this, position + ",", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	//剔除=号前面的数字
	String[] getRealCity(String[] str) {
		String[] temp = new String[str.length];
		for(int i = 0; i < str.length; i++) {
			temp[i] = str[i].substring(str[i].indexOf("=") + 1);
		}
		return temp;
	}
	
	/**
	 * 加载天气信息
	 */
	public void LoadingWeatherInfo(View view) { 
		Intent intent = new Intent(ChooseCity.this, WeatherInfo.class);
		
		Bundle bundle = new Bundle();
		bundle.putInt("province", choosedProvince);
		bundle.putInt("city", choosedCity);
		intent.putExtras(bundle);
		
		startActivity(intent);
		overridePendingTransition(anim.right_to_mid, anim.mid_to_left);
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
