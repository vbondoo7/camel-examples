package com.od.eai.services.ctusoap.routes;

import javax.ws.rs.core.HttpHeaders;

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
	public static final String HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE = "direct:hystrixEnabledCTUInternalRoute";
	private static final String HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE_ID = "HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE";
	private static final String HYSTRIX_ENABLED_BACKEND_SERVICES_FALLBACK_ROUTE = "hystrixEnabledBackendServicesFallbackRoute";
	private static final String DIRECT_HANDLE_FALLBACK_ROUTE = "direct:handleFallbackRoute";

	@Value("${hystrix.execution-timeout-in-milliseconds}")
	private int executionTimeoutInMilliseconds;

	@Value("${hystrix.circuit-breaker-sleep-window-in-milliseconds}")
	private int circuitBreakerSleepWindowInMilliseconds;

	@Autowired
	private String ctuLookUpServiceUsername;

	@Autowired
	private String ctuLookUpServicePassword;

	@Override
	public void configureRoutes() throws Exception {
		
		//TranslationLookupRequest
		from(HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.routeId(Configurator.getStepId(HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE_ID)).streamCaching()
			.log(LoggingLevel.INFO, "Calling started for CTU Internal URL for ${header.operationName}")
			.setProperty("operationName", header("operationName"))
			.log(LoggingLevel.INFO, "Request received for CTU Internal URL for ${header.operationName} --> ${body}")
			.to(DIRECT_SET_AUTHORIZATION_HEADER)
			.hystrix()
				.hystrixConfiguration()
					.executionTimeoutInMilliseconds(executionTimeoutInMilliseconds)
					.circuitBreakerSleepWindowInMilliseconds(circuitBreakerSleepWindowInMilliseconds)
				.end()
				//.to(ctuLookupTranslationReqServiceURL)
				.toD("${exchangeProperty.ctuInternalURL}")
				.onFallback()
					.to(DIRECT_HANDLE_FALLBACK_ROUTE)
				.end()
			.log(LoggingLevel.INFO, "Response Received from CTU Internal URL for ${exchangeProperty.operationName} : ${body}")
			.log(LoggingLevel.INFO, "Calling finished for CTU Internal URL for ${exchangeProperty.operationName}");
		
		// setAuthorizationHeader
		from(DIRECT_SET_AUTHORIZATION_HEADER).routeId("setAuthorizationHeader")
			.log(LoggingLevel.INFO, "Setting authorization header for CTU Internal service..").removeHeaders("*")
			.setHeader(HttpHeaders.AUTHORIZATION, constant(
					HeaderUtil.buildAuthorizationHeader(ctuLookUpServiceUsername, ctuLookUpServicePassword)));
	}

	@Override
	protected void configureExceptions() {
		from(DIRECT_HANDLE_FALLBACK_ROUTE).id(Configurator.getStepId(HYSTRIX_ENABLED_BACKEND_SERVICES_FALLBACK_ROUTE))
			.routeId(HYSTRIX_ENABLED_BACKEND_SERVICES_FALLBACK_ROUTE)
			.bean(ExceptionMessageHandler.class, "handleFallback");
	}
}
