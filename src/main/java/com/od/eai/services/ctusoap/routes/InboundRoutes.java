package com.od.eai.services.ctusoap.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseInboundRouteBuilder;

@Component("ctusoapInboundRoutes")
public class InboundRoutes extends BaseInboundRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(InboundRoutes.class);
	
	@Override
	protected void preConfigure() {}
	
	@Override
	public void configureRoutes() throws Exception {
		rest("/").get("/sayhello").consumes("application/json").produces("application/json")
		.to("direct:sayHelloResponse") ;
		
	}

	@Override
	protected void configureExceptions() {
		// TODO Auto-generated method stub
		

	}

}
