/**
 * 
 */
package com.yeegol.DIYWear.clz;

import android.widget.PopupWindow.OnDismissListener;

/**
 * a modified listener base on {@link OnDismissListener}
 * 
 * @author sharl
 * 
 */
public interface MyOnDismissListener extends OnDismissListener {

	static final String TAG = MyOnDismissListener.class.getName();

	void onDismiss(boolean needRefresh);
}
