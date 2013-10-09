/**
 * 
 */
package com.yeegol.DIYWear.util;

import java.util.Date;

/**
 * @author sharl
 * 
 */
public class DateUtil {

	private static final String TAG = DateUtil.class.getName();

	public static String getTimeStamp() {
		Date now = new Date();
		return StrUtil.longToString(now.getTime());
	}

}
