package com.example.integration.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.stereotype.Component;

/**
 * @author saxena.vishal1986
 *
 */
@Component("jmsProcessingRoutes")
public class JMSProcessingRoutes extends EndpointRouteBuilder {

	public static final String JMS_PROCESSING_ROUTE		= "direct:jmsProcessingRoute";
	
	@Override
	public void configure() throws Exception {
		
		//configureExceptions();
		
		from(JMS_PROCESSING_ROUTE)
			.routeId("jms-processing-route")
			.errorHandler(noErrorHandler())
			.split().tokenizeXML("order")//.streaming()
				.to(JmsToSOAPOutboundRoutes.DIRECT_MULTICAST_OUTBOUND_ROUTE)
			.end()
			.log(LoggingLevel.INFO, "Processing finished for Orders :: ${body}");
		
	}

	private void configureExceptions() {
		onException(Exception.class)
			.id("exceptionJmsProcessingRoute")
			.log(LoggingLevel.ERROR, "Exception occurred in JmsProcessingRoute : ${exception}");
	}

}
