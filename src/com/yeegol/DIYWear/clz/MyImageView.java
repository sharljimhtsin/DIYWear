/**
 * 
 */
package com.yeegol.DIYWear.clz;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yeegol.DIYWear.util.NetUtil;
import com.yeegol.DIYWear.util.ThreadUtil;

/**
 * ImageView with web attribute,base on {@link ImageView}
 * 
 * @author sharl
 * 
 */
public class MyImageView extends ImageView {

	private static final String TAG = MyImageView.class.getName();

	private static final String KEY = "bitmap";

	/**
	 * @param context
	 */
	public MyImageView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * set & bind image of remote
	 * 
	 * @param url
	 *            http://……
	 */
	public void setURL(final String url) {
		final ImageView imageView = this;
		final Handler handler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				Bitmap bitmap = msg.getData().getParcelable(KEY);
				imageView.setImageBitmap(bitmap);
				return true;
			}
		});
		ThreadUtil.doInBackground(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = NetUtil.getImageFromWeb(url,
						NetUtil.DOMAIN_FILE_PURE);
				Message message = new Message();
				Bundle data = new Bundle();
				data.putParcelable(KEY, bitmap);
				message.setData(data);
				handler.sendMessage(message);
			}
		});
	}

}
