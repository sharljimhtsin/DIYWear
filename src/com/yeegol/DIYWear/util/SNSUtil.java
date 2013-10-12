/**
 * 
 */
package com.yeegol.DIYWear.util;

import android.app.Activity;
import android.graphics.Bitmap;

import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;

/**
 * @author sharl
 * 
 */
public class SNSUtil {
	private static final String TAG = SNSUtil.class.getName();

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
