package com.example.integration.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.integration.exception.handler.ExceptionMessageHandler;

/**
 * @author saxena.vishal1986
 *
 */
@Component("outboundRoutes")
public class OutboundRoutes extends EndpointRouteBuilder {

	public static final String DIRECT_SET_AUTHORIZATION_HEADER = "direct:setAuthorizationHeader";
	public static final String HYSTRIX_ENABLED_EXTERNAL_ROUTE = "direct:hystrixEnabledInternalRoute";
	private static final String HYSTRIX_ENABLED_EXTERNAL_ROUTE_ID = "HYSTRIX_ENABLED_EXTERNAL_ROUTE";
	private static final String HYSTRIX_ENABLED_BACKEND_SERVICES_FALLBACK_ROUTE = "hystrixEnabledBackendServicesFallbackRoute";
	private static final String DIRECT_HANDLE_FALLBACK_ROUTE = "direct:handleFallbackRoute";

	@Value("${hystrix.execution-timeout-in-milliseconds}")
	private int executionTimeoutInMilliseconds;

	@Value("${calc.thirdparty.service.url}")
	private String thirdPartyCalcServiceURL;
	
	@Value("${calc.thirdparty.service.wsdl}")
	private String thirdPartyCalcServiceWsdlURL;

	@Override
	public void configure() throws Exception {
		
		//errorHandler(deadLetterChannel("mock:dead").maximumRedeliveries(3).redeliveryDelay(2));

		configureExceptions();
		
		from(HYSTRIX_ENABLED_EXTERNAL_ROUTE)
			.routeId(HYSTRIX_ENABLED_EXTERNAL_ROUTE_ID).streamCaching()
			//.errorHandler(noErrorHandler())
			.log(LoggingLevel.INFO, "Request received for External URL for ${header.operationName} --> ${body}")
			//.to(DIRECT_SET_AUTHORIZATION_HEADER)
			.setHeader(CxfConstants.OPERATION_NAME, header(CxfConstants.OPERATION_NAME))
			.setHeader(CxfConstants.OPERATION_NAMESPACE, constant("http://tempuri.org/"))
			.circuitBreaker()
				.resilience4jConfiguration()
					.timeoutEnabled(true).timeoutDuration(executionTimeoutInMilliseconds)
					.end()
				.to(cxf(thirdPartyCalcServiceURL)
					.publishedEndpointUrl(thirdPartyCalcServiceURL)
					.serviceClass((Class<Object>) Class.forName("org.tempuri.CalculatorSoap"))
					.wsdlURL(thirdPartyCalcServiceWsdlURL))
				.onFallback()
					.to(DIRECT_HANDLE_FALLBACK_ROUTE)
				.end()
				.throwException(new ArithmeticException("new error"))
			.log(LoggingLevel.INFO, "Response Received from External URL for ${header.operationName} : ${body}")
			.log(LoggingLevel.INFO, "Calling finished for External URL for ${header.operationName}");
		
	}

	private void configureExceptions() {
		
		onException(ArithmeticException.class)
			.routeId("exception-route")
			.maximumRedeliveries(3).redeliveryDelay(2000)
			.logRetryAttempted(true).retryAttemptedLogLevel(LoggingLevel.ERROR).logStackTrace(true)
			.log(LoggingLevel.ERROR,"received error ${exception.stacktrace}")
			.to(DIRECT_HANDLE_FALLBACK_ROUTE)
			.handled(true);
			
		from(DIRECT_HANDLE_FALLBACK_ROUTE)
			.routeId(HYSTRIX_ENABLED_BACKEND_SERVICES_FALLBACK_ROUTE)
			.to(bean("exceptionMessageHandler"));
	}
}
