package com.od.eai.services.ctusoap.exception.handler;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExceptionMessageHandler {
	
	private static final Logger LOGGER 	= LoggerFactory.getLogger(ExceptionMessageHandler.class);
	
	public void handleFallback(Exchange exchange) throws Exception {
		if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null) {
			Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("An Exception has been thrown by Exact Target service: {}", exception.getMessage()==null?exception.toString():exception.getMessage());
			
			
		}
	}

}
