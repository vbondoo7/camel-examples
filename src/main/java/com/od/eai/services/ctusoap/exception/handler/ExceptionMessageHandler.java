package com.od.eai.services.ctusoap.exception.handler;

import java.util.Objects;

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.od.eai.services.ctusoap.util.FaultUtil;
import com.officedepot.eai.service.translationutility.BulkTranslationLookupResponseType;
import com.officedepot.eai.service.translationutility.BulkTranslationUpsertResponseType;
import com.officedepot.eai.service.translationutility.ResultType;
import com.officedepot.eai.service.translationutility.StatusType;
import com.officedepot.eai.service.translationutility.TranslationDeleteResponseType;
import com.officedepot.eai.service.translationutility.TranslationLookupResponseType;
import com.officedepot.eai.service.translationutility.TranslationUpsertResponseType;

@Component
public class ExceptionMessageHandler {
	
	private static final Logger LOGGER 	= LoggerFactory.getLogger(ExceptionMessageHandler.class);
	private static final String ERROR = "ERROR";
	
	@Autowired
	private ObjectMapper objectMapper;
	
	public void handleFallback(Exchange exchange) throws Exception {
		if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null
				|| exchange.getIn().getBody(String.class).contains("\"ERROR\"")) {
			Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			String operationName = (String) exchange.getProperty(CxfConstants.OPERATION_NAME);
			operationName = (null == operationName) ? exchange.getIn().getHeader(CxfConstants.OPERATION_NAME, String.class) : operationName;
			if(Objects.nonNull(exception))
				LOGGER.error("An Exception has been thrown by CTU Soap service for operation {} : {}", operationName, exception.getMessage()==null?exception.toString():exception.getMessage());
			else {
				JSONObject responseObj = new JSONObject(exchange.getIn().getBody(String.class));
				String statusDescription = (String) responseObj.get("statusDescription");
				LOGGER.error("An Error response has been received from ctu-lookup-service for operation {} : {}", operationName, statusDescription);
				exchange.getOut().setFault(true);
				exchange.getOut().setBody(FaultUtil.createServerFault(ERROR, statusDescription));
				return;
			}
			if(operationName.trim().equalsIgnoreCase("translationLookUpRequest")) {
				TranslationLookupResponseType translationLookupResponseType = new TranslationLookupResponseType();
				StatusType statusType = new StatusType();
				ResultType result = new ResultType();
				statusType.setStatus(ERROR);
				statusType.setStatusCode(1);
				statusType.setStatusDescription(exception.getMessage());
				result.setStatus(statusType);
				translationLookupResponseType.setResult(result);
				exchange.getOut().setBody(translationLookupResponseType);
			}
			else if(operationName.trim().equalsIgnoreCase("bulkTranslationLookUpRequest")) {
				BulkTranslationLookupResponseType bulkTranslationLookupResponseType = new BulkTranslationLookupResponseType();
				StatusType statusType = new StatusType();
				ResultType result = new ResultType();
				statusType.setStatus(ERROR);
				statusType.setStatusCode(1);
				statusType.setStatusDescription(exception.getMessage());
				result.setStatus(statusType);
				bulkTranslationLookupResponseType.getResults().add(result);
				exchange.getOut().setBody(bulkTranslationLookupResponseType); 
			}
			else if(operationName.trim().equalsIgnoreCase("translationUpsertRequest")) {
				TranslationUpsertResponseType translationUpsertResponse = new TranslationUpsertResponseType();
				translationUpsertResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(translationUpsertResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("bulkTranslationUpsertRequest")) {
				BulkTranslationUpsertResponseType bulkTranslationUpsertResponse = new BulkTranslationUpsertResponseType();
				bulkTranslationUpsertResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bulkTranslationUpsertResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("translationDeleteRequest")) {
				TranslationDeleteResponseType translationDeleteResponse = new TranslationDeleteResponseType();
				translationDeleteResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(translationDeleteResponse));
			}
			else { //if(operationName.trim().equalsIgnoreCase("CCCOATranslationOldToNewLookup")) {
				exchange.getOut().setFault(true);
				exchange.getOut().setBody(FaultUtil.createServerFault(ERROR, exception.getMessage()));
			}
			
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
		}
	}
	
	/*public void handle(Exchange exchange) throws Exception {
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
		if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null) {
			Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("An Exception has been thrown by CTU Soap service: {}", exception.getMessage()==null?exception.toString():exception.getMessage());
			exchange.getOut().setFault(true);
			exchange.getOut().setBody(FaultUtil.createServerFault("ERROR", exception.getMessage()));
		}
	}*/

	private StatusType getStatusTypeWithFailure(String errorMessage) {
		StatusType statusType = new StatusType();
		statusType.setStatus(ERROR);
		statusType.setStatusCode(1);
		statusType.setStatusDescription(errorMessage);
		return statusType;
	}
}
