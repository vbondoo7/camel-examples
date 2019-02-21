package com.od.eai.services.ctusoap.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseOutboundRouteBuilder;

@Component("ctusoapOutboundRoutes")
public class OutboundRoutes extends BaseOutboundRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(OutboundRoutes.class);
	
	@Override
	public void configureRoutes() throws Exception {
		

	}

	@Override
	protected void configureExceptions() {
		// TODO Auto-generated method stub
		
	}

}
