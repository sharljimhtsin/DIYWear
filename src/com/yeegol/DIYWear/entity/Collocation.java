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
 * Collocation entity class mapping API
 * 
 * @author sharl
 * @detail 
 *         {"id":"1","name":"OL\u98ce\u683c","username":"pureadmin","preview":""}
 */
public class Collocation {

	private static final String TAG = Collocation.class.getName();

	int id;
	String name;
	String username;
	String photo;

	public static List<Collocation> doCollocationgetList(int brandId,
			int goodsId, int gender, String season, String ageGroup, int page,
			int size) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method",
				"Collocation.getList");
		pairs.add(method);
		if (brandId != -1) {
			NameValuePair brandIdd = new BasicNameValuePair("brandId",
					StrUtil.intToString(goodsId));
			pairs.add(brandIdd);
		}
		if (goodsId != -1) {
			NameValuePair goodsIdd = new BasicNameValuePair("goodsId",
					StrUtil.intToString(goodsId));
			pairs.add(goodsIdd);
		}
		NameValuePair genderr = new BasicNameValuePair("gender",
				StrUtil.intToString(gender));
		pairs.add(genderr);
		if (!"".equals(season)) {
			NameValuePair seasonn = new BasicNameValuePair("season", season);
			pairs.add(seasonn);
		}
		NameValuePair ageGroupp = new BasicNameValuePair("ageGroup", ageGroup);
		pairs.add(ageGroupp);
		NameValuePair pagee = new BasicNameValuePair("page",
				StrUtil.intToString(page));
		pairs.add(pagee);
		NameValuePair sizee = new BasicNameValuePair("size",
				StrUtil.intToString(size));
		pairs.add(sizee);
		String json = NetUtil.getTextFromWeb(NetUtil.buildURL(pairs),
				NetUtil.DOMAIN_API_PURE);
		json = JSONUtil.getValueByName(json, "rows");
		// there is no recommend goods
		if (json.equals("false")) {
			return null;
		}
		return JSONUtil.getObjectInArray(json,
				new TypeToken<List<Collocation>>() {
				}.getType());
	}

	public static Model doCollocationgetInfo(int id) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method",
				"Collocation.getInfo");
		pairs.add(method);
		NameValuePair idd = new BasicNameValuePair("id",
				StrUtil.intToString(id));
		pairs.add(idd);
		String json = NetUtil.getTextFromWeb(NetUtil.buildURL(pairs),
				NetUtil.DOMAIN_API_PURE);
		return JSONUtil.getObject(json, Model.class);
	}

	/**
	 * @author sharl
	 * @detail {"id":"2","name":"\u7b80\u7ea6\u98ce","preview":"","username":
	 *         "pureadmin"
	 *         ,"brandIds":"1","goodsIds":"9,22","season":"1,3","gender"
	 *         :"2","ageGroup":"2","description":"
	 * 
	 *         \u7b80\u7ea6\u98ce\u683c\u7684\u642d\u914d<\/p>\n"}
	 */
	public class Model {
		int id;
		String name;
		String preview;
		String username;
		String brandIds;
		String goodsIds;
		String season;
		int gender;
		int ageGroup;
		String description;

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
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @param username
		 *            the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * @return the brandIds
		 */
		public String getBrandIds() {
			return brandIds;
		}

		/**
		 * @param brandIds
		 *            the brandIds to set
		 */
		public void setBrandIds(String brandIds) {
			this.brandIds = brandIds;
		}

		/**
		 * @return the goodsIds
		 */
		public String getGoodsIds() {
			return goodsIds;
		}

		/**
		 * @param goodsIds
		 *            the goodsIds to set
		 */
		public void setGoodsIds(String goodsIds) {
			this.goodsIds = goodsIds;
		}

		/**
		 * @return the season
		 */
		public String getSeason() {
			return season;
		}

		/**
		 * @param season
		 *            the season to set
		 */
		public void setSeason(String season) {
			this.season = season;
		}

		/**
		 * @return the gender
		 */
		public int getGender() {
			return gender;
		}

		/**
		 * @param gender
		 *            the gender to set
		 */
		public void setGender(int gender) {
			this.gender = gender;
		}

		/**
		 * @return the ageGroup
		 */
		public int getAgeGroup() {
			return ageGroup;
		}

		/**
		 * @param ageGroup
		 *            the ageGroup to set
		 */
		public void setAgeGroup(int ageGroup) {
			this.ageGroup = ageGroup;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @param description
		 *            the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}

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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the preview
	 */
	public String getPreview() {
		return photo;
	}

	/**
	 * @param preview
	 *            the preview to set
	 */
	public void setPreview(String preview) {
		this.photo = preview;
	}

	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}

	/**
	 * @param photo
	 *            the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}

}
