package com.imageclassifier.user.leafsnap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import adapter.MyHistgramAdapter;
import beans.Plant;
import tools.Tools;
import tools.WhoDBAdapter;

public class DemoActivity extends BaseActivity {

	private static final String TAG = "DemoActivity";
	private List<MatOfPoint> contours;

	public static final int MSG_TRAIN_SUCCESS = 0x123;
	public static final int MSG_TRAIN_FAILED = 0x124;
	public static final int MSG_PREDICT_FINISH = 0x125;
	public static Mat binaryMat;

	Button btn_pics, btn_resize, btn_em, btn_pengzhang, btn_tophat,
			btn_contours, btn_curvImg, btn_histograms, btn_adddatabase,
			btn_find, btn_test;

	Button morepics;

	ImageView imageView, img_add;
	Bitmap bitmap,bitmap_ori;
	ListView list;
	Dialog dialog;
	TextView tv;

	String hists;
	String picUrl;
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_TRAIN_SUCCESS:
				tv.setText("EM处理结束");
				break;
			case MSG_TRAIN_FAILED:
				tv.setText("EM处理失败");
				break;
			case MSG_PREDICT_FINISH:
				dialog.dismiss();
				imageView.setImageBitmap(bitmap);
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);

		init();
	}

	private void init() {
		list = (ListView) findViewById(R.id.lv_curv_img);
		btn_pics = (Button) findViewById(R.id.btn_select_pic);
		btn_resize = (Button) findViewById(R.id.btn_resize);
		btn_em = (Button) findViewById(R.id.btn_em);
		btn_pengzhang = (Button) findViewById(R.id.btn_pengzhang);
		btn_tophat = (Button) findViewById(R.id.btn_tophat);
		btn_contours = (Button) findViewById(R.id.btn_contours);
		btn_curvImg = (Button) findViewById(R.id.btn_curvImg);
		btn_histograms = (Button) findViewById(R.id.btn_histograms);
		btn_adddatabase = (Button) findViewById(R.id.btn_database);
		btn_find = (Button) findViewById(R.id.btn_analyze);
		imageView = (ImageView) findViewById(R.id.iv_demo);

		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.leaf);

		//若有SnapActivity中的图片，则使用那种图片；否则使用默认图片
		if (SnapActivity.bitmap != null) {
			bitmap = SnapActivity.bitmap;
			imageView.setImageBitmap(bitmap);
		}


		//OnClickListense接口对各种点击事件进行分发，按钮注册点击事件
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button btn = (Button) v;
				//listView仅在曲率直方图处出现
				if (R.id.btn_histograms != btn.getId()) {
					list.setVisibility(View.GONE);
				}

				switch (btn.getId()) {
				case R.id.btn_select_pic:

					Intent intent = new Intent();

					intent.setType("image/*");

					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(intent, 1);
					break;
				case R.id.btn_resize:
					if(bitmap == null){
						Toast.makeText(getApplicationContext(), "需要先选择图片，再进行图片缩放处理",
								Toast.LENGTH_LONG).show();
						break;
					}
					bitmap = Tools.resize(bitmap, 300, 400);
					imageView.setImageBitmap(bitmap);
					break;
				case R.id.btn_em:
					if(bitmap == null){
						Toast.makeText(getApplicationContext(), "需要先选择图片，再进行图片EM处理",
								Toast.LENGTH_LONG).show();
						break;
					}
					dialog = new Dialog(DemoActivity.this, R.style.mydialog);
					dialog.setCancelable(true);
					dialog.setContentView(R.layout.general_progress_dialog);

					tv = (TextView) dialog
							.findViewById(R.id.progress_dialog_text);
					tv.setText("EM处理中");
					dialog.show();
					//在子线程中进行EM处理，处理完毕后通过handler发送消息
					new Thread() {
						@Override
						public void run() {
							bitmap = Tools.em(bitmap, getApplicationContext(),
									handler);
							handler.sendEmptyMessage(DemoActivity.MSG_PREDICT_FINISH);
						}

					}.start();

					break;
				case R.id.btn_pengzhang:
					bitmap = Tools.dilation();
					if(bitmap == null){
						Toast.makeText(getApplicationContext(), "需要先得到二值化图片，再进行膨胀处理",
								Toast.LENGTH_LONG).show();
						break;
					}
					imageView.setImageBitmap(bitmap);
					Toast.makeText(getApplicationContext(), "膨胀操作",
							Toast.LENGTH_SHORT).show();

					break;
				case R.id.btn_tophat:
					Bitmap bitmap2 = Tools.topHat();
					if(bitmap == null){
						Toast.makeText(getApplicationContext(), "需要先得到二值化图片，再进行顶帽处理",
								Toast.LENGTH_LONG).show();
						break;
					}
					imageView.setImageBitmap(bitmap2);
					Toast.makeText(getApplicationContext(), "顶帽操作",
							Toast.LENGTH_SHORT).show();
					break;
				case R.id.btn_contours:
					contours = Tools.getContours();
					if(contours == null){
						Toast.makeText(getApplicationContext(), "需要先得到二值化图片，再进行轮廓寻找处理",
								Toast.LENGTH_LONG).show();
						break;
					}
					Mat mat = new Mat(300, 400, CvType.CV_8UC1);
					Imgproc.drawContours(mat, contours, -1, new Scalar(255,
							255, 255), 1);
					Bitmap bitmapContours = Bitmap.createBitmap(mat.width(),
							mat.height(), Config.RGB_565);
					Utils.matToBitmap(mat, bitmapContours);
					imageView.setImageBitmap(bitmapContours);
					break;
				case R.id.btn_curvImg:
					if(contours == null){
						Toast.makeText(getApplicationContext(), "需要先得到轮廓，再进行曲率灰度图像处理",
								Toast.LENGTH_LONG).show();
						break;
					}
					Mat curvMat = Tools.getCurvImg(contours);
					Mat dst = new Mat();
					curvMat.convertTo(dst, CvType.CV_8UC1);
					Bitmap bitmapCurv = Bitmap.createBitmap(curvMat.width(),
							curvMat.height(), Config.ARGB_8888);

					Utils.matToBitmap(dst, bitmapCurv);
					// dialog.dismiss();
					imageView.setImageBitmap(bitmapCurv);
					break;
				case R.id.btn_histograms:
					if(Tools.curvMat == null){
						Toast.makeText(getApplicationContext(), "需先得到曲率，再进行曲率直方图处理",Toast.LENGTH_LONG).show();
						break;
					}
					// Tools.test();
					// break;
					list.setVisibility(View.VISIBLE);
					List<Bitmap> data = new ArrayList<Bitmap>();
					List<Mat> histImags = Tools.getHistograms(Tools.curvMat);

					for (int i = 0; i < histImags.size(); i++) {

						Mat histImg = histImags.get(i);
						Bitmap bitmapHist = Bitmap.createBitmap(
								histImg.width(), histImg.height(),
								Config.ARGB_8888);
						Utils.matToBitmap(histImg, bitmapHist);
						data.add(bitmapHist);
					}

					MyHistgramAdapter adapter = new MyHistgramAdapter(
							getApplicationContext(), data);
					list.setAdapter(adapter);
					break;
				case R.id.btn_database:
					View dialogSave = getLayoutInflater().inflate(
							R.layout.add_plant, null);

					final EditText et_name = (EditText) dialogSave
							.findViewById(R.id.et_plant_name);
					final EditText et_desc = (EditText) dialogSave
							.findViewById(R.id.et_plant_desc);
					morepics = (Button) dialogSave
							.findViewById(R.id.btn_more_pics);
					img_add = (ImageView) dialogSave
							.findViewById(R.id.iv_more_pics);
					morepics.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();

							intent.setType("image/*");

							intent.setAction(Intent.ACTION_GET_CONTENT);


							startActivityForResult(intent, 2);
						}
					});

					//添加内容整体为Dialog的形式
					new AlertDialog.Builder(DemoActivity.this)
							.setView(dialogSave)
							.setPositiveButton("保存",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											savePics();

											Plant plant = new Plant();
											plant.setPid((int) System
													.currentTimeMillis());
											plant.setPname(et_name.getText()
													.toString());
											plant.setPdesc(et_desc.getText()
													.toString());



										//	Log.i("存入数据库的文件地址为：" , picUrl);
										//	Log.i("picUrl", picUrl);
											plant.setPics(picUrl);//
											plant.setDatatime(String.valueOf(System
													.currentTimeMillis()));

											if(Tools.listHists==null){
												Toast.makeText(getApplicationContext(), "直方图为空",Toast.LENGTH_LONG).show();
												return;
											}
												StringBuffer str = Tools
														.histGramsToString(Tools.listHists);



											String s = str.toString();
											plant.setPhists(s);
											Log.i(TAG, s);
											Log.i(TAG,
													"str length:"
															+ str.length());
											WhoDBAdapter dbAdapter = new WhoDBAdapter(
													getApplicationContext());
											dbAdapter.open();
											long rowid = dbAdapter
													.addPlant(plant);
											dbAdapter.close();
											if (rowid > 0) {
												Toast.makeText(
														getApplicationContext(),
														"确定"
																+ rowid,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												Toast.makeText(
														getApplicationContext(),
														"取消"
																+ rowid,
														Toast.LENGTH_SHORT)
														.show();
											}

										}

										//存储bitmap到指定文件夹下的图片
										private void savePics() {
											//public File(File parent, String child)
											File imageDir = new File(Environment.getExternalStorageDirectory(),"images");
											if(!imageDir.exists()){
												imageDir.mkdir();
											}
											String fileName = System.currentTimeMillis()+".jpg";
											File file = new File(imageDir, fileName);//父文件，子文件名
											FileOutputStream outputStream = null;
											try {
												outputStream = new FileOutputStream(
														file);
												bitmap_ori.compress(
														CompressFormat.JPEG,
														100, outputStream);
												outputStream.flush();
												outputStream.close();
												//方案1：在数据库中存的是图片的绝对地址
												picUrl = file.getAbsolutePath();
												Log.i(TAG, file.getAbsolutePath());
												Log.i(TAG, "已保存文件");
											} catch (Exception e) {
												Log.i(TAG, e.getMessage());
												Log.i(TAG, "保存图片失败");
											}

											//将文件插入图库
											try{
												MediaStore.Images.Media.insertImage
														( getApplicationContext().getContentResolver(),
																file.getAbsolutePath(), fileName, null);
											}catch (Exception e){
												e.printStackTrace();
											}
											//无这句话时已通知图库更新了
											getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
													Uri.parse("file://" + file.getAbsolutePath())));
										}
									}).setNegativeButton("取消", null).show();
					;
					break;
				case R.id.btn_analyze:
					WhoDBAdapter whoDBAdapter = new WhoDBAdapter(
							getApplicationContext());
					whoDBAdapter.open();
					Cursor cursor = whoDBAdapter.getAllPlants();
					Log.i(TAG, "cursor size: " + cursor.getCount());
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

					Tools.topList = Tools
							.getTopSortMatchedPlants(plantList, 2);
					if(Tools.topList == null){
						Toast.makeText(getApplicationContext(), "未得到曲率直方图，不能进行匹配",Toast.LENGTH_LONG).show();
						break;
					}

					Log.i(TAG, "sort vals ::");
					for (Plant plant : Tools.topList) {
						Log.i(TAG, "sort vals ::" + plant.getVal());
					}

					Intent intent2 = new Intent(DemoActivity.this,
							ResultActivity.class);
					startActivity(intent2);
					break;
				default:
					break;
				}
			}
		};

		//各个按钮均注册该接口
		btn_pics.setOnClickListener(listener);
		btn_resize.setOnClickListener(listener);
		btn_em.setOnClickListener(listener);
		btn_pengzhang.setOnClickListener(listener);
		btn_tophat.setOnClickListener(listener);
		btn_contours.setOnClickListener(listener);
		btn_curvImg.setOnClickListener(listener);
		btn_histograms.setOnClickListener(listener);
		btn_adddatabase.setOnClickListener(listener);
		btn_find.setOnClickListener(listener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			picUrl = uri.toString();

			Log.i(TAG, uri.toString());
			ContentResolver cr = this.getContentResolver();
			try {
				bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
				bitmap_ori = BitmapFactory.decodeStream(cr.openInputStream(uri));
				switch (requestCode) {
				case 1:
					imageView.setImageBitmap(bitmap);
					break;
				case 2:
					img_add.setImageBitmap(bitmap);
					break;

				default:
					break;
				}
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
