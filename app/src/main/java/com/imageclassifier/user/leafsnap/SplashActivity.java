package com.imageclassifier.user.leafsnap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//1s后跳到MainActivity
		new Handler().postDelayed(new Runnable(){  
            @Override  
            public void run() {  
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);  
                SplashActivity.this.finish();  
            }  
        }, 1000);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	
	
}
