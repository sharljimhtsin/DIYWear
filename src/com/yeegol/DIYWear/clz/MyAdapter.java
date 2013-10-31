/**
 * 
 */
package com.yeegol.DIYWear.clz;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.yeegol.DIYWear.R;
import com.yeegol.DIYWear.entity.Collocation;
import com.yeegol.DIYWear.entity.Goods;
import com.yeegol.DIYWear.util.NetUtil;

/**
 * customize class base on {@link BaseAdapter} for ListView
 * 
 * @author sharl
 * 
 */
public class MyAdapter extends BaseAdapter {

	private static final String TAG = MyAdapter.class.getName();

	Context context;

	Object[] list;

	Handler handler;

	/**
	 * 
	 */
	public MyAdapter(Context c, List<?> l, Handler h) {
		context = c;
		list = l.toArray();
		handler = h;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return list.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		return list[arg0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		final Object o = list[arg0];
		final boolean isGood = o instanceof Goods;
		final ImageView imageView = new ImageView(context);
		imageView.setTag(arg0);// record the position as key for further find
		imageView.setImageResource(R.drawable.ic_launcher); // default icon
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new LayoutParams(100, 100));
		if (isGood ? ((Goods) o).getPreview() != null : ((Collocation) o)
				.getPreview() != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Bitmap bm = NetUtil.getImageFromWeb(
							isGood ? NetUtil.buildURLForThumb(((Goods) o)
									.getPreview()) : NetUtil
									.buildURLForCollocation(((Collocation) o)
											.getPreview()),
							NetUtil.DOMAIN_FILE_PURE);
					Object[] obj = new Object[] { imageView, bm };
					// notice the UI thread to draw
					handler.sendMessage(handler.obtainMessage(6, obj));
				}
			}).start();
		}
		return imageView;
	}
}
