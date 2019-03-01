package com.od.eai.services.ctusoap.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseProcessingRouteBuilder;
import com.od.eai.framework.core.dispatch.Configurator;
import com.od.eai.services.ctusoap.exception.handler.ExceptionMessageHandler;
import com.od.eai.services.ctusoap.util.DataFormatUtil;
import com.officedepot.eai.service.translationutility.BulkTranslationLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkTranslationLookupResponseType;
import com.officedepot.eai.service.translationutility.TranslationLookupRequestType;
import com.officedepot.eai.service.translationutility.TranslationLookupResponseType;

@Component("ctusoapProcessingRoutes")
public class ProcessingRoutes extends BaseProcessingRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(ProcessingRoutes.class);
	public static final String DIRECT_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE = "direct:translationLookupRequestProcessingRoute";
	public static final String DIRECT_BULK_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE = "direct:bulkTranslationLookupRequestProcessingRoute";
	public static final String DIRECT_UNSUPPORTED_OPERATION = "direct:unsupportedOperation";
	
	@Value("${ctu.soap.translation.request.processing.route}")
	private String ctuSoapProcessingRouteForLookup;
	
	@Value("${ctu.soap.bulk.translation.request.processing.route}")
	private String ctuSoapProcessingRouteForBulkLookup;
	
	@Override
	public void configureRoutes() throws Exception {
		//TranslationLookupRequest
		from(DIRECT_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE)
			.id(Configurator.getStepId(ctuSoapProcessingRouteForLookup))
			.routeDescription("This Receive Translation Lookup Request For CTU LookUp Service.")
			.log(LoggingLevel.INFO, "Processing Started for Translation Lookup CXF Endpoint...")
			.convertBodyTo(TranslationLookupRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(BulkTranslationLookupRequestType.class))
			.log(LoggingLevel.INFO, "Body after conversion to Json: ${body}")
			.to(OutboundRoutes.HYSTRIX_ENABLED_ENDPOINT_FOR_TRANSLATION_LOOKUP)
			.unmarshal().json(JsonLibrary.Jackson, TranslationLookupResponseType.class)
			.log(LoggingLevel.INFO, "Processing For Translation Lookup Finished !!!");
		
		//BulkTranslationLookupRequest
		from(DIRECT_BULK_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE)
			.id(Configurator.getStepId(ctuSoapProcessingRouteForBulkLookup))
			.routeDescription("This Receive Bulk Translation Lookup Request For CTU LookUp Service.")
			.log(LoggingLevel.INFO, "Processing Started for Bulk Translation Lookup CXF Endpoint...")
			.convertBodyTo(BulkTranslationLookupRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(BulkTranslationLookupRequestType.class))
			.log(LoggingLevel.INFO, "Body after conversion to Json: ${body}")
			.to(OutboundRoutes.HYSTRIX_ENABLED_ENDPOINT_FOR_TRANSLATION_LOOKUP)
			.unmarshal().json(JsonLibrary.Jackson, BulkTranslationLookupResponseType.class)
			.log(LoggingLevel.INFO, "Processing For Bulk Translation Lookup Finished !!!");
		
		//Unsupported Operation
		from(DIRECT_UNSUPPORTED_OPERATION)
			.log("Unsupported Operation")
			.setFaultBody(constant("Unsupported Operation"));

	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("exceptionProcessingRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.bean(ExceptionMessageHandler.class, "handle");
		
	}

}
