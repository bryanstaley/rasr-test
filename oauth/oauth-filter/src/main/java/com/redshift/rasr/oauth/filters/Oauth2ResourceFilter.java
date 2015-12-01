package com.redshift.rasr.oauth.filters;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;

import com.redshift.rasr.oauth.data.Tokens;
import com.redshift.rasr.oauth.services.TokenDAO;
import com.redshift.rasr.oauth.services.TokenService;

@WebFilter(filterName = "Oauth2ResourceFilter", urlPatterns = { "/rasr/*" })
public class Oauth2ResourceFilter implements Filter {

	private Logger logger = Logger.getLogger(Oauth2ResourceFilter.class.getName());
	
	private TokenService tokenService = new TokenDAO();

	private static String NOT_FOUND_MESSAGE = "Token does not exist";
	private static String EXPIRED_MESSAGE = "Token Expired";
	
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("Oauth2Resource filter initialization");

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		logger.info("Oauth2Resource doFilter");

		HttpServletResponse httpResponse = (HttpServletResponse) response;

		try {
			HttpServletRequest servletRequest = (HttpServletRequest) request;
			OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(servletRequest,
					ParameterStyle.HEADER);

			// Get the access token
			String accessToken = oauthRequest.getAccessToken();

			// ... validate access token
			Tokens token = tokenService.getToken(accessToken);

			if (token == null) {
				logger.warning(String.format("Token %s not found in the database!", accessToken));
				OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setErrorDescription(Oauth2ResourceFilter.NOT_FOUND_MESSAGE).setError(Oauth2ResourceFilter.NOT_FOUND_MESSAGE)
						.buildHeaderMessage();
				for (Map.Entry<String,String> entry : oauthResponse.getHeaders().entrySet()) {
					httpResponse.setHeader(entry.getKey().toString(), entry.getValue().toString());
					logger.info(String.format("For access token %s, Adding header %s value %s:",accessToken, entry.getKey(),entry.getValue()));

				}
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} else if (token.getExpires().compareTo(new Date()) < 0) {
				OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setErrorDescription(Oauth2ResourceFilter.EXPIRED_MESSAGE).setError(Oauth2ResourceFilter.EXPIRED_MESSAGE)
						.buildHeaderMessage();
				for (Map.Entry<String,String> entry : oauthResponse.getHeaders().entrySet()) {
					httpResponse.setHeader(entry.getKey().toString(), entry.getValue().toString());
					logger.info(String.format("For access token %s, Adding header %s value %s:",accessToken, entry.getKey(),entry.getValue()));

				}
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			} else {
				//Authentication passed!
				logger.info(String.format("Authentication succeeded for access token %s",accessToken));
				chain.doFilter(request, response);
			}
		} catch (OAuthProblemException ex) {
			logger.severe(String.format("Oauth2 Filter caught exception %s",ex.getMessage()));
			// build error response
			OAuthResponse oauthResponse;
			try {
				oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setErrorDescription(ex.getDescription()).setError(ex.getDescription()).buildHeaderMessage();
				for (Map.Entry<String,String> entry : oauthResponse.getHeaders().entrySet()) {
					httpResponse.setHeader(entry.getKey().toString(), entry.getValue().toString());
					logger.info(String.format("Adding header %s value %s:",entry.getKey(),entry.getValue()));
				}
			} catch (OAuthSystemException e) {
				logger.severe(String.format("Oauth2 Filter caught outer exception %s",ex.getMessage()));
				e.printStackTrace();
			}

			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		} catch (Exception e) {
			logger.severe(String.format("Oauth2 Filter caught outer exception %s",e.getMessage()));
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}

	public void destroy() {
		logger.info("Oauth2Resource filter destroy");
	}

}
