package com.yeegol.DIYWear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yeegol.DIYWear.clz.MyBitmap;
import com.yeegol.DIYWear.clz.MySurfaceView;
import com.yeegol.DIYWear.entity.Category;
import com.yeegol.DIYWear.entity.Goods;
import com.yeegol.DIYWear.entity.Model;
import com.yeegol.DIYWear.entity.Model.BrandModel;
import com.yeegol.DIYWear.res.DataHolder;
import com.yeegol.DIYWear.util.LogUtil;
import com.yeegol.DIYWear.util.NetUtil;
import com.yeegol.DIYWear.util.NotificUtil;
import com.yeegol.DIYWear.util.StrUtil;

/**
 * main window for display,modify the model's looking
 * 
 * @author sharl
 * 
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback,
		OnClickListener, OnDismissListener, OnTouchListener {

	private static final String TAG = MainActivity.class.getName();

	MySurfaceView mSurfaceView;

	LinearLayout mTypeLayout;

	LinearLayout mFunctionLayout;

	LinearLayout mMainLayout;

	LinearLayout mListLayout;

	Handler mHandler;

	Model.BrandModel mBrandModel;

	List<Category> mCategoryList;

	List<Goods> mGoodsList;

	Context mContext;

	String mCurrentDirect; // for further load

	int mCurrentLayer;

	Goods mCurrentGoods;

	ProgressDialog mProgressDialog;

	SparseArray<Goods> mTempCart;

	List<Goods> mCart;

	Button mCartButton;

	PopupWindow mPopupWindow;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// initial variables
		mContext = this;
		mCurrentDirect = Model.MODEL_DIRECT_FRONT;
		mTempCart = new SparseArray<Goods>();
		mCart = new ArrayList<Goods>();
		DataHolder.init(mContext);
		// sync with Model class
		Model.getInstance().setCurrentDirection(mCurrentDirect);
		mHandler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					mHandler.sendMessage(mHandler.obtainMessage(97));
					prepareModelList();
					prepareLeftSidebar();
					prepareRightSidebar();
					break;
				case 1:
					pickUpIfNeed();
					break;
				case 2:
					mHandler.sendMessage(mHandler.obtainMessage(97));
					prepareLayer(mCurrentDirect);
					break;
				case 3:
					drawModel();
					mHandler.sendMessage(mHandler.obtainMessage(98));
					break;
				case 4:
					buildLeftSidebar();
					break;
				case 5:
					buildBottomBar();
					break;
				case 6:
					ImageView i = (ImageView) ((Object[]) msg.obj)[0];
					Bitmap b = (Bitmap) ((Object[]) msg.obj)[1];
					i.setImageBitmap(b);
					break;
				case 7:
					mCurrentGoods = mTempCart.get(StrUtil.ObjToInt(msg.obj));
					prepareGoodsInfoWindows();
					break;
				case 97:
					mProgressDialog.show();
					break;
				case 98:
					mProgressDialog.hide();
					break;
				case 99:
					mProgressDialog.dismiss();
					break;
				default:
					break;
				}
				return true;
			}
		});
		// initial controls
		prepareProgressDialog();
		ImageButton showTypeButton = (ImageButton) findViewById(R.id.Button_showType);
		ImageButton showFunctionButton = (ImageButton) findViewById(R.id.Button_showFunction);
		// get controls
		mSurfaceView = (MySurfaceView) findViewById(R.id.surface_main);
		mTypeLayout = (LinearLayout) findViewById(R.id.LinearLayout_goodsType);
		mFunctionLayout = (LinearLayout) findViewById(R.id.LinearLayout_functionArea);
		mMainLayout = (LinearLayout) findViewById(R.id.LinearLayout_main);
		mListLayout = (LinearLayout) findViewById(R.id.LinearLayout_goodsList);
		mCartButton = (Button) findViewById(R.id.Button_cart);
		// set listener
		showTypeButton.setOnClickListener(this);
		showFunctionButton.setOnClickListener(this);
		mCartButton.setOnClickListener(this);
		mSurfaceView.getHolder().addCallback(this);
		mSurfaceView.setOnTouchListener(this);
		// trigger
		mHandler.sendMessage(mHandler.obtainMessage(0));
	}

	/**
	 * get model list from web
	 */
	private void prepareModelList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Model.getInstance().setModels(Model.doBrandModelgetList());
				mHandler.sendMessage(mHandler.obtainMessage(1));
			}
		}).start();
	}

	/**
	 * build a progress dialog for display
	 */
	private void prepareProgressDialog() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setTitle(R.string.progress_dialog_title);
		mProgressDialog.setMessage(getText(R.string.progress_dialog_content));
	}

	/**
	 * check the model's quality
	 */
	private void pickUpIfNeed() {
		List<BrandModel> list = Model.getInstance().getModels();
		if (list != null && list.size() > 1) {
			// TODO: popup a dialogue here
			mBrandModel = list.get(1);
		} else {
			mBrandModel = list.get(0);
		}
		mHandler.sendMessage(mHandler.obtainMessage(2));
	}

	/**
	 * get all basic layers of selected model & their position descriptor
	 * 
	 * @param direcetion
	 *            front in default.back,portrait,portrait_back available
	 */
	private void prepareLayer(final String direcetion) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String tmp1 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "shadow0.png");
				Bitmap tmp2 = NetUtil.getImageFromWeb(tmp1,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelShadow(
						new MyBitmap(tmp2, tmp1, direcetion));

				String tmp3 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "body.png");
				Bitmap tmp4 = NetUtil.getImageFromWeb(tmp3,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelBody(
						new MyBitmap(tmp4, tmp3, direcetion));

				String tmp5 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "face.png");
				Bitmap tmp6 = NetUtil.getImageFromWeb(tmp5,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelFace(
						new MyBitmap(tmp6, tmp5, direcetion));

				String tmp7 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "hair.png");
				Bitmap tmp8 = NetUtil.getImageFromWeb(tmp7,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelHair(
						new MyBitmap(tmp8, tmp7, direcetion));

				String tmp9 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "underwear.png");
				Bitmap tmp10 = NetUtil.getImageFromWeb(tmp9,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelUnderwear(
						new MyBitmap(tmp10, tmp9, direcetion));
				// get position description
				Model.getInstance().setPosDescribe(
						NetUtil.getTextFromWeb(
								NetUtil.buildURLForBasicConf(
										mBrandModel.getPreview(), direcetion),
								NetUtil.DOMAIN_FILE_PURE));
				mHandler.sendMessage(mHandler.obtainMessage(3));
			}
		}).start();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawModel();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button_showType:
			toggleVisibilty(mTypeLayout);
			break;
		case R.id.Button_showFunction:
			toggleVisibilty(mFunctionLayout);
			break;
		case R.id.Button_switchToMan:
			break;
		case R.id.Button_switchToWoman:
			break;
		case R.id.Button_turnBack:
			toggleDirection();
			mHandler.sendMessage(mHandler.obtainMessage(2));
			break;
		case R.id.Button_undo:
			break;
		case R.id.Button_save:
			break;
		case R.id.Button_share:
			break;
		case R.id.Button_cart:
			if (toggleButton(v, false)) {
				prepareCartWindows();
			} else {
				// make button UN-click-able
				v.setClickable(false);
				// push all goods to REAL cart
				for (int i = 0; i < mTempCart.size(); i++) {
					mCart.add(mTempCart.valueAt(i));
				}
				NotificUtil
						.showShortToast(R.string.toast_all_add_to_cart_successlly);
			}
			break;
		case R.id.Button_item_cart_add:
			Goods g = mTempCart.valueAt(StrUtil.ObjToInt(v.getTag()));
			if (mCart.contains(g)) {
				NotificUtil.showShortToast(R.string.toast_do_not_add_it_twice);
			} else {
				mCart.add(g);
				NotificUtil
						.showShortToast(R.string.toast_add_to_cart_successlly);
			}
			break;
		case R.id.Button_item_cart_remove:
			mTempCart.removeAt(StrUtil.ObjToInt(v.getTag()));
			NotificUtil
					.showShortToast(R.string.toast_remove_from_list_successlly);
			// refresh current cart window
			mPopupWindow.dismiss(); // close old one
			prepareCartWindows(); // generate new one
			break;
		default:
			break;
		}
	}

	private void prepareGoodsInfoWindows() {
		LogUtil.logDebug(mCurrentGoods.getName(), TAG);
	}

	private void prepareCartWindows() {
		mPopupWindow = new PopupWindow(mContext);
		LinearLayout listView = new LinearLayout(mContext);
		listView.setOrientation(LinearLayout.VERTICAL);
		// get layout inflater
		LayoutInflater inflater = LayoutInflater.from(mContext);
		for (int i = 0; i < mTempCart.size(); i++) {
			Goods g = mTempCart.valueAt(i);
			RelativeLayout layout = (RelativeLayout) inflater.inflate(
					R.layout.item_cart, null);
			// get controls
			ImageView iconImageView = (ImageView) layout
					.findViewById(R.id.ImageView_item_cart_icon);
			TextView nameTextView = (TextView) layout
					.findViewById(R.id.TextView_item_cart_name);
			TextView priceTextView = (TextView) layout
					.findViewById(R.id.TextView_item_cart_price);
			Button addButton = (Button) layout
					.findViewById(R.id.Button_item_cart_add);
			Button removeButton = (Button) layout
					.findViewById(R.id.Button_item_cart_remove);
			// set value
			iconImageView.setImageBitmap(Model.getInstance()
					.getBitmapFromCache(
							NetUtil.buildURLForThumb(g.getPreview())));
			nameTextView.setText(g.getName());
			priceTextView.setText(StrUtil.dobToString(g.getSalePrice()));
			addButton.setTag(i);
			addButton.setOnClickListener(this);
			removeButton.setTag(i);
			removeButton.setOnClickListener(this);
			listView.addView(layout);
		}
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.setContentView(listView);
		mPopupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
		mPopupWindow.update(mSurfaceView.getWidth() / 2,
				mSurfaceView.getHeight() / 2);
	}

	int i = 0; // number counter for record button press

	/**
	 * @param v
	 *            target button
	 * @param isBackKey
	 *            is back key pressed or not
	 * @return
	 */
	private boolean toggleButton(View v, boolean isBackKey) {
		if (!isBackKey) {
			i++;
			// check if is the "add all" button
			if (i % 2 == 0) {
				return false; // skip this action
			}
		} else {
			// reset the counter
			i = 0;
		}
		Button button = (Button) v;
		if (StrUtil.objToString(button.getTag()).equals("null")
				|| StrUtil.ObjToInt(button.getTag()) == 0) {
			button.setText(R.string.popup_all_to_cart);
			button.setTag(1);
			return true;
		} else {
			button.setText(R.string.popup_goods_list);
			button.setTag(0);
			// make button click-able
			button.setClickable(true);
			return false;
		}
	}

	/**
	 * variable that hold model's status of direction
	 */
	private void toggleDirection() {
		if (mCurrentDirect.equals(Model.MODEL_DIRECT_FRONT)) {
			mCurrentDirect = Model.MODEL_DIRECT_BACK;
		} else {
			mCurrentDirect = Model.MODEL_DIRECT_FRONT;
		}
		// sync with Model class
		Model.getInstance().setCurrentDirection(mCurrentDirect);
	}

	private void toggleVisibilty(View v) {
		if (v.getVisibility() == View.VISIBLE) {
			v.setVisibility(View.GONE);
			if (R.id.LinearLayout_functionArea == v.getId()) {
				mMainLayout.scrollBy(-100, 0); // hard-coded here
			}
		} else {
			v.setVisibility(View.VISIBLE);
			if (R.id.LinearLayout_functionArea == v.getId()) {
				mMainLayout.scrollBy(100, 0); // hard-coded here
			}
		}
	}

	/**
	 * should not call directly
	 * 
	 * @author sharl
	 */
	private void drawModel() {
		SurfaceHolder holder = mSurfaceView.getHolder();
		Canvas canvas = holder.lockCanvas();
		// check if the surface view is ready
		if (canvas == null) {
			return;
		}
		// clear the canvas
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		// draw the model
		Model.getInstance().drawModel(canvas, mContext);
		holder.unlockCanvasAndPost(canvas);
	}

	/**
	 * get category list from web
	 */
	private void prepareLeftSidebar() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (mCategoryList != null) {

				} else {
					mCategoryList = Category.doCategorygetTree();
				}
				mHandler.sendMessage(mHandler.obtainMessage(4));
			}
		}).start();
	}

	/**
	 * fill the category bar with data from web
	 */
	private void buildLeftSidebar() {
		// create listener
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int categoryId = StrUtil.ObjToInt(v.getTag(R.string.tag_id));
				int dressDepthId = StrUtil.ObjToInt(v
						.getTag(R.string.tag_dress_map_id));
				// TODO: use dress depth id further
				String categoryName = StrUtil.objToString(v
						.getTag(R.string.tag_name));
				mCurrentLayer = DataHolder.getInstance().getMappingLayerByName(
						categoryName);
				prepareBottomBar(categoryId, mBrandModel.getGender(),
						mBrandModel.getAgeGroup());
			}
		};
		// build the bar
		for (Category category : mCategoryList) {
			TextView textView = new TextView(mContext);
			textView.setWidth(100);
			textView.setHeight(50);
			textView.setTag(R.string.tag_id, category.getTitle().getId()); // category
																			// id
			textView.setTag(R.string.tag_dress_map_id, category.getTitle()
					.getDressMapId()); // dress-depth id
			textView.setTag(R.string.tag_name, category.getTitle().getName()); // category
																				// name
			textView.setText(category.getTitle().getName());
			textView.setGravity(Gravity.CENTER);
			textView.setOnClickListener(listener);
			if (category.getChildren() == null) {
				mTypeLayout.addView(textView);
				// insert the divider
				View view = new View(mContext);
				view.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, 1));
				view.setBackgroundColor(Color.BLACK);
				mTypeLayout.addView(view);
			} else {
				for (Category c : category.getChildren()) {
					TextView textView2 = new TextView(mContext);
					textView2.setWidth(100);
					textView2.setHeight(50);
					textView2.setTag(R.string.tag_id, c.getTitle().getId()); // category
																				// id
					textView2.setTag(R.string.tag_dress_map_id, c.getTitle()
							.getDressMapId()); // dress-depth id
					textView2.setTag(R.string.tag_name, c.getTitle().getName()); // category
																					// name
					textView2.setText(c.getTitle().getName());
					textView2.setGravity(Gravity.CENTER);
					textView2.setOnClickListener(listener);
					mTypeLayout.addView(textView2);
					// insert the divider
					View view = new View(mContext);
					view.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT, 1));
					view.setBackgroundColor(Color.BLACK);
					mTypeLayout.addView(view);
				}
			}
		}
	}

	/**
	 * get goods list under specify category id from web
	 * 
	 * @param categoryId
	 */
	private void prepareBottomBar(final int categoryId, final int gender,
			final int ageGroup) {
		mHandler.sendMessage(mHandler.obtainMessage(97));
		new Thread(new Runnable() {

			@Override
			public void run() {
				mGoodsList = Goods.doGoodsgetList(1, 20, categoryId, 0, gender,
						ageGroup, null, 0, 0, null);
				mHandler.sendMessage(mHandler.obtainMessage(5));
			}
		}).start();
	}

	/**
	 * fill the goods list with every item
	 */
	private void buildBottomBar() {
		// listener for each item click
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(final View v) {
				mHandler.sendMessage(mHandler.obtainMessage(97));
				new Thread(new Runnable() {

					@Override
					public void run() {
						// get the goods object
						Goods goods = mGoodsList.get(StrUtil.ObjToInt(v
								.getTag()));
						// add to temporary cart,not final
						mTempCart.put(mCurrentLayer, goods);
						// build the URI
						String url = NetUtil.buildURLForNormal(
								goods.getPreview(), mCurrentDirect);
						// retrieve the goods's image
						Bitmap bm = NetUtil.getImageFromWeb(url,
								NetUtil.DOMAIN_FILE_PURE);
						// set it
						Model.getInstance().setLayer(mCurrentLayer,
								new MyBitmap(bm, url, mCurrentDirect));
						// get the goods's [x,y] data
						String json = NetUtil.getTextFromWeb(NetUtil
								.buildURLForNormalConf(goods.getPreview()),
								NetUtil.DOMAIN_FILE_PURE);
						// set it
						Model.getInstance().setPosDescribe(json, mCurrentLayer,
								mCurrentDirect);
						// notice UI thread to refresh
						mHandler.sendMessage(mHandler.obtainMessage(3));
						LogUtil.logDebug(TAG, "current id = " + goods.getName());
					}
				}).start();
			}
		};
		// clear all views first
		mListLayout.removeAllViews();
		int i = 0;
		for (final Goods goods : mGoodsList) {
			final ImageView imageView = new ImageView(mContext);
			imageView.setOnClickListener(listener);
			imageView.setLayoutParams(new LayoutParams(100, 100));
			imageView.setTag(i);// record the position as key for further find
			imageView.setImageResource(R.drawable.ic_launcher); // default icon
			if (goods.getPreview() != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						Bitmap bm = NetUtil.getImageFromWeb(
								NetUtil.buildURLForThumb(goods.getPreview()),
								NetUtil.DOMAIN_FILE_PURE);
						Object[] obj = new Object[] { imageView, bm };
						// notice the UI thread to draw
						mHandler.sendMessage(mHandler.obtainMessage(6, obj));
					}
				}).start();
			}
			mListLayout.addView(imageView);
			i++;
		}
		mListLayout.setVisibility(View.VISIBLE);
		mCartButton.setVisibility(View.GONE);
		mHandler.sendMessage(mHandler.obtainMessage(98));
	}

	/**
	 * build function bar
	 */
	private void prepareRightSidebar() {
		// get controls
		Button switchToManButton = (Button) findViewById(R.id.Button_switchToMan);
		Button switchToWomenButton = (Button) findViewById(R.id.Button_switchToWoman);
		Button turnBackButton = (Button) findViewById(R.id.Button_turnBack);
		Button undoButton = (Button) findViewById(R.id.Button_undo);
		Button saveButton = (Button) findViewById(R.id.Button_save);
		Button shareButton = (Button) findViewById(R.id.Button_share);
		// register event
		switchToManButton.setOnClickListener(this);
		switchToWomenButton.setOnClickListener(this);
		turnBackButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		shareButton.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// close pop-up window by back key
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			toggleButton(mCartButton, true);
			return;
		}
		// hide goods list by back key
		if (mListLayout.getVisibility() == View.GONE) {
			super.onBackPressed();
		} else {
			mListLayout.setVisibility(View.GONE);
			mCartButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDismiss() {
	}

	/**
	 * function to auto detect current layer finger touched
	 * 
	 * @param x
	 * @param y
	 * @return current layer
	 * @see "http://docs.oracle.com/javase/6/docs/api/java/util/Comparator.html#compare%28T,%20T%29"
	 */
	private int detectLayer(float x, float y) {
		int currentLayer = -1;
		List<String> l = new ArrayList<String>();
		// purge invalid layer data
		for (String key : Model.getInstance().getLayer_pos().keySet()) {
			if (key.indexOf("#") == -1 || key.indexOf(mCurrentDirect) == -1) {
				continue;
			}
			l.add(key);
		}
		// sort from MAX to MIN
		Collections.sort(l, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				int old = StrUtil.StringToInt(lhs.split("#")[0]);
				int neo = StrUtil.StringToInt(rhs.split("#")[0]);
				return -old + neo;
			}
		});
		// detect layer by finger's x,y position,from outer to inner
		for (String key : l) {
			Integer[] pos = Model.getInstance().getLayer_pos().get(key);
			int xStart = pos[0];
			int yStart = pos[1];
			int xEnd = xStart + pos[2];
			int yEnd = yStart + pos[3];
			if (xStart < x && x < xEnd && yStart < y && y < yEnd) {
				currentLayer = StrUtil.StringToInt(key.split("#")[0]);
				break;
			}
		}
		return currentLayer;
	}

	int layer = -1; // identifier of layer finger touch on surfaceView

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// check which layer first
			layer = detectLayer(event.getX(), event.getY());
			LogUtil.logDebug("Current layer is " + layer, TAG);
			LogUtil.logDebug(
					"lip,and x:" + event.getX() + ",y:" + event.getY(), TAG);
			break;
		case MotionEvent.ACTION_MOVE:
			LogUtil.logDebug("X is " + event.getX(), TAG);
			LogUtil.logDebug("Y is " + event.getY(), TAG);
			break;
		case MotionEvent.ACTION_UP:
			if (layer != -1) {
				mHandler.sendMessage(mHandler.obtainMessage(7, layer));
			}
			layer = -1;
			LogUtil.logDebug(
					"lip over,and x:" + event.getX() + ",y:" + event.getY(),
					TAG);
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}
		return true;
	}
}
