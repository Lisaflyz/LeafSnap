package com.imageclassifier.user.leafsnap;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

import adapter.MyALlPlantsOptionAdapter;
import beans.Plant;
import tools.WhoDBAdapter;

public class BrowseActivity extends BaseActivity implements
		SearchView.OnQueryTextListener {
	public  static final String FLAG_KEY = "FLAG";
	public  static final String FLAG_VALUE_BROWSEACTIVITY = "BrowseActivity";

	SearchView searchView;

	public static List<Plant> plants;
	ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);
		
		init();
	}

	private void init() {
		searchView = (SearchView) findViewById(R.id.sv_search_plant_browse);
		searchView.setOnQueryTextListener(this);
		searchView.setSubmitButtonEnabled(true);
		listview=(ListView) findViewById(R.id.lv_search_plant_browse);
		query("");
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		query(query);
		return true;
	}

	private void query(String query) {
		WhoDBAdapter dbAdapter = new WhoDBAdapter(getApplicationContext());
		dbAdapter.open();

		Cursor cursor = dbAdapter.searchPlants(query);
		plants = new ArrayList<Plant>();
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
			plant.setPhists(cursor.getString(cursor
					.getColumnIndex(WhoDBAdapter.PLANT_HISTGRAMS)));
			plant.setDatatime(cursor.getString(cursor
					.getColumnIndex(WhoDBAdapter.PLANT_DATATIME)));
			plants.add(plant);

		}
		cursor.close();
		dbAdapter.close();
		
		MyALlPlantsOptionAdapter adapter=new MyALlPlantsOptionAdapter(plants, getApplicationContext());
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(BrowseActivity.this,PlantDetailActivity.class);
				intent.putExtra("position", position);
				intent.putExtra(FLAG_KEY, FLAG_VALUE_BROWSEACTIVITY);
				startActivity(intent);
				
			}
		});
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		query(newText);
		return false;
	}
}
