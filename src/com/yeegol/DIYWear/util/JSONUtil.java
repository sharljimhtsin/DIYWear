/**
 * 
 */
package com.yeegol.DIYWear.util;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * Utils class for parse JSON format
 * 
 * @author sharl
 * 
 */
public class JSONUtil {

	private static final String TAG = JSONUtil.class.getName();

	/**
	 * @param json
	 * @return ret
	 */
	public static String getValueByName(String json, String name) {
		String ret = "";

		try {
			JSONObject jsonObject = new JSONObject(json);
			ret = String.valueOf(jsonObject.get(name));
		} catch (JSONException e) {
			LogUtil.logException(e, TAG);
		}
		return ret;
	}

	public static <T> T getObjectInArray(String json, Type t) {
		Gson gson = new Gson();
		return gson.fromJson(json, t);
	}

	public static <T> T getObject(String json, Class<T> clz) {
		Gson gson = new Gson();
		return gson.fromJson(json, clz);
	}

}
