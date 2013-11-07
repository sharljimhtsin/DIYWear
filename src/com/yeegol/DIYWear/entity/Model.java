/**
 * 
 */
package com.yeegol.DIYWear.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.LruCache;

import com.google.gson.reflect.TypeToken;
import com.yeegol.DIYWear.clz.MyBitmap;
import com.yeegol.DIYWear.res.DataHolder;
import com.yeegol.DIYWear.util.JSONUtil;
import com.yeegol.DIYWear.util.NetUtil;
import com.yeegol.DIYWear.util.StrUtil;

/**
 * model entity class for create,modify,draw the shape
 * 
 * @author sharl
 * 
 */
public class Model {

	private static final String TAG = Model.class.getName();

	static Model instance;

	List<BrandModel> models;

	LruCache<String, Bitmap> bitmapCache;

	HashMap<String, Integer[]> layer_pos;

	LinkedList<MyBitmap> layers; // a container for model layers,from inner to
									// outer

	// all possible layers
	public static final int BG_LAYER = 0;
	public static final int MODEL_SHADOW_LAYER = 1;
	public static final int MODEL_BODY_LAYER = 2;
	public static final int MODEL_FACE_LAYER = 3;
	public static final int MODEL_HAIR_LAYER = 4;
	public static final int MODEL_UNDERWEAR_LAYER = 5;
	public static final int SHOES_LAYER = 6;
	public static final int DOWN_CLOTHES_LAYER = 7;
	public static final int UPPER_CLOTHES_LAYER = 8;
	public static final int COAT_LAYER = 9;
	public static final int SHAWL_LAYER = 10;
	public static final int ACCESSORY_LAYER = 11;

	static final int LAYER_COUNT = 12;

	public static final String MODEL_DIRECT_FRONT = "front";
	public static final String MODEL_DIRECT_BACK = "back";
	public static final String MODEL_DIRECT_PORTRAIT = "portrait";
	public static final String MODEL_DIRECT_PORTRAIT_BACK = "portrait_back";

	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	final int cacheSize = maxMemory / 8;

	/**
	 * offset on x,y axis,different on different phone screen
	 * 
	 * @deprecated bad due to hard-coded
	 */
	interface Offset {
		static final int OFFSET_OF_MODEL_ON_X = 0;
		static final int OFFSET_OF_MODEL_ON_Y = 0;
	}

	String currentDirection;

	BrandModel currentBrandModel;

	private Model() {
		bitmapCache = new LruCache<String, Bitmap>(cacheSize);
		layer_pos = new HashMap<String, Integer[]>();
		layers = new LinkedList<MyBitmap>();
		resetLinkedList();
	}

	public void resetLinkedList() {
		// fill layer container with empty value
		for (int i = 0; i < LAYER_COUNT; i++) {
			layers.add(null); // never do remove/add action after filled it
		}
	}

	public static Model getInstance() {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}

	public void putBitmapToCache(String key, Bitmap value) {
		if (bitmapCache.get(key) == null) {
			bitmapCache.put(key, value);
		}
	}

	public Bitmap getBitmapFromCache(String key) {
		return bitmapCache.get(key);
	}

	int xoff, yoff = 0;

	/**
	 * @param json
	 * @return Integer[] {x,y,width,height}
	 */
	private Integer[] getNodeValueAsListFromJson(JSONObject json) {
		int x = StrUtil.StringToInt(JSONUtil.getValueByName(json, "x"));
		int y = StrUtil.StringToInt(JSONUtil.getValueByName(json, "y"));
		int width = StrUtil.StringToInt(JSONUtil.getValueByName(json, "width"));
		int height = StrUtil.StringToInt(JSONUtil
				.getValueByName(json, "height"));
		// calculate the model's offset of height & width
		if (yoff == 0) {
			Bitmap background = layers.get(BG_LAYER).getBitmap();
			Bitmap body = layers.get(MODEL_BODY_LAYER).getBitmap();
			int xdiff = background.getWidth() - body.getWidth();
			int ydiff = background.getHeight() - body.getHeight();
			xoff = Math.abs(xdiff / 2 - x);
			yoff = Math.abs(ydiff / 2 - 100 - y);
		}
		Integer[] xy = new Integer[] { x + xoff, y + yoff, width, height };
		return xy;
	}

	/**
	 * record x,y position for basic part of model,head,hair,body etc
	 * 
	 * @param json
	 * @param direct
	 * @throws JSONException
	 */
	public void setPosDescribe(String json, String direct) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		JSONObject innerJsonObject = jsonObject.getJSONObject(direct);
		for (Iterator<?> iterator = innerJsonObject.keys(); iterator.hasNext();) {
			String type = (String) iterator.next();// face,hair,etc
			int currentPercent = DataHolder.getInstance().getProperResolution();
			// pick up right scale
			if (type.endsWith(StrUtil.intToString(currentPercent))) {
				Integer[] xy = getNodeValueAsListFromJson(innerJsonObject
						.getJSONObject(type));
				String[] item = type.split("_");
				// save the x,y position
				if (item[0].equals("body")) {
					layer_pos.put(String.valueOf(MODEL_BODY_LAYER), xy);
				} else if (item[0].equals("face")) {
					layer_pos.put(String.valueOf(MODEL_FACE_LAYER), xy);
				} else if (item[0].equals("hair")) {
					layer_pos.put(String.valueOf(MODEL_HAIR_LAYER), xy);
				} else if (item[0].equals("shadow0")) {
					layer_pos.put(String.valueOf(MODEL_SHADOW_LAYER), xy);
				} else {
					layer_pos.put(String.valueOf(MODEL_UNDERWEAR_LAYER), xy);
				}
			}
		}
	}

	/**
	 * record x,y position data of decoration layers
	 * 
	 * @param json
	 * @param layer
	 * @throws JSONException
	 */
	public void setPosDescribe(String json, int layer) throws JSONException {
		JSONObject jsonObject = new JSONObject(
				StrUtil.purgeChar(json, "[", "]"));
		for (Iterator<?> iterator = jsonObject.keys(); iterator.hasNext();) {
			String type = (String) iterator.next();// front,back,etc
			int currentPercent = DataHolder.getInstance().getProperResolution();
			if (type.endsWith(StrUtil.intToString(currentPercent))) {
				JSONObject innerJsonObject = jsonObject.getJSONObject(type);
				Integer[] xy = getNodeValueAsListFromJson(innerJsonObject);
				// save the x,y position of every direct
				layer_pos
						.put(layer + "#"
								+ StrUtil.purgeChar(type, "_" + currentPercent),
								xy); // e.g. 1#front
			}
		}
	}

	public void setLayer(int index, MyBitmap layer) {
		switch (index) {
		case BG_LAYER:
			setBackground(layer);
			break;
		case MODEL_SHADOW_LAYER:
			setModelShadow(layer);
			break;
		case MODEL_BODY_LAYER:
			setModelBody(layer);
			break;
		case MODEL_FACE_LAYER:
			setModelFace(layer);
			break;
		case MODEL_HAIR_LAYER:
			setModelHair(layer);
			break;
		case MODEL_UNDERWEAR_LAYER:
			setModelUnderwear(layer);
			break;
		case SHOES_LAYER:
			setShoes(layer);
			break;
		case SHAWL_LAYER:
			setShawl(layer);
			break;
		case DOWN_CLOTHES_LAYER:
			setDownClothes(layer);
			break;
		case UPPER_CLOTHES_LAYER:
			setUpperClothes(layer);
			break;
		case COAT_LAYER:
			setCoat(layer);
			break;
		case ACCESSORY_LAYER:
			setAccessory(layer);
			break;
		default:
			break;
		}
	}

	public void setBackground(MyBitmap b) {
		layers.set(BG_LAYER, b);
	}

	public void setModelShadow(MyBitmap b) {
		layers.set(MODEL_SHADOW_LAYER, b);
	}

	public void setModelBody(MyBitmap b) {
		layers.set(MODEL_BODY_LAYER, b);
	}

	public void setModelFace(MyBitmap b) {
		layers.set(MODEL_FACE_LAYER, b);
	}

	public void setModelHair(MyBitmap b) {
		layers.set(MODEL_HAIR_LAYER, b);
	}

	public void setModelUnderwear(MyBitmap b) {
		layers.set(MODEL_UNDERWEAR_LAYER, b);
	}

	public void setShoes(MyBitmap b) {
		layers.set(SHOES_LAYER, b);
	}

	public void setShawl(MyBitmap b) {
		layers.set(SHAWL_LAYER, b);
	}

	public void setDownClothes(MyBitmap b) {
		layers.set(DOWN_CLOTHES_LAYER, b);
	}

	public void setUpperClothes(MyBitmap b) {
		layers.set(UPPER_CLOTHES_LAYER, b);
	}

	public void setCoat(MyBitmap b) {
		layers.set(COAT_LAYER, b);
	}

	public void setAccessory(MyBitmap b) {
		layers.set(ACCESSORY_LAYER, b);
	}

	/**
	 * @return model wear shoes or not
	 */
	private boolean haveShoes() {
		return layers.get(SHOES_LAYER) != null;
	}

	/**
	 * @return model is a women or not
	 */
	private boolean isFemale() {
		return currentBrandModel.getGender() == 2;
	}

	/**
	 * draw the model with all elements user selected
	 * 
	 * @param canvas
	 *            obtain from the surface view
	 * 
	 * @param context
	 *            removed,deprecated
	 * 
	 * @return final bitmap,for export further
	 */
	public Bitmap drawModel(Canvas canvas) {
		Bitmap baseBitmap = Bitmap.createBitmap(canvas.getWidth(),
				canvas.getHeight(), Config.ARGB_8888);
		Canvas baseCanvas = new Canvas(baseBitmap);
		int i = 0;
		for (MyBitmap b : layers) {
			// skip empty layer
			if (b == null) {
				i++;
				continue;
			}
			final int j = i;
			switch (DataHolder.getInstance().getMappingLayer(i)) {
			case BG_LAYER:
				baseCanvas.drawBitmap(b.getBitmap(), 0f, 0f, null);
				break;
			case MODEL_SHADOW_LAYER:
				baseCanvas.drawBitmap(b.getBitmap(),
						layer_pos.get(String.valueOf(j))[0],
						layer_pos.get(String.valueOf(j))[1], null);
				break;
			case MODEL_BODY_LAYER:
				baseCanvas.drawBitmap(b.getBitmap(),
						layer_pos.get(String.valueOf(j))[0],
						layer_pos.get(String.valueOf(j))[1], null);
				break;
			case MODEL_FACE_LAYER:
				baseCanvas.drawBitmap(b.getBitmap(),
						layer_pos.get(String.valueOf(j))[0],
						layer_pos.get(String.valueOf(j))[1], null);
				break;
			case MODEL_HAIR_LAYER:
				baseCanvas.drawBitmap(b.getBitmap(),
						layer_pos.get(String.valueOf(j))[0],
						layer_pos.get(String.valueOf(j))[1], null);
				break;
			case MODEL_UNDERWEAR_LAYER:
				baseCanvas.drawBitmap(
						haveShoes() && isFemale() ? b.getBitmapWithCutOff() : b
								.getBitmap(),
						layer_pos.get(String.valueOf(j))[0], layer_pos
								.get(String.valueOf(j))[1], null);
				break;
			case SHOES_LAYER:
				baseCanvas.drawBitmap(
						b.getBitmapWithDirection(currentDirection),
						layer_pos.get(j + "#" + currentDirection)[0],
						layer_pos.get(j + "#" + currentDirection)[1], null);
				break;
			case SHAWL_LAYER:
				baseCanvas.drawBitmap(
						b.getBitmapWithDirection(currentDirection),
						layer_pos.get(j + "#" + currentDirection)[0],
						layer_pos.get(j + "#" + currentDirection)[1], null);
				break;
			case DOWN_CLOTHES_LAYER:
				baseCanvas.drawBitmap(
						b.getBitmapWithDirection(currentDirection),
						layer_pos.get(j + "#" + currentDirection)[0],
						layer_pos.get(j + "#" + currentDirection)[1], null);
				break;
			case UPPER_CLOTHES_LAYER:
				baseCanvas.drawBitmap(
						b.getBitmapWithDirection(currentDirection),
						layer_pos.get(j + "#" + currentDirection)[0],
						layer_pos.get(j + "#" + currentDirection)[1], null);
				break;
			case COAT_LAYER:
				baseCanvas.drawBitmap(
						b.getBitmapWithDirection(currentDirection),
						layer_pos.get(j + "#" + currentDirection)[0],
						layer_pos.get(j + "#" + currentDirection)[1], null);
				break;
			case ACCESSORY_LAYER:
				baseCanvas.drawBitmap(
						b.getBitmapWithDirection(currentDirection),
						layer_pos.get(j + "#" + currentDirection)[0],
						layer_pos.get(j + "#" + currentDirection)[1], null);
				break;
			default:
				break;
			}
			i++;
		}
		canvas.drawBitmap(baseBitmap, new Matrix(), null);
		return baseBitmap;
	}

	public static List<BrandModel> doBrandModelgetList() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		NameValuePair method = new BasicNameValuePair("method",
				"BrandModel.getList");
		pairs.add(method);
		String content = NetUtil.getTextFromWeb(NetUtil.buildURL(pairs),
				NetUtil.DOMAIN_API_PURE);
		return JSONUtil.getObjectInArray(content,
				new TypeToken<List<BrandModel>>() {
				}.getType());
	}

	/**
	 * BrandModel entity class mapping API
	 * 
	 * @author sharl
	 * 
	 * @detail "id":"9","name":"nv","gender":"2","ageGroup":"2","preview":
	 *         "6\/model\/9\/"
	 */
	public class BrandModel {
		int id;
		String name;
		int gender;
		int ageGroup;
		String preview;

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
	}

	/**
	 * @return the models
	 */
	public List<BrandModel> getModels() {
		return models;
	}

	/**
	 * @param models
	 *            the models to set
	 */
	public void setModels(List<BrandModel> models) {
		this.models = models;
	}

	/**
	 * @return the currentDirection
	 */
	public String getCurrentDirection() {
		return currentDirection;
	}

	/**
	 * @param currentDirection
	 *            the currentDirection to set
	 */
	public void setCurrentDirection(String currentDirection) {
		this.currentDirection = currentDirection;
	}

	/**
	 * @return the layer_pos
	 */
	public HashMap<String, Integer[]> getLayer_pos() {
		return layer_pos;
	}

	/**
	 * @param layer_pos
	 *            the layer_pos to set
	 */
	public void setLayer_pos(HashMap<String, Integer[]> layer_pos) {
		this.layer_pos = layer_pos;
	}

	/**
	 * @return the layers
	 */
	public LinkedList<MyBitmap> getLayers() {
		return layers;
	}

	/**
	 * @param layers
	 *            the layers to set
	 */
	public void setLayers(LinkedList<MyBitmap> layers) {
		this.layers = layers;
	}

	/**
	 * @return the currentBrandModel
	 */
	public BrandModel getCurrentBrandModel() {
		return currentBrandModel;
	}

	/**
	 * @param currentBrandModel
	 *            the currentBrandModel to set
	 */
	public void setCurrentBrandModel(BrandModel currentBrandModel) {
		this.currentBrandModel = currentBrandModel;
	}
}
