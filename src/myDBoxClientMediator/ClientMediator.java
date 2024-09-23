package myDBoxClientMediator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class ClientMediator {

	private String queryResult;

	public ClientMediator() {

		System.out.println("-------------");
		System.out.println("-------------");
		System.out.println("-------------");
		System.out.println("I am Mediator...");
		System.out.println("-------------");
	}

	public void sendRequest(String str) throws URISyntaxException, IOException {
		System.out.println("SendRequest :");

		URI redirectURI = new URI("http://localhost:8082/MyDBoxClient/"); // any url to where you want to redirect the
																			// user
		String appKey = "a7vvbvrhs7196je"; // app key that would be get after creating an app at Dropbox
		URI uri = new URI("https://www.dropbox.com/oauth2/authorize");
		StringBuilder requestUri = new StringBuilder(uri.toString());
		requestUri.append("?client_id=");
		requestUri.append(URLEncoder.encode(appKey, "UTF-8"));
		requestUri.append("&response_type=");
		requestUri.append(URLEncoder.encode("code", "UTF-8"));
		requestUri.append("&redirect_uri=" + redirectURI.toString());

		URL requestUrl = new URL(requestUri.toString());
		System.out.println("RequestURL : " + requestUrl.toString());

		queryResult = requestUri.toString();

	}

	public void accessToken(String str) throws URISyntaxException, IOException {

		System.out.println("-----IN Access Token--------");

		String code = "" + str; // code get from previous step
		String appKey = "a7vvbvrhs7196je"; // appKey get using previous step
		String appSecret = "51emzzgim5c1cyf"; // appSecret get using previous step
		String redirectURI = "http://localhost:8082/MyDBoxClient/"; // any url to where you want to redirect the user
		StringBuilder tokenUri = new StringBuilder("code=");
		tokenUri.append(URLEncoder.encode(code, "UTF-8"));
		tokenUri.append("&grant_type=");
		tokenUri.append(URLEncoder.encode("authorization_code", "UTF-8"));
		tokenUri.append("&client_id=");
		tokenUri.append(URLEncoder.encode(appKey, "UTF-8"));
		tokenUri.append("&client_secret=");
		tokenUri.append(URLEncoder.encode(appSecret, "UTF-8"));
		tokenUri.append("&redirect_uri=" + redirectURI.toString());
		URL url = new URL("https://api.dropbox.com/oauth2/token");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {

			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + tokenUri.toString().length());

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(tokenUri.toString());
			outputStreamWriter.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			queryResult = response.toString();

		} finally {
			connection.disconnect();
		}

	}

	public void getAccountInfo(String accessToken, String accountId) throws URISyntaxException, IOException {

		System.out.println("-----IN Account Info--------");

		// Request body content for the account info
		String content = "{\"account_id\": \"" + accountId + "\"}";

		// URL for Dropbox API v2 get_account
		URL url = new URL("https://api.dropboxapi.com/2/users/get_account");

		// Open connection
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			// Set the request method to POST
			connection.setRequestMethod("POST");

			// Set the necessary headers
			connection.setRequestProperty("Authorization", "Bearer " + accessToken);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true); // Enables output for the body

			// Write the JSON body (account_id) to the request
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = content.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			int responseCode = connection.getResponseCode();

			// Check the response code
			if (responseCode == 200) {
				// Successful request, read the input stream
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				StringBuilder response = new StringBuilder();
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// Print the successful response
				System.out.println("Response: " + response.toString());
				queryResult = response.toString(); // Assuming 'queryResult' is a field you're storing this in.

			} else if (responseCode == 400) {
				// Bad request, read the error stream to get the response body
				BufferedReader errorReader = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"));
				StringBuilder errorResponse = new StringBuilder();
				String errorLine;

				while ((errorLine = errorReader.readLine()) != null) {
					errorResponse.append(errorLine);
				}
				errorReader.close();

				// Print the error response
				System.out.println("Error response (400): " + errorResponse.toString());

			} else {
				// Handle other response codes as needed
				System.out.println("Unexpected response code: " + responseCode);
			}

		} finally {
			// Disconnect after completion
			connection.disconnect();
		}
	}

	public void uploadFile(String token, String path) throws URISyntaxException, IOException {

		System.out.println("-----IN upload--------");

		System.out.println("path is : " + path);

		String access_token = "" + token;
		String sourcePath = "" + path; // required file path on local file system
		Path pathFile = Paths.get(sourcePath);
		byte[] data = Files.readAllBytes(pathFile);

		String content = "{\"path\": \"/MyDBoxClient_App01_files/images/image_initial_uploaded.png\",\"mode\": \"add\",\"autorename\": true,\"mute\": false,\"strict_conflict\": false}";
		URL url = new URL("https://content.dropboxapi.com/2/files/upload");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {

			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + access_token);
			connection.setRequestProperty("Content-Type", "application/octet-stream");
			connection.setRequestProperty("Dropbox-API-Arg", "" + content);
			connection.setRequestProperty("Content-Length", String.valueOf(data.length));

			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			queryResult = response.toString();

		} finally {
			connection.disconnect();
		}

	}

	public void createFolder(String accessToken, String folderPath) throws IOException {
		System.out.println("-----IN Create Folder--------");

		// String access_token = ""+token;
		String content = "{\"path\": \"" + folderPath + "\",\"autorename\": true}";
		URL url = new URL("https://api.dropboxapi.com/2/files/create_folder_v2");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + accessToken);
			connection.setRequestProperty("Content-Type", "application/json");

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(content);
			outputStreamWriter.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println(response.toString());
			queryResult = response.toString();

		} finally {
			connection.disconnect();
		}
	}

	public void setProfilePhoto(String accessToken, JSONObject jsonObject) throws IOException {

		// Dropbox API endpoint for setting the profile photo
		String apiEndpoint = "https://api.dropboxapi.com/2/account/set_profile_photo";
		URL url = new URL(apiEndpoint);

		// Open HTTPS connection
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("POST");

		// Set the required headers
		connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true); // This allows sending a request body

		// Convert the JSONObject to a string
		String jsonRequestBody = jsonObject.toString();

		// Write the JSON request body (with the base64 image data) to the output stream
		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = jsonRequestBody.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		// Get the response code to determine if the request was successful
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpsURLConnection.HTTP_OK) {
			// Success - read the response from the input stream
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			StringBuilder response = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Print out the successful response
			System.out.println("Profile photo set successfully. Response: " + response.toString());
			queryResult = response.toString();
		} else {
			// Error - read and print the error response
			BufferedReader errorReader = new BufferedReader(
					new InputStreamReader(connection.getErrorStream(), "utf-8"));
			StringBuilder errorResponse = new StringBuilder();
			String errorLine;

			while ((errorLine = errorReader.readLine()) != null) {
				errorResponse.append(errorLine);
			}
			errorReader.close();

			// Print the error details
			System.out.println("Error response: " + errorResponse.toString());
		}

		// Disconnect the connection after completion
		connection.disconnect();
	}

	public void getAccountInfo_(String str) throws URISyntaxException, IOException {

		System.out.println("-----IN account info--------");

		String access_token = "" + str;
		StringBuilder accountInfoUri = new StringBuilder("https://api.dropbox.com/1/account/info");
		accountInfoUri.append("?access_token=");
		accountInfoUri.append(URLEncoder.encode(access_token, "UTF-8"));
		URL url = new URL(accountInfoUri.toString());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {

			connection.setRequestMethod("GET");
			InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
			String responseDropBox = inputStreamReader.toString();
			System.out.println(responseDropBox);
			// responseDropBox contains result JSON from Dropbox is something like:
			// {"access_token": "<access token>", "token_type": "Bearer", "uid": "<user
			// ID>"}

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			queryResult = response.toString();

		} finally {
			connection.disconnect();
		}

	}

	public void uploadFile_(String token, String path) throws URISyntaxException, IOException {

		System.out.println("-----IN upload--------");

		System.out.println("path is : " + path);

		String access_token = "" + token;
		String sourcePath = "" + path; // required file path on local file system
		Path pathFile = Paths.get(sourcePath);
		byte[] data = Files.readAllBytes(pathFile);
//			URL url = new URL("https://api-content.dropbox.com/1/files_put/${root}/${destinationPath}?access_token=${accessToken}");

		StringBuilder accountInfoUri = new StringBuilder(
				"https://api-content.dropbox.com/1/files_put/dropbox/MyFirstDApp_files/images/image_initial_uploaded.png");
		accountInfoUri.append("?access_token=");
		accountInfoUri.append(URLEncoder.encode(access_token, "UTF-8"));
		URL url = new URL(accountInfoUri.toString());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "mime/type");
			connection.setRequestProperty("Content-Length", String.valueOf(data.length));
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			queryResult = response.toString();

		} finally {
			connection.disconnect();
		}

	}

	public void downloadFile(String token, String filePath, String downloadLocation) throws IOException {
		System.out.println("-----IN download--------");
		System.out.println("Downloading file from: " + filePath);

		String accessToken = token; // Dropbox API Access Token

		// Prepare the Dropbox download endpoint
		URL url = new URL("https://content.dropboxapi.com/2/files/download");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			// Set up the connection properties
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + accessToken);
			connection.setRequestProperty("Dropbox-API-Arg", "{\"path\":\"" + filePath + "\"}");
			connection.setDoOutput(true);

			// Check for valid response (HTTP 200 OK)
			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new IOException("Failed to download file. HTTP response code: " + responseCode);
			}

			// Execute the request
			InputStream inputStream = connection.getInputStream();

			// Create the output file where you want to save the downloaded file
			File file = new File(downloadLocation);
			FileOutputStream fileOutputStream = new FileOutputStream(file);

			// Write the file to the output stream
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, bytesRead);
			}

			fileOutputStream.flush();
			fileOutputStream.close();
			inputStream.close();

			System.out.println("File downloaded successfully to: " + downloadLocation);

		} finally {
			connection.disconnect();
		}
	}

	public void listDropboxItems(String token, String folderPath) throws IOException {
		System.out.println("-----IN listDropboxItems--------");
		System.out.println("Listing items in folder: " + folderPath);

		String access_token = "" + token;

		// Fix: If folderPath is "/", set it to an empty string
		String path = (folderPath == null || folderPath.equals("/") || folderPath.isEmpty()) ? "" : folderPath;

		String content = "{\"path\": \"" + path
				+ "\",\"recursive\": false,\"include_media_info\": false,\"include_deleted\": false,\"include_has_explicit_shared_members\": false,\"include_mounted_folders\": true,\"include_non_downloadable_files\": true}";

		URL url = new URL("https://api.dropboxapi.com/2/files/list_folder");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + access_token);
			connection.setRequestProperty("Content-Type", "application/json");

			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(content.getBytes("UTF-8"));
			outputStream.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Print the result for debugging
			System.out.println("Dropbox folder items: " + response.toString());

			// Store the result in the queryResult variable to send it to the servlet
			// response
			queryResult = response.toString();

		} catch (IOException e) {
			System.err.println("Error in Dropbox request: " + e.getMessage());
			throw new IOException("Failed to list Dropbox items. HTTP response code: " + connection.getResponseCode()
					+ ". Error: " + e.getMessage());
		} finally {
			connection.disconnect();
		}
	}

	public void deleteFile(String token, String path) throws URISyntaxException, IOException {

		System.out.println("-----IN delete--------");

		String access_token = "" + token;
		String filePath = "" + path; // Dropbox path of the file you want to delete

		String content = "{\"path\": \"" + filePath + "\"}";
		URL url = new URL("https://api.dropboxapi.com/2/files/delete_v2");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {

			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + access_token);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(content.length()));

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(content);
			outputStreamWriter.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			queryResult = response.toString();

		} finally {
			connection.disconnect();
		}
	}

	public void getFileMetadata(String token, String filePath) throws IOException {
		String accessToken = token;
		String path = filePath; // The file path in Dropbox

		String content = "{\"path\": \"" + path + "\"}";
		URL url = new URL("https://api.dropboxapi.com/2/files/get_metadata");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + accessToken);
			connection.setRequestProperty("Content-Type", "application/json");

			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(content.getBytes());
			outputStream.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Print metadata result
			System.out.println("result: " + response.toString());
			queryResult = response.toString();
		} finally {
			connection.disconnect();
		}
	}

	public String getResult() {
		// queryResult = "Done!";
		return queryResult;
	}

}
