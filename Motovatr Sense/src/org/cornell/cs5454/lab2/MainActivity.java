package org.cornell.cs5454.lab2;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	/* TODO: add a field to store a reference to the 'go to SensorActivity' (aka 'continue') button */
	private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// for those who are less familiar with Java, the following line
    	// invokes the superclass constructor (in this case, Activity), which
    	// does some basic setup
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /* TODO: get reference to 'continue' button, store it to the field you created...
         * you do this via the findViewById(R.id.continue_button)
         * Note that the id you specify should match the one you declared in the layout file.
         * */
        continueButton = (Button) findViewById(R.id.continue_button);
        
        /* TODO: call setOnClickListener(), either passing it a new instance of android.view.View.OnClickListener
         * or implementing android.view.View.OnClickListener in this Activity and passing it 'this'
         */
        continueButton.setOnClickListener(this);
        
        /*
        // alternatively...
        continueButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), "Howdy", Toast.LENGTH_SHORT).show();
			}
		});
		*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /* TODO: implement click handler for the 'continue' button */
	@Override
	public void onClick(View clicked) {
		// TODO Auto-generated method stub
		switch (clicked.getId()) {
			case R.id.continue_button:
				// to switch to a new activity, we first create an 'intent', essentially
				// a message to the OS that tells it we want to launch some other task.
				// we create the intent, using the 
				Intent goNext = new Intent(MainActivity.this, SensorActivity.class);
				startActivity(goNext);
				break;
		}
		
	}
}
