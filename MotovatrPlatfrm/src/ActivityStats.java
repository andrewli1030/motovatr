import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ActivityStats
 */
@WebServlet("/ActivityStats/api")
public class ActivityStats extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ActivityStats() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		String name = request.getParameter("name");
		String activity = request.getParameter("activity");

		String json = "";

//		JSONObject json = null;
		if (type.equals("user")) {
			if (activity.equals("all")) {
				Map<String, Integer> stats = FirebaseClient.user(name)
						.getStats();
				json = stats.toString();

//				json = new JSONObject(stats);
			} else if (activity.equals("walk")) {
				Map<String, Integer> stats = FirebaseClient.user(name).getWalkStats();
				json = stats.toString();

//				json = new JSONObject(stats);
			}
		} else if (type.equals("group")) {
			if (activity.equals("all")) {
				Map<String, Map<String, Integer>> stats = FirebaseClient.group(
						name).getStats();
				json = stats.toString();

//				json = new JSONObject(stats);
			} else if (activity.equals("walk")) {
				Map<String, Map<String, Integer>> stats = FirebaseClient.group(name)
						.getWalkStats();
				json = stats.toString();

//				json = new JSONObject(stats);
			}
		}
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		// Get the printwriter object from response to write the required json
		// object to the output stream
		PrintWriter out = response.getWriter();
		// Assuming your json object is **jsonObject**, perform the following,
		// it will return your json object
		out.print(json);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
