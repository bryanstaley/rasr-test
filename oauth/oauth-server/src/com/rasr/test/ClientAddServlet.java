package com.rasr.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rasr.oauth.data.Client;
import com.rasr.oauth.utils.Utilities;

import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;

/**
 * Servlet implementation class AuthServer
 */
@WebServlet("/client/add")
public class ClientAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public ClientAddServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Session s = null;
		try {
			s = Utilities.getSession();
			
			String clientId = request.getParameter("client_id");
			String clientSecret = request.getParameter("client_secret");
			byte[] data = MessageDigest.getInstance("SHA-1").digest(clientSecret.getBytes("utf8"));
			String sha1Secret = DigestUtils.sha1Hex(data);
			Client c=new Client(clientId,sha1Secret);
			Transaction currentTransaction = s.beginTransaction();
			s.save(c);
			currentTransaction.commit();
			
		}catch (Exception e) {
			System.out.println(e);
		} finally {
			if (s != null && s.isOpen())
				s.close();

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
