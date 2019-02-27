package com.od.eai.services.ctusoap.routes;

import org.apache.camel.LoggingLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseOutboundRouteBuilder;
import com.od.eai.framework.core.dispatch.Configurator;
import com.od.eai.services.ctusoap.exception.handler.ExceptionMessageHandler;
import com.od.eai.services.ctusoap.util.HeaderUtil;

@Component("ctusoapOutboundRoutes")
public class OutboundRoutes extends BaseOutboundRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(OutboundRoutes.class);
	public static final String DIRECT_SET_AUTHORIZATION_HEADER = "direct:setAuthorizationHeader";
	public static final String HYSTRIX_ENABLED_CTU_LOOKUP_ENDPOINT = "direct:hystrixEnabledCTULookupEndpoint";
	private static final String HYSTRIX_ENABLED_CTU_LOOKUP_ENDPOINT_ID = "HYSTRIX_ENABLED_ELASTICSEARCH_ENDPOINT_ROUTE";
	private static final String HYSTRIX_ENABLED_BACKEND_SERVICES_FALLBACK_ROUTE = "hystrixEnabledBackendServicesFallbackRoute";
	private static final String DIRECT_HANDLE_FALLBACK_ROUTE = "direct:handleFallbackRoute";

	@Value("${hystrix.execution-timeout-in-milliseconds}")
	private int executionTimeoutInMilliseconds;

	@Value("${hystrix.circuit-breaker-sleep-window-in-milliseconds}")
	private int circuitBreakerSleepWindowInMilliseconds;

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

		from(HYSTRIX_ENABLED_CTU_LOOKUP_ENDPOINT)
				.routeId(Configurator.getStepId(HYSTRIX_ENABLED_CTU_LOOKUP_ENDPOINT_ID)).streamCaching()
				.log(LoggingLevel.INFO, "Calling started for CTU Lookup Endpoint...")
				.id("eaiaudit_HYSTRIX_ENABLED_CTU_LOOKUP_ENDPOINT_payload").hystrix().hystrixConfiguration()
				.executionTimeoutInMilliseconds(executionTimeoutInMilliseconds)
				.circuitBreakerSleepWindowInMilliseconds(circuitBreakerSleepWindowInMilliseconds).end()
				.log(LoggingLevel.INFO, "Request received for CTU Lookup Service --> ${body}")
				.to(DIRECT_SET_AUTHORIZATION_HEADER).to(ctuLookupServiceURL).onFallback()
				.to(DIRECT_HANDLE_FALLBACK_ROUTE)
				.log(LoggingLevel.INFO, "Response Received from CTU Lookup Service : ${body}").end()
				.log(LoggingLevel.INFO, "Calling finished for Exact Target");

		// setAuthorizationHeader
		from(DIRECT_SET_AUTHORIZATION_HEADER).routeId("setAuthorizationHeader")
				.log(LoggingLevel.INFO, "Setting authorization header for CTU Lookup service..").removeHeaders("*")
				.setHeader("Authorization", constant(
						HeaderUtil.buildAuthorizationHeader(ctuLookUpServiceUsername, ctuLookUpServicePassword)));
	}

	@Override
	protected void configureExceptions() {
		from(DIRECT_HANDLE_FALLBACK_ROUTE).id(Configurator.getStepId(HYSTRIX_ENABLED_BACKEND_SERVICES_FALLBACK_ROUTE))
				.routeId(HYSTRIX_ENABLED_BACKEND_SERVICES_FALLBACK_ROUTE)
				.bean(ExceptionMessageHandler.class, "handleFallback");

	}

}
