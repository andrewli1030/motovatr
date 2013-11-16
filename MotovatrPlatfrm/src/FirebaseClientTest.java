import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

public class FirebaseClientTest {

	public FirebaseClientTest() {
		// TODO Auto-generated constructor stub
	}

	// User
	public void testGetWalkStat() {
		FirebaseClient.FirebaseUser user = FirebaseClient.user("Andrew");
		Map<String, Integer> walkStats = user.getWalkStats();
		fail("Not yet implemented");
	}

	public void testGetStats() {
		FirebaseClient.FirebaseUser user = FirebaseClient.user("Andrew");
		Map<String, Integer> walkStats = user.getStats();
		fail("Not yet implemented");
	}

	// Group
	public void testGetWalkStats() {
		FirebaseClient.FirebaseGroup group = FirebaseClient.group("cornell");
		Map<String, Map<String, Integer>> walkStats = group.getWalkStats();
		fail("Not yet implemented");
	}

	public void testGetUsers() {
		FirebaseClient.FirebaseGroup group = FirebaseClient.group("cornell");
		List<String> userNames = group.getUserNames();
		fail("Not yet implemented");
	}

	public void testGetServletHost() {
		String hostname = FirebaseClient.getServletHost();
		fail("Not yet implemented");
	}
}
