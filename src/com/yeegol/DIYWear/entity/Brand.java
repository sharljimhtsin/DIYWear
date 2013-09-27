/**
 * 
 */
package com.yeegol.DIYWear.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.reflect.TypeToken;
import com.yeegol.DIYWear.util.JSONUtil;
import com.yeegol.DIYWear.util.NetUtil;
import com.yeegol.DIYWear.util.StrUtil;

/**
 * Brand entity class mapping API
 * 
 * @author sharl
 * @detail {"id":"2","cnName":"\u963f\u6851\u5a1c","enName":"Azona","logo":
 *         "7\/brand\/2\/logo.jpg"}
 */
public class Brand {

	private static final String TAG = Brand.class.getName();

	int id;
	String cnName;
	String enName;
	String logo;

	/**
	 * @param categoryId
	 * @param name
	 * @param gender
	 * @param ageGroup
	 * @param page
	 * @param size
	 * @return brand in array
	 */
	public static List<Brand> doBrandgetList(int categoryId, String name,
			int gender, int ageGroup, int page, int size) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method", "Brand.getList");
		pairs.add(method);
		NameValuePair categoryIdd = new BasicNameValuePair("categoryId",
				StrUtil.intToString(categoryId));
		pairs.add(categoryIdd);
		NameValuePair genderr = new BasicNameValuePair("gender",
				StrUtil.intToString(gender));
		pairs.add(genderr);
		NameValuePair ageGroupp = new BasicNameValuePair("ageGroup",
				StrUtil.intToString(ageGroup));
		pairs.add(ageGroupp);
		// TODO: more condition
		String content = NetUtil.getTextFromWeb(NetUtil.buildURL(pairs),
				NetUtil.DOMAIN_API_PURE);
		content = JSONUtil.getValueByName(content, "rows");
		// there is no goods item under this category
		if (content.equals("false")) {
			return null;
		}
		return JSONUtil.getObjectInArray(content, new TypeToken<List<Brand>>() {
		}.getType());
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the cnName
	 */
	public String getCnName() {
		return cnName;
	}

	/**
	 * @param cnName
	 *            the cnName to set
	 */
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	/**
	 * @return the enName
	 */
	public String getEnName() {
		return enName;
	}

	/**
	 * @param enName
	 *            the enName to set
	 */
	public void setEnName(String enName) {
		this.enName = enName;
	}

	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo
	 *            the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}
}
