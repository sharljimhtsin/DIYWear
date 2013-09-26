/**
 * 
 */
package com.yeegol.DIYWear.util;

/**
 * Util class for String process
 * 
 * @author sharl
 * 
 */
public class StrUtil {

	private static final String TAG = StrUtil.class.getName();

	public static int StringToInt(String s) {
		return Integer.valueOf(s);
	}

	public static int ObjToInt(Object o) {
		return StringToInt(String.valueOf(o));
	}

	public static String objToString(Object o) {
		return String.valueOf(o);
	}

	public static String intToString(int i) {
		return String.valueOf(i);
	}

	public static String dobToString(double d) {
		return String.valueOf(d);
	}

	public static int dobToInt(double d) {
		return (int) d;
	}

	public static String charToString(CharSequence c) {
		return c.toString();
	}
}
