/**
 * 
 */
package com.yeegol.DIYWear.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

/**
 * Utils class for file system operation
 * 
 * @author sharl
 * 
 */
public class FSUtil {

	private static final String TAG = FSUtil.class.getName();

	public static String StreamToString(InputStream is) {
		InputStreamReader reader = new InputStreamReader(is);
		StringWriter writer = new StringWriter();
		char[] buffer = new char[1024];
		int n;
		try {
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (IOException e) {
			LogUtil.logException(e, TAG);
		}
		return writer.toString();
	}

	public static Bitmap streamToBitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	public static Bitmap loadOriginalImageFromResource(Resources resources,
			int id) {
		BitmapFactory.Options optionsOfRealSize = new Options();
		optionsOfRealSize.inScaled = false; // ensure load image without any
											// scale
		return BitmapFactory.decodeResource(resources, id, optionsOfRealSize);
	}
}
