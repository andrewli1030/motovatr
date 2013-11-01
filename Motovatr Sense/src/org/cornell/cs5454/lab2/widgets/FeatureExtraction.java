package org.cornell.cs5454.lab2.widgets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cornell.cs5454.lab2.SensorActivity;

public class FeatureExtraction {

	public enum Activity {
		WALK, RUN, STILL
	}

	public static final int MIN_FREQUENCY = 1;
	public static final int MAX_FREQUENCY = 5;

	public static void main(String[] args) {

		BufferedReader br = null;

		try {

			FileWriter writer = new FileWriter("accelfeatures.csv");
			writer.append("mean,variance,fft_1,fft_2,fft_3,fft_4,fft_5,label\n");

			String line;

			br = new BufferedReader(new FileReader("acceldata.txt"));

			List<Double> magnitudes = new ArrayList<Double>();
			double magnitudesSum = 0;
			Integer currentSecond = null;

			while ((line = br.readLine()) != null) {
				String[] accelReadings = line.split(", ");
				double currentTimestamp = Double.valueOf(accelReadings[0]);
				double x = Double.valueOf(accelReadings[1]);
				double y = Double.valueOf(accelReadings[2]);
				double z = Double.valueOf(accelReadings[3]);
				String activity = accelReadings[4];
				double mag = getMagnitude(x, y, z);

				if (currentSecond == null) {
					currentSecond = (int) (currentTimestamp / 1000);
				}

				if ((int) (currentTimestamp / 1000) == currentSecond) {
					magnitudes.add(mag);
					magnitudesSum += mag;
				} else {
					double mean = magnitudesSum / magnitudes.size();
					double variance = calculateVariance(magnitudes, mean);
					writer.append(String.valueOf(mean));
					writer.append(",");
					writer.append(String.valueOf(variance));
					writer.append(",");
					for (int i = MIN_FREQUENCY; i <= MAX_FREQUENCY; i++) {
						writer.append(String.valueOf(goertzel(magnitudes, i,
								magnitudes.size())));
						writer.append(",");
					}
					writer.append(activity);
					writer.append("\n");

					currentSecond = (int) (currentTimestamp / 1000);
					magnitudes.clear();
					magnitudes.add(mag);
					magnitudesSum = mag;
				}
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	private Integer prevSecond;
	List<Double> magnitudes = new ArrayList<Double>();
	double magnitudesSum = 0;
	String prevActivity;

	public void addAcceleromoterReading(float x, float y, float z,
			String groundTruth) {
		long time = System.currentTimeMillis();
		int currentSecond = (int) (time / 1000);
		if (prevSecond == null) {
			prevSecond = (int) (time / 1000);
		}
		if (prevActivity == null) {
			prevActivity = groundTruth;
		}

		double mag = FeatureExtraction.getMagnitude(x, y, z);

		if (prevActivity.equals(groundTruth) && prevSecond == currentSecond) {
			magnitudes.add(mag);
			magnitudesSum += mag;
		} else {
			Double[] goertzels = new Double[6];
			double mean = magnitudesSum / magnitudes.size();
			double variance = FeatureExtraction.calculateVariance(magnitudes,
					mean);
			for (int i = FeatureExtraction.MIN_FREQUENCY; i <= FeatureExtraction.MAX_FREQUENCY; i++) {
				goertzels[i] = FeatureExtraction.goertzel(magnitudes, i,
						magnitudes.size());
			}

			prevSecond = currentSecond;
			magnitudes.clear();
			magnitudes.add(mag);
			magnitudesSum = mag;
			prevActivity = groundTruth;

			String classification = classify(mean, variance, goertzels);
			FirebaseClient client = new FirebaseClient(SensorActivity.mUsername
					.getText().toString());
			client.addActivity(classification);

			SensorActivity.mAccelDump.recordActivity(classification,
					groundTruth);
		}
	}

	public static double calculateVariance(List<Double> magnitudes, double mean) {
		double sum = 0.0;
		for (Double mag : magnitudes) {
			sum += Math.pow(mag - mean, 2.0);
		}
		return sum / magnitudes.size();
	}

	static Double getMagnitude(double x, double y, double z) {
		return Math
				.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
	}

	public static double goertzel(List<Double> accData, double freq, double sr) {
		double s_prev = 0;
		double s_prev2 = 0;
		double coeff = 2 * Math.cos((2 * Math.PI * freq) / sr);
		double s;
		for (int i = 0; i < accData.size(); i++) {
			double sample = accData.get(i);
			s = sample + coeff * s_prev - s_prev2;
			s_prev2 = s_prev;
			s_prev = s;
		}
		double power = s_prev2 * s_prev2 + s_prev * s_prev - coeff * s_prev2
				* s_prev;
		return power;
	}

	private String classify(double mean, double variance, Double[] fft) {
		String result;

		// variance is the most informative characteristic.
		// Very low = still or driving, very high = running, moderate = walking

		if (variance <= 4.033733) {

			// within the low variance range, need to consult other features to
			// discriminate between sitting and driving
			if (variance <= 0.005868) {

				// extremely low variance means sitting
				if (variance <= 0.004573)
					result = "still";

				// tough cases - probably some overfitting
				else if (mean <= 10.338448) {
					if (mean <= 10.296169)
						result = "still";
					else {
						// fft[2] is equivalent to the feature fft_3
						if (fft[2] <= 0.160918)
							result = "still";
						else
							result = "other";
					}
				} else
					result = "still";
			}

			// slightly elevated variance indicates driving
			else
				result = "other";
		}

		// moderate vs high variance
		else if (variance <= 33.614675)
			result = "walk";
		else
			result = "run";

		return result;
	}
}
