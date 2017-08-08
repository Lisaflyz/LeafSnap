package com.imageclassifier.user.leafsnap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import adapter.MyFindResultAdapter;
import tools.ActivityCollector;
import tools.Tools;

public class ResultActivity extends Activity {

	protected static final int GET_TOP_PLANTS_LISTS_SUCCESS = 0x123;
	ListView resultList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		init();
	}


	private void init() {

		resultList = (ListView) findViewById(R.id.lv_find_result);
		
		MyFindResultAdapter adapter = new MyFindResultAdapter(
				getApplicationContext(), Tools.topList);
		resultList.setAdapter(adapter);

		resultList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent(ResultActivity.this,
						PlantDetailActivity.class);
				intent.putExtra("position", position);
				startActivity(intent);

			}
		});
	}
}
