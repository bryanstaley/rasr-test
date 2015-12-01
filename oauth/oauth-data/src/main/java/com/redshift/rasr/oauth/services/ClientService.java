package com.redshift.rasr.oauth.services;

import com.redshift.rasr.oauth.data.Client;

public interface ClientService {

	void addClient(String id,String secret) throws Exception;
	
	Client getClient(String id) throws Exception;
	
	Client getClient(String id,String secret) throws Exception;
}
