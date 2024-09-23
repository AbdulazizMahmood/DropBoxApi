package myDBoxClientMediator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DBoxJsonMediator
 */
@WebServlet("/DBoxJsonMediator")
public class DBoxJsonMediator extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DBoxJsonMediator() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		

		ClientMediator mediator = new ClientMediator();

		StringBuilder sb = new StringBuilder();
		String line;

		// Read the JSON request body
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately
		}

		// Convert the StringBuilder content to a string (the JSON request body)
		String jsonRequestBody = sb.toString();
		System.out.println("Received JSON Request Body: " + jsonRequestBody);

		// Parse the JSON object
		JSONObject jsonObject = new JSONObject(jsonRequestBody);

		// Get the 'reqType' from the JSON request body (if present)
		if (!jsonObject.has("reqType") || !jsonObject.getString("reqType").equals("setProfilePhoto")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
//		var requestBody = {
//			    "reqType": "setProfilePhoto",  // Adding the reqType for request identification
//			    "access_token": accessToken,   // The access token for authentication
//			    "photo": {
//			        ".tag": "base64_data",     // Specifies the format of the photo data
//			        "base64_data": selectedProfileImageBase64  // The base64-encoded image data
//			    }
//			};

		// Extract the 'access_token' from the JSON body
		String accessToken = jsonObject.getString("access_token");
		System.out.println("Access token: " + accessToken);

		// Remove them from the JSON object
		jsonObject.remove("access_token");
		jsonObject.remove("reqType");

		
		

		// Pass the access token and photo JSON to the mediator (implement this method
		// accordingly)
		mediator.setProfilePhoto(accessToken, jsonObject);

		// Send response back to the client
		PrintWriter out = response.getWriter();
		out.write(mediator.getResult());
		out.flush();
		out.close();
	}
}
