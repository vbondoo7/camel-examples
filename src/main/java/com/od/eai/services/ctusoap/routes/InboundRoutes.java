package com.od.eai.services.ctusoap.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.model.rest.RestBindingMode;
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
	protected void preConfigure() {
		restConfiguration().enableCORS(true).corsHeaderProperty("Access-Control-Allow-Origin", "*").corsHeaderProperty(
				"Access-Control-Allow-Headers",
				"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization")
				.component("servlet").bindingMode(RestBindingMode.off)
				.dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES")
				.dataFormatProperty("json.in.enableFeatures", "FAIL_ON_NUMBERS_FOR_ENUMS,USE_BIG_DECIMAL_FOR_FLOATS");
	}
	
	@Override
	public void configureRoutes() throws Exception {
		restConfiguration().component("servlet");
		
		from(translationUtilityLookupcxfURL).id(Configurator.getStepId(traslationUtilityRequest))
		  .log("******* Received Translation Utility Lookup Request ********")
		  .to(ProcessingRoutes.DIRECT_TRANSLATION_UTILITY_PROCESSING_ROUTE);
		
	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("exceptionInboundRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.handled(true);

	}

}
