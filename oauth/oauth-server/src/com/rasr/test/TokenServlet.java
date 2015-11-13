package com.rasr.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rasr.oauth.data.Client;
import com.rasr.oauth.data.Tokens;
import com.rasr.oauth.utils.Utilities;

import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;

/**
 * Servlet implementation class AuthServer
 */
@WebServlet("/token")
public class TokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final int TOKEN_EXPIRE_IN_SECS=3600;

	private static final String AUTH_SERVER="RASR_AUTH_SERVER";
	/**
	 * Default constructor.
	 */
	public TokenServlet() {
		// TODO Auto-generated constructor stub
	}

	OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

	class UnauthorizedException extends Exception{
		
		public UnauthorizedException(String error){
			super(error);
		}
		
	};
	
	private Client validateClient(String clientId, String clientSecret) throws Exception {
		Session s = null;
		try {
			s = Utilities.getSession();
			Transaction currentTransaction = s.beginTransaction();
			String sha1Hash = DigestUtils
					.sha1Hex(MessageDigest.getInstance("SHA-1").digest(clientSecret.getBytes("utf8")));
			String selectHql = "FROM Client where clientId = :id and clientSecret = :secret";
			Query query = s.createQuery(selectHql);
			query.setString("id", clientId);
			query.setString("secret", sha1Hash);
			return (Client) query.uniqueResult();

		} catch (Exception e) {
			System.out.println(e);
			throw e;
		} finally {
			if (s != null && s.isOpen())
				s.close();

		}
	}
	
	private void addToken(Client client,String token,Date expiration)
	{
		Session s = null;
		try {
			s = Utilities.getSession();
			Transaction currentTransaction = s.beginTransaction();
			Tokens newToken = new Tokens(client, token, expiration,this.AUTH_SERVER);
			s.save(newToken);
			currentTransaction.commit();

		} catch (Exception e) {
			System.out.println(e);
			throw e;
		} finally {
			if (s != null && s.isOpen())
				s.close();

		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OAuthResponse r = null;
		try {
			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);

			Client foundClient = validateClient(request.getParameter("client_id"),
					request.getParameter("client_secret"));
			
			if (foundClient == null)
			{
				throw new UnauthorizedException("Credentials didn't match!");
			}

			String authzCode = oauthRequest.getCode();

			// some code
			String accessToken = oauthIssuerImpl.accessToken();
			String refreshToken = oauthIssuerImpl.refreshToken();

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.SECOND, this.TOKEN_EXPIRE_IN_SECS);
			
			addToken(foundClient, accessToken, cal.getTime());
			
			// some code
			r = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK).setAccessToken(accessToken)
					.setExpiresIn(Integer.toString(this.TOKEN_EXPIRE_IN_SECS)).setRefreshToken(refreshToken).buildJSONMessage();

			response.setStatus(r.getResponseStatus());
			PrintWriter pw = response.getWriter();
			pw.print(r.getBody());
			pw.flush();
			pw.close();
			// if something goes wrong
		}catch(UnauthorizedException uae)
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		catch(Exception ex){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			try {
//				r = OAuthResponse.errorResponse(401).buildJSONMessage();
//			} catch (OAuthSystemException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
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

}
