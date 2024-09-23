package myDBoxClientMediator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myDBoxClientMediator.ClientMediator;

/**
 * Servlet implementation class DBoxClientMediator
 */
@WebServlet("/DBoxClientMediator")
public class DBoxClientMediator extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DBoxClientMediator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());

		doPost(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);

		ClientMediator mediator = new ClientMediator();

		if (request.getParameter("reqType").toString().equals("doQuery")) {
			String endPoint = "";
			try {
				mediator.sendRequest(endPoint);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * //System.out.println("Try to redirect to : " + mediator.getResult()); //
			 * response.sendRedirect(mediator.getResult());
			 * 
			 * response.setContentType("text/html");
			 * 
			 * response.setStatus(response.SC_MOVED_TEMPORARILY);
			 * response.setHeader("Location", mediator.getResult());
			 */

			PrintWriter out = response.getWriter();
			out.write(mediator.getResult());
			out.flush();
			out.close();

		} else if (request.getParameter("reqType").toString().equals("doQuery1")) {

			String code = "";
			try {
				code = request.getParameter("code").toString();
				mediator.accessToken(code);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PrintWriter out = response.getWriter();
			out.write(mediator.getResult());
			out.flush();
			out.close();

		} else if (request.getParameter("reqType").toString().equals("doQuery2")) {

			String access_token = "";
			String account_id = "";
			try {
				access_token = request.getParameter("access_token").toString();
				account_id = request.getParameter("account_id").toString();
				mediator.getAccountInfo(access_token, account_id);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PrintWriter out = response.getWriter();
			out.write(mediator.getResult());
			out.flush();
			out.close();

		} else if (request.getParameter("reqType").toString().equals("doQuery3")) {

			String access_token = "";
			String filePath = "";
			try {
				access_token = request.getParameter("access_token").toString();

				filePath = request.getParameter("filePath").toString();
				System.out.println("___ Path : " + filePath);
				ServletContext context = getServletContext();
				String fullPath = context.getRealPath("/" + filePath);
//	    		URL resourceUrl = context.getResource(filePath);
//	    		String fullPath = resourceUrl.toString();
				System.out.println("Path : " + fullPath);

				mediator.uploadFile(access_token, fullPath);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PrintWriter out = response.getWriter();
			out.write(mediator.getResult());
			out.flush();
			out.close();

		} else if (request.getParameter("reqType").equals("doQuery4")) {
			String accessToken = request.getParameter("access_token");
			String folderPath = request.getParameter("folderPath");

			try {
				mediator.createFolder(accessToken, folderPath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			PrintWriter out = response.getWriter();
			out.write(mediator.getResult());
			out.flush();
			out.close();
		} else if (request.getParameter("reqType").toString().equals("downloadFile")) {
			System.out.println("do Dwonlad : ");
			String access_token = request.getParameter("access_token").toString();
			String filePath = request.getParameter("filePath").toString();
			String downloadPath = request.getParameter("downloadPath").toString();
			String downloadLocation = downloadPath + new File(filePath).getName();

			try {
				mediator.downloadFile(access_token, filePath, downloadLocation);
			} catch (IOException e) {
				e.printStackTrace();
			}

			PrintWriter out = response.getWriter();
			out.write("{\"message\": \"File downloaded successfully\"}");
			out.flush();
			out.close();
		} else if (request.getParameter("reqType").toString().equals("listDropboxItems")) {
			String access_token = request.getParameter("access_token").toString();
			String folderPath = request.getParameter("folderPath") != null
					? request.getParameter("folderPath").toString()
					: "";

			try {
				mediator.listDropboxItems(access_token, folderPath);

				String result = mediator.getResult();
				if (result == null || result.isEmpty()) {
					result = "{\"error\": \"No data returned from Dropbox.\"}";
				}

				PrintWriter out = response.getWriter();
				out.write(result);
				out.flush();
				out.close();

			} catch (Exception e) {
				e.printStackTrace();

				PrintWriter out = response.getWriter();
				out.write("{\"error\": \"Failed to list Dropbox items: " + e.getMessage() + "\"}");
				out.flush();
				out.close();
			}
		} else if (request.getParameter("reqType").toString().equals("doQuery5")) {
			String access_token = "";
			String filePath = "";
			try {
				access_token = request.getParameter("access_token").toString();
				filePath = request.getParameter("filePath").toString();

				ServletContext context = getServletContext();
				String fullPath = context.getRealPath("/" + filePath);

				mediator.deleteFile(access_token, fullPath);

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			PrintWriter out = response.getWriter();
			out.write(mediator.getResult());
			out.flush();
			out.close();
		} else if (request.getParameter("reqType").toString().equals("getMetadata")) {
			String accessToken = request.getParameter("access_token").toString();
			String filePath = request.getParameter("filePath").toString();
			mediator.getFileMetadata(accessToken, filePath);

			PrintWriter out = response.getWriter();
			if (out != null && mediator.getResult() != null) {
				out.write(mediator.getResult());
			} else {
				System.out.println("Error: Null response or result.");
			}
			out.flush();
			out.close();
		} else {
			// Handle other request types or respond with an error
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

}
