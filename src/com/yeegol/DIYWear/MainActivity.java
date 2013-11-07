package com.yeegol.DIYWear;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.SparseArray;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.yeegol.DIYWear.clz.MyAdapter;
import com.yeegol.DIYWear.clz.MyBitmap;
import com.yeegol.DIYWear.clz.MyImageView;
import com.yeegol.DIYWear.clz.MyLinearLayout;
import com.yeegol.DIYWear.clz.MyOnDismissListener;
import com.yeegol.DIYWear.clz.MyPopupWindow;
import com.yeegol.DIYWear.clz.MySurfaceView;
import com.yeegol.DIYWear.entity.Brand;
import com.yeegol.DIYWear.entity.Category;
import com.yeegol.DIYWear.entity.Collocation;
import com.yeegol.DIYWear.entity.Goods;
import com.yeegol.DIYWear.entity.Model;
import com.yeegol.DIYWear.entity.Model.BrandModel;
import com.yeegol.DIYWear.res.DataHolder;
import com.yeegol.DIYWear.util.DateUtil;
import com.yeegol.DIYWear.util.FSUtil;
import com.yeegol.DIYWear.util.ImgUtil;
import com.yeegol.DIYWear.util.LogUtil;
import com.yeegol.DIYWear.util.NetUtil;
import com.yeegol.DIYWear.util.NotificUtil;
import com.yeegol.DIYWear.util.StrUtil;
import com.yeegol.DIYWear.util.ThreadUtil;

/**
 * main window for display,modify the model's looking
 * 
 * @author sharl
 * 
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback,
		OnClickListener, MyOnDismissListener, OnTouchListener,
		OnGestureListener, OnScrollListener, TabContentFactory {

	private static final String TAG = MainActivity.class.getName();

	MySurfaceView mSurfaceView;

	LinearLayout mFunctionLayout;

	LinearLayout mGoodsLayout;

	RelativeLayout mMainLayout;

	ListView mListLayout;

	LinearLayout mColDetailLayout;

	Handler mHandler;

	Model.BrandModel mBrandModel;

	List<Category> mCategoryList;

	List<Goods> mGoodsList;

	List<Collocation> mCollocationsList;

	Context mContext;

	String mCurrentDirect; // for further load

	int mCurrentLayer;

	Goods mCurrentGoods;

	ProgressDialog mProgressDialog;

	SparseArray<Goods> mTempCart;

	List<Goods> mCart;

	MyPopupWindow mPopupWindow;

	TabHost mConditionContainer;

	Bitmap mBitmap;

	Bitmap mPreviousBitmap;

	UMSocialService mSocialService;

	int mCategoryId;

	String mBrandIds;

	LinkedList<Goods> mPreviousGoods;

	LinkedList<Goods> mNextGoods;

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
		mSocialService = UMServiceFactory.getUMSocialService(TAG,
				RequestType.SOCIAL);
		mPreviousGoods = new LinkedList<Goods>();
		mNextGoods = new LinkedList<Goods>();
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
					prepareBottombar();
					break;
				case 1:
					pickUpIfNeed(true);
					break;
				case 2:
					mHandler.sendMessage(mHandler.obtainMessage(97));
					prepareLeftSidebar();
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
					// avoid removed layer
					if (mCurrentGoods != null) {
						prepareGoodsInfoWindow();
					}
					break;
				case 8:
					// check this window already closed or not
					if (mPopupWindow != null && mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
					}
					break;
				case 9:
					toggleVisibilty(mConditionContainer, true);
					break;
				case 10:
					((BaseAdapter) mListLayout.getAdapter())
							.notifyDataSetChanged();
					underWorking = false;
					break;
				case 11:
					NotificUtil.showShortToast("networking error");
					break;
				case 12:
					// update count at header
					prepareRightSidebar();
					break;
				case 13:
					buildCollocationPanel((Object[]) msg.obj);
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
		ImageButton showFunctionButton = (ImageButton) findViewById(R.id.Button_showMoreFunction);
		Button cartButton = (Button) findViewById(R.id.Button_cart);
		// get controls
		mSurfaceView = (MySurfaceView) findViewById(R.id.surface_main);
		mFunctionLayout = (LinearLayout) findViewById(R.id.LinearLayout_functionArea);
		mGoodsLayout = (LinearLayout) findViewById(R.id.LinearLayout_goodsList);
		mMainLayout = (RelativeLayout) findViewById(R.id.RelativeLayout_main);
		mListLayout = (ListView) findViewById(R.id.ListView_goodsList);
		mColDetailLayout = (LinearLayout) findViewById(R.id.LinearLayout_collocation_detail);
		mConditionContainer = (TabHost) findViewById(R.id.TabHost_goodsCondition);
		// set listener
		showTypeButton.setOnClickListener(this);
		showFunctionButton.setOnClickListener(this);
		cartButton.setOnClickListener(this);
		mSurfaceView.getHolder().addCallback(this);
		mSurfaceView.setOnTouchListener(this);
		mConditionContainer.setOnTouchListener(this);
		// trigger
		mHandler.sendMessage(mHandler.obtainMessage(0));
	}

	/**
	 * get model list from web,run once
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
	 * check the model's quality,and order user to choice one
	 */
	private void pickUpIfNeed(boolean isStart) {
		List<BrandModel> list = Model.getInstance().getModels();
		if (list != null && list.size() > 1 && !isStart) {
			mPopupWindow = new MyPopupWindow(mContext);
			MyLinearLayout viewRoot = new MyLinearLayout(mContext);
			viewRoot.setOrientation(LinearLayout.HORIZONTAL);
			viewRoot.setGravity(Gravity.CENTER);
			viewRoot.setList(list);
			viewRoot.setListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					BrandModel model = (BrandModel) v.getTag();
					if (mBrandModel == model) {
						NotificUtil
								.showShortToast(R.string.toast_no_need_to_switch);
						return;
					}
					reset();
					toggleVisibilty(mGoodsLayout, View.GONE);
					mBrandModel = model;
					// set direct to front
					mCurrentDirect = Model.MODEL_DIRECT_FRONT;
					// sync it
					Model.getInstance().setCurrentDirection(mCurrentDirect);
					Model.getInstance().setCurrentBrandModel(mBrandModel);
					mHandler.sendMessage(mHandler.obtainMessage(2));
					mPopupWindow.dismiss();
				}
			});
			viewRoot.bindUI();
			mPopupWindow.setOnDismissListener(null);
			mPopupWindow.setOutsideTouchable(false);
			mPopupWindow.setContentView(viewRoot);
			mPopupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
			mPopupWindow.update(mSurfaceView.getWidth(),
					mSurfaceView.getHeight());
		} else {
			mBrandModel = list.get(2);// make lady default
			Model.getInstance().setCurrentBrandModel(mBrandModel);
			mHandler.sendMessage(mHandler.obtainMessage(2));
		}
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
				int currentPercent = DataHolder.getInstance()
						.getProperResolution();

				Model.getInstance().setBackground(
						new MyBitmap(ImgUtil
								.scaleBitmapToFullScreen(BitmapFactory
										.decodeResource(
												mContext.getResources(),
												R.drawable.bg_model)),
								"no need", "no need"));

				String tmp1 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "shadow0_"
								+ currentPercent + ".png");
				Bitmap tmp2 = NetUtil.getImageFromWeb(tmp1,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelShadow(
						new MyBitmap(tmp2, tmp1, direcetion));

				String tmp3 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "body_"
								+ currentPercent + ".png");
				Bitmap tmp4 = NetUtil.getImageFromWeb(tmp3,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelBody(
						new MyBitmap(tmp4, tmp3, direcetion));

				String tmp5 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "face_"
								+ currentPercent + ".png");
				Bitmap tmp6 = NetUtil.getImageFromWeb(tmp5,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelFace(
						new MyBitmap(tmp6, tmp5, direcetion));

				String tmp7 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "hair_"
								+ currentPercent + ".png");
				Bitmap tmp8 = NetUtil.getImageFromWeb(tmp7,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelHair(
						new MyBitmap(tmp8, tmp7, direcetion));

				String tmp9 = NetUtil.buildURLForBasic(
						mBrandModel.getPreview(), direcetion, "underwear_"
								+ currentPercent + ".png");
				Bitmap tmp10 = NetUtil.getImageFromWeb(tmp9,
						NetUtil.DOMAIN_FILE_PURE);
				Model.getInstance().setModelUnderwear(
						new MyBitmap(tmp10, tmp9, direcetion));
				// get position description
				try {
					Model.getInstance().setPosDescribe(
							NetUtil.getTextFromWeb(NetUtil
									.buildURLForBasicConf(mBrandModel
											.getPreview()),
									NetUtil.DOMAIN_FILE_PURE), mCurrentDirect);
				} catch (JSONException e) {
					LogUtil.logException(e, TAG);
				}
				mHandler.sendMessage(mHandler.obtainMessage(3));
			}
		}).start();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	/**
	 * check wrong action on bottom bar
	 * 
	 * @return this figurer action is valid or not.
	 */
	private boolean shouldSkipAction() {
		if (timeList.isEmpty() || timeList.size() == 1) {
			return false;
		}
		long interval = timeList.getLast() - timeList.get(timeList.size() - 2);
		return interval < 1000; // less than one second
	}

	@Override
	public void onClick(View v) {
		if (lockTouch) {
			return;
		}
		if (shouldSkipAction()) {
			LogUtil.logDebug("meaningless action,ignored", TAG);
			return;
		}
		switch (v.getId()) {
		case R.id.Button_showType:
			toggleVisibilty(mConditionContainer);
			break;
		case R.id.Button_showMoreFunction:
			prepareMoreFunctionWindow();
			break;
		case R.id.Button_switchModel:
			if (allDisabled) {
				return;
			}
			mHandler.sendMessage(mHandler.obtainMessage(8));
			toggleSex();
			break;
		case R.id.Button_turnBack:
			if (allDisabled) {
				return;
			}
			toggleDirection();
			mHandler.sendMessage(mHandler.obtainMessage(2));
			break;
		case R.id.Button_undo:
			if (allDisabled) {
				return;
			}
			// skip if no goods to remove
			if (mPreviousGoods.getLast() != null) {
				Goods g = mPreviousGoods.getLast();
				setGoods(g, mCurrentDirect, DataHolder.getInstance()
						.getMappingLayerByName(g.getCategoryName()));
				drawModel();
				mPreviousGoods.removeLast();
				mNextGoods.addLast(g);
				return;
			}
			break;
		case R.id.Button_redo:
			if (allDisabled) {
				return;
			}
			if (mNextGoods.getLast() != null) {
				Goods g = mNextGoods.getLast();
				setGoods(g, mCurrentDirect, DataHolder.getInstance()
						.getMappingLayerByName(g.getCategoryName()));
				drawModel();
				mNextGoods.removeLast();
				mPreviousGoods.addLast(g);
				return;
			}
			break;
		case R.id.Button_save:
			if (allDisabled) {
				return;
			}
			mPopupWindow.dismiss();
			prepareConfirmSave();
			break;
		case R.id.Button_share:
			if (allDisabled) {
				return;
			}
			mPopupWindow.dismiss();
			mSocialService.setShareContent(StrUtil
					.charToString(getText(R.string.umeng_share_content)));
			mSocialService.setShareImage(new UMImage(mContext, mBitmap));
			mSocialService.openShare(this, false);
			break;
		case R.id.Button_cart:
			if (allDisabled) {
				return;
			}
			prepareCartWindow();
			break;
		case -1:
			// TODO: add button
			// push all goods to REAL cart
			for (int i = 0; i < mTempCart.size(); i++) {
				mCart.add(mTempCart.valueAt(i));
			}
			NotificUtil
					.showShortToast(mTempCart.size() != 0 ? R.string.toast_all_add_to_cart_successlly
							: R.string.toast_all_add_to_cart_failed);
			// close pop-up window with virtual back-key press
			onBackPressed();
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
			int index = StrUtil.ObjToInt(v.getTag());
			// get its layer
			int layer = mTempCart.keyAt(index);
			removeGoodsFromTempCartAndRefreshUI(layer);
			NotificUtil
					.showShortToast(R.string.toast_remove_from_list_successlly);
			mPopupWindow.dismiss(); // close old one
			if ("1".equals(v.getTag(R.string.tag_from_cart_window))) {
				// refresh current cart window
				prepareCartWindow(); // generate new one
			}
			break;
		case R.id.Button_goodsList_sort:
			CharSequence[] items = new CharSequence[] {
					getText(R.string.main_goods_list_view_sort_price_asc),
					getText(R.string.main_goods_list_view_sort_price_desc),
					getText(R.string.main_goods_list_view_sort_id),
					getText(R.string.main_goods_list_view_sort_time) };
			android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					sortListView(mGoodsList != null ? mGoodsList
							: mCollocationsList, which);
					mHandler.sendMessage(mHandler.obtainMessage(10));
				}
			};
			NotificUtil.showAlertDiaWithMultiItem(
					R.string.main_goods_list_view_sort, items, mContext,
					listener);
			break;
		case R.id.Button_changeBg:
			mPopupWindow.dismiss();
			prepareBgPickWindow();
			break;
		case R.id.Button_diff:
			prepareDiffWindow();
			break;
		default:
			break;
		}
	}

	/**
	 * sort list with order
	 * 
	 * @param l
	 *            list
	 * @param w
	 *            0 refer min to max <br>
	 *            1 refer max to min
	 */
	private void sortListView(List<?> l, final int w) {
		Collections.sort(l, new Comparator<Object>() {

			@Override
			public int compare(Object lhs, Object rhs) {
				if (lhs instanceof Goods) {
					switch (w) {
					case 0:
						return StrUtil.dobToInt(((Goods) lhs).getSalePrice()
								- ((Goods) rhs).getSalePrice());
					case 1:
						return StrUtil.dobToInt(((Goods) rhs).getSalePrice()
								- ((Goods) lhs).getSalePrice());
					default:
						break;
					}
				} else {
				}
				return 0;
			}
		});
	}

	/**
	 * change button's background while touched
	 * 
	 * @param v
	 */
	private void toggleFunctionBtn(View v) {
		if (v.getBackground() == null) {
			v.setBackgroundResource(R.drawable.bg_btn_bottom);
		} else {
			v.setBackground(null);
		}
	}

	List<Collocation> tmpCollocations;

	/**
	 * create & pop-up the goods information window
	 */
	private void prepareGoodsInfoWindow() {
		mPopupWindow = new MyPopupWindow(mContext);
		// get layout inflater
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.view_goods_info, null);
		// get controls
		TextView dressWayLabelTextView = (TextView) layout
				.findViewById(R.id.TextView_view_goods_info_dress_way_label);
		Button dressWayOneButton = (Button) layout
				.findViewById(R.id.Button_view_goods_info_dress_way_one);
		Button dressWayTwoButton = (Button) layout
				.findViewById(R.id.Button_view_goods_info_dress_way_two);
		Button detailButton = (Button) layout
				.findViewById(R.id.Button_view_goods_info_detail);
		Button removeButton = (Button) layout
				.findViewById(R.id.Button_view_goods_info_remove);
		Button previousButton = (Button) layout
				.findViewById(R.id.Button_view_goods_info_previous);
		Button nextButton = (Button) layout
				.findViewById(R.id.Button_view_goods_info_next);
		final MyImageView previewImageView = (MyImageView) layout
				.findViewById(R.id.ImageView_view_goods_info_preview);
		final TextView recommendNameTextView = (TextView) layout
				.findViewById(R.id.TextView_view_goods_info_recommend_name);
		// handler
		final Handler handler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					@SuppressWarnings("unchecked")
					List<Collocation> colList = (List<Collocation>) msg.obj;
					LinearLayout layout = (LinearLayout) previewImageView
							.getParent().getParent();
					if (colList != null) {
						Collocation c = colList.get(0);
						previewImageView.setURL(NetUtil
								.buildURLForCollocation(c.getPreview()));
						previewImageView.setTag(c);
						recommendNameTextView.setText(c.getName());
					} else {
						layout.removeAllViews();
						layout.addView(inflater.inflate(
								R.layout.view_empty_collation, null));
					}
					// show collation part
					toggleVisibilty(layout);
					tmpCollocations = colList;
					break;
				case 1:
					NotificUtil.showAlertDia(R.string.alert_dial_info_title,
							mCurrentGoods.toString(), mContext);
					break;
				default:
					break;
				}
				return true;
			}
		});
		// listener
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// get location of current entity
				int index = -1;
				if (tmpCollocations != null) {
					index = tmpCollocations.indexOf(previewImageView.getTag());
				}
				// get its layer
				int layer = mTempCart.keyAt(mTempCart
						.indexOfValue(mCurrentGoods));
				switch (v.getId()) {
				case R.id.Button_view_goods_info_remove:
					removeGoodsFromTempCartAndRefreshUI(layer);
					mHandler.sendMessage(mHandler.obtainMessage(8));
					break;
				case R.id.Button_view_goods_info_dress_way_one:
					DataHolder.getInstance().mapThisToLow(layer);
					// refresh UI
					drawModel();
					break;
				case R.id.Button_view_goods_info_dress_way_two:
					DataHolder.getInstance().mapThisToHigh(layer);
					// refresh UI
					drawModel();
					break;
				case R.id.Button_view_goods_info_detail:
					handler.sendMessage(handler.obtainMessage(1));
					break;
				case R.id.Button_view_goods_info_previous:
					// check if is the first
					if (index == 0 || index == -1) {
						return;
					}
					// bind the object
					Collocation c = tmpCollocations.get(index - 1);
					previewImageView.setTag(c);
					// set image
					previewImageView.setURL(NetUtil.buildURLForCollocation(c
							.getPreview()));
					// set name
					recommendNameTextView.setText(c.getName());
					break;
				case R.id.ImageView_view_goods_info_preview:
					if (index == -1) {
						return;
					}
					final Collocation collocation = (Collocation) v.getTag();
					mHandler.sendMessage(mHandler.obtainMessage(97));
					new Thread(new Runnable() {
						public void run() {
							setCollocation(collocation);
							mHandler.sendMessage(mHandler.obtainMessage(98));
							mHandler.sendMessage(mHandler.obtainMessage(8));
						}
					}).start();
					break;
				case R.id.Button_view_goods_info_next:
					// check if is the last
					if (index == -1 || index == tmpCollocations.size() - 1) {
						return;
					}
					// bind object
					Collocation c1 = tmpCollocations.get(index + 1);
					previewImageView.setTag(c1);
					// set image
					previewImageView.setURL(NetUtil.buildURLForCollocation(c1
							.getPreview()));
					// set name
					recommendNameTextView.setText(c1.getName());
					break;
				default:
					break;
				}
			}
		};
		// set value
		createCartItem(layout, mCurrentGoods,
				mTempCart.indexOfValue(mCurrentGoods), false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<Collocation> list = Collocation.doCollocationgetList(0,
						mCurrentGoods.getId(), mBrandModel.getGender(), "",
						StrUtil.intToString(mBrandModel.getAgeGroup()), 0, 0);
				handler.sendMessage(handler.obtainMessage(0, list));
			}
		}).start();
		dressWayOneButton.setOnClickListener(listener);
		dressWayTwoButton.setOnClickListener(listener);
		detailButton.setOnClickListener(listener);
		removeButton.setOnClickListener(listener);
		previousButton.setOnClickListener(listener);
		nextButton.setOnClickListener(listener);
		previewImageView.setOnClickListener(listener);
		// make other unusable
		togglePanelTouchable();
		// attach view to popupWindow & show
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setContentView(layout);
		mPopupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
		mPopupWindow.update(StrUtil.dobToInt(mSurfaceView.getWidth() * 0.5),
				StrUtil.dobToInt(mSurfaceView.getHeight() * 0.6));
	}

	/**
	 * remove goods from temporarily cart & refresh the model
	 * 
	 * @param layer
	 *            key of the layer
	 */
	private void removeGoodsFromTempCartAndRefreshUI(int layer) {
		// remove the layer & data related
		Model.getInstance().setLayer(layer, null);
		mTempCart.remove(layer);
		// refresh UI
		drawModel();
	}

	/**
	 * @param layout
	 *            parent view
	 * @param g
	 *            the goods
	 * @param i
	 *            index of goods in temporarily cart
	 */
	private void createCartItem(View layout, Goods g, int i,
			boolean fromCartWindow) {
		// get controls
		MyImageView iconImageView = (MyImageView) layout
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
		iconImageView.setURL(NetUtil.buildURLForThumb(g.getPreview()));
		nameTextView.setText(g.getGoodsName());
		priceTextView.setText(StrUtil.dobToString(g.getSalePrice()));
		addButton.setTag(i);
		addButton.setOnClickListener(this);
		removeButton.setTag(i);
		if (fromCartWindow) {
			removeButton.setTag(R.string.tag_from_cart_window, "1");
		} else {
			removeButton.setTag(R.string.tag_from_cart_window, "0");
		}
		removeButton.setOnClickListener(this);
	}

	/**
	 * create & pop-up the cart windows
	 */
	private void prepareCartWindow() {
		mPopupWindow = new MyPopupWindow(mContext);
		LinearLayout listView = new LinearLayout(mContext);
		listView.setOrientation(LinearLayout.VERTICAL);
		// get layout inflater
		LayoutInflater inflater = LayoutInflater.from(mContext);
		for (int i = 0; i < mTempCart.size(); i++) {
			Goods g = mTempCart.valueAt(i);
			RelativeLayout layout = (RelativeLayout) inflater.inflate(
					R.layout.item_cart, null);
			createCartItem(layout, g, i, true);
			listView.addView(layout);
		}
		// make other unusable
		togglePanelTouchable();
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setContentView(listView.getChildCount() > 0 ? listView
				: inflater.inflate(R.layout.view_empty_cart, null));
		mPopupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
		mPopupWindow.update(StrUtil.dobToInt(mSurfaceView.getWidth() * 0.8),
				StrUtil.dobToInt(mSurfaceView.getHeight() * 0.8));
	}

	private void prepareNoClothOnModel() {
		mPopupWindow = new MyPopupWindow(mContext);
		LayoutInflater inflater = getLayoutInflater();
		View viewRoot = inflater.inflate(R.layout.view_no_cloth_on_model, null);
		Button okButton = (Button) viewRoot.findViewById(R.id.Button_save_yes);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mPopupWindow.setOnDismissListener(null);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setContentView(viewRoot);
		mPopupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
		mPopupWindow.update(mSurfaceView.getWidth(), mSurfaceView.getHeight());
	}

	private void prepareConfirmSave() {
		LayoutInflater inflater = getLayoutInflater();
		View viewRoot = inflater.inflate(R.layout.view_confirm_save, null);
		Button yesButton = (Button) viewRoot.findViewById(R.id.Button_save_yes);
		Button noButton = (Button) viewRoot.findViewById(R.id.Button_save_no);
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.Button_save_yes) {
					try {
						String directoryName = "/yeegol";
						String fileName = "/image" + DateUtil.getTimeStamp()
								+ ".jpg";
						if (FSUtil.writeBitmapToFileOnSdcard(mContext, mBitmap,
								fileName, directoryName)) {
							NotificUtil
									.showLongToast(getText(R.string.toast_image_saved_to_local_successlly)
											+ directoryName + fileName);
						}
					} catch (IOException e) {
						LogUtil.logException(e, TAG);
					}
				} else {
					onBackPressed();
				}
			}
		};
		yesButton.setOnClickListener(listener);
		noButton.setOnClickListener(listener);
		NotificUtil.showAlertDia(viewRoot, mContext);
	}

	private void prepareDiffWindow() {
		mPopupWindow = new MyPopupWindow(mContext);
		// get inflater
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout viewRoot = (LinearLayout) inflater.inflate(
				R.layout.view_diff, null);
		// get controls
		ImageView aImageView = (ImageView) viewRoot
				.findViewById(R.id.ImageView_model_a);
		ImageView bImageView = (ImageView) viewRoot
				.findViewById(R.id.ImageView_model_b);
		if (mPreviousBitmap != null) {
			aImageView.setImageBitmap(mPreviousBitmap);
			bImageView.setImageBitmap(mBitmap);
			mPopupWindow.setOnDismissListener(null);
			mPopupWindow.setOutsideTouchable(false);
			mPopupWindow.setContentView(viewRoot);
			mPopupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
			mPopupWindow.update(mSurfaceView.getWidth(),
					mSurfaceView.getHeight());
		} else {
			NotificUtil.showShortToast("no diff");
		}
	}

	private void prepareMoreFunctionWindow() {
		mPopupWindow = new MyPopupWindow(mContext);
		// get inflater
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout viewRoot = (LinearLayout) inflater.inflate(
				R.layout.view_more_funct, null);
		// get controls
		Button changeBgButton = (Button) viewRoot
				.findViewById(R.id.Button_changeBg);
		Button saveButton = (Button) viewRoot.findViewById(R.id.Button_save);
		Button shareButton = (Button) viewRoot.findViewById(R.id.Button_share);
		// set event
		changeBgButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		shareButton.setOnClickListener(this);
		// attach to popup window
		mPopupWindow.setOnDismissListener(null);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setContentView(viewRoot);
		mPopupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
		mPopupWindow.update(mSurfaceView.getWidth(), mSurfaceView.getHeight());
	}

	private void prepareBgPickWindow() {
		mPopupWindow = new MyPopupWindow(mContext);
		// test data
		List<Integer> colors = new ArrayList<Integer>();
		colors.add(Color.BLACK);
		colors.add(Color.BLUE);
		colors.add(Color.CYAN);
		colors.add(Color.GRAY);
		MyLinearLayout listView = new MyLinearLayout(mContext);
		listView.setOrientation(LinearLayout.HORIZONTAL);
		listView.setGravity(Gravity.CENTER);
		listView.setList(colors);
		listView.setListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int color = StrUtil.ObjToInt(v.getTag());
				Model.getInstance().setModelShadow(
						new MyBitmap(ImgUtil.scaleBitmapToFullScreen(ImgUtil
								.createBitmapWithSingleColor(color)),
								"no need", "no need"));
				drawModel();
				mPopupWindow.dismiss();
			}
		});
		listView.bindUI();
		mPopupWindow.setOnDismissListener(null);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setContentView(listView);
		mPopupWindow.showAtLocation(mMainLayout, Gravity.CENTER, 0, 0);
		mPopupWindow.update(mSurfaceView.getWidth(), mSurfaceView.getHeight());
	}

	boolean allDisabled = false;

	/**
	 * make the left/right/bottom side-bar enable/disable
	 * 
	 */
	private void togglePanelTouchable() {
		if (mListLayout.isEnabled()) {
			mListLayout.setEnabled(false);
			allDisabled = true;
		} else {
			mListLayout.setEnabled(true);
			allDisabled = false;
		}
	}

	/**
	 * mark view & its child view click-able or UN-click-able,recursively
	 * 
	 * @param v
	 * @param clickable
	 */
	@SuppressWarnings("unused")
	private void toggleViewClickable(View v, boolean clickable) {
		v.setClickable(clickable);
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				toggleViewClickable(vg.getChildAt(i), clickable);
			}
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

	/**
	 * hide or show the view
	 * 
	 * @param v
	 *            view
	 */
	private void toggleVisibilty(View v) {
		if (v.getVisibility() == View.VISIBLE) {
			v.setVisibility(View.GONE);
		} else {
			v.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * set view's visibility to given
	 * 
	 * @param v
	 * @param state
	 */
	private void toggleVisibilty(View v, int state) {
		v.setVisibility(state);
	}

	/**
	 * function used to auto-hide left side-bar
	 * 
	 * @param v
	 * @param skipIfShown
	 */
	private synchronized void toggleVisibilty(View v, boolean skipIfShown) {
		if (skipIfShown && v.getVisibility() == View.VISIBLE) {
			toggleVisibilty(v);
		}
	}

	/**
	 * function to (re)-draw the model area,should not call directly
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
		Bitmap bitmap = Model.getInstance().drawModel(canvas);
		if (mBitmap != null) {
			mPreviousBitmap = mBitmap; // for compare
			mBitmap = bitmap;
		} else {
			mPreviousBitmap = null;
			mBitmap = bitmap;
		}
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

	static final int PAGE = 1;
	static final int OFFSET = 20;

	/**
	 * fill the category bar with data from web
	 */
	private void buildLeftSidebar() {
		// build the bar
		mConditionContainer.setup();
		mConditionContainer.clearAllTabs();
		mConditionContainer.addTab(mConditionContainer.newTabSpec("Brand")
				.setContent(this)
				.setIndicator(getText(R.string.main_goods_list_brand)));
		mConditionContainer.addTab(mConditionContainer.newTabSpec("Type")
				.setContent(this)
				.setIndicator(getText(R.string.main_goods_list_category)));
		mConditionContainer.setCurrentTab(0);
	}

	/**
	 * build the category list <b>recursively</b>
	 * 
	 * @param category
	 *            category list from top
	 * @param viewRoot
	 *            container of views
	 * @param listener
	 *            onClickListener
	 */
	private void buildLeftSidebarRecursively(List<Category> category,
			LinearLayout viewRoot, OnClickListener listener) {
		// get inflater
		LayoutInflater inflater = getLayoutInflater();
		for (Category c : category) {
			LinearLayout itemLayout = (LinearLayout) inflater.inflate(
					R.layout.item_menu, null);
			TextView textView = (TextView) itemLayout
					.findViewById(R.id.TextView_item_menu_name);
			textView.setText(c.getTitle().getName());
			viewRoot.addView(itemLayout);
			// insert the divider
			View view = new View(mContext);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
			view.setBackgroundColor(Color.parseColor("#d1d1d1"));
			viewRoot.addView(view);
			if (c.getChildren() != null) {
				// create a sub linearLayout
				final LinearLayout subLayout = new LinearLayout(mContext);
				subLayout.setOrientation(LinearLayout.VERTICAL);
				subLayout.setVisibility(View.GONE);
				viewRoot.addView(subLayout);
				textView.setTextSize(25);
				textView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						toggleVisibilty(subLayout);
					}
				});
				buildLeftSidebarRecursively(c.getChildren(), subLayout,
						listener);
			} else {
				// bind data
				textView.setTag(R.string.tag_id, c.getTitle().getId()); // categoryId
				textView.setTag(R.string.tag_brands_id, "-1"); // not use now
				textView.setTextSize(15);
				textView.setOnClickListener(listener);
			}
		}
	}

	/**
	 * add collocation entrance into menu,and others
	 * 
	 * @param viewRoot
	 * @param listener
	 */
	private void addCollationWithCategory(LinearLayout viewRoot,
			OnClickListener listener) {
		// get inflater
		LayoutInflater inflater = getLayoutInflater();
		// add "All Category"
		LinearLayout itemLayout = (LinearLayout) inflater.inflate(
				R.layout.item_menu, null);
		TextView textView = (TextView) itemLayout
				.findViewById(R.id.TextView_item_menu_name);
		textView.setText(R.string.main_goods_list_menu_category_all);
		textView.setTag(R.string.tag_id, 0);
		textView.setTag(R.string.tag_brands_id, "-1");
		textView.setTextSize(25);
		textView.setOnClickListener(listener);
		viewRoot.addView(itemLayout);
		// insert the divider
		View view = new View(mContext);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
		view.setBackgroundColor(Color.parseColor("#d1d1d1"));
		viewRoot.addView(view);
		// add "Collocation"
		itemLayout = (LinearLayout) inflater.inflate(R.layout.item_menu, null);
		textView = (TextView) itemLayout
				.findViewById(R.id.TextView_item_menu_name);
		textView.setText(R.string.main_goods_list_menu_collocation_all);
		textView.setTag(R.string.tag_id, "-1");
		textView.setTag(R.string.tag_brands_id, "-1");
		textView.setTextSize(25);
		textView.setOnClickListener(listener);
		viewRoot.addView(itemLayout);
		// insert the divider
		view = new View(mContext);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
		view.setBackgroundColor(Color.parseColor("#d1d1d1"));
		viewRoot.addView(view);
	}

	/**
	 * insert brand item to left side-bar
	 * 
	 * @param viewRoot
	 *            parent view
	 * @param listener
	 *            trigger
	 */
	private void addBrandTypeWithCategory(final LinearLayout viewRoot,
			final OnClickListener listener) {
		// get inflater
		final LayoutInflater inflater = getLayoutInflater();
		// add "All Brand" item
		LinearLayout itemLayout = (LinearLayout) inflater.inflate(
				R.layout.item_menu, null);
		TextView textView = (TextView) itemLayout
				.findViewById(R.id.TextView_item_menu_name);
		// bind data
		textView.setTag(R.string.tag_id, -1);
		textView.setTag(R.string.tag_brands_id, 0);
		textView.setText(R.string.main_goods_list_menu_brand_all);
		textView.setTextSize(25);
		textView.setOnClickListener(listener);
		viewRoot.addView(itemLayout);
		// insert the divider
		View view = new View(mContext);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
		view.setBackgroundColor(Color.parseColor("#d1d1d1"));
		viewRoot.addView(view);
		final Handler handler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message arg0) {
				@SuppressWarnings("unchecked")
				List<Brand> list = (List<Brand>) arg0.obj;
				// avoid NULL
				if (list == null) {
					return false;
				}
				for (Brand brand : list) {
					LinearLayout itemLayout = (LinearLayout) inflater.inflate(
							R.layout.item_menu, null);
					TextView textView = (TextView) itemLayout
							.findViewById(R.id.TextView_item_menu_name);
					// bind data
					textView.setTag(R.string.tag_id, -1);
					textView.setTag(R.string.tag_brands_id, brand.getId());
					textView.setText(brand.getCnName());
					textView.setTextSize(25);
					textView.setOnClickListener(listener);
					viewRoot.addView(itemLayout);
					// insert the divider
					View view = new View(mContext);
					view.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT, 1));
					view.setBackgroundColor(Color.parseColor("#d1d1d1"));
					viewRoot.addView(view);
				}
				return true;
			}
		});
		// get brand list
		ThreadUtil.doInForeground(new Runnable() {

			@Override
			public void run() {
				List<Brand> list = Brand.doBrandgetList(-1, "",
						mBrandModel.getGender(), mBrandModel.getAgeGroup(), 1,
						20);
				handler.sendMessage(handler.obtainMessage(0, list));
			}
		});
	}

	/**
	 * append brand type under end-category by sub-query
	 * 
	 * @deprecated no need
	 * 
	 * @param category
	 *            ended-category
	 * @param viewRoot
	 *            viewGroup container
	 * @param listener
	 *            trigger
	 * 
	 */
	@SuppressWarnings("unused")
	private void appendBrandTypeToCategory(final Category category,
			final LinearLayout viewRoot, final OnClickListener listener) {
		// get inflater
		LayoutInflater inflater = getLayoutInflater();
		final LinearLayout itemLayout = (LinearLayout) inflater.inflate(
				R.layout.item_menu, null);
		final Handler handler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message arg0) {
				@SuppressWarnings("unchecked")
				List<Brand> list = (List<Brand>) arg0.obj;
				// avoid NULL
				if (list == null) {
					return false;
				}
				for (Brand brand : list) {
					TextView textView = (TextView) itemLayout
							.findViewById(R.id.TextView_item_menu_name);
					textView.setText(brand.getCnName());
					textView.setTextSize(10);
					textView.setTag(R.string.tag_id, category.getTitle()
							.getId()); // categoryId
					textView.setTag(R.string.tag_brands_id, brand.getId()); // brandId
					textView.setOnClickListener(listener);
					viewRoot.addView(textView);
				}
				return true;
			}
		});
		// get brand list
		ThreadUtil.doInForeground(new Runnable() {

			@Override
			public void run() {
				List<Brand> list = Brand.doBrandgetList(category.getTitle()
						.getId(), "", mBrandModel.getGender(), mBrandModel
						.getAgeGroup(), 1, 20);
				handler.sendMessage(handler.obtainMessage(0, list));
			}
		});
	}

	/**
	 * get goods list under specify category id & more conditions from web
	 * 
	 * @param page
	 * @param size
	 * @param categoryId
	 * @param brandIds
	 * @param gender
	 * @param ageGroup
	 * @param isNew
	 *            reset or append
	 */
	private void prepareRightBar(final int page, final int size,
			final int categoryId, final String brandIds, final int gender,
			final int ageGroup, final boolean isNew) {
		// no more data to query
		if (page == -1) {
			underWorking = false;
			return;
		}
		final boolean isCollocationMod = categoryId == -1
				&& "-1".equals(brandIds);
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (isNew) {
					mHandler.sendMessage(mHandler.obtainMessage(97));
					if (isCollocationMod) {
						mCollocationsList = Collocation.doCollocationgetList(
								-1, -1, gender, "",
								StrUtil.intToString(ageGroup), page, size);
						mGoodsList = null;
					} else {
						mGoodsList = Goods.doGoodsgetList(page, size,
								categoryId, brandIds, gender, ageGroup, null,
								0, 0, null);
						mCollocationsList = null;
					}
					mHandler.sendMessage(mHandler.obtainMessage(5));
				} else {
					if (isCollocationMod) {
						List<Collocation> tmpList = Collocation
								.doCollocationgetList(-1, -1, gender, "",
										StrUtil.intToString(ageGroup), page,
										size);
						mCollocationsList.addAll(tmpList);
					} else {
						List<Goods> tmpList = Goods.doGoodsgetList(page, size,
								categoryId, brandIds, gender, ageGroup, null,
								0, 0, null);
						mGoodsList.addAll(tmpList);
					}
					// notify the listView to refresh
					mHandler.sendMessage(mHandler.obtainMessage(10));
					mHandler.sendMessage(mHandler.obtainMessage(12));
				}
				if (mGoodsList != null) {
					LogUtil.logDebug("goods list count:" + mGoodsList.size(),
							TAG);
				} else {
					LogUtil.logDebug("goods list is null", TAG);
				}
			}
		}).start();
	}

	/**
	 * fill the goods/collocation list with every item
	 * 
	 * @notice change to RightSideBar now
	 */
	private void buildBottomBar() {
		if (mGoodsList == null && mCollocationsList == null) {
			mGoodsLayout.setVisibility(View.INVISIBLE);
			mHandler.sendMessage(mHandler.obtainMessage(98));
			NotificUtil
					.showShortToast(R.string.toast_no_goods_item_under_the_type);
			return;
		}
		// listener for each item click when goods list
		final OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(final View v) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						mHandler.sendMessage(mHandler.obtainMessage(97));
						// get the goods object from array
						Goods goods = mGoodsList.get(StrUtil.ObjToInt(v
								.getTag()));
						// pick up goods's id & name
						int id = goods.getId();
						String name = goods.getName();
						// get whole goods from web
						goods = Goods.doGoodsgetInfo(goods.getId());
						// set id & name
						goods.setId(id);
						goods.setName(name);
						// distinct its layer
						mCurrentLayer = DataHolder.getInstance()
								.getMappingLayerByName(goods.getCategoryName());
						// check if already contain it,if so,take off it & end
						Goods tmpG;
						if ((tmpG = mTempCart.get(mCurrentLayer)) != null
								&& tmpG.getId() == goods.getId()) {
							removeGoodsFromTempCartAndRefreshUI(mCurrentLayer);
							mHandler.sendMessage(mHandler.obtainMessage(98));
							return;
						}
						// deploy this goods
						if (setGoods(goods, mCurrentDirect, mCurrentLayer)) {
							// notice UI thread to refresh
							mHandler.sendMessage(mHandler.obtainMessage(3));
							LogUtil.logDebug(TAG,
									"current id = " + goods.getName());
						} else {
							mHandler.sendMessage(mHandler.obtainMessage(11));
							mHandler.sendMessage(mHandler.obtainMessage(98));
						}
					}
				}).start();
			}
		};
		// listener for each item click when collocation list
		final OnClickListener listener2 = new OnClickListener() {

			@Override
			public void onClick(final View v) {
				ThreadUtil.doInBackgroundWithTip(new Runnable() {

					@Override
					public void run() {
						Collocation collocation = mCollocationsList.get(StrUtil
								.ObjToInt(v.getTag()));
						setCollocation(collocation);
					}
				}, mHandler);
			}
		};
		mListLayout.setAdapter(new MyAdapter(mContext,
				mGoodsList != null ? mGoodsList : mCollocationsList, mHandler));
		mListLayout.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mGoodsList != null) {
					listener.onClick(arg1);
				} else {
					listener2.onClick(arg1);
				}
			}
		});
		mListLayout.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (mGoodsList == null) {
					ThreadUtil.doInBackgroundWithTip(new Runnable() {

						@Override
						public void run() {
							Collocation collocation = mCollocationsList
									.get(position);
							List<Goods> list = getGoodsByCollocation(collocation);
							Object[] args = new Object[] { collocation, list };
							mHandler.sendMessage(mHandler.obtainMessage(13,
									args));
						}
					}, mHandler);
					return true;
				}
				return false;
			}
		});
		mListLayout.setOnScrollListener(this);
		// set count at header
		prepareRightSidebar();
		mGoodsLayout.setVisibility(View.VISIBLE);
		mHandler.sendMessage(mHandler.obtainMessage(98));
	}

	/**
	 * @param objects
	 */
	private void buildCollocationPanel(Object... objects) {
		// get controls
		MyImageView previewImageView = (MyImageView) findViewById(R.id.ImageView_collocation_preview);
		TextView countTextView = (TextView) findViewById(R.id.TextView_collocation_count);
		GridLayout itemsGridLayout = (GridLayout) findViewById(R.id.GridLayout_collocation_items);
		// get object
		Collocation collocation = (Collocation) objects[0];
		@SuppressWarnings("unchecked")
		List<Goods> list = (List<Goods>) objects[1];
		// bind data
		previewImageView.setURL(NetUtil.buildURLForCollocation(collocation
				.getPreview()));
		countTextView
				.setText(getText(R.string.main_goods_list_view_total_count_prefix)
						+ ""
						+ list.size()
						+ getText(R.string.main_goods_list_view_total_count_suffix));
		// clear first
		itemsGridLayout.removeAllViews();
		for (Goods goods : list) {
			MyImageView miv = new MyImageView(mContext);
			miv.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
			miv.setScaleType(ScaleType.CENTER_CROP);
			miv.setURL(NetUtil.buildURLForThumb(goods.getPreview()));
			itemsGridLayout.addView(miv);
		}
		// show and hide
		toggleVisibilty(mColDetailLayout, View.VISIBLE);
		toggleVisibilty(mListLayout, View.GONE);
	}

	/**
	 * add & deploy this goods
	 * 
	 * @param goods
	 * @param direct
	 * @param layer
	 */
	private boolean setGoods(Goods goods, String direct, int layer) {
		// build the URI
		String url = NetUtil.buildURLForNormal(goods.getPreview(), direct);
		// retrieve the goods's image
		Bitmap bm = NetUtil.getImageFromWeb(url, NetUtil.DOMAIN_FILE_PURE);
		// get the goods's [x,y] data
		String json = NetUtil.getTextFromWeb(
				NetUtil.buildURLForNormalConf(goods.getPreview()),
				NetUtil.DOMAIN_FILE_PURE);
		if (bm == null || json == null) {
			return false;
		}
		// add to temporary cart,not final
		mTempCart.put(layer, goods);
		// add to history
		mPreviousGoods.add(goods);
		// set it
		Model.getInstance().setLayer(layer, new MyBitmap(bm, url, direct));
		try {
			Model.getInstance().setPosDescribe(json, layer);
		} catch (JSONException e) {
			LogUtil.logException(e, TAG);
		}
		return true;
	}

	/**
	 * get all goods of specify collocation
	 * 
	 * @param collocation
	 * @return Goods under this collocation
	 */
	private List<Goods> getGoodsByCollocation(Collocation collocation) {
		List<Goods> ret = new ArrayList<Goods>();
		com.yeegol.DIYWear.entity.Collocation.Model model = Collocation
				.doCollocationgetInfo(collocation.getId());
		String[] ids = model.getGoodsIds().split(",");
		for (String id : ids) {
			Goods goods = Goods.doGoodsgetInfo(StrUtil.StringToInt(id));
			ret.add(goods);
		}
		return ret;
	}

	/**
	 * deploy selected collocation
	 * 
	 * @param collocation
	 *            collocation user selected
	 */
	private void setCollocation(Collocation collocation) {
		// remove all goods first
		reset();
		// re-get model's layer
		mHandler.sendMessage(mHandler.obtainMessage(2));
		// do with recommended goods
		List<Goods> list = getGoodsByCollocation(collocation);
		for (Goods goods : list) {
			// wear it
			boolean success = setGoods(
					goods,
					mCurrentDirect,
					DataHolder.getInstance().getMappingLayerByName(
							goods.getCategoryName()));
			if (!success) {
				mHandler.sendMessage(mHandler.obtainMessage(11));
				return;
			}
		}
		// refresh UI
		drawModel();
	}

	/**
	 * build function bar
	 */
	private void prepareBottombar() {
		// get controls
		Button switchModelButton = (Button) findViewById(R.id.Button_switchModel);
		Button turnBackButton = (Button) findViewById(R.id.Button_turnBack);
		Button undoButton = (Button) findViewById(R.id.Button_undo);
		Button redoButton = (Button) findViewById(R.id.Button_redo);
		Button diffButton = (Button) findViewById(R.id.Button_diff);
		// register event
		switchModelButton.setOnClickListener(this);
		turnBackButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		redoButton.setOnClickListener(this);
		diffButton.setOnClickListener(this);
		switchModelButton.setOnTouchListener(this);
		turnBackButton.setOnTouchListener(this);
		undoButton.setOnTouchListener(this);
		redoButton.setOnTouchListener(this);
		diffButton.setOnTouchListener(this);
	}

	/**
	 * 
	 */
	private void prepareRightSidebar() {
		// get controls
		TextView countTextView = (TextView) findViewById(R.id.TextView_goodsList_count);
		Button sortButton = (Button) findViewById(R.id.Button_goodsList_sort);
		// set value
		countTextView
				.setText(getText(R.string.main_goods_list_view_total_count_prefix)
						+ ""
						+ (mGoodsList != null ? mGoodsList.size()
								: mCollocationsList.size())
						+ getText(R.string.main_goods_list_view_total_count_suffix));
		sortButton.setText(R.string.main_goods_list_view_sort);
		// set listener
		countTextView.setOnClickListener(this);
		sortButton.setOnClickListener(this);
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
			mPopupWindow.dismiss(true);
			return;
		}
		// hide goods list by back key;switch to goods list if collaction list
		// shown
		if (mColDetailLayout.getVisibility() == View.VISIBLE) {
			toggleVisibilty(mColDetailLayout);
			toggleVisibilty(mListLayout);
		} else if (mGoodsLayout.getVisibility() == View.VISIBLE) {
			toggleVisibilty(mGoodsLayout);
		} else {
			System.exit(0);
		}
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
			if (key.indexOf("#") == -1
					|| key.indexOf(mCurrentDirect) == -1
					|| mTempCart.get(StrUtil.StringToInt(key.split("#")[0])) == null) {
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
	VelocityTracker tracker;
	boolean lockTouch;
	LinkedList<Long> timeList = new LinkedList<Long>(); // with order-sort
	int lockButtonId = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// while on left side-bar
		if (v.getId() == R.id.TabHost_goodsCondition) {
			if (tracker == null) {
				tracker = VelocityTracker.obtain();
			}
			tracker.addMovement(event);
			if (event.getAction() == MotionEvent.ACTION_UP) {
				tracker.computeCurrentVelocity(1000);
				if (tracker.getXVelocity() < -3000) {
					toggleVisibilty(v);
					tracker.recycle();
				} else {
					// reset the timer
					if (mHandler.hasMessages(9)) {
						mHandler.removeMessages(9);
					}
					mHandler.sendMessageDelayed(mHandler.obtainMessage(-1),
							10000);
				}
			}
			return false;
		}
		// while on bottom bar
		if (v.getId() == R.id.Button_switchModel
				|| v.getId() == R.id.Button_undo
				|| v.getId() == R.id.Button_turnBack
				|| v.getId() == R.id.Button_redo
				|| v.getId() == R.id.Button_diff) {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					|| event.getAction() == MotionEvent.ACTION_UP) {
				// lock the whole panel if user hold one key
				if (lockButtonId == 0
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					lockTouch = !lockTouch;
					lockButtonId = v.getId();
				} else if (lockButtonId == v.getId()
						&& event.getAction() == MotionEvent.ACTION_UP) {
					lockTouch = !lockTouch;
					lockButtonId = 0;
				}
				LogUtil.logDebug(lockTouch ? "function panel locked now"
						: "function panel unlocked now", TAG);
				toggleFunctionBtn(v);
				// record time point
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					timeList.clear();
				} else {
					timeList.add(System.currentTimeMillis());
				}
			}
			return false;
		}
		// while on main screen
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

	/**
	 * reset model to original status
	 */
	private void reset() {
		Model.getInstance().getLayers().clear();
		Model.getInstance().getLayer_pos().clear();
		Model.getInstance().resetLinkedList();
		mTempCart.clear();
		DataHolder.getInstance().resetLayerMapping();
	}

	/**
	 * switch model's gender
	 * 
	 * @param gender
	 *            target sex,1 for male,2 for female,omit
	 */
	private void toggleSex() {
		if (Model.getInstance().getModels().size() == 1) {
			NotificUtil.showShortToast(R.string.toast_only_one_model_now);
		} else {
			pickUpIfNeed(false);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	/**
	 * calculate the right page number for current ListView
	 * 
	 * @return the page to query
	 */
	private int getNextPage() {
		if (mListLayout.getAdapter().getCount() % OFFSET == 0) {
			return mListLayout.getAdapter().getCount() / OFFSET + 1;
		} else {
			return -1;
		}
	}

	int scrollState;
	boolean underWorking = false;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& !underWorking && visibleItemCount < totalItemCount) {
			underWorking = true;
			prepareRightBar(getNextPage(), OFFSET, mCategoryId, mBrandIds,
					mBrandModel.getGender(), mBrandModel.getAgeGroup(), false);
			LogUtil.logDebug("hit bottom", TAG);
		}
	}

	@Override
	public void onDismiss(boolean needRefresh) {
	}

	@Override
	public void onDismiss() {
		togglePanelTouchable();
	}

	@Override
	public View createTabContent(String tag) {
		// create listener
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (allDisabled) {
					return;
				}
				mCategoryId = StrUtil.ObjToInt(v.getTag(R.string.tag_id));
				mBrandIds = StrUtil.objToString(v
						.getTag(R.string.tag_brands_id));
				prepareRightBar(PAGE, OFFSET, mCategoryId, mBrandIds,
						mBrandModel.getGender(), mBrandModel.getAgeGroup(),
						true);
			}
		};
		// pick the top of the tree
		Category treeTop;
		if (mBrandModel.getGender() == 1) {
			treeTop = mCategoryList.get(0);
		} else {
			treeTop = mCategoryList.get(1);
		}
		// get inflater
		LayoutInflater inflater = getLayoutInflater();
		View v = inflater.inflate(R.layout.item_condition, null);
		LinearLayout typeLayout = (LinearLayout) v
				.findViewById(R.id.LinearLayout_goodsType);
		// clear first
		typeLayout.removeAllViews();
		if ("Brand".equals(tag)) {
			addBrandTypeWithCategory(typeLayout, listener);
		} else {
			addCollationWithCategory(typeLayout, listener);
			buildLeftSidebarRecursively(treeTop.getChildren(), typeLayout,
					listener);
		}
		return v;
	}
}
