package com.od.eai.services.ctusoap.util;

import org.springframework.util.Base64Utils;

public class HeaderUtil {
	
	public static String buildAuthorizationHeader(String username, String password) {
		String concatenated = username + ":" + password;
		return "Basic " + new String(Base64Utils.encode(concatenated.getBytes()));
	}
	
}
