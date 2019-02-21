package com.od.eai.services.ctusoap.routes;

import org.apache.camel.component.cxf.CxfEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseInboundRouteBuilder;
import com.od.eai.framework.core.dispatch.Configurator;

@Component("ctusoapInboundRoutes")
public class InboundRoutes extends BaseInboundRouteBuilder {
	
	@Autowired
	private CxfEndpoint translationUtilityLookupcxfURL;
	
	@Value("${translation.utility.request.route}")
	private String traslationUtilityRequest;
	public static final Logger log = LoggerFactory.getLogger(InboundRoutes.class);
	
	@Override
	protected void preConfigure() {}
	
	@Override
	public void configureRoutes() throws Exception {
		restConfiguration().component("servlet");
		
		rest("/").get("/sayhello").consumes("application/json").produces("application/json")
		.to("direct:sayHelloResponse") ;
		
		from(translationUtilityLookupcxfURL).id(Configurator.getStepId(traslationUtilityRequest))
		  .log("******* Received Translation Utility Lookup Request ********")
		  .to(ProcessingRoutes.DIRECT_TRANSLATION_UTILITY_PROCESSING_ROUTE);
		
	}

	@Override
	protected void configureExceptions() {
		// TODO Auto-generated method stub
		

	}

}
