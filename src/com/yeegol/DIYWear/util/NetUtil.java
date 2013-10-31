/**
 * 
 */
package com.yeegol.DIYWear.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yeegol.DIYWear.R;
import com.yeegol.DIYWear.entity.Model;
import com.yeegol.DIYWear.res.DataHolder;

/**
 * utils class for remote resource access
 * 
 * @author sharl
 * 
 */
public class NetUtil {

	private final static String TAG = NetUtil.class.getName();

	private final static String KEY = DataHolder.getInstance().getResource()
			.getString(R.string.api_key);

	public final static String DOMAIN_API = DataHolder.getInstance()
			.getResource().getString(R.string.api_domain_api);

	public final static String DOMAIN_API_PURE = DataHolder.getInstance()
			.getResource().getString(R.string.api_domain_api_pure);

	public final static String DOMAIN_FILE = DataHolder.getInstance()
			.getResource().getString(R.string.api_domain_file);

	public final static String DOMAIN_FILE_PURE = DataHolder.getInstance()
			.getResource().getString(R.string.api_domain_file_pure);

	/**
	 * @param paras
	 * @return url http://abc.cd/?efg=hi&jk=lmn
	 */
	public static String buildURL(List<NameValuePair> paras) {
		String url = DOMAIN_API;
		url += "?1=1"; // dummy parameters for better loop
		BasicNameValuePair token = new BasicNameValuePair("key", KEY);
		paras.add(token);
		for (NameValuePair nameValuePair : paras) {
			url += "&" + nameValuePair.getName() + "="
					+ nameValuePair.getValue();
		}
		return url;
	}

	public static String buildURLForBasic(String preview, String direcetion,
			String fileName) {
		return NetUtil.DOMAIN_FILE + preview + "views/" + direcetion + "/"
				+ fileName;
	}

	public static String buildURLForBasicConf(String preview, String direcetion) {
		return NetUtil.DOMAIN_FILE + preview + "views/" + direcetion + "/1.txt";
	}

	public static String buildURLForNormal(String preview, String direction) {
		return NetUtil.DOMAIN_FILE + preview + "variants/0/" + direction + "/"
				+ direction + ".png";
	}

	public static String buildURLForNormalConf(String preview) {
		return NetUtil.DOMAIN_FILE + preview + "variants/0/" + "/1.txt";
	}

	public static String buildURLForThumb(String preview) {
		return NetUtil.DOMAIN_FILE + preview + "preview/thumb.jpg";
	}

	public static String buildURLForCollocation(String preview) {
		return NetUtil.DOMAIN_FILE + preview;
	}

	/**
	 * @param url
	 * @return text
	 */
	public static String getTextFromWeb(String url, String host) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpHost httpHost = new HttpHost(host, 80, HttpHost.DEFAULT_SCHEME_NAME);
		HttpRequest httpRequest = new BasicHttpRequest(HttpGet.METHOD_NAME, url);
		HttpResponse httpResponse = null;
		String body = "";
		try {
			httpResponse = httpClient.execute(httpHost, httpRequest);
			if (HttpStatus.SC_OK != httpResponse.getStatusLine()
					.getStatusCode()) {
				body = "";
			} else {
				body = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			LogUtil.logException(e, TAG);
		} catch (IOException e) {
			LogUtil.logException(e, TAG);
		} finally {
			LogUtil.logDebug("API URL is:" + url, TAG);
		}
		return body;
	}

	/**
	 * @param url
	 * @return image
	 */
	public static Bitmap getImageFromWeb(String url, String host) {
		// try to get Bitmap from local cache
		if (Model.getInstance().getBitmapFromCache(url) != null) {
			return Model.getInstance().getBitmapFromCache(url);
		}
		HttpClient httpClient = new DefaultHttpClient();
		HttpHost httpHost = new HttpHost(host, 80, HttpHost.DEFAULT_SCHEME_NAME);
		HttpRequest httpRequest = new BasicHttpRequest(HttpGet.METHOD_NAME, url);
		HttpResponse httpResponse = null;
		Bitmap image = null;
		try {
			httpResponse = httpClient.execute(httpHost, httpRequest);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine()
					.getStatusCode()) {
				HttpEntity entity = httpResponse.getEntity();
				image = BitmapFactory.decodeStream(entity.getContent());
			}
		} catch (ClientProtocolException e) {
			LogUtil.logException(e, TAG);
		} catch (IOException e) {
			LogUtil.logException(e, TAG);
		} finally {
			LogUtil.logDebug("Image URL is:" + url, TAG);
			if (image != null) {
				Model.getInstance().putBitmapToCache(url, image); // cache it
			}
		}
		return image;
	}

	/**
	 * @param url
	 * @return ret InputStream
	 */
	public static InputStream getObjectFromWebInLowLevel(String url) {
		InputStream ret = null;
		try {
			URL path = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) path
					.openConnection();
			ret = connection.getInputStream();
		} catch (MalformedURLException e) {
			LogUtil.logException(e, TAG);
		} catch (IOException e) {
			LogUtil.logException(e, TAG);
		}
		return ret;
	}

}
