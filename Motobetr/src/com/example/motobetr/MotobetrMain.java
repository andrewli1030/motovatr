package com.example.motobetr;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter.LengthFilter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.motovatr.android.MotovatrAndroidClient;

public class MotobetrMain extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_motobetr_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.motobetr_main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment implements
			OnClickListener, OnChronometerTickListener {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private static final String MOTOVATR_PIE_URL = "http://motowager.ngrok.com";

		public DummySectionFragment() {
		}

		private EditText mUser1;
		private EditText mUser2;
		private EditText mHostname;
		private View mRootView;
		private Button mRefresh;
		private TextView mUser1Stats;
		private TextView mUser2Stats;
		private WebView mPieWebView;
		private Chronometer mChronometer;
		private MotovatrAndroidClient mMotovatrAndroidClient;
		private EditText mTimerEditText;
		private TextView mWinnerTextView;
		private Button mChallenge;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_motobetr_main_dummy,
					container, false);
			// TextView dummyTextView = (TextView) rootView
			// .findViewById(R.id.section_label);
			// dummyTextView.setText(Integer.toString(getArguments().getInt(
			// ARG_SECTION_NUMBER)));
			this.init();
			return mRootView;
		}

		private void init() {
			this.mUser1 = (EditText) mRootView
					.findViewById(R.id.user1_edittext);

			this.mUser2 = (EditText) mRootView
					.findViewById(R.id.user2_edittext);
			this.mHostname = (EditText) mRootView
					.findViewById(R.id.hostname_edittext);
			this.mRefresh = (Button) mRootView
					.findViewById(R.id.refresh_button);
			this.mChallenge = (Button) mRootView
					.findViewById(R.id.challenge_button);
			this.mUser1Stats = (TextView) mRootView
					.findViewById(R.id.user1_stats);
			this.mUser2Stats = (TextView) mRootView
					.findViewById(R.id.user2_stats);

			mPieWebView = (WebView) mRootView.findViewById(R.id.pie_webview);
			mPieWebView.setWebViewClient(new WebViewClient());
			mPieWebView.getSettings().setJavaScriptEnabled(true);
			mPieWebView.loadUrl(MOTOVATR_PIE_URL);
			mPieWebView.getSettings().setLoadWithOverviewMode(true);
			mPieWebView.getSettings().setUseWideViewPort(true);
			mPieWebView.getSettings().setBuiltInZoomControls(true);

			mChronometer = (Chronometer) this.mRootView
					.findViewById(R.id.chronometer1);

			mTimerEditText = (EditText) this.mRootView
					.findViewById(R.id.timer_edittext);

			mWinnerTextView = (TextView) this.mRootView
					.findViewById(R.id.winner_textview);

			mChallenge.setOnClickListener(this);
			mRefresh.setOnClickListener(this);
			mChronometer.setOnChronometerTickListener(this);
			this.mMotovatrAndroidClient = new MotovatrAndroidClient();
		}

		@Override
		public void onClick(View v) {
			String buttonText = ((Button) v).getText().toString();
			if (buttonText.equals("Refresh")) {
				try {
					String hostname = this.mHostname.getText().toString();
					JSONObject stats1 = this.mMotovatrAndroidClient.getStats(
							"user", this.mUser1.getText().toString(), "walk");
					JSONObject stats2 = this.mMotovatrAndroidClient.getStats(
							"user", this.mUser2.getText().toString(), "walk");
					// this.mUser1Stats.setText(stats1.getInt("walk"));
					// this.mUser2Stats.setText(stats2.getInt("walk"));
					System.out.println();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (buttonText.equals("Challenge")) {
				mChronometer.setBase(SystemClock.elapsedRealtime());
				mChronometer.start();
			}
		}

		@Override
		public void onChronometerTick(Chronometer chronometer) {
			long t = SystemClock.elapsedRealtime() - chronometer.getBase();
			chronometer.setText(DateFormat.format("mm:ss", t));
			// String time = chronometer.getFormat();
			if (t / 1000.0 > getTimeSum(mTimerEditText.getText().toString())) {
				chronometer.stop();

				try {
					JSONObject stats1 = this.mMotovatrAndroidClient.getStats(
							"user", this.mUser1.getText().toString(), "walk");
					JSONObject stats2 = this.mMotovatrAndroidClient.getStats(
							"user", this.mUser2.getText().toString(), "walk");
					try {
						String winner = this.mUser1.getText().toString();
						if (stats1.getInt("walk") < stats2.getInt("walk")) {
							winner = this.mUser2.getText().toString();
						}
						mWinnerTextView.setText(String.format("%s wins!",
								winner));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		private int getTimeSum(String time) {
			String[] timeSplit = time.split(":");
			int sum = 0;
			int[] multipliers = { 1, 60, 60 * 60 };
			int mult_index = 0;
			for (int i = timeSplit.length - 1; i >= 0; i--) {
				sum += (Integer.valueOf(timeSplit[i]) * multipliers[mult_index]);
				mult_index++;
			}
			return sum;
		}
	}

}
