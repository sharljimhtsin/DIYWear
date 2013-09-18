/**
 * 
 */
package com.yeegol.DIYWear.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.yeegol.DIYWear.res.DataHolder;

/**
 * util class for display notification
 * 
 * @author sharl
 * 
 */
public class NotificUtil {
	public static void showShortToast(String msg) {
		Toast.makeText(DataHolder.getInstance().getContext(), msg,
				Toast.LENGTH_SHORT).show();
	}

	public static void showShortToast(int resId) {
		Toast.makeText(DataHolder.getInstance().getContext(), resId,
				Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(String msg) {
		Toast.makeText(DataHolder.getInstance().getContext(), msg,
				Toast.LENGTH_LONG).show();
	}

	public static void showLongToast(int resId) {
		Toast.makeText(DataHolder.getInstance().getContext(), resId,
				Toast.LENGTH_LONG).show();
	}

	public static void showAlertDia(String title, String msg, Context context) {
		AlertDialog dialog = new AlertDialog.Builder(context).setMessage(msg)
				.setTitle(title).create();
		dialog.show();
	}

	public static void showAlertDia(int resId, String msg, Context context) {
		AlertDialog dialog = new AlertDialog.Builder(context).setMessage(msg)
				.setTitle(resId).create();
		dialog.show();
	}
}
