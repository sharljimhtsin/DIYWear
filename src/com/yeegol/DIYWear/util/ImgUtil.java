/**
 * 
 */
package com.yeegol.DIYWear.util;

import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * utils class for image process
 * 
 * @author sharl
 * 
 */
public class ImgUtil {

	private static final String TAG = ImgUtil.class.getName();

	/**
	 * resize image
	 * 
	 * @param b
	 * @return bitmap in full screen size
	 */
	public static Bitmap scaleBitmapToFullScreen(Bitmap b) {
		return Bitmap.createScaledBitmap(b, Resources.getSystem()
				.getDisplayMetrics().widthPixels, Resources.getSystem()
				.getDisplayMetrics().heightPixels, false);
	}
}
