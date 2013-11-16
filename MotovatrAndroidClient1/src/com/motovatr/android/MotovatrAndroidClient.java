package com.motovatr.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

public class MotovatrAndroidClient {

	private String mHostname;

	public MotovatrAndroidClient() {
		// mHostname = getServletHost();
		// mHostname = "192.168.1.139";
		mHostname = "10.33.45.171";
	}

	public static String getServletHost() {
		String MOTOVATR_FB_URL = "https://motovatr.firebaseio.com/";
		Firebase homeRef = new Firebase(MOTOVATR_FB_URL);

		Firebase hostRef = homeRef.child("servlet_host");
		final Bean hostname = new Bean();
		hostRef.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot arg0) {

				if (arg0.getValue() == null) {
					hostname.value = "";
				} else {
					hostname.value = arg0.getValue(String.class);
				}
				synchronized (hostname) {
					hostname.notify();
				}
			}

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub

			}
		});

		while (hostname.value == null) {

			try {
				synchronized (hostname) {
					hostname.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return (String) hostname.value;

	}

	private String prepareRequest(String type, String name, String activity)
			throws UnsupportedEncodingException {
		if (mHostname.length() == 0) {
			mHostname = "192.168.1.139";
		}

		String url = String.format(
				"http://%s/MotovatrPlatfrm/ActivityStats/api", mHostname);
		String charset = "UTF-8";
		String query = String.format("type=%s&name=%s&activity=%s",
				URLEncoder.encode(type, charset),
				URLEncoder.encode(name, charset),
				URLEncoder.encode(activity, charset));
		return url + "?" + query;
	}

	public JSONObject getStats(String type, String name, String activity)
			throws ClientProtocolException, IOException {
		String url = prepareRequest(type, name, activity);
		Bean resp = new Bean();
		new RequestTask(resp).execute(url);
		synchronized (resp) {
			while (resp.value == null) {
				try {
					resp.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return (JSONObject) resp.value;
	}

	private static class Bean {
		Object value;
	}

	class RequestTask extends AsyncTask<String, String, String> {

		Bean mResp;

		public RequestTask(Bean resp) {
			this.mResp = resp;
		}

		@Override
		protected String doInBackground(String... uri) {
			DefaultHttpClient httpclient = new DefaultHttpClient();

			final HttpParams httpParameters = httpclient.getParams();

			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000);

			HttpResponse response;
			String responseString = null;
			try {

				response = httpclient.execute(new HttpGet(uri[0]));
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				// TODO Handle problems..
			} catch (IOException e) {
				// TODO Handle problems..
			}
			try {
				JSONObject ob = new JSONObject(responseString);
				synchronized (this.mResp) {
					this.mResp.value = ob;
					this.mResp.notify();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Do anything with response..
		}
	}
}
