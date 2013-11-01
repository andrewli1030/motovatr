package com.example.motovatrtree;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class MotovatrTreeActivity extends Activity implements OnClickListener {

	private static final String MOTOVATR_TREE_URL = "http://motovatr.ngrok.com";
	private WebView mTreeWebView;
	private EditText mUrl;
	private Button mLoadButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mUrl = (EditText) findViewById(R.id.url_edittext);
		mTreeWebView = (WebView) findViewById(R.id.tree_webview);
		mTreeWebView.setWebViewClient(new WebViewClient());
		mTreeWebView.getSettings().setJavaScriptEnabled(true);
		mTreeWebView.loadUrl(MOTOVATR_TREE_URL);
		mTreeWebView.getSettings().setLoadWithOverviewMode(true);
		mTreeWebView.getSettings().setUseWideViewPort(true);
		mTreeWebView.getSettings().setBuiltInZoomControls(true);
		
		mLoadButton = (Button) findViewById(R.id.load_button);
		mLoadButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		mTreeWebView.loadUrl(mUrl.getText().toString());
	}

}
