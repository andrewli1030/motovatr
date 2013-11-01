package org.cornell.cs5454.lab2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cornell.cs5454.lab2.widgets.DumpView;
import org.cornell.cs5454.lab2.widgets.FeatureExtraction;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SensorActivity extends Activity implements SensorEventListener, OnClickListener {

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Button mToggleAccelButton;
	private TextView mStatusText;
	private boolean mIsCollecting;
	public static DumpView mAccelDump;
	public static EditText mUsername;
	private FeatureExtraction mClassifier;
	
	public static final String NONE = "none";
	public static final String STILL = "still";
	public static final String WALK = "walk";
	public static final String RUN = "run";
	public static final String OTHER = "other";
	
	private static String trainingMode = NONE;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		
		// get a handle to the sensor manager, so we can in turn acquire the accelerometer
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// get a handle to the accelerometer via the sensor manager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        // setup button handler
        mToggleAccelButton = (Button)findViewById(R.id.accel_toggle_button);
        
        mStatusText = (TextView)findViewById(R.id.statusTextView);
        
        mUsername = (EditText)findViewById(R.id.username);
        
        final SensorEventListener parentActivity = this;
        
        mToggleAccelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// get a button reference to the view, so we can change the text
				Button me = (Button)v;
				
				if (!mIsCollecting) {
					// start collecting
					me.setText(R.string.stop_collecting);
					if (trainingMode.equals(NONE))
						mStatusText.setText("Use the radio buttons to set ground truth");
					else
						mStatusText.setText("Training " + trainingMode);
			        // set ourselves as the receiver of events that the accelerometer produces
			        // (this includes when the reading as well as the estimated accuracy of the reading changes)
			        mSensorManager.registerListener(parentActivity, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
			        
			        // ...and set us as collecting
			        mIsCollecting = true;
				}
				else {
					// we were previously collecting, so stop
					me.setText(R.string.start_collecting);
					mSensorManager.unregisterListener(parentActivity);
					mIsCollecting = false;
					mStatusText.setText("Start accelerometer to collect data");
				}
			}
		});
        
        // get reference to text box so we can dump to it when we get a sensor event
        mAccelDump = (DumpView)findViewById(R.id.accel_dump);
        RadioGroup modes = (RadioGroup)findViewById(R.id.modes);
        mClassifier = new FeatureExtraction();
	}

	
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
		
	    // Check which radio button was clicked
	    int sel = view.getId();
	    switch(sel) {
	        case R.id.still:
	            if (checked)
		            trainingMode = STILL;
	            break;
	        case R.id.walk:
	            if (checked)
	            	trainingMode = WALK;
	            break;
	        case R.id.run:
	            if (checked)
	            	trainingMode = RUN;
	            break;
	        case R.id.other:
	            if (checked)
	            	trainingMode = OTHER;
	            break;
	        case R.id.stoprec:
	        	if (checked)
	        		trainingMode = NONE;
	        	break;
	        default:
	        	break;
	    }
	    if (mIsCollecting)
	    {
		    if (trainingMode.equals(NONE))
				mStatusText.setText("Use the radio buttons to set ground truth");
			else
				mStatusText.setText("Training " + trainingMode);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// stop the accelerometer so we don't waste battery while the screen's off
		// we'll let them start it up again manually
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mToggleAccelButton.setText(R.string.start_collecting);
		mSensorManager.unregisterListener(this);
		mIsCollecting = false;
		trainingMode = NONE;
		// also close the file in our dump view
		mAccelDump.destroy();
	}

	@Override
	public void onClick(View v) {
		// get a button reference to the view, so we can change the text
		Button me = (Button)v;
		
		if (!mIsCollecting) {
			// start collecting
			me.setText(R.string.stop_collecting);

	        // set ourselves as the receiver of events that the accelerometer produces
	        // (this includes when the reading as well as the estimated accuracy of the reading changes)
	        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	        
	        // ...and set us as collecting
	        mIsCollecting = true;
		}
		else {
			// we were previously collecting, so stop
			me.setText(R.string.start_collecting);
			mSensorManager.unregisterListener(this);
			mIsCollecting = false;
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// we don't care about this method
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// log the accelerometer reading to the screen and to a file
		mAccelDump.record(event.values[0], event.values[1], event.values[2], trainingMode);
		mClassifier.addAcceleromoterReading(event.values[0], event.values[1], event.values[2], trainingMode);
	}
}
