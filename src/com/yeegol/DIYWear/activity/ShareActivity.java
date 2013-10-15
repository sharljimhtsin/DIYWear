/**
 * 
 */
package com.yeegol.DIYWear.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import com.tencent.weibo.sdk.android.model.ModelResult;
import com.tencent.weibo.sdk.android.network.HttpCallback;
import com.yeegol.DIYWear.util.NotificUtil;
import com.yeegol.DIYWear.util.SNSUtil;

/**
 * @author sharl
 * 
 */
public class ShareActivity extends Activity implements IWeiboHandler.Response,
		IWeiboDownloadListener, OnAuthListener {

	Context mContext;

	String TAG = ShareActivity.class.getName();

	Bitmap mBitmap;

	String mMsg;

	IWeiboAPI mIWeiboAPI;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mIWeiboAPI = WeiboSDK.createWeiboAPI(mContext, SNSUtil.SINA_APP_KEY,
				true);
		mIWeiboAPI.responseListener(getIntent(), this);
		mIWeiboAPI.registerWeiboDownloadListener(this);
		// get data
		Bundle data = getIntent().getExtras();
		mMsg = data.getString("msg");
		mBitmap = BitmapFactory.decodeFile(data.getString("img"));
		// launch API
		if ("sina".equals(data.getString("which"))) {
			mIWeiboAPI.registerApp();
			if (!mIWeiboAPI.isWeiboAppInstalled()
					|| !mIWeiboAPI.isWeiboAppSupportAPI()) {
				return;
			}
			SNSUtil.shareToSinaWeibo(this, mIWeiboAPI, mMsg, mBitmap);
		} else {
			SNSUtil.authOnTencentMicroblog(mContext, SNSUtil.TENCENT_APP_KEY,
					SNSUtil.TENCENT_APP_SECRET_KEY, this);
		}
	}

	@Override
	public void onResponse(BaseResponse arg0) {
		switch (arg0.errCode) {
		case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
			NotificUtil.showShortToast("ok");
			break;
		case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_CANCEL:
			NotificUtil.showShortToast("cancel");
			break;
		case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
			NotificUtil.showShortToast("fail");
			break;
		}
	}

	@Override
	public void onCancel() {
		NotificUtil.showShortToast("download cancel");
	}

	@Override
	public void onAuthFail(int arg0, String arg1) {
		NotificUtil.showShortToast("auth fail");
	}

	@Override
	public void onAuthPassed(String arg0, WeiboToken arg1) {
		// store authorize data
		Util.saveSharePersistent(mContext, "ACCESS_TOKEN", arg1.accessToken);
		Util.saveSharePersistent(mContext, "EXPIRES_IN",
				String.valueOf(arg1.expiresIn));
		Util.saveSharePersistent(mContext, "OPEN_ID", arg1.openID);
		Util.saveSharePersistent(mContext, "REFRESH_TOKEN", "");
		Util.saveSharePersistent(mContext, "CLIENT_ID", SNSUtil.TENCENT_APP_KEY);
		Util.saveSharePersistent(mContext, "AUTHORIZETIME",
				String.valueOf(System.currentTimeMillis() / 1000l));
		// share it
		HttpCallback callback = new HttpCallback() {

			@Override
			public void onResult(Object arg0) {
				if (arg0 instanceof ModelResult) {
					ModelResult mr = (ModelResult) arg0;
					if (mr.isSuccess()) {
						NotificUtil.showShortToast("make it!");
					}
				}
			}
		};
		SNSUtil.shareToTencentMicroblog(mContext, mMsg, mBitmap, callback);
	}

	@Override
	public void onWeiBoNotInstalled() {
		NotificUtil.showShortToast("weibo not install");
	}

	@Override
	public void onWeiboVersionMisMatch() {
		NotificUtil.showShortToast("weibo version not match");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mIWeiboAPI.responseListener(getIntent(), this);
	}

}
