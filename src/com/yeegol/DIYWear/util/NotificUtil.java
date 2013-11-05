/**
 * 
 */
package com.yeegol.DIYWear.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.Toast;

import com.yeegol.DIYWear.R;
import com.yeegol.DIYWear.res.DataHolder;

/**
 * util class for display notification
 * 
 * @author sharl
 * 
 */
public class NotificUtil {

	private static final String TAG = NotificUtil.class.getName();

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

	public static void showAlertDia(View view, Context context) {
		AlertDialog dialog = new AlertDialog.Builder(context).setView(view)
				.create();
		dialog.show();
	}

	public static void showAlertDia(int resId, String msg, Context context) {
		AlertDialog dialog = new AlertDialog.Builder(context).setMessage(msg)
				.setTitle(resId).create();
		dialog.show();
	}

	public static void showAlertDiaWithYesOrNo(int resId, String msg,
			Context context, OnClickListener listener) {
		AlertDialog dialog = new AlertDialog.Builder(context).setMessage(msg)
				.setTitle(resId)
				.setPositiveButton(R.string.alert_dial_undo_yes_btn, listener)
				.setNegativeButton(R.string.alert_dial_undo_no_btn, listener)
				.create();
		dialog.show();
	}

	public static void showAlertDiaWithMultiItem(String title,
			CharSequence[] items, Context context, OnClickListener listener) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
				.setItems(items, listener).create();
		dialog.show();
	}

	public static void showAlertDiaWithMultiItem(int resId,
			CharSequence[] items, Context context, OnClickListener listener) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(resId)
				.setItems(items, listener).create();
		dialog.show();
	}
}
