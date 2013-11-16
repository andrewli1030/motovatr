import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

public class FirebaseClient {

	private static final String MOTOVATR_FB_URL = "https://motovatr.firebaseio.com/";
	private static final String DEFAULT = "default";
	private static final Firebase homeRef = new Firebase(
			FirebaseClient.MOTOVATR_FB_URL);

	public static String[] users = { "andrew", "ted", "david" };

	private static enum ActivityField {
		walk, run
	}

	public FirebaseClient() {
		this("");
	}

	public static FirebaseUser user(String username) {
		return new FirebaseUser(username);
	}

	public static FirebaseGroup group(String groupName) {
		return new FirebaseGroup(groupName);
	}

	private static class Bean {
		private Object value;

		public Bean(Object value) {
			this.value = value;
		}
	}

	public static String getServletHost() {
		Firebase hostRef = homeRef.child("servlet_host");

		final Bean hostname = new Bean(null);
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

	public static class FirebaseGroup {

		private Firebase groupRef;
		private Firebase usersRef;
		private String mGroupName;

		public FirebaseGroup(String groupName) {
			if (groupName == null || groupName.equals("")) {
				groupName = DEFAULT;
			}
			this.mGroupName = groupName;
			// this.users = new ArrayList<FirebaseUser>();
			this.groupRef = FirebaseClient.homeRef.child("groups").child(
					this.mGroupName);
			this.usersRef = this.groupRef.child("usernames");

		}

		public void addUser(String username) {
			Firebase newPushRef = this.usersRef.push();
			newPushRef.setValue(username);
		}

		public List<String> getUserNames() {
			final Bean listBean = new Bean(null);
			this.usersRef
					.addListenerForSingleValueEvent(new ValueEventListener() {

						@Override
						public void onDataChange(DataSnapshot arg0) {
							if (arg0.getValue() == null) {
								listBean.value = new ArrayList<String>();
							} else {
								listBean.value = ((List<String>) arg0
										.getValue());
							}
							synchronized (listBean) {
								listBean.notify();
							}
							// users.add(arg0);
						}

						@Override
						public void onCancelled() {
							// TODO Auto-generated method stub

						}
					});
			synchronized (listBean) {
				while (listBean.value == null) {
					try {
						listBean.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return (List<String>) listBean.value;
		}

		public Map<String, Map<String, Integer>> getWalkStats() {
			Map<String, Map<String, Integer>> stats = new HashMap<String, Map<String, Integer>>();
			final List<String> users = this.getUserNames();
			for (String username : users) {
				if (username != null) {
					stats.put(username, FirebaseClient.user(username)
							.getWalkStats());
				}
			}
			return stats;
		}

		public Map<String, Map<String, Integer>> getStats() {
			Map<String, Map<String, Integer>> stats = new HashMap<String, Map<String, Integer>>();
			final List<String> users = this.getUserNames();
			for (String username : users) {
				if (username != null) {
					stats.put(username, FirebaseClient.user(username)
							.getStats());
				}
			}
			return stats;
		}
	}

	public static class FirebaseUser {
		private Firebase userRef;
		private Firebase activitiesListRef;
		private Firebase statsRef;
		private String mUsername;

		public FirebaseUser(String username) {
			if (username == null || username.equals("")) {
				username = DEFAULT;
			}
			mUsername = username;

			userRef = FirebaseClient.homeRef.child("users").child(mUsername);
			activitiesListRef = userRef.child("activities_list");
			statsRef = userRef.child("stats");
		}

		public Map<String, Integer> getWalkStats() {
			Firebase walkStatsRef = statsRef.child("walk");
			final Bean walkStat = new Bean(null);
			walkStatsRef
					.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot snapshot) {
							int walkStats;
							if (snapshot.getValue() == null) {
								walkStats = 0;
							} else {
								walkStats = snapshot.getValue(Integer.class);
							}
							walkStat.value = walkStats;
							synchronized (walkStat) {
								walkStat.notify();
							}
							// map.put(mUsername, walkStats);
						}

						@Override
						public void onCancelled() {
							System.err.println("Listener was cancelled");
						}
					});
			while (walkStat.value == null) {
				try {
					synchronized (walkStat) {
						walkStat.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Map<String, Integer> walkStats = new HashMap<String, Integer>();
			walkStats.put("walk", (Integer) walkStat.value);
			return walkStats;
		}

		public Map<String, Integer> getStats() {
			final Bean stats = new Bean(null);
			statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					Map<String, Integer> walkStats;
					if (snapshot.getValue() == null) {
						walkStats = new HashMap<String, Integer>();
					} else {
						walkStats = (Map<String, Integer>) snapshot.getValue();
					}
					stats.value = walkStats;
					synchronized (stats) {
						stats.notify();
					}
					// map.put(mUsername, walkStats);
				}

				@Override
				public void onCancelled() {
					System.err.println("Listener was cancelled");
				}
			});
			while (stats.value == null) {
				try {
					synchronized (stats) {
						stats.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return (Map<String, Integer>) stats.value;
		}

		public void addActivity(String activity) {
			if (activity.equals("still") || activity.equals("other")) {
				return;
			}
			// Firebase newPushRef = activitiesListRef.push();
			// newPushRef.setValue(activity, new Date().getTime());
			statsRef.child(activity).runTransaction(new Transaction.Handler() {
				@Override
				public Transaction.Result doTransaction(MutableData currentData) {
					if (currentData.getValue() == null) {
						currentData.setValue(1);
					} else {
						int count = currentData.getValue(Integer.class);
						currentData.setValue(count + 1);
					}
					// populateItems();
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

	public FirebaseClient(String username) {

		this.getUsers();
	}

	private static Comparator<Entry<String, Integer>> c = new Comparator<Map.Entry<String, Integer>>() {

		@Override
		public int compare(Entry<String, Integer> lhs,
				Entry<String, Integer> rhs) {
			if (lhs.getValue() > rhs.getValue()) {
				return -1;
			} else {
				return 1;
			}
		}
	};
	private final static Map<String, Integer> leaderboardMap = new HashMap<String, Integer>();
	private final static SortedSet<Entry<String, Integer>> sortedleaderboard = new TreeSet<Entry<String, Integer>>(
			c);
	private static final List<String> items = new ArrayList<String>();

	private static Set<String> mUsers;

	public void getUsers() {

		// FirebaseClient client = new FirebaseClient();
		Firebase usersRef = homeRef.child("users");
		usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				int walkStats;
				if (snapshot.getValue() == null) {
					mUsers = new HashSet<String>();
				} else {
					mUsers = ((HashMap) snapshot.getValue()).keySet();
				}
			}

			@Override
			public void onCancelled() {
				System.err.println("Listener was cancelled");
			}
		});
	}

	void populateItems() {
		for (String user : mUsers) {
			FirebaseClient client = new FirebaseClient(user);
			// client.getWalkStats(leaderboardMap);
		}
		sortedleaderboard.clear();
		sortedleaderboard.addAll(leaderboardMap.entrySet());

		for (Entry<String, Integer> entry : sortedleaderboard) {
			String row = String.format("%s: %d", entry.getKey(),
					entry.getValue());
			items.add(row);
		}
	};

}
