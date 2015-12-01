package com.redshift.rasr.oauth.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Utilities {

	public static SessionFactory factory;

	static {
		factory = new Configuration().configure().buildSessionFactory();
	}

	public static Session getSession() {

		return factory.getCurrentSession();

	}

}