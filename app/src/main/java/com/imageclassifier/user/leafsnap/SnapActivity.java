package com.imageclassifier.user.leafsnap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.android.Utils;
import org.opencv.core.MatOfPoint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

import adapter.MyFindResultAdapter;
import beans.Plant;
import tools.Tools;
import tools.WhoDBAdapter;

public class SnapActivity extends AppCompatActivity {

	private static final String TAG = "SnapActivity";
	protected static final int GET_TOP_PLANTS_LISTS_SUCCESS = 0x123;
	protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
	private static final int permissionsCode = 42;
	Dialog dialog;
	ListView resultList;
	private AlertDialog mAlertDialog;
	private Uri imageUri;
	private String imagePath;
	private Button bt_take_photo, bt_match;
	private ImageView imageView;

	public static Bitmap bitmap;


	ViewFlipper viewFlipper;
	private String mTempPhotoPath;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case GET_TOP_PLANTS_LISTS_SUCCESS:
					dialog.dismiss();
					show();
					break;
				default:
					break;
			}
		}

	};

	//显示匹配结果，点击跳到细节
	private void show() {
		if(Tools.topList == null){
			Toast.makeText(this, "匹配失败", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent2 = new Intent(SnapActivity.this,
				ResultActivity.class);
		startActivity(intent2);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	//在onCreate中判断版本号
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snap1);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.leaf);
		imageView = (ImageView) findViewById(R.id.camera_view1);
		bt_take_photo = (Button)findViewById(R.id.btn_take_pic);
		bt_match = (Button)findViewById(R.id.btn_match);

		//拍照后返回，bitmap会改变
		if(getIntent() != null){
			Intent intent = getIntent();
			 imagePath = intent.getStringExtra("ImagePath");
			imageUri = Uri.parse("file://" + imagePath);
			try{
				bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
			}catch(Exception e){
				e.printStackTrace();
			}

		}

		imageView.setImageBitmap(bitmap);
		bt_take_photo.setOnClickListener(new OnClickListener() {

			//先检查是否已授予权限，若未授予，先申请权限
			//若已授予，则直接操作
			@Override
			public void onClick(View v) {
				if((ContextCompat.checkSelfPermission(SnapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED) ||
						(ContextCompat.checkSelfPermission(SnapActivity.this, Manifest.permission.CAMERA)
						!= PackageManager.PERMISSION_GRANTED)){
					ActivityCompat.requestPermissions(SnapActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.CAMERA},1);
					Log.i(TAG, "请求相机权限和存储权限");
				}else{
					Intent intent = new Intent(SnapActivity.this, CameraActivity.class);
					startActivity(intent);
				}

			}
		});

		bt_match.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											find();
										}
									}

		);


	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int []grantResults) {
		switch (requestCode) {
			case 1: {
				System.out.println("grantResults.length = " + grantResults.length);
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
						grantResults[1] == PackageManager.PERMISSION_GRANTED)
				{
					Intent intent = new Intent(SnapActivity.this, CameraActivity.class);
					startActivity(intent);


				} else {
					Toast.makeText(this, "权限未开启，需手动开启",Toast.LENGTH_LONG).show();

				}
			}
			break;
			default:
				break;
		}
	}

	protected void find() {


		if(bitmap == null){
			Toast.makeText(SnapActivity.this, "图片为空，请先拍摄图片",Toast.LENGTH_LONG);
			return;

		}
		dialog = new Dialog(SnapActivity.this, R.style.mydialog);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.general_progress_dialog);

		TextView tv = (TextView) dialog.findViewById(R.id.progress_dialog_text);
		tv.setText("正在识别树叶...");
		dialog.show();

		new Thread() {
			@Override
			public void run() {
				// =============================================================
				Bitmap resizedBitmap = Tools.resize(bitmap, 300,
						400);
				Tools.em(resizedBitmap, getApplicationContext(), null);
				List<MatOfPoint> contours = Tools.getContours();
				Tools.getCurvImg(contours);
				Tools.getHistograms(Tools.rawCurvMat);

				// ======================================================
				WhoDBAdapter whoDBAdapter = new WhoDBAdapter(
						getApplicationContext());
				whoDBAdapter.open();
				Cursor cursor = whoDBAdapter.getAllPlants();
				List<Plant> plantList = new ArrayList<Plant>();
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
					plantList.add(plant);
				}
				cursor.close();
				whoDBAdapter.close();
				Tools.topList = Tools.getTopSortMatchedPlants(plantList, 2);

				handler.sendEmptyMessage(GET_TOP_PLANTS_LISTS_SUCCESS);
				// ===============================================================

			}
		}.start();

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


	//拍照后返回的逻辑
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == this.RESULT_OK) {
			switch (requestCode) {
				case 1:   // 调用相机拍照
					try {
						bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
						imageView.setImageBitmap(bitmap);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}


		}
	}


}
