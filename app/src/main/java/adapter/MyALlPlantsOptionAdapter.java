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

public class MyALlPlantsOptionAdapter extends BaseAdapter {
	private class AllPlantsOptions {
		ImageView imgv;
		TextView name;
		TextView desc;
		TextView time;
		// int position;
	}

	private List<Plant> mlist;
	private Context mContext;
	private AllPlantsOptions mholder;

	public MyALlPlantsOptionAdapter(List<Plant> mlist, Context mContext) {
		super();
		this.mlist = mlist;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (null != convertView) {
			mholder = (AllPlantsOptions) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_all_plant_option, null);
			mholder = new AllPlantsOptions();
			mholder.imgv = (ImageView) convertView
					.findViewById(R.id.iv_allplant_pic);
			mholder.name = (TextView) convertView
					.findViewById(R.id.tv_allplant_name_option);
			mholder.desc = (TextView) convertView
					.findViewById(R.id.tv_allplant_desc_result);

			mholder.time = (TextView) convertView
					.findViewById(R.id.tv_allplant_time_option);

			convertView.setTag(mholder);
		}

		// mholder.position = position;
		Plant plant = mlist.get(position);
		if (null != plant) {
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

			String datetime = plant.getDatatime();
			Date date = new Date(Long.valueOf(datetime));
			mholder.time.setText(date.toString());
			convertView.setTag(mholder);
		}

		return convertView;
	}

}
