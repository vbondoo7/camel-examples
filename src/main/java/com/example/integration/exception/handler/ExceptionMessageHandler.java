package com.example.integration.exception.handler;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.example.integration.util.FaultUtil;
import com.mydemo.calcutilityservice.Error;
import com.mydemo.calcutilityservice.ErrorMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author saxena.vishal1986
 *
 */
@Slf4j
@Component("exceptionMessageHandler")
public class ExceptionMessageHandler {

	@Handler
	public void handle(Exchange exchange) throws Exception {
		/*if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null) {
			Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			log.error("An Exception has been thrown : {}",
					exception.getMessage() == null ? exception.toString() : exception.getMessage());
			com.mydemo.calcutilityservice.Error faultInfo = new Error();
			faultInfo.setReason(exception.getMessage() == null ? exception.toString() : exception.getMessage());
			ErrorMessage errorMsg = new ErrorMessage(exception.getMessage() == null ? exception.toString() : exception.getMessage(), faultInfo);
		}*/
		if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null) {
			Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			log.error("An Exception has been thrown by Soap service: {}",
					exception.getMessage() == null ? exception.toString() : exception.getMessage());
			com.mydemo.calcutilityservice.Error faultInfo = new Error();
			faultInfo.setReason(exception.getMessage() == null ? exception.toString() : exception.getMessage());
			ErrorMessage err = new ErrorMessage(exception.getMessage() == null ? exception.toString() : exception.getMessage(), faultInfo);
			exchange.getMessage().setBody(err);
			//exchange.getMessage().setBody(FaultUtil.createServerFault("ERROR", exception.getMessage()));
		}
	}

}
