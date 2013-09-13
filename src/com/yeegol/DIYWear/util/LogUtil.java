/**
 * 
 */
package com.yeegol.DIYWear.util;

import android.util.Log;

/**
 * @author sharl
 * 
 */
public class LogUtil {

	private static String TAG = LogUtil.class.getName();

	public static void logDebug(String msg, String tag) {
		Log.d(tag, msg);
	}

	public static void logError(String msg, String tag) {
		Log.e(tag, msg);
	}

	public static void logWarn(String msg, String tag) {
		Log.w(tag, msg);
	}

	public static void logInfo(String msg, String tag) {
		Log.i(tag, msg);
	}

	public static void log(String msg) {
		Log.d(msg, TAG);
	}

	public static void logException(Exception e, String tag) {
		for (StackTraceElement element : e.getStackTrace()) {
			logError(element.toString(), tag);
		}
	}

}
