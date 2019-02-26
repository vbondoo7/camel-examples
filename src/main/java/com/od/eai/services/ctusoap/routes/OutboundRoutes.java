package com.od.eai.services.ctusoap.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseOutboundRouteBuilder;
import com.od.eai.framework.core.dispatch.Configurator;
import com.od.eai.services.ctusoap.util.HeaderUtil;
import com.officedepot.eai.service.translationutility.TranslationLookupResponseType;

@Component("ctusoapOutboundRoutes")
public class OutboundRoutes extends BaseOutboundRouteBuilder {

	public static final String DIRECT_SET_AUTHORIZATION_HEADER = "direct:setAuthorizationHeader";
	public static final Logger log = LoggerFactory.getLogger(OutboundRoutes.class);
	public static final String DIRECT_CTU_LOOKUP_SERVICE = "direct:ctuLookupService";
	
	@Value("${ctu.lookup.service.request.route}")
	private String ctuLookupServiceRoute;
	
	@Value("${ctu.lookup.service.url}")
	private String ctuLookupServiceURL;
	
	@Autowired
	private String ctuLookUpServiceUsername;
	
	@Autowired
	private String ctuLookUpServicePassword;

	@Override
	public void configureRoutes() throws Exception {
		from(DIRECT_CTU_LOOKUP_SERVICE)
			.id(Configurator.getStepId(ctuLookupServiceRoute))
			.log(LoggingLevel.INFO, "Calling CTU LookUp Service....")
			.to(DIRECT_SET_AUTHORIZATION_HEADER)
			.log(LoggingLevel.INFO,"Request Body for CTU Lookup service : ${body}")
			.to(ctuLookupServiceURL)
			.unmarshal().json(JsonLibrary.Jackson, TranslationLookupResponseType.class);

		// setAuthorizationHeader
		from(DIRECT_SET_AUTHORIZATION_HEADER)
			.routeId("setAuthorizationHeader")
			.log(LoggingLevel.INFO, "Setting authorization header for CTU Lookup service..")
			.removeHeaders("*")
			.setHeader("Authorization",
						constant(HeaderUtil.buildAuthorizationHeader(ctuLookUpServiceUsername, ctuLookUpServicePassword)));
	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("exceptionOutboundRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.handled(true);

	}

}
