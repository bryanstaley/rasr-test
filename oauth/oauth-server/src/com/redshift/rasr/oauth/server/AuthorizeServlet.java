package com.redshift.rasr.oauth.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;

/**
 * Servlet implementation class AuthServer
 */
@WebServlet("/authorize")
public class AuthorizeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static String dbLoc = "/home/bstaley/oauth";
	private static long expireSecs = 10000;
	private static String SERVER_NAME="testAccessServer";
	
			
	/**
	 * Default constructor.
	 */
	public AuthorizeServlet() {
		// TODO Auto-generated constructor stub

		try {
			OAuthClientRequest request = OAuthClientRequest
					.authorizationLocation("https://localhost:8080/oauth-server/authorize").setClientId("test-client")
					.setRedirectURI("http://www.example.com/redirect").buildQueryMessage();
			System.out.println(request.getLocationUri());
		} catch (OAuthSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// dynamically recognize an OAuth profile based on request
			// characteristic (params,
			// method, content type etc.), perform validation
			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
			
			// Authenticate client using client_id and client_secret
			
			

			// validateRedirectionURI(oauthRequest);
			
			String accessCode = oauthIssuerImpl.authorizationCode();
			addCodeToDB(accessCode, "todo", SERVER_NAME);
			
			// build OAuth response
			OAuthResponse resp = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND)
					.setCode(accessCode).location(oauthRequest.getRedirectURI())
					.buildQueryMessage();

			response.sendRedirect(resp.getLocationUri());

			// if something goes wrong
		} catch (OAuthProblemException ex) {
			OAuthResponse resp;
			try {
				resp = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND).error(ex)
						.location(ex.getRedirectUri()).buildQueryMessage();
				response.sendRedirect(resp.getLocationUri());
			} catch (OAuthSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (OAuthSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void addCodeToDB(String code, String forClient, String authServer) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbLoc);
			System.out.println("Opened database successfully");
			long expireTime = System.currentTimeMillis() / 1000L + expireSecs;

			stmt = c.createStatement();
			String sql = String.format("INSERT INTO access_codes VALUES ('%s','%s','%s',%s)", code, forClient,
					authServer, expireTime);

			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
