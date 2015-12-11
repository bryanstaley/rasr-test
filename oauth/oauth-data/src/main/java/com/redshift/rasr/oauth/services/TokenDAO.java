package com.redshift.rasr.oauth.services;

import java.util.Date;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.redshift.rasr.oauth.data.Client;
import com.redshift.rasr.oauth.data.Tokens;
import com.redshift.rasr.oauth.utils.Utilities;

public class TokenDAO implements TokenService {
	private Logger logger = Logger.getLogger(TokenDAO.class.getName());

	public Tokens getToken(String id) throws Exception {
		Session s = null;
		try {
			s = Utilities.getSession();
			s.beginTransaction();
			String selectHql = "FROM Tokens where token = :token";
			Query query = s.createQuery(selectHql);
			query.setString("token", id);
			Tokens retToken = (Tokens) query.uniqueResult();

			logger.info(String.format("Retrieved token %s for id %s", retToken, id));
			return retToken;

		} catch (Exception e) {
			logger.info(String.format("Unable to retrieve token %s due to %s ", id, e));
			throw e;
		} finally {
			if (s != null && s.isOpen())
				s.close();

		}
	}

	public void addToken(Client client, String token, Date expiration, String authServer) throws Exception {
		Session s = null;
		try {
			s = Utilities.getSession();
			Transaction currentTransaction = s.beginTransaction();
			Tokens newToken = new Tokens(client, token, expiration, authServer);
			s.save(newToken);

			currentTransaction.commit();
		} catch (Exception e) {
			logger.severe(String.format("Unable to add token %s, client %s, date %s auth server %s due to %s", token,
					client, expiration, authServer));
			throw e;
		} finally {
			if (s != null && s.isOpen())
				s.close();

		}

	}

}
