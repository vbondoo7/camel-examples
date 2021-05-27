package com.example.integration.util;

import org.springframework.util.Base64Utils;

/**
 * @author saxena.vishal1986
 *
 */
public class HeaderUtil {
	
	public static String buildAuthorizationHeader(String username, String password) {
		String concatenated = username + ":" + password;
		return "Basic " + new String(Base64Utils.encode(concatenated.getBytes()));
	}
	
}
