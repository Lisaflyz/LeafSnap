package com.imageclassifier.user.leafsnap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import tools.ActivityCollector;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.activities.add(this);
	}

	protected void onDestroy(){
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	private AlertDialog mAlertDialog;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			final Dialog selectDialog = new Dialog(BaseActivity.this, R.style.mydialog);
			selectDialog.setCancelable(true);
			selectDialog.setContentView(R.layout.dialog_exit);
			Button yesBtn=(Button) selectDialog.findViewById(R.id.btn_dialog_optimistic);
			Button noBtn=(Button) selectDialog.findViewById(R.id.btn_dialog_negative);

			yesBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					System.exit(0);
				}
			});
			
			noBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectDialog.dismiss();
				}
			});
			selectDialog.show();
		}
		return true;
	}

}
