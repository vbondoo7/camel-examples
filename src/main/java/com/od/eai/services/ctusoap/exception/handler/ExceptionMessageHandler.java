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
import com.officedepot.eai.service.translationutility.BulkCCCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkCCCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.BulkODCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkODCOATranslationNewToOldLookupResponseType;
import com.officedepot.eai.service.translationutility.BulkODCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkODCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.BulkTranslationLookupResponseType;
import com.officedepot.eai.service.translationutility.BulkTranslationUpsertResponseType;
import com.officedepot.eai.service.translationutility.CCCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationNewToOldLookupResponseType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.CcCoaOldToNewCriteriaType;
import com.officedepot.eai.service.translationutility.CcCoaOldToNewResultDocType;
import com.officedepot.eai.service.translationutility.CcCoaOldToNewResultType;
import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupResponseType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.OdCoaNewToOldCriteriaType;
import com.officedepot.eai.service.translationutility.OdCoaNewToOldResultDocType;
import com.officedepot.eai.service.translationutility.OdCoaNewToOldResultType;
import com.officedepot.eai.service.translationutility.OdCoaOldToNewCriteriaType;
import com.officedepot.eai.service.translationutility.OdCoaOldToNewResultDocType;
import com.officedepot.eai.service.translationutility.OdCoaOldToNewResultType;
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
				statusType.setStatusDescription(exception.getMessage()==null?exception.toString():exception.getMessage());
				result.setStatus(statusType);
				translationLookupResponseType.setResult(result);
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(translationLookupResponseType));
			}
			else if(operationName.trim().equalsIgnoreCase("bulkTranslationLookUpRequest")) {
				BulkTranslationLookupResponseType bulkTranslationLookupResponseType = new BulkTranslationLookupResponseType();
				StatusType statusType = new StatusType();
				ResultType result = new ResultType();
				statusType.setStatus(ERROR);
				statusType.setStatusCode(1);
				statusType.setStatusDescription(exception.getMessage()==null?exception.toString():exception.getMessage());
				result.setStatus(statusType);
				bulkTranslationLookupResponseType.getResults().add(result);
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bulkTranslationLookupResponseType)); 
			}
			else if(operationName.trim().equalsIgnoreCase("translationUpsertReq")) {
				TranslationUpsertResponseType translationUpsertResponse = new TranslationUpsertResponseType();
				translationUpsertResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(translationUpsertResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("bulkTranslationUpsertReq")) {
				BulkTranslationUpsertResponseType bulkTranslationUpsertResponse = new BulkTranslationUpsertResponseType();
				bulkTranslationUpsertResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bulkTranslationUpsertResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("translationDeleteReq")) {
				TranslationDeleteResponseType translationDeleteResponse = new TranslationDeleteResponseType();
				translationDeleteResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(translationDeleteResponse));
			}
			/*else { //if(operationName.trim().equalsIgnoreCase("CCCOATranslationOldToNewLookup")) {
				exchange.getOut().setFault(true);
				exchange.getOut().setBody(FaultUtil.createServerFault(ERROR, exception.getMessage()));
			}*/
			else if(operationName.trim().equalsIgnoreCase("oDCOATranslationOldToNewLookupReq")) {
				ODCOATranslationOldToNewLookupRequestType request = (ODCOATranslationOldToNewLookupRequestType) exchange.getProperty("originalPayload");
				ODCOATranslationOldToNewLookupResponseType oDCOATranslationOldToNewLookupResponse = new ODCOATranslationOldToNewLookupResponseType();
				oDCOATranslationOldToNewLookupResponse.setHeader(request.getHeader());
				oDCOATranslationOldToNewLookupResponse.setAccount("");
				oDCOATranslationOldToNewLookupResponse.setCostCenter("");
				oDCOATranslationOldToNewLookupResponse.setEntity("");
				oDCOATranslationOldToNewLookupResponse.setInterCompany("");
				oDCOATranslationOldToNewLookupResponse.setLob("");
				oDCOATranslationOldToNewLookupResponse.setLocation("");
				oDCOATranslationOldToNewLookupResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(oDCOATranslationOldToNewLookupResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("bulkODCOATranslationOldToNewLookupReq")) {
				BulkODCOATranslationOldToNewLookupRequestType request = (BulkODCOATranslationOldToNewLookupRequestType) exchange.getProperty("originalPayload");
				BulkODCOATranslationOldToNewLookupResponseType bulkODCOATranslationOldToNewLookupResponse = new BulkODCOATranslationOldToNewLookupResponseType();
				bulkODCOATranslationOldToNewLookupResponse.setHeader(request.getHeader());
				for(OdCoaOldToNewCriteriaType criteria : request.getCriteria()) {
					OdCoaOldToNewResultType odCoaOldToNewResultType = new OdCoaOldToNewResultType();
					odCoaOldToNewResultType.setCriteria(criteria);
					
					OdCoaOldToNewResultDocType resultDoc = new OdCoaOldToNewResultDocType();
					resultDoc.setAccount("");
					resultDoc.setCostCenter("");
					resultDoc.setEntity("");
					resultDoc.setInterCompany("");
					resultDoc.setLob("");
					resultDoc.setLocation("");
					
					odCoaOldToNewResultType.setResult(resultDoc);
					odCoaOldToNewResultType.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
					bulkODCOATranslationOldToNewLookupResponse.getResults().add(odCoaOldToNewResultType);
				}
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bulkODCOATranslationOldToNewLookupResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("oDCOATranslationNewToOldLookupReq")) {
				ODCOATranslationNewToOldLookupRequestType request = (ODCOATranslationNewToOldLookupRequestType) exchange.getProperty("originalPayload");
				ODCOATranslationNewToOldLookupResponseType oDCOATranslationNewToOldLookupResponse = new ODCOATranslationNewToOldLookupResponseType();
				oDCOATranslationNewToOldLookupResponse.setHeader(request.getHeader());
				oDCOATranslationNewToOldLookupResponse.setAccount("");
				oDCOATranslationNewToOldLookupResponse.setCostCenter("");
				oDCOATranslationNewToOldLookupResponse.setCompany("");
				oDCOATranslationNewToOldLookupResponse.setInterCompany("");
				oDCOATranslationNewToOldLookupResponse.setLob("");
				oDCOATranslationNewToOldLookupResponse.setLocation("");
				oDCOATranslationNewToOldLookupResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(oDCOATranslationNewToOldLookupResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("bulkODCOATranslationNewToOldLookupReq")) {
				BulkODCOATranslationNewToOldLookupRequestType request = (BulkODCOATranslationNewToOldLookupRequestType) exchange.getProperty("originalPayload");
				BulkODCOATranslationNewToOldLookupResponseType bulkODCOATranslationNewToOldLookupResponse = new BulkODCOATranslationNewToOldLookupResponseType();
				bulkODCOATranslationNewToOldLookupResponse.setHeader(request.getHeader());
				for(OdCoaNewToOldCriteriaType criteria : request.getCriteria()) {
					OdCoaNewToOldResultType odCoaNewToOldResultType = new OdCoaNewToOldResultType();
					odCoaNewToOldResultType.setCriteria(criteria);
					
					OdCoaNewToOldResultDocType resultDoc = new OdCoaNewToOldResultDocType();
					resultDoc.setAccount("");
					resultDoc.setCostCenter("");
					resultDoc.setCompany("");
					resultDoc.setInterCompany("");
					resultDoc.setLob("");
					resultDoc.setLocation("");
					resultDoc.setFuture("");
					
					odCoaNewToOldResultType.setResult(resultDoc);
					odCoaNewToOldResultType.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
					bulkODCOATranslationNewToOldLookupResponse.getResults().add(odCoaNewToOldResultType);
				}
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bulkODCOATranslationNewToOldLookupResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("cCCOATranslationOldToNewLookupReq")) {
				CCCOATranslationOldToNewLookupRequestType request = (CCCOATranslationOldToNewLookupRequestType) exchange.getProperty("originalPayload");
				CCCOATranslationOldToNewLookupResponseType cCCOATranslationOldToNewLookupResponse = new CCCOATranslationOldToNewLookupResponseType();
				cCCOATranslationOldToNewLookupResponse.setHeader(request.getHeader());
				cCCOATranslationOldToNewLookupResponse.setAccount("");
				cCCOATranslationOldToNewLookupResponse.setCostCenter("");
				cCCOATranslationOldToNewLookupResponse.setEntity("");
				cCCOATranslationOldToNewLookupResponse.setInterCompany("");
				cCCOATranslationOldToNewLookupResponse.setLob("");
				cCCOATranslationOldToNewLookupResponse.setLocation("");
				cCCOATranslationOldToNewLookupResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cCCOATranslationOldToNewLookupResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("bulkCCCOATranslationOldToNewLookupReq")) {
				BulkCCCOATranslationOldToNewLookupRequestType request = (BulkCCCOATranslationOldToNewLookupRequestType) exchange.getProperty("originalPayload");
				BulkCCCOATranslationOldToNewLookupResponseType bulkCCCOATranslationOldToNewLookupResponse = new BulkCCCOATranslationOldToNewLookupResponseType();
				bulkCCCOATranslationOldToNewLookupResponse.setHeader(request.getHeader());
				for(CcCoaOldToNewCriteriaType criteria : request.getCriteria()) {
					CcCoaOldToNewResultType ccCoaOldToNewResultType = new CcCoaOldToNewResultType();
					ccCoaOldToNewResultType.setCriteria(criteria);
					
					CcCoaOldToNewResultDocType resultDoc = new CcCoaOldToNewResultDocType();
					resultDoc.setAccount("");
					resultDoc.setCostCenter("");
					resultDoc.setEntity("");
					resultDoc.setInterCompany("");
					resultDoc.setLob("");
					resultDoc.setLocation("");
					
					ccCoaOldToNewResultType.setResult(resultDoc);
					ccCoaOldToNewResultType.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
					bulkCCCOATranslationOldToNewLookupResponse.getResults().add(ccCoaOldToNewResultType);
				}
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bulkCCCOATranslationOldToNewLookupResponse));
			}
			else if(operationName.trim().equalsIgnoreCase("cCCOATranslationNewToOldLookupReq")) {
				CCCOATranslationNewToOldLookupRequestType request = (CCCOATranslationNewToOldLookupRequestType) exchange.getProperty("originalPayload");
				CCCOATranslationNewToOldLookupResponseType cCCOATranslationNewToOldLookupResponse = new CCCOATranslationNewToOldLookupResponseType();
				cCCOATranslationNewToOldLookupResponse.setHeader(request.getHeader());
				cCCOATranslationNewToOldLookupResponse.setAccount("");
				cCCOATranslationNewToOldLookupResponse.setCostCenter("");
				cCCOATranslationNewToOldLookupResponse.setEntity("");
				cCCOATranslationNewToOldLookupResponse.setInterCompany("");
				cCCOATranslationNewToOldLookupResponse.setLob("");
				cCCOATranslationNewToOldLookupResponse.setLocation("");
				cCCOATranslationNewToOldLookupResponse.setStatus(getStatusTypeWithFailure(exception.getMessage()==null?exception.toString():exception.getMessage()));
				exchange.getOut().setBody(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cCCOATranslationNewToOldLookupResponse));
			}
			/*
			 * exchange.getIn().setFault(false); exchange.getOut().setFault(false);
			 */
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
			//exchange.removeProperty(Exchange.EXCEPTION_CAUGHT);
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
