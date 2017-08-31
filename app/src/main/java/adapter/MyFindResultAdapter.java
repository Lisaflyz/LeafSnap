package adapter;

import java.io.File;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imageclassifier.user.leafsnap.R;

import beans.Plant;

public class MyFindResultAdapter extends BaseAdapter {
	
	private class FindResult{
		ImageView imgv;
		TextView name;
		TextView time;
		TextView desc;
		TextView sim;
	}
	

	private Context mContext;
	private List<Plant> mList;
	private FindResult mholder;

	public MyFindResultAdapter(Context mContext, List<Plant> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {

		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		View view;
		if (convertView != null) {
			mholder = (FindResult) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_find_result, null);
			
			mholder=new FindResult();
			
			mholder.imgv = (ImageView) convertView.findViewById(R.id.iv_find_result);
			mholder.name = (TextView) convertView
					.findViewById(R.id.tv_plant_name_result);
			mholder.desc = (TextView) convertView
					.findViewById(R.id.tv_plant_desc_result);
			mholder.sim = (TextView) convertView
					.findViewById(R.id.tv_result_similarity);
			mholder.time = (TextView) convertView
					.findViewById(R.id.tv_plant_time_result);
			convertView.setTag(mholder);//缓存mholder
			
		}
		
		Plant plant = mList.get(position);
		if(null!=plant){
			if (null != plant.getPics()) {
				String url = plant.getPics();
				File file = new File(url);
				if (file.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(url);
					mholder.imgv.setImageBitmap(bitmap);
				}
			}
			mholder.name.setText(plant.getPname());
			mholder.desc.setText(plant.getPdesc());
			mholder.sim.setText((float) (100 * plant.getVal() / 25) + "%");
			String datetime = plant.getDatatime();
			Date date = new Date(Long.valueOf(datetime));
			mholder.time.setText(date.toString());
		}



		return convertView;
	}

}
