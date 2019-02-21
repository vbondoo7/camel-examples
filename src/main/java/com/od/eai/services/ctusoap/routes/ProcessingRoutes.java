package com.od.eai.services.ctusoap.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseProcessingRouteBuilder;

@Component("ctusoapProcessingRoutes")
public class ProcessingRoutes extends BaseProcessingRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(ProcessingRoutes.class);
	
	@Override
	public void configureRoutes() throws Exception {
		String json = "{\"imsaying\": \"{{ctusoap.sample}}\"}";
		
		from("direct:sayHelloResponse").routeId("setHelloResponse")
		.log(LoggingLevel.INFO,"begining of rout")
		.setBody(simple(json))
		.log(LoggingLevel.INFO,"after set body")
		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant("200"))
		.log(LoggingLevel.INFO,"after set header response code")
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.log(LoggingLevel.INFO, ">>>>>>>>>>>>> body = ${body}");

	}

	@Override
	protected void configureExceptions() {
		// TODO Auto-generated method stub
		
	}

}
