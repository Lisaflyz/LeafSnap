package com.imageclassifier.user.leafsnap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import beans.Plant;
import tools.WhoDBAdapter;


public class ChangePlantActivity extends AppCompatActivity {

	EditText name;
	EditText desc;
	TextView tv_time;
	ImageView img;
	Button rePhoto;
	private Uri imageUri;
	String url;
	private Bitmap newBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_plant);

		init();

	}

	private void init() {
		Intent intent = getIntent();
		int position = intent.getIntExtra("position", 0);
		Plant plant = OptionActivity.plants.get(position);

		name = (EditText) findViewById(R.id.et_plant_name_change);
		desc = (EditText) findViewById(R.id.et_plant_desc_change);
		tv_time = (TextView) findViewById(R.id.tv_modified_last_time);
		img = (ImageView) findViewById(R.id.iv_rephoto);
		rePhoto = (Button) findViewById(R.id.btn_rephoto);

		rePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				takePhotoDirectly();

			}
		});



		name.setText(plant.getPname());
		desc.setText(plant.getPdesc());
		Date data = new Date(Long.valueOf(plant.getDatatime()));
		tv_time.setText(data.toString());

		if (null != plant.getPics()) {
			url = plant.getPics();
			File file = new File(plant.getPics());
			if (file.exists()) {
				Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
				img.setImageBitmap(bm);
			}
		}



	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.change,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int position = item.getItemId();

		switch (position) {
		case R.id.change_plant:
			WhoDBAdapter adapter = new WhoDBAdapter(getApplicationContext());
			adapter.open();

			Plant plant = new Plant();
			if (null == name.getText().toString()
					|| name.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "植物名不能为空，未修改原有值ֲ",
						Toast.LENGTH_SHORT).show();
				name.requestFocus();
				return true;
			} else {

				plant.setPname(name.getText().toString());
			}
			if (null == desc.getText().toString()
					|| desc.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "植物描述不能为空，未修改原有值",
						Toast.LENGTH_SHORT).show();
				desc.requestFocus();
				return true;
			} else {

				plant.setPdesc(desc.getText().toString());
			}
			plant.setDatatime(String.valueOf(System.currentTimeMillis()));
			int pos = getIntent().getIntExtra("position", 0);
			plant.setPhists(OptionActivity.plants.get(pos).getPhists());
			if(null==url||url.equals("")){
				url=OptionActivity.plants.get(pos).getPics();
			}
			plant.setPics(url);
			plant.setPid(OptionActivity.plants.get(pos).getPid());
			if (adapter.updatePlant(plant) <= 0) {
				Toast.makeText(getApplicationContext(),
						"Oops...Something went wrong...", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(getApplicationContext(), "修改完成",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			adapter.close();
			break;
		case R.id.quit:
			finish();
			break;

		default:
			break;
		}

		return true;
	}
	
	public void showBigPic(View v) {
		Intent intent = new Intent(ChangePlantActivity.this,
				MyScalableActivity.class);
		intent.putExtra("pic", url);
		startActivity(intent);

	}
	private void takePhotoDirectly(){
		File photoDir = new File(Environment.getExternalStorageDirectory(), "images");
		if(!photoDir.exists()){
			photoDir.mkdir();
		}
		String fileName = System.currentTimeMillis()+".jpg";
		File photo = new File(photoDir, fileName);

		imageUri = FileProvider.getUriForFile(this,
				"com.imageclassifier.user.leafsnap.fileprovider", photo) ;
		Intent takeIntent = new Intent();
		takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
		takeIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
		takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
		startActivityForResult(takeIntent, 1);
	}

	//拍照完成后根据图片Uri得到文件，
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == this.RESULT_OK) {
			switch (requestCode) {
				case 1:   // 调用相机拍照
					try {
						newBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
						img.setImageBitmap(newBitmap);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}


		}
	}

}
