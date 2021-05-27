package com.example.integration.routes;

import java.net.SocketException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.integration.aggregators.JMSRollbackAggregationStrategy;

/**
 * @author saxena.vishal1986
 *
 */
@Component("jmsOutboundRoutes")
public class JmsToSOAPOutboundRoutes extends RouteBuilder {

	public static final String DIRECT_MULTICAST_OUTBOUND_ROUTE = "direct:multicastOutboundRoute";

	@Override
	public void configure() throws Exception {
		
		//configureExceptions();
		
		from(DIRECT_MULTICAST_OUTBOUND_ROUTE)
			.routeId("multicast-outbound-route")
			.errorHandler(noErrorHandler())
			.log(LoggingLevel.INFO, "External call for single Order : ${body}")
			//.doTry()
				//.log(LoggingLevel.ERROR, "inside doTry()")
				.multicast(new JMSRollbackAggregationStrategy())
					.parallelProcessing()
					.to("direct:soapRoute-A", "direct:soapRoute-B")
				.end()
			//.doCatch(Exception.class)
				//.log(LoggingLevel.ERROR, "Order processed in soapRoute-A")
			//.endDoTry()
			.choice()
				.when(exchangeProperty("exception").isNull())
					.to("direct:final-route")
				.otherwise()
					.throwException(new Exception("Order processing failed. Rollback please."))
			.end();
		
		from("direct:soapRoute-A")
			.routeId("soapRoute-A")
			.errorHandler(noErrorHandler())
			.log(LoggingLevel.INFO, "Order processed in soapRoute-A");
		
		from("direct:soapRoute-B")
			.routeId("soapRoute-B")
			.errorHandler(noErrorHandler())
			.choice()
				.when(xpath("//id = '3'"))
					.throwException(new SocketException("Connectivity Failed"))
				.otherwise()
					.log(LoggingLevel.INFO, "Order processed in soapRoute-B")
			.end();
		
		from("direct:final-route")
			.routeId("final-route")
			//.to(dummy-final-endpoint)
			.log(LoggingLevel.INFO, "Orders processed successfully: ${body}");
				
	}

	private void configureExceptions() {
		
		onException(ArithmeticException.class)
			.routeId("jms-outbound-exception-route")
			.maximumRedeliveries(3).redeliveryDelay(2000)
			.logRetryAttempted(true).retryAttemptedLogLevel(LoggingLevel.ERROR).logStackTrace(true)
			.log(LoggingLevel.ERROR, "Exception occurred in JmsOutboundRoute : ${exception}");
			
	}
}
