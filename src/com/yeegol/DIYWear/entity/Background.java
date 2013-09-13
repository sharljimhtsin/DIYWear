/**
 * 
 */
package com.yeegol.DIYWear.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.yeegol.DIYWear.util.NetUtil;

/**
 * @author sharl
 * 
 */
public class Background {
	public static String doBackgroundgetList(int page, int size) {
		List<NameValuePair> pair = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method",
				"Background.getList");
		NameValuePair p = new BasicNameValuePair("page", String.valueOf(page));
		NameValuePair s = new BasicNameValuePair("size", String.valueOf(size));
		pair.add(method);
		pair.add(p);
		pair.add(s);
		return NetUtil.getTextFromWeb(NetUtil.buildURL(pair), "");
	}
}
