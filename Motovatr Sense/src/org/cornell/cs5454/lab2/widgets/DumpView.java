package org.cornell.cs5454.lab2.widgets;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cornell.cs5454.lab2.R;
import org.cornell.cs5454.lab2.SensorActivity;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DumpView extends LinearLayout {
	// displays up to 'mCapacity' readings (default 20)
	private ListView mList = new ListView(getContext());
	// holds the displayed readings
	ArrayList<String> readings = new ArrayList<String>();
	// relates readings and mList
	private ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
			getContext(), R.layout.dump_list_item, readings);
	// defines the number of readings displayed
	private int mCapacity = 15;
	// holds a handle to the output dump file where we'll store all the readings
	// (note that this may become cumbersome in the future)
	private FileOutputStream accelFile;
	private FileOutputStream activityFile;

	public DumpView(Context context) {
		super(context);
		setupView();
	}

	public DumpView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView();
	}

	protected void setupView() {
		// and stick our child control into this collection
		addView(mList);
		mList.setAdapter(mAdapter);
		mList.setDivider(null);
		mList.setDividerHeight(0);

		// get a handle to the dump file so that we can record readings there,
		// too
		try {
			accelFile = new FileOutputStream(
					Environment.getExternalStorageDirectory() + "/"
							+ getContext().getString(R.string.accelfile_name),
					true);
			activityFile = new FileOutputStream(
					Environment.getExternalStorageDirectory()
							+ "/"
							+ getContext()
									.getString(R.string.activityfile_name),
					true);
		} catch (FileNotFoundException e) {
			// we're more or less going to ignore this
			// (it likely won't occur since we're using MODE_APPEND, which
			// should create the file if it doesn't exist)
			e.printStackTrace();
		}

	}

	public void destroy() {
		// close the input file
		try {
			accelFile.close();
		} catch (IOException e) {
			// we're more or less going to ignore this, too...
			e.printStackTrace();
		}
	}

	/**
	 * Returns the number of lines that this DumpView displays.
	 * 
	 * @return the maximum number of lines that the dump displays
	 */
	public int getCapacity() {
		return mCapacity;
	}

	/**
	 * Sets the maximum number of lines that this DumpView will display.
	 * 
	 * @param mCapacity
	 *            the maximum number of lines that the dump will display
	 */
	public void setCapacity(int mCapacity) {
		this.mCapacity = mCapacity;
	}

	/**
	 * Records the accelerometer data to the training data file, with the label
	 * from the radio button. TODO: store the data in a buffer, and once per
	 * second process it to recognize the user's activity.
	 * 
	 * @param in
	 *            the line of output to add to the dump view
	 */
	private Integer prevSecond;
	List<Double> magnitudes = new ArrayList<Double>();
	double magnitudesSum = 0;
	String prevActivity;

	public void record(float x, float y, float z, String groundTruth) {
		// write the reading out to our dump file
		long time = System.currentTimeMillis();
		try {
			if (accelFile != null)
				accelFile.write(String.format("%d, %f, %f, %f, %s%n", time, x,
						y, z, groundTruth).getBytes());
		} catch (IOException e) {
			// we're more or less going to ignore this
			e.printStackTrace();
		}
		// int currentSecond = (int) (time / 1000);
		// if (prevSecond == null) {
		// prevSecond = (int) (time / 1000);
		// }
		// if (prevActivity == null) {
		// prevActivity = groundTruth;
		// }
		//
		// double mag = FeatureExtraction.getMagnitude(x, y, z);
		//
		// if (prevActivity.equals(groundTruth) && prevSecond == currentSecond)
		// {
		// magnitudes.add(mag);
		// magnitudesSum += mag;
		// } else {
		// Double[] goertzels = new Double[6];
		// double mean = magnitudesSum / magnitudes.size();
		// double variance = FeatureExtraction.calculateVariance(magnitudes,
		// mean);
		// for (int i = FeatureExtraction.MIN_FREQUENCY; i <=
		// FeatureExtraction.MAX_FREQUENCY; i++) {
		// goertzels[i] = FeatureExtraction.goertzel(magnitudes, i,
		// magnitudes.size());
		// }
		//
		// prevSecond = currentSecond;
		// magnitudes.clear();
		// magnitudes.add(mag);
		// magnitudesSum = mag;
		// prevActivity = groundTruth;
		//
		// String classification = classify(mean, variance, goertzels);
		// recordActivity(classification, groundTruth);
		// }
	}

//	public String classify(double mean, double variance, Double[] goertzels) {
//		double fft_1 = goertzels[1];
//		double fft_2 = goertzels[2];
//		double fft_3 = goertzels[3];
//		double fft_4 = goertzels[4];
//		double fft_5 = goertzels[5];
//		if (mean <= 10.114439) {
//			if (mean <= 9.39583) {
//				return "run";
//			} else {
//				if (fft_1 <= 20.574161) {
//					return "still";
//				} else {
//					return "walk";
//				}
//			}
//		} else {
//			if (variance <= 44.236584) {
//				if (mean <= 17.969607) {
//					if (mean <= 11.378997) {
//						return "walk";
//					} else {
//						if (mean <= 13.076778) {
//							if (fft_4 <= 1473.579482) {
//								return "run";
//							} else {
//								return "walk";
//							}
//						} else {
//							return "walk";
//						}
//					}
//				} else {
//					return "run";
//				}
//			} else {
//				return "run";
//			}
//		}
//	}

	/**
	 * Adds a line of output to the dump view, removing the oldest line if the
	 * capacity is exceeded. When a classification other than "none" is passed,
	 * it writes the result to the classifier results file (activitydata.txt on
	 * the sdcard). Your final code will call this once per second.
	 * 
	 * Note that dumps are displayed with the newest element at the top.
	 * 
	 * @param in
	 *            the line of output to add to the dump view
	 */
	public void recordActivity(String classification, String groundTruth) {
		try {
			if (activityFile != null
					&& !classification.equals(SensorActivity.NONE))
				activityFile.write(String
						.format("%d, %s, %s%n", System.currentTimeMillis(),
								classification, groundTruth).getBytes());
		} catch (IOException e) {
			// we're more or less going to ignore this
			e.printStackTrace();
		}

		// put the new item at the beginning of the display list
		readings.add(0, String.format("classifier: %s, ground truth: %s",
				classification, groundTruth));

		// ensure that we haven't exceeded capacity
		// if we have, remove the last reading and check again
		while (readings.size() > mCapacity)
			readings.remove(readings.size() - 1);

		mAdapter.notifyDataSetChanged();
	}
}
