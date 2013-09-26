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
 * @author sharl
 * @detail "id":"7441","name":
 *         "\u7537\u6b3e\u767d\u8272\u65f6\u5c1a\u7b80\u7ea6\u4f11\u95f2\u978b"
 *         ,"preview":null,"tagPrice":"0.00","salePrice":"293.00"
 * 
 *         "categoryName":"shangzhuang","brandCnName":"\u7eaf\u4e4b\u8272",
 *         "brandEnName"
 *         :"\u7eaf\u4e4b\u82721","goodsName":"\u4e0a\u8863","preview":
 *         "6\/goods\/7519\/","tagPrice":"100.00","salePrice":"2000.00","url":""
 */
public class Goods {
	int id;
	String name;
	String preview;
	double tagPrice;
	double salePrice;
	String categoryName;
	String brandCnName;
	String brandEnName;
	String goodsName;
	String url;

	/**
	 * query goods list from web
	 * 
	 * @detail 
	 *         http://api.ihomebay.com/?key=d3c111daa7f83ab4b6930e2e45113dd4&method
	 *         = Goods.getList&page=1&size=20&id=13
	 * @param page
	 * @param size
	 * @param categoryId
	 * @param brandId
	 * @param gender
	 * @param ageGroup
	 * @param name
	 * @param priceMin
	 * @param priceMax
	 * @param attribute
	 * @return goods list
	 */
	public static List<Goods> doGoodsgetList(int page, int size,
			int categoryId, String brandId, int gender, int ageGroup,
			String name, int priceMin, int priceMax, String attribute) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method", "Goods.getList");
		pairs.add(method);
		NameValuePair pagee = new BasicNameValuePair("page",
				StrUtil.intToString(page));
		pairs.add(pagee);
		NameValuePair sizee = new BasicNameValuePair("size",
				StrUtil.intToString(size));
		pairs.add(sizee);
		if (categoryId != -1) {
			NameValuePair categoryIdd = new BasicNameValuePair("categoryId",
					StrUtil.intToString(categoryId));
			pairs.add(categoryIdd);
		}
		if (!brandId.equals("-1")) {
			NameValuePair brandIdss = new BasicNameValuePair("brandId", brandId);
			pairs.add(brandIdss);
		}
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
		// there is no goods item under this type
		if (content.equals("false")) {
			return null;
		}
		return JSONUtil.getObjectInArray(content, new TypeToken<List<Goods>>() {
		}.getType());
	}

	/**
	 * query a whole goods by id
	 * 
	 * @param id
	 * @return single goods entity
	 */
	public static Goods doGoodsgetInfo(int id) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method", "Goods.getInfo");
		pairs.add(method);
		NameValuePair ids = new BasicNameValuePair("id", String.valueOf(id));
		pairs.add(ids);
		String content = NetUtil.getTextFromWeb(NetUtil.buildURL(pairs),
				NetUtil.DOMAIN_API_PURE);
		return JSONUtil.getObject(content, Goods.class);
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the preview
	 */
	public String getPreview() {
		return preview;
	}

	/**
	 * @param preview
	 *            the preview to set
	 */
	public void setPreview(String preview) {
		this.preview = preview;
	}

	/**
	 * @return the tagPrice
	 */
	public double getTagPrice() {
		return tagPrice;
	}

	/**
	 * @param tagPrice
	 *            the tagPrice to set
	 */
	public void setTagPrice(double tagPrice) {
		this.tagPrice = tagPrice;
	}

	/**
	 * @return the salePrice
	 */
	public double getSalePrice() {
		return salePrice;
	}

	/**
	 * @param salePrice
	 *            the salePrice to set
	 */
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName
	 *            the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the brandCnName
	 */
	public String getBrandCnName() {
		return brandCnName;
	}

	/**
	 * @param brandCnName
	 *            the brandCnName to set
	 */
	public void setBrandCnName(String brandCnName) {
		this.brandCnName = brandCnName;
	}

	/**
	 * @return the brandEnName
	 */
	public String getBrandEnName() {
		return brandEnName;
	}

	/**
	 * @param brandEnName
	 *            the brandEnName to set
	 */
	public void setBrandEnName(String brandEnName) {
		this.brandEnName = brandEnName;
	}

	/**
	 * @return the goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}

	/**
	 * @param goodsName
	 *            the goodsName to set
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "name is " + name + "\ntagPrice is " + tagPrice
				+ "\nsalePrice is " + salePrice + "\ncategoryName is "
				+ categoryName + "\nbrandCnName is " + brandCnName
				+ "\nbrandEnName is " + brandEnName + "\ngoodsName is "
				+ goodsName;
	}
}
