package de.cmc.android.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.cmc.android.R;

/**
 * Created by Deniz Kalem on 15.08.17.
 */

public class ViewPagerAdapter extends PagerAdapter {

	private String headerUrl, limitations;
	private Context context;

	public ViewPagerAdapter(Context context, String headerUrl, String limitations) {
		this.context = context;
		this.headerUrl = headerUrl;
		this.limitations = limitations;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		if ("null".equals(limitations)) {
			return 1;
		} else {
			return 2;
		}
	}

	@Override
	public Object instantiateItem(ViewGroup viewGroup, int position) {
		LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int resId = -1;

		switch (position) {
			case 0:
				resId = R.layout.pager_image;
				break;
			case 1:
				resId = R.layout.pager_text;
				break;
		}

		View view = inflater.inflate(resId, viewGroup, false);

		switch (position) {
			case 0:
				ImageView image = (ImageView) view.findViewById(R.id.iv_image);
				Picasso.with(context).load(headerUrl).into(image);
				break;
			case 1:
				TextView text = (TextView) view.findViewById(R.id.tv_limitations);
				text.setText(context.getString(R.string.detail_voucher_conditions, limitations));
				break;
		}

		viewGroup.addView(view, 0);

		return view;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

}
