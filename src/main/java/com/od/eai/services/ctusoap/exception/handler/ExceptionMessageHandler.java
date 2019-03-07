package com.od.eai.services.ctusoap.exception.handler;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.officedepot.eai.service.translationutility.ResultType;
import com.officedepot.eai.service.translationutility.StatusType;
import com.officedepot.eai.service.translationutility.TranslationLookupResponseType;

@Component
public class ExceptionMessageHandler {
	
	private static final Logger LOGGER 	= LoggerFactory.getLogger(ExceptionMessageHandler.class);
	
	public void handleFallback(Exchange exchange) throws Exception {
		if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null) {
			Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("An Exception has been thrown by CTU Soap service: {}", exception.getMessage()==null?exception.toString():exception.getMessage());
			TranslationLookupResponseType translationLookupResponseType = new TranslationLookupResponseType();
			StatusType statusType = new StatusType();
			ResultType result = new ResultType();
			statusType.setStatus("error");
			statusType.setStatusCode(1);
			statusType.setStatusDescription("connection error with CTU Lookup Services");
			result.setStatus(statusType);
			translationLookupResponseType.setResult(result);
		}
	}
	
	public void handle(Exchange exchange) throws Exception {
		if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null) {
			Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("An Exception has been thrown by CTU Soap service: {}", exception.getMessage()==null?exception.toString():exception.getMessage());
			TranslationLookupResponseType translationLookupResponseType = new TranslationLookupResponseType();
			StatusType statusType = new StatusType();
			ResultType result = new ResultType();
			statusType.setStatus("error");
			statusType.setStatusCode(1);
			statusType.setStatusDescription(exception.getMessage());
			result.setStatus(statusType);
			translationLookupResponseType.setResult(result);
		}
	}

}
