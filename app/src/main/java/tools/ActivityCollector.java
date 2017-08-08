package tools;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/8/6.
 */

public class ActivityCollector {
	public static List<Activity>  activities = new ArrayList<Activity>();
	public static void addActivity(Activity activity){
		activities.add(activity);
	}

	public static void removeActivity(Activity activity){
		activities.remove(activity);
	}
	public static void finishAll(){
		for(Activity activity : activities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}
}
