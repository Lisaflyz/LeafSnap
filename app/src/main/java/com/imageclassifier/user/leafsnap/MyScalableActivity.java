package com.imageclassifier.user.leafsnap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;

import view.MyScalableView;

public class MyScalableActivity extends Activity {
	private MyScalableView imageView;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_scalable_view);
		findView();
		config();

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER;
		getWindow().setAttributes(lp);
	}

	private void findView() {
		imageView = (MyScalableView) findViewById(R.id.my_scalable_view);
		Intent intent = getIntent();

		String url = intent.getStringExtra("pic");

		if (null != url) {
			bitmap = BitmapFactory.decodeFile(url);
		} else {
			bitmap = ((BitmapDrawable) (getResources()
					.getDrawable(R.drawable.leaf))).getBitmap();
		}
	}

	private void config() {
		imageView.setImageBitmap(bitmap);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getY() > imageView.getTop()
				&& event.getY() < imageView.getBottom()
				&& event.getX() > imageView.getLeft()
				&& event.getX() < imageView.getRight()) {
			if (event.getPointerCount() == 2) {
				imageView.scaleWithFinger(event);
			}

			else if (event.getPointerCount() == 1) {
				imageView.moveWithFinger(event);
			}
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
			System.gc();
		}
		finish();
		return true;
	}
}
