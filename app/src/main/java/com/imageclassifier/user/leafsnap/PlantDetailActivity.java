package com.imageclassifier.user.leafsnap;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import beans.Plant;
import tools.Tools;

public class PlantDetailActivity extends Activity {

	private TextView name;
	private TextView desc;
	private ImageView img;

	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plant_detail);

		name = (TextView) findViewById(R.id.tv_detail_plant_name);
		desc = (TextView) findViewById(R.id.tv_detail_plant_desc);
		img = (ImageView) findViewById(R.id.iv_detail_plant_pic);

		Intent intent = getIntent();
		int position = intent.getIntExtra("position", -1);
		String flag = intent.getStringExtra(BrowseActivity.FLAG_KEY);
		if (position != -1) {
			Plant plant;
			if (flag != null
					&& flag.equals(BrowseActivity.FLAG_VALUE_BROWSEACTIVITY)) {
				plant = BrowseActivity.plants.get(position);
			} else {

				plant = Tools.topList.get(position);
			}
			name.setText(plant.getPname());
			desc.setText(plant.getPdesc());
			if (null != plant.getPics()) {
				url = plant.getPics();
				File file = new File(plant.getPics());
				if (file.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(file
							.getAbsolutePath());
					img.setImageBitmap(bitmap);
				}
			}
		}

	}

	public void showBigPic(View v) {
		Intent intent = new Intent(PlantDetailActivity.this,
				MyScalableActivity.class);
		intent.putExtra("pic", url);
		startActivity(intent);

	}

}
