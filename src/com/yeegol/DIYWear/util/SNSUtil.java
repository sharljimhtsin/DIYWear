/**
 * 
 */
package com.yeegol.DIYWear.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.model.AccountModel;
import com.tencent.weibo.sdk.android.network.HttpCallback;
import com.yeegol.DIYWear.R;
import com.yeegol.DIYWear.res.DataHolder;

/**
 * @author sharl
 * 
 */
public class SNSUtil {

	private static final String TAG = SNSUtil.class.getName();

	public static final String SINA_APP_KEY = StrUtil.charToString(DataHolder
			.getInstance().getResource().getText(R.string.sina_app_key));

	public static final String SINA_APP_SECRET_KEY = StrUtil
			.charToString(DataHolder.getInstance().getResource()
					.getText(R.string.sina_app_secret_key));

	public static final long TENCENT_APP_KEY = StrUtil.charToLong(DataHolder
			.getInstance().getResource().getText(R.string.tencent_app_key));

	public static final String TENCENT_APP_SECRET_KEY = StrUtil
			.charToString(DataHolder.getInstance().getResource()
					.getText(R.string.tencent_app_secret_key));

	public static void shareToSinaWeibo(Activity activity, IWeiboAPI api,
			String msg, Bitmap img) {
		// create message
		WeiboMultiMessage multiMessage = new WeiboMultiMessage();
		multiMessage.textObject = prepareTextForWeibo(msg);
		multiMessage.imageObject = prepareImageForWeibo(img);
		// create request
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request.transaction = StrUtil.longToString(System.currentTimeMillis());
		request.multiMessage = multiMessage;
		// send it
		api.sendRequest(activity, request);
	}

	public static void authOnTencentMicroblog(Context c, long appId,
			String secretKey, OnAuthListener listener) {
		AuthHelper.register(c, appId, secretKey, listener);
		AuthHelper.auth(c, "");
	}

	public static void shareToTencentMicroblog(Context c, String msg,
			Bitmap img, HttpCallback callback) {
		WeiboAPI api = new WeiboAPI(new AccountModel(Util.getSharePersistent(c,
				"ACCESS_TOKEN")));
		api.addPic(c, msg, "json", 0l, 0l, img, 0, 0, callback, null, 4);
	}

	private static TextObject prepareTextForWeibo(String s) {
		TextObject object = new TextObject();
		object.text = s;
		return object;
	}

	private static ImageObject prepareImageForWeibo(Bitmap b) {
		ImageObject object = new ImageObject();
		object.setImageObject(b);
		return object;
	}
}
