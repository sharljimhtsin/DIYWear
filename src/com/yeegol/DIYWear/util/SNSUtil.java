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
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;

/**
 * @author sharl
 * 
 */
public class SNSUtil {

	private static final String TAG = SNSUtil.class.getName();

	public static final String SINA_APP_KEY = "";

	public static final String SINA_APP_SECRET_KEY = "";

	public static final long TENCENT_APP_KEY = 0l;

	public static final String TENCENT_APP_SECRET_KEY = "";

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

	public static void shareToTencentMicroblog(Context c, long appId,
			String secretKey, OnAuthListener listener) {
		AuthHelper.register(c, appId, secretKey, listener);
		AuthHelper.auth(c, "");
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
