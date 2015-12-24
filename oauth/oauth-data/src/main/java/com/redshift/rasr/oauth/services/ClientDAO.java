package com.redshift.rasr.oauth.services;

import java.security.MessageDigest;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.redshift.rasr.oauth.data.Client;
import com.redshift.rasr.oauth.utils.Utilities;

public class ClientDAO implements ClientService{
	private Logger logger = Logger.getLogger(ClientDAO.class.getName());

	public void addClient(String id, String secret) throws Exception {
		Session s = null;
		try {
			logger.info(String.format("Adding client %s", id));
			s = Utilities.getSession();
			byte[] data = MessageDigest.getInstance("SHA-1").digest(secret.getBytes("utf8"));
			String sha1Secret = DigestUtils.sha1Hex(data);
			Client c=new Client(id,sha1Secret);
			Transaction currentTransaction = s.beginTransaction();
			s.save(c);
			currentTransaction.commit();
		}
		catch(HibernateException he)
		{
			s = Utilities.getSessionFactory().openSession();
			byte[] data = MessageDigest.getInstance("SHA-1").digest(secret.getBytes("utf8"));
			String sha1Secret = DigestUtils.sha1Hex(data);
			Client c=new Client(id,sha1Secret);
			Transaction currentTransaction = s.beginTransaction();
			s.save(c);
			currentTransaction.commit();
		}catch (Exception e) {
			logger.warning(String.format("Unable to add client %s due to %s", id,e));
			throw e;
		} finally {
			if (s != null && s.isOpen())
				s.close();
		}
	}

	public Client getClient(String id) throws Exception{
		Session s = null;
		try {
			logger.info(String.format("Retrieving client %s", id));
			s = Utilities.getSession();
			s.beginTransaction();
			String selectHql = "FROM Client where client_id = :id";
			Query query = s.createQuery(selectHql);
			query.setString("id", id);
			return (Client) query.uniqueResult();

		} catch(HibernateException he)
		{
			s = Utilities.getSessionFactory().openSession();
			s.beginTransaction();
			String selectHql = "FROM Client where client_id = :id";
			Query query = s.createQuery(selectHql);
			query.setString("id", id);
			return (Client) query.uniqueResult();
		}
		catch (Exception e) {
			logger.warning(String.format("Unable to retieve client %s due to %s", id,e));
			throw e;
		} finally {
			if (s != null && s.isOpen())
				s.close();
		}
	}

	public Client getClient(String id, String secret) throws Exception {
		Session s = null;
		try {
			logger.info(String.format("Retrieving client %s with secret", id));
			s = Utilities.getSession();
			s.beginTransaction();
			String sha1Hash = DigestUtils
					.sha1Hex(MessageDigest.getInstance("SHA-1").digest(secret.getBytes("utf8")));
			String selectHql = "FROM Client where clientId = :id and clientSecret = :secret";
			Query query = s.createQuery(selectHql);
			query.setString("id", id);
			query.setString("secret", sha1Hash);
			return (Client) query.uniqueResult();

		} catch(HibernateException he)
		{
			s = Utilities.getSessionFactory().openSession();
			s.beginTransaction();
			String sha1Hash = DigestUtils
					.sha1Hex(MessageDigest.getInstance("SHA-1").digest(secret.getBytes("utf8")));
			String selectHql = "FROM Client where clientId = :id and clientSecret = :secret";
			Query query = s.createQuery(selectHql);
			query.setString("id", id);
			query.setString("secret", sha1Hash);
			return (Client) query.uniqueResult();
		}
		catch (Exception e) {
			logger.warning(String.format("Unable to retieve client %s due to %s", id,e));
			throw e;
		} finally {
			if (s != null && s.isOpen())
				s.close();
		}
	}

}
