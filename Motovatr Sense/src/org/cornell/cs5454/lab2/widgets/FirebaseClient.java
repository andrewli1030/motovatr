package org.cornell.cs5454.lab2.widgets;

import java.util.Date;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

public class FirebaseClient {

	private static final String MOTOVATR_FB_URL = "https://motovatr.firebaseio.com/";
	private static final String DEFAULT_USER = "default";

	private static enum ActivityField {
		COUNT, DURATION
	}

	private Firebase homeRef;
	private Firebase usersRef;
	private Firebase activitiesListRef;
	private Firebase statsRef;

	public FirebaseClient(String username) {
		if (username == null || username.equals("")) {
			username = DEFAULT_USER;
		}
		homeRef = new Firebase(MOTOVATR_FB_URL);
		usersRef = homeRef.child("users").child(username);
		activitiesListRef = usersRef.child("activities_list");
		statsRef = usersRef.child("stats");
	}

	public void addActivityDuration(String activity, long duration) {
		usersRef.child(activity).child(ActivityField.DURATION.toString())
				.setValue(duration);
	}

	public void addActivity(String activity) {
		if (activity.equals("still") || activity.equals("other")) {
			return;
		}
		Firebase newPushRef = activitiesListRef.push();
		newPushRef.setValue(activity, new Date().getTime());
		statsRef.child(activity).runTransaction(new Transaction.Handler() {
			@Override
			public Transaction.Result doTransaction(MutableData currentData) {
				if (currentData.getValue() == null) {
					currentData.setValue(1);
				} else {
					int count = currentData.getValue(Integer.class);
					currentData.setValue(count + 1);
				}

				return Transaction.success(currentData);
			}

			@Override
			public void onComplete(FirebaseError error, boolean committed,
					DataSnapshot currentData) {

			}
		});

		// usersRef.child(activity).child(ActivityField.COUNT.toString()).setValue(count);
	}

}
