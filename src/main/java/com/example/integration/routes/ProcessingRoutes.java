package com.example.integration.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.integration.exception.handler.ExceptionMessageHandler;

/**
 * @author saxena.vishal1986
 *
 */
@Component("processingRoutes")
public class ProcessingRoutes extends EndpointRouteBuilder {

	private static final String INTERNAL_URL 								= "InternalURL";
	public static final String DIRECT_ADD_REQUEST_PROCESSING_ROUTE	 		= "direct:addRequestProcessingRoute";
	public static final String DIRECT_SUBTRACT_REQUEST_PROCESSING_ROUTE		= "direct:subtractRequestProcessingRoute";
	public static final String DIRECT_MULTIPLY_REQUEST_PROCESSING_ROUTE 	= "direct:multiplyRequestProcessingRoute";
	public static final String DIRECT_DIVIDE_REQUEST_PROCESSING_ROUTE 		= "direct:divideRequestProcessingRoute";
	public static final String DIRECT_UNSUPPORTED_OPERATION 				= "direct:unsupportedOperation";
	
	@Value("${calc.thirdparty.service.url}")
	private String thirdPartyCalcServiceURL;

	@Override
	public void configure() throws Exception {
		
		configureExceptions();
		
		from(DIRECT_ADD_REQUEST_PROCESSING_ROUTE)
			.routeId("add-processing-route").routeDescription("This receives Add request processing.")
			.log(LoggingLevel.INFO, "Processing Started for Add operation...")
			//.to(bean("requestAssembler"))
			.to(OutboundRoutes.HYSTRIX_ENABLED_EXTERNAL_ROUTE)
			.log(LoggingLevel.INFO, "Processing For CalcUtility Finished !!!");
		
	}

	private void configureExceptions() {
		onException(Exception.class)
			.id("exceptionProcessingRoute")
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.bean(ExceptionMessageHandler.class);
	}

}
