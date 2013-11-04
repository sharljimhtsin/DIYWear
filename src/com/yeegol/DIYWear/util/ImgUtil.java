/**
 * 
 */
package com.yeegol.DIYWear.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.view.View;

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

	public static Bitmap createBitmapWithSingleColor(int color) {
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
		// fill full with color
		bitmap.eraseColor(color);
		return bitmap;
	}

	/**
	 * snapshot of specify view,NOT WORK on SurfaceView
	 * 
	 * @param view
	 * @return view's looking in bitmap
	 */
	public static Bitmap snapshotOfView(View view) {
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}
}
