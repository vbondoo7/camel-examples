package com.od.eai.services.ctusoap.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseProcessingRouteBuilder;
import com.od.eai.framework.core.dispatch.Configurator;
import com.od.eai.services.ctusoap.assembler.CCCOATranslationNewToOldLookupAssembler;
import com.od.eai.services.ctusoap.assembler.ODCOATranslationNewToOldLookupAssembler;
import com.od.eai.services.ctusoap.exception.handler.ExceptionMessageHandler;
import com.od.eai.services.ctusoap.util.DataFormatUtil;
import com.officedepot.eai.service.translationutility.BulkTranslationLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkTranslationLookupResponseType;
import com.officedepot.eai.service.translationutility.BulkTranslationUpsertRequestType;
import com.officedepot.eai.service.translationutility.BulkTranslationUpsertResponseType;
import com.officedepot.eai.service.translationutility.CCCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.TranslationDeleteRequestType;
import com.officedepot.eai.service.translationutility.TranslationDeleteResponseType;
import com.officedepot.eai.service.translationutility.TranslationLookupRequestType;
import com.officedepot.eai.service.translationutility.TranslationLookupResponseType;
import com.officedepot.eai.service.translationutility.TranslationUpsertRequestType;
import com.officedepot.eai.service.translationutility.TranslationUpsertResponseType;

@Component("ctusoapProcessingRoutes")
public class ProcessingRoutes extends BaseProcessingRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(ProcessingRoutes.class);
	private static final String CTU_INTERNAL_URL 											= "ctuInternalURL";
	public static final String DIRECT_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE 			= "direct:translationLookupRequestProcessingRoute";
	public static final String DIRECT_BULK_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE 		= "direct:bulkTranslationLookupRequestProcessingRoute";
	public static final String DIRECT_UNSUPPORTED_OPERATION 								= "direct:unsupportedOperation";
	public static final String DIRECT_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE 			= "direct:translationUpsertRequestProcessingRoute";
	public static final String DIRECT_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE_ID 		= "TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE";
	public static final String DIRECT_BULK_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE 		= "direct:bulkTranslationUpsertRequestProcessingRoute";
	public static final String DIRECT_BULK_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE_ID 	= "BULK_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE";
	public static final String DIRECT_TRANSLATION_DELETE_REQUEST_PROCESSING_ROUTE 			= "direct:translationDeleteRequestProcessingRoute";
	private static final String DIRECT_TRANSLATION_DELETE_REQUEST_PROCESSING_ROUTE_ID 		= "TRANSLATION_DELETE_REQUEST_PROCESSING_ROUTE";
	public static final String DIRECT_CCCOA_TRANSLATION_NEW_TO_OLD_LOOKUP 					= "direct:CCCOATranslationNewToOldLookup";
	public static final String DIRECT_CCCOA_TRANSLATION_OLD_TO_NEW_LOOKUP 					= "direct:CCCOATranslationOldToNewLookup";
	public static final String DIRECT_ODCOA_TRANSLATION_NEW_TO_OLD_LOOKUP 					= "direct:ODCOATranslationNewToOldLookup";
	public static final String DIRECT_ODCOA_TRANSLATION_OLD_TO_NEW_LOOKUP 					= "direct:ODCOATranslationOldToNewLookup";
	
	@Value("${ctu.soap.translation.request.processing.route}")
	private String ctuSoapProcessingRouteForLookup;
	
	@Value("${ctu.soap.bulk.translation.request.processing.route}")
	private String ctuSoapProcessingRouteForBulkLookup;
	
	@Value("${ctu.lookup.service.url}")
	private String ctuLookupTranslationReqServiceURL;
	
	@Value("${ctu.bulk.lookup.service.url}")
	private String ctuLookupBulkTranslationReqServiceURL;
	
	@Value("${ctu.upsert.service.url}")
	private String ctuUpsertUrl;
	
	@Value("${ctu.bulk.upsert.service.url}")
	private String ctuBulkUpsertUrl;
	
	@Value("${ctu.delete.service.url}")
	private String ctuDeleteUrl;
	
	@Value("${ctu.ccCOAOldToNew.translation.lookup.url}")
	private String ccCOAOldToNewLookupUrl;
	
	@Value("${ctu.odCOAOldToNew.translation.lookup.url}")
	private String odCOAOldToNewLookupUrl;
	
	@Override
	public void configureRoutes() throws Exception {
		//TranslationLookupRequest
		from(DIRECT_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE)
			.routeId(Configurator.getStepId(ctuSoapProcessingRouteForLookup))
			.routeDescription("This Receives Translation Lookup Request For CTU LookUp Service.")
			.log(LoggingLevel.INFO, "Processing Started for Translation Lookup CXF Endpoint...")
			.convertBodyTo(TranslationLookupRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(BulkTranslationLookupRequestType.class))
			.log(LoggingLevel.INFO, "TranslationLookupRequestTypeBody after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(ctuLookupTranslationReqServiceURL))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.unmarshal().json(JsonLibrary.Jackson, TranslationLookupResponseType.class)
			.log(LoggingLevel.INFO, "Processing For Translation Lookup Finished !!!");
		
		//BulkTranslationLookupRequest
		from(DIRECT_BULK_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE)
			.routeId(Configurator.getStepId(ctuSoapProcessingRouteForBulkLookup))
			.routeDescription("This Receives Bulk Translation Lookup Request For CTU LookUp Service.")
			.log(LoggingLevel.INFO, "Processing Started for Bulk Translation Lookup CXF Endpoint...")
			.convertBodyTo(BulkTranslationLookupRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(BulkTranslationLookupRequestType.class))
			.log(LoggingLevel.INFO, "BulkTranslationLookupRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(ctuLookupBulkTranslationReqServiceURL))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.unmarshal().json(JsonLibrary.Jackson, BulkTranslationLookupResponseType.class)
			.log(LoggingLevel.INFO, "Processing For Bulk Translation Lookup Finished !!!");
		
		//TranslationUpsertRequest
		from(DIRECT_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE)
			.routeId(Configurator.getStepId(DIRECT_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE_ID))
			.routeDescription("This Receives Translation Upsert Request For CTU Service.")
			.log(LoggingLevel.INFO, "Processing Started for Translation Upsert CXF Endpoint...")
			.convertBodyTo(TranslationUpsertRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(TranslationUpsertRequestType.class))
			.log(LoggingLevel.INFO, "TranslationUpsertRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(ctuUpsertUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.unmarshal().json(JsonLibrary.Jackson, TranslationUpsertResponseType.class)
			.log(LoggingLevel.INFO, "Processing For Translation Upsert Finished !!!");
				
		//BulkTranslationUpsertRequest
		from(DIRECT_BULK_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE)
			.routeId(Configurator.getStepId(DIRECT_BULK_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE_ID))
			.routeDescription("This Receives Bulk Translation Upsert Request For CTU Service.")
			.log(LoggingLevel.INFO, "Processing Started for Bulk Translation Upsert CXF Endpoint...")
			.convertBodyTo(BulkTranslationUpsertRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(BulkTranslationUpsertRequestType.class))
			.log(LoggingLevel.INFO, "BulkTranslationUpsertRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(ctuBulkUpsertUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.unmarshal().json(JsonLibrary.Jackson, BulkTranslationUpsertResponseType.class)
			.log(LoggingLevel.INFO, "Processing For Bulk Translation Upsert Finished !!!");
    
		//TranslationDeleteRequest
		from(DIRECT_TRANSLATION_DELETE_REQUEST_PROCESSING_ROUTE)
			.routeId(Configurator.getStepId(DIRECT_TRANSLATION_DELETE_REQUEST_PROCESSING_ROUTE_ID))
			.routeDescription("This Receives Translation Delete Request For CTU Service.")
			.log(LoggingLevel.INFO, "Processing Started for Translation Delete CXF Endpoint...")
			.convertBodyTo(TranslationDeleteRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(TranslationDeleteRequestType.class))
			.log(LoggingLevel.INFO, "TranslationDeleteRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(ctuDeleteUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.unmarshal().json(JsonLibrary.Jackson, TranslationDeleteResponseType.class)
			.log(LoggingLevel.INFO, "Processing For Translation Delete Finished !!!");
    
		//ODCOATranslationOldToNewLookup
		from(DIRECT_ODCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
			.routeId(Configurator.getStepId("ODCOATranslationOldToNewLookupRoute"))
			.routeDescription("This Receives ODCOATranslationOldToNewLookup.")
			.log(LoggingLevel.INFO, "Processing Started for ODCOATranslationOldToNewLookup CXF Endpoint...")
			.convertBodyTo(ODCOATranslationOldToNewLookupRequestType.class)
			//.bean(ODCOATranslationOldToNewLookupAssembler.class, "assembler")
			.marshal(DataFormatUtil.dataFormatInstance(ODCOATranslationOldToNewLookupRequestType.class))
			.log(LoggingLevel.INFO, "ODCOATranslationOldToNewLookup Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(odCOAOldToNewLookupUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.convertBodyTo(String.class)
			.choice()
				.when(body().contains("\"ERROR\""))
					.bean(ExceptionMessageHandler.class, "handleFallback")
				.otherwise()
					.unmarshal().json(JsonLibrary.Jackson, ODCOATranslationOldToNewLookupResponseType.class)
				.end()
			.log(LoggingLevel.INFO, "Processing For ODCOATranslationOldToNewLookupRoute Finished !!!");
		
		//ODCOATranslationNewToOldLookup
		from(DIRECT_ODCOA_TRANSLATION_NEW_TO_OLD_LOOKUP)
			.routeId(Configurator.getStepId("ODCOATranslationNewToOldLookupRoute"))
			.routeDescription("This Receives ODCOATranslationNewToOldLookup.")
			.log(LoggingLevel.INFO, "Processing Started for ODCOATranslationNewToOldLookup CXF Endpoint...")
			.convertBodyTo(ODCOATranslationNewToOldLookupRequestType.class)
			.bean(ODCOATranslationNewToOldLookupAssembler.class, "assembler")
			.log(LoggingLevel.INFO, "Processing For ODCOATranslationNewToOldLookupRoute Finished !!!");
		
		//CCCOATranslationOldToNewLookup
		from(DIRECT_CCCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
			.routeId(Configurator.getStepId("CCCOATranslationOldToNewLookupRoute"))
			.routeDescription("This Receives CCCOATranslationOldToNewLookup.")
			.log(LoggingLevel.INFO, "Processing Started for CCCOATranslationOldToNewLookup CXF Endpoint...")
			.convertBodyTo(CCCOATranslationOldToNewLookupRequestType.class)
			//.bean(CCCOATranslationOldToNewLookupAssembler.class, "assembler")
			.marshal(DataFormatUtil.dataFormatInstance(CCCOATranslationOldToNewLookupRequestType.class))
			.log(LoggingLevel.INFO, "CCCOATranslationOldToNewLookupRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(ccCOAOldToNewLookupUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.convertBodyTo(String.class)
			.choice()
				.when(body().contains("\"ERROR\""))
					.bean(ExceptionMessageHandler.class, "handleFallback")
				.otherwise()
					.unmarshal().json(JsonLibrary.Jackson, CCCOATranslationOldToNewLookupResponseType.class)
				.end()
			.log(LoggingLevel.INFO, "Processing For CCCOATranslationOldToNewLookupRoute Finished !!!");
		
		//CCCOATranslationNewToOldLookup
		from(DIRECT_CCCOA_TRANSLATION_NEW_TO_OLD_LOOKUP)
			.routeId(Configurator.getStepId("CCCOATranslationNewToOldLookupRoute"))
			.routeDescription("This Receives CCCOATranslationNewToOldLookup.")
			.log(LoggingLevel.INFO, "Processing Started for CCCOATranslationNewToOldLookup CXF Endpoint...")
			.convertBodyTo(CCCOATranslationNewToOldLookupRequestType.class)
			.bean(CCCOATranslationNewToOldLookupAssembler.class, "assembler")
			.log(LoggingLevel.INFO, "Processing For CCCOATranslationNewToOldLookupRoute Finished !!!");
	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("exceptionProcessingRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.bean(ExceptionMessageHandler.class, "handleFallback");
	}

}
