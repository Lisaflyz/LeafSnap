package com.imageclassifier.user.leafsnap;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import adapter.MyALlPlantsOptionAdapter;
import beans.Plant;
import tools.WhoDBAdapter;


public class OptionActivity extends BaseActivity {

	private static final String TAG = "OptionActivity";
	ListView listview;
	public static List<Plant> plants;
	MyALlPlantsOptionAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		init();
	}

	private void init() {
		listview = (ListView) findViewById(R.id.lv_all_samples);

		WhoDBAdapter dbAdapter = new WhoDBAdapter(getApplicationContext());
		dbAdapter.open();

		plants = new ArrayList<Plant>();
		Cursor cursor = dbAdapter.getAllPlants();

		while (cursor.moveToNext()) {
			Plant plant = new Plant();
			plant.setPid(cursor.getInt(cursor
					.getColumnIndex(WhoDBAdapter.PLANT_ID)));
			plant.setPname(cursor.getString(cursor
					.getColumnIndex(WhoDBAdapter.PLANT_NAME)));
			plant.setPdesc(cursor.getString(cursor
					.getColumnIndex(WhoDBAdapter.PLANT_DESCRIPTION)));
			plant.setPics(cursor.getString(cursor
					.getColumnIndex(WhoDBAdapter.PLANT_PICS)));
			plant.setDatatime(cursor.getString(cursor
					.getColumnIndex(WhoDBAdapter.PLANT_DATATIME)));

			plants.add(plant);
			Log.i(TAG, plant.getPname()+"pic url"+plant.getPics());
		}
		cursor.close();
		dbAdapter.close();

		adapter = new MyALlPlantsOptionAdapter(plants,
				getApplicationContext());
		listview.setAdapter(adapter);
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(0, 0, ContextMenu.NONE, "修改植物信息");
				menu.add(0, 1, ContextMenu.NONE, "删除植物信息");
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(OptionActivity.this,ChangePlantActivity.class);
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = (int) info.id;
		switch (item.getItemId()) {
		case 0:
			Intent intent=new Intent(OptionActivity.this,ChangePlantActivity.class);
			intent.putExtra("position", position);
			
			startActivity(intent);
			break;
		
		case 1:
			WhoDBAdapter dbadapter = new WhoDBAdapter(getApplicationContext());
			dbadapter.open();
			if(
			dbadapter.deletePlant(plants.get(position).getPid())>0){//删除元素再初始化
				init();
				Toast.makeText(getApplicationContext(), "删除植物", Toast.LENGTH_SHORT).show();
			}else{
				
				Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
			}
			dbadapter.close();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

}
