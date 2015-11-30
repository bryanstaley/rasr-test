package com.redshift.rasr.oauth.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redshift.rasr.oauth.services.ClientDAO;
import com.redshift.rasr.oauth.services.ClientService;

/**
 * Servlet implementation class AuthServer
 */
@WebServlet("/client/add")
public class ClientAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(ClientAddServlet.class.getName());
	private ClientService clientService= new ClientDAO();

	/**
	 * Default constructor.
	 */
	public ClientAddServlet() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String clientId = request.getParameter("client_id");
		String clientSecret = request.getParameter("client_secret");
		
		if (clientId == null || clientSecret == null)
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Both client_id and client_secret are required to create a user!");
			return;
		}
		
		try{
			clientService.addClient(clientId, clientSecret);
			logger.info(String.format("Created client %s",clientId));
		}catch (Exception e)
		{
			logger.warning(String.format("Unable to create client %s due to %s",clientId,e));
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
