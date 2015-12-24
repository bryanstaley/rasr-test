package com.redshift.rasr.oauth.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.message.OAuthResponse;

import com.redshift.rasr.oauth.data.Client;
import com.redshift.rasr.oauth.services.ClientDAO;
import com.redshift.rasr.oauth.services.ClientService;
import com.redshift.rasr.oauth.services.TokenDAO;
import com.redshift.rasr.oauth.services.TokenService;

import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;

/**
 * Servlet implementation class AuthServer
 */
@WebServlet("/token")
public class TokenServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(TokenServlet.class.getName());
	private static final long serialVersionUID = 1L;
	
	private static final int TOKEN_EXPIRE_IN_SECS=3600;

	private static final String AUTH_SERVER="RASR_AUTH_SERVER";
	
	private ClientService clientService = new ClientDAO();
	
	private TokenService tokenService = new TokenDAO();
	/**
	 * Default constructor.
	 */
	public TokenServlet() {
		logger.info(String.format("Starting token servlet with expire time %s and auth server %s", TokenServlet.TOKEN_EXPIRE_IN_SECS,TokenServlet.AUTH_SERVER));
	}

	OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

	class UnauthorizedException extends Exception{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UnauthorizedException(String error){
			super(error);
		}
		
	};

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OAuthResponse r = null;
		logger.info("doGet for /token");
		
		try {
			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);

			String clientId = request.getParameter("client_id");
			String clientSecret = request.getParameter("client_secret");
			Client foundClient = clientService.getClient(clientId,clientSecret);
			
			if (foundClient == null)
			{
				logger.warning(String.format("Unable to find client %s.  Perhaps secret was wrong?", clientId));
				throw new UnauthorizedException("Credentials didn't match!");
			}

			oauthRequest.getCode();

			// some code
			String accessToken = oauthIssuerImpl.accessToken();
			String refreshToken = oauthIssuerImpl.refreshToken();

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.SECOND, TokenServlet.TOKEN_EXPIRE_IN_SECS);
			
			tokenService.addToken(foundClient, accessToken, cal.getTime(),TokenServlet.AUTH_SERVER);
			
			// some code
			r = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK).setAccessToken(accessToken)
					.setExpiresIn(Integer.toString(TokenServlet.TOKEN_EXPIRE_IN_SECS)).setRefreshToken(refreshToken).buildJSONMessage();

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
			logger.severe("received exception" + ex);
			ex.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
