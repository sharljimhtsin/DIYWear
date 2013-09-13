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

/**
 * @author sharl
 * @detail "title":{"id":"11","dressMapId":"0","name":"xiezi","parentId":"0"},
 *         "children":false
 * 
 */
public class Category {
	Model title;
	List<Category> children;

	public static List<Category> doCategorygetTree() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method",
				"Category.getTree");
		pairs.add(method);
		String json = NetUtil.getTextFromWeb(NetUtil.buildURL(pairs),
				NetUtil.DOMAIN_API_PURE);
		// purge invalidate children node
		json = json.replaceAll(",\"children\":false", "");
		return JSONUtil.getObjectInArray(json, new TypeToken<List<Category>>() {
		}.getType());
	}

	public static List<Category> doCategorygetChildren(int parentId) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method",
				"Category.getChildren");
		pairs.add(method);
		NameValuePair pid = new BasicNameValuePair("id",
				String.valueOf(parentId));
		pairs.add(pid);
		String json = NetUtil.getTextFromWeb(NetUtil.buildURL(pairs),
				NetUtil.DOMAIN_API_PURE);
		// purge invalidate children node
		json = json.replaceAll(",\"children\":false", "");
		return JSONUtil.getObjectInArray(json, new TypeToken<List<Category>>() {
		}.getType());
	}

	public class Model {
		int id;
		int dressMapId;
		String name;
		int parentId;

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
		 * @return the dressMapId
		 */
		public int getDressMapId() {
			return dressMapId;
		}

		/**
		 * @param dressMapId
		 *            the dressMapId to set
		 */
		public void setDressMapId(int dressMapId) {
			this.dressMapId = dressMapId;
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
		 * @return the parentId
		 */
		public int getParentId() {
			return parentId;
		}

		/**
		 * @param parentId
		 *            the parentId to set
		 */
		public void setParentId(int parentId) {
			this.parentId = parentId;
		}
	}

	/**
	 * @return the title
	 */
	public Model getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(Model title) {
		this.title = title;
	}

	/**
	 * @return the children
	 */
	public List<Category> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<Category> children) {
		this.children = children;
	}
}
