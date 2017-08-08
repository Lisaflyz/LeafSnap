package adapter;

import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imageclassifier.user.leafsnap.R;

public class MyHistgramAdapter extends BaseAdapter {

	private Context mContext;
	private List<Bitmap> mList;

	public MyHistgramAdapter(Context c, List<Bitmap> list) {
		super();
		mContext = c;
		mList = list;
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
		View view;
		view = LayoutInflater.from(mContext).inflate(R.layout.histgrams_item,
				null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_histgrams);
		TextView tv = (TextView) view.findViewById(R.id.tv_radius);
		Bitmap bitmap = mList.get(position);
		image.setImageBitmap(bitmap);
		tv.setText("radius = " + (position + 2));
		return view;
	}
}
