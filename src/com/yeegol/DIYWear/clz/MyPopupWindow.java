/**
 * 
 */
package com.yeegol.DIYWear.clz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

/**
 * @author sharl
 * 
 */
public class MyPopupWindow extends PopupWindow {

	MyOnDismissListener listener;

	boolean tag;

	/**
	 * 
	 */
	public MyPopupWindow() {
	}

	/**
	 * @param context
	 */
	public MyPopupWindow(Context context) {
		super(context);
	}

	/**
	 * @param contentView
	 */
	public MyPopupWindow(View contentView) {
		super(contentView);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MyPopupWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param width
	 * @param height
	 */
	public MyPopupWindow(int width, int height) {
		super(width, height);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyPopupWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param contentView
	 * @param width
	 * @param height
	 */
	public MyPopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 * @param defStyleRes
	 */
	public MyPopupWindow(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * @param contentView
	 * @param width
	 * @param height
	 * @param focusable
	 */
	public MyPopupWindow(View contentView, int width, int height,
			boolean focusable) {
		super(contentView, width, height, focusable);
	}

	/**
	 * close this window with additional signal
	 * 
	 * @param needFresh
	 */
	public void dismiss(boolean needFresh) {
		if (listener != null) {
			listener.onDismiss(needFresh);
		}
		super.dismiss();
	}

	/**
	 * @return the listener
	 */
	public MyOnDismissListener getListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(MyOnDismissListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the tag
	 */
	public boolean isTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(boolean tag) {
		this.tag = tag;
	}
}
