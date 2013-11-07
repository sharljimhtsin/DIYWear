/**
 * 
 */
package com.yeegol.DIYWear.util;

import java.lang.Thread.State;

import android.os.Handler;

/**
 * utils class for multi-thread holder
 * 
 * @author sharl
 * 
 */
public class ThreadUtil {

	private static final String TAG = ThreadUtil.class.getName();

	public static void doInBackground(Runnable r) {
		Thread t = new Thread(r);
		t.start();
	}

	public static void doInForeground(Runnable r) {
		Thread t = new Thread(r);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			LogUtil.logException(e, TAG);
		}
	}

	/**
	 * different from {@link ThreadUtil}.doInForeground is will not block UI
	 * thread
	 * 
	 * @param r
	 *            runnable
	 * @param h
	 *            handle to be notify
	 * @see doInForeground
	 */
	public static void doInBackgroundWithTip(Runnable r, final Handler h) {
		final Thread t = new Thread(r);
		t.start();
		// start a new non-UI thread to show tip
		new Thread(new Runnable() {

			@Override
			public void run() {
				h.sendMessageAtFrontOfQueue(h.obtainMessage(97));
				while (!(t.getState() == State.TERMINATED)) {
					// do nothing
				}
				h.sendMessage(h.obtainMessage(98));
			}
		}).start();
	}

	/**
	 * run worker thread in foreground with "waiting" tip
	 * 
	 * @param r
	 * @param h
	 */
	public static void doInForegroundWithTip(Runnable r, Handler h) {
		h.sendMessage(h.obtainMessage(97));
		Thread t = new Thread(r);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			LogUtil.logException(e, TAG);
		} finally {
			h.sendMessage(h.obtainMessage(98));
		}
	}
}
