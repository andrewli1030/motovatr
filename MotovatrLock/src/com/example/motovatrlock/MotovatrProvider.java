package com.example.motovatrlock;

//canned imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.motovatr.android.MotovatrAndroidClient;

public class MotovatrProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d("Motovatr", "onUpdate()");
		// To prevent ANR timeouts, we perform the update in a service
		// The service - receiver pair function like a server & client, but both
		// resident on the mobile device
		// The service runs detached, and provides updates to the receiver,
		// which is connected to the UI
		// Android guidelines require receivers to time out, so the device does
		// not sit and spin too long.
		// Since we are accessing a remote server to generate update data, we
		// could pass the deadline.
		// Android guidelines recommend using a service to override the
		// deadline.
		// If we still get timeouts, we may need to restructure this to use a
		// thread
		context.startService(new Intent(context, UpdateService.class));
	}

	public static class UpdateService extends Service {
		@Override
		public void onStart(Intent intent, int startId) {
			Log.d("Motovatr", "onStart()");

			// Retrieve the update data from the remote server
			RemoteViews updateViews = buildUpdate(this);
			Log.d("Motovatr", "update built");

			// Push update for "this widget" to the lock screen
			ComponentName thisWidget = new ComponentName(this,
					MotovatrProvider.class);

			// Get access to the OS widget manager, so we can tell it to push
			// the update
			AppWidgetManager manager = AppWidgetManager.getInstance(this);

			// Do it
			manager.updateAppWidget(thisWidget, updateViews);
			Log.d("Motovatr", "widget updated");
		}

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		public RemoteViews buildUpdate(Context context) {

			// Get access to the widget's resources
			Resources res = context.getResources();

			RemoteViews views = null;

			// when we take the app live, this section will access data from
			// the remote server to update the lock screen visualization.
			// In the meantime, just use this hardcoded data.
			// FIREBASE API CALLS GO HERE
			Log.d("motovatr lock entry", "getting stats!");
			MotovatrAndroidClient client = new MotovatrAndroidClient();
			JSONObject resp = null;
			try {
				resp = client.getStats("group", "cornell", "walk");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("motovatr lock entry", "exception: " + e.toString());

			}
			Iterator<String> iterator = resp.keys();
			List<String> leaderboard = new ArrayList<String>();
			final String sep = ": ";
			while (iterator.hasNext()) {
				try {
					String name = iterator.next();
					int stats = resp.getJSONObject(name).getInt("walk");
					String entry = String.format("%s%s%s", name, sep, stats);
					Log.d("motovatr lock entry", entry);
					leaderboard.add(entry);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Collections.sort(leaderboard, new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					String[] split1 = arg0.split(sep);
					int count1 = Integer.valueOf(split1[1]);
					String[] split2 = arg1.split(sep);
					int count2 = Integer.valueOf(split2[1]);
					if (count1 > count2) {
						return -1;
					} else {
						return 1;
					}
				}

			});
			String leaderBoard = join(leaderboard, "\n");
			// String leaderBoard =
			// "David       35\nAndrew     29\nTed        21";
			// int myScore = 1;
			int myScore = (new Random().nextInt(6));

			if (myScore > 0) {
				// Build an update that holds the updated widget contents
				views = new RemoteViews(context.getPackageName(),
						R.layout.activity_motovatr_provider);

				// Set a background image determined by the user's level of
				// achievement
				// We could also vary display depending on the season, or the
				// type of incentive the
				// user has selected, such as progress vs personal goal,
				// competition, buddy system, group walkathon, etc.
				switch (myScore) {
				case 1:
					views.setImageViewResource(R.id.tree, R.drawable.tree1);
					break;
				case 2:
					views.setImageViewResource(R.id.tree, R.drawable.tree2);
					break;
				case 3:
					views.setImageViewResource(R.id.tree, R.drawable.tree3);
					break;
				case 4:
					views.setImageViewResource(R.id.tree, R.drawable.tree4);
					break;
				case 5:
					views.setImageViewResource(R.id.tree, R.drawable.tree5);
					break;
				case 6:
					views.setImageViewResource(R.id.tree, R.drawable.tree6);
					break;
				default:
					Log.d("Motovatr", "image switch failed");
					views.setImageViewResource(R.id.tree, R.drawable.tree);
					break;
				}
				// views.setImageViewResource(R.id.tree, R.drawable.tree);

				// On top of the background image, overlay additional imagery
				// tied to the user's progress
				views.setImageViewResource(R.id.overlay, R.drawable.bubbles);

				// Update the leaderboard on-screen with information pulled from
				// the remote server
				views.setTextViewText(R.id.update, leaderBoard);

				// THANKS TO CAMERON KETCHAM FOR A HUGE ASSIST ON THIS PART.
				// Since the user can install the widget in multiple places in
				// their UI,
				// Build a list of all widget instances that need updating
				ComponentName thisWidget = new ComponentName(this,
						MotovatrProvider.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(this);
				int[] allWidgetIds = manager.getAppWidgetIds(thisWidget);

				// When user clicks on widget, launch to widget provider to
				// perform another update
				Intent defineIntent = new Intent(context,
						MotovatrProvider.class);

				// Tell the OS widget manager that we intend to do an update
				defineIntent
						.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

				// Tell the OS widget manager what we want to update
				defineIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
						allWidgetIds);

				// Having defined the launch parameters, identify the provider
				// as a broadcast receiver
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
						0, defineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				// Intent defineIntent = new Intent(Intent.ACTION_MAIN);
				// PendingIntent pendingIntent =
				// PendingIntent.getActivity(context,
				// 0 /* no requestCode */, defineIntent, 0 /* no flags */);

				// Link the pending update action to a screen click
				views.setOnClickPendingIntent(R.id.layout, pendingIntent);

			} else {
				// Didn't find an update, so show error message
				views = new RemoteViews(context.getPackageName(),
						R.layout.activity_motovatr_provider);
				views.setTextViewText(R.id.update,
						context.getString(R.string.widget_error));
			}
			return views;
		}
	}

	static String join(Collection<?> s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (!iter.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}

}

/*
 * eclipse boilerplate not used
 * 
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * setContentView(R.layout.activity_motovatr_provider); }
 * 
 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
 * menu; this adds items to the action bar if it is present.
 * getMenuInflater().inflate(R.menu.motovatr, menu); return true; }
 */