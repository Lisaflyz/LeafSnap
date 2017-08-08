package com.imageclassifier.user.leafsnap;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	private TabHost mTabHost;

	RadioGroup radioGroup;

	RadioButton rbtn_home;
	RadioButton rbtn_browse;
	RadioButton rbtn_collection;
	RadioButton rbtn_option;
	RadioButton rbtn_snap;

	public static final String ACTION_PAGE_CHANGED = "action_page_changed";
	public static final int PAGE_INDEX_HOME = 0;
	public static final int PAGE_INDEX_BROWSE = 1;
	public static final int PAGE_INDEX_COLLECTION = 2;
	public static final int PAGE_INDEX_OPTION = 3;
	public static final int PAGE_INDEX_SNAP = 4;
	public static final int PAGE_INDEX_DEMO = 5;
	public static final String PAGE_CURRENT_KEY = "PAGE_Current_key";

	public static final String HOME = "HOME";
	public static final String BROWSE = "BROWSE";
	public static final String COLLECTION = "COLLECTION";
	public static final String OPTION = "OPTION";
	public static final String SNAP = "SNAP";
	public static final String DEMO = "DEMO";

	private static final String TAG = "MainActivity";

	private BroadcastReceiver receiver;

	private int[] rbtnId = new int[] { R.id.rbtn_main_home,
			R.id.rbtn_main_browse, R.id.rbtn_main_collection,
			R.id.rbtn_main_options, R.id.rbtn_main_snap ,R.id.rbtn_main_demo};

	private String[] tabTag = new String[] { HOME, BROWSE, COLLECTION, OPTION,
			SNAP ,DEMO};

	//加载openCV状态的回调函数
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();//初始化

		IntentFilter filter = new IntentFilter(ACTION_PAGE_CHANGED);
		receiver = new BroadcastReceiver() {//MainActivity新建广播接收器，重写onReceive方法

			@Override
			//若接收到的广播为调整到主页面，则跳到主页面
			public void onReceive(Context context, Intent data) {
				int pageIndex = data.getIntExtra(PAGE_CURRENT_KEY,
						PAGE_INDEX_HOME);
				changeToPage(pageIndex);
			}
		};
		registerReceiver(receiver, filter);//注册有IntentFilter的关闭
	}

	@Override
	//广播接收器解除绑定
	protected void onDestroy() {
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	//将对象和布局绑定，设置各种点击意图
	private void init() {

		mTabHost = getTabHost();

		radioGroup = (RadioGroup) findViewById(R.id.rg_main_tab);

		rbtn_home = (RadioButton) findViewById(R.id.rbtn_main_home);
		rbtn_browse = (RadioButton) findViewById(R.id.rbtn_main_browse);
		rbtn_collection = (RadioButton) findViewById(R.id.rbtn_main_collection);
		rbtn_option = (RadioButton) findViewById(R.id.rbtn_main_options);
		rbtn_snap = (RadioButton) findViewById(R.id.rbtn_main_snap);

		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, HomeActivity.class);//跳转到homeActivity
		spec = mTabHost.newTabSpec(HOME).setIndicator(HOME).setContent(intent);

		if (spec == null || mTabHost == null) {
			Log.i(TAG, "mTabHost || spec ==null");
		}
		mTabHost.addTab(spec);

		intent = new Intent().setClass(this, BrowseActivity.class);
		spec = mTabHost.newTabSpec(BROWSE).setIndicator(BROWSE)
				.setContent(intent);
		mTabHost.addTab(spec);

		intent = new Intent().setClass(this, CollectionActivity.class);
		spec = mTabHost.newTabSpec(COLLECTION).setIndicator(COLLECTION)
				.setContent(intent);
		mTabHost.addTab(spec);

		intent = new Intent().setClass(this, OptionActivity.class);
		spec = mTabHost.newTabSpec(OPTION).setIndicator(OPTION)
				.setContent(intent);
		mTabHost.addTab(spec);

		intent = new Intent().setClass(this, SnapActivity.class);
		spec = mTabHost.newTabSpec(SNAP).setIndicator(SNAP).setContent(intent);
		mTabHost.addTab(spec);

		intent = new Intent().setClass(this, DemoActivity.class);
		spec = mTabHost.newTabSpec(DEMO).setIndicator(DEMO).setContent(intent);
		mTabHost.addTab(spec);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			//点击别的标签时当前标签变为白色
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				((RadioButton) findViewById(checkedId))
						.setTextColor(getResources()
								.getColor(R.color.color_FFF));
				setcolor(checkedId);
			}
		});

		//MainActivty首先跳转到主页
		try {
			int pageIndex = this.getIntent().getIntExtra(PAGE_CURRENT_KEY,
					PAGE_INDEX_HOME);
			changeToPage(pageIndex);
		} catch (Exception e) {
			Log.i(TAG, e.getMessage());
		}

	}

	//切换页面，因为已为每个RadioButton设置过跳转时间，切换到对应radioButton即可
	private void changeToPage(int pageIndex) {
		mTabHost.setCurrentTab(pageIndex);
		RadioButton radioButton = null;
		switch (pageIndex) {
		case PAGE_INDEX_HOME:
			radioButton = (RadioButton) radioGroup
					.findViewById(R.id.rbtn_main_home);
			break;
		case PAGE_INDEX_BROWSE:
			radioButton = (RadioButton) radioGroup
					.findViewById(R.id.rbtn_main_browse);
			break;
		case PAGE_INDEX_COLLECTION:
			radioButton = (RadioButton) radioGroup
					.findViewById(R.id.rbtn_main_collection);
			break;

		case PAGE_INDEX_OPTION:
			radioButton = (RadioButton) radioGroup
					.findViewById(R.id.rbtn_main_options);
			break;
		case PAGE_INDEX_SNAP:
			radioButton = (RadioButton) radioGroup
					.findViewById(R.id.rbtn_main_snap);
			break;
		case PAGE_INDEX_DEMO:
			radioButton = (RadioButton) radioGroup
			.findViewById(R.id.rbtn_main_demo);
			break;
		default:
			break;
		}
		if (radioButton != null) {
			radioButton.setChecked(true);
		}
	}


	//设置其余标签颜色，设置当前标签
	private void setcolor(int id) {
		int index = -1;
		for (int i = 0; i < rbtnId.length; i++) {
			if (id != rbtnId[i]) {
				((RadioButton) findViewById(rbtnId[i]))
						.setTextColor(getResources()
								.getColor(R.color.color_888));
			} else {
				index = i;
			}
		}

		mTabHost.setCurrentTabByTag(tabTag[index]);
	}
	
	@Override
	//onResume时加载openCV库，并调用回调方法看是否成功加载
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
				mLoaderCallback);
	}

}
