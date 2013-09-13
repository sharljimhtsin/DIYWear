/**
 * 
 */
package com.yeegol.DIYWear.res;

import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;

import com.yeegol.DIYWear.R;
import com.yeegol.DIYWear.entity.Model;

/**
 * a static,singleton class for store some never changed data
 * 
 * @author sharl
 * 
 */
public class DataHolder {

	static DataHolder instance;

	HashMap<String, Integer> charMapping;

	static Context context;

	private DataHolder() {
		charMapping = new HashMap<String, Integer>();
		charMapping.put("xiezi", R.string.main_type_shoes);
		charMapping.put("weijin", R.string.main_type_shawl);
		charMapping.put("waitao", R.string.main_type_coat);
		charMapping.put("xiazhuang", R.string.main_type_upper_clothes);
		charMapping.put("shangzhuang", R.string.main_type_down_clothes);
		charMapping.put("fuzhuang", R.string.main_type_wears);
		charMapping.put("裤子", Model.DOWN_CLOTHES_LAYER);
		charMapping.put("外套", Model.COAT_LAYER);
		charMapping.put("针织衫", Model.UPPER_CLOTHES_LAYER);
		charMapping.put("衬衫", Model.UPPER_CLOTHES_LAYER);
		charMapping.put("T恤", Model.UPPER_CLOTHES_LAYER);
		charMapping.put("围巾", Model.SHAWL_LAYER);
	}

	public int getResIdByPinyin(String s) {
		return charMapping.get(s);
	}

	public int getMappingLayerByName(String s) {
		return charMapping.get(s);
	}

	public static DataHolder getInstance() {
		if (instance == null) {
			instance = new DataHolder();
		}
		return instance;
	}

	public static void init(Context c) {
		context = c;
		getInstance();
	}

	public Resources getResource() {
		return context.getResources();
	}

	public Context getContext() {
		return context;
	}
}