/**
 * 
 */
package com.yeegol.DIYWear.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;

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

	/**
	 * write bitmap as image to local phone's private data area,e.g.
	 * /data/data/PACKAGE.NAME/...
	 * 
	 * @param c
	 * @param image
	 * @param fileName
	 * @return success or fail
	 * @throws IOException
	 */
	public static boolean writeBitmapToFile(Context c, Bitmap image,
			String fileName) throws IOException {
		// use file in internal storage
		FileOutputStream fos = c.openFileOutput(fileName, Context.MODE_PRIVATE);
		boolean success = image.compress(CompressFormat.JPEG, 80, fos);
		fos.close();
		return success;
	}

	/**
	 * write bitmap as image to local phone,can be SdCard
	 * 
	 * @param c
	 * @param image
	 * @param fileName
	 * @param directoryName
	 * @return success or not
	 * @throws IOException
	 */
	public static boolean writeBitmapToFileOnSdcard(Context c, Bitmap image,
			String fileName, String directoryName) throws IOException {
		directoryName = Environment.getExternalStorageDirectory().getPath()
				+ directoryName;
		File target = new File(directoryName);
		createIfNotExist(target);
		target = new File(target, fileName);
		DeleteIfExist(target);
		FileOutputStream fos = new FileOutputStream(target);
		boolean success = image.compress(CompressFormat.JPEG, 80, fos);
		fos.close();
		return success;
	}

	public static void DeleteIfExist(File fileOrDir) {
		if (fileOrDir.exists()) {
			if (fileOrDir.isFile()) {
				fileOrDir.delete();
			} else {
				for (File file : fileOrDir.listFiles()) {
					DeleteIfExist(file);
				}
			}
		}
	}

	public static void createIfNotExist(File fileOrDir) throws IOException {
		if (!fileOrDir.exists()) {
			if (fileOrDir.isFile()) {
				fileOrDir.createNewFile();
			} else {
				fileOrDir.mkdir();
			}
		}
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
