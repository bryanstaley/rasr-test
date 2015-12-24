package com.redshift.rasr.oauth.services;

import java.util.Date;

import com.redshift.rasr.oauth.data.Client;
import com.redshift.rasr.oauth.data.Token;

public interface TokenService {
	
	Token getToken(String id) throws Exception;
	
	void addToken(Client client,String token,Date expiration,String authServer)throws Exception;

}
