package de.cmc.android.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Deniz Kalem on 08.09.17.
 */

public class WrapContentViewPager extends ViewPager {

	private int mCurrentPagePosition = 0;

	public WrapContentViewPager(Context context) {
		super(context);
	}

	public WrapContentViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			View child = getChildAt(mCurrentPagePosition);
			if (child != null) {
				child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				int h = this.getMeasuredHeight();
				int w = this.getMeasuredWidth();

				int size = Math.min(h, w);

				heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}

