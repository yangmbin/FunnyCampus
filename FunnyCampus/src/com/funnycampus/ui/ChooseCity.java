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
	
	//����ѡ��ʡ�ݺͳ���
	private int choosedProvince = 0;
	private int choosedCity = 0;
	
	//����ʡ�ݱ仯�ĳ������ݼ�
	private String[] cityItemSet = getRealCity(CityCode.CDDE_CITY[0]);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_city);
		
		//��ȡ��ͼ
		province = (Spinner)findViewById(R.id.province_spinner);
		city = (Spinner)findViewById(R.id.city_spinner);
		
		/**
		 * ����ʡ����ʾ
		 */
		provinceAdapter = 
				new ArrayAdapter<String>(this, R.layout.spinner_display_style, R.id.txtvwSpinner, CityCode.PROVINCE);
		//���������б�ķ��
		provinceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
		//����������Spinner��ͼ��
		province.setAdapter(provinceAdapter);
		
		/**
		 * ������е���ʾ
		 */
		
		cityAdapter = 
				new ArrayAdapter<String>(this, R.layout.spinner_display_style, R.id.txtvwSpinner, cityItemSet);
		cityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
		city.setAdapter(cityAdapter);
		
		
		//provinceΪ��Ŀ��Ӽ�����
		province.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//parent��province����
				choosedProvince = position;
				//Toast.makeText(ChooseCity.this, position + ",", Toast.LENGTH_SHORT).show();
				//����ı���ʡ�ݣ�������ı�
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
		
		//city�����Ŀ��������
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
	
	//�޳�=��ǰ�������
	String[] getRealCity(String[] str) {
		String[] temp = new String[str.length];
		for(int i = 0; i < str.length; i++) {
			temp[i] = str[i].substring(str[i].indexOf("=") + 1);
		}
		return temp;
	}
	
	/**
	 * ����������Ϣ
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
