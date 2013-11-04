/**
 * 
 */
package com.yeegol.DIYWear.clz;

import java.util.List;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.yeegol.DIYWear.entity.Model.BrandModel;
import com.yeegol.DIYWear.util.NetUtil;

/**
 * @author sharl
 * 
 */
public class MyLinearLayout extends LinearLayout {

	Context context;

	List<?> list;

	OnClickListener listener;

	public MyLinearLayout(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * @return the list
	 */
	public List<?> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<?> list) {
		this.list = list;
	}

	/**
	 * @return the listener
	 */
	public OnClickListener getListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}

	public void bindUI() {
		LinearLayout viewRoot = this;
		for (Object o : list) {
			if (o instanceof Integer) {
				int i = (Integer) o;
				ImageView iv = new ImageView(context);
				iv.setTag(i);
				iv.setBackgroundColor(i);
				iv.setScaleType(ScaleType.CENTER_CROP);
				iv.setLayoutParams(new LayoutParams(100, 200));
				iv.setOnClickListener(listener);
				viewRoot.addView(iv);
			} else {
				BrandModel m = (BrandModel) o;
				MyImageView miv = new MyImageView(context);
				miv.setTag(m);
				miv.setURL(NetUtil.buildURLForThumb(m.getPreview()));
				miv.setScaleType(ScaleType.CENTER_CROP);
				miv.setLayoutParams(new LayoutParams(100, 200));
				miv.setOnClickListener(listener);
				viewRoot.addView(miv);
			}
		}
	}

}
