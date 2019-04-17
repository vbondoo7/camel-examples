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
import com.officedepot.eai.service.translationutility.BulkODCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkODCOATranslationNewToOldLookupResponseType;
import com.officedepot.eai.service.translationutility.BulkODCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkODCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupResponseType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupResponseType;

@Component("ctusoapOdCoaProcessingRoutes")
public class ODCOAProcessingRoutes extends BaseProcessingRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(ODCOAProcessingRoutes.class);
	private static final String CTU_INTERNAL_URL 											= "ctuInternalURL";
	public static final String DIRECT_ODCOA_TRANSLATION_NEW_TO_OLD_LOOKUP 					= "direct:ODCOATranslationNewToOldLookup";
	public static final String DIRECT_ODCOA_TRANSLATION_OLD_TO_NEW_LOOKUP 					= "direct:ODCOATranslationOldToNewLookup";
	public static final String DIRECT_BULK_ODCOA_TRANSLATION_NEW_TO_OLD_LOOKUP 				= "direct:BulkODCOATranslationNewToOldLookup";
	public static final String DIRECT_BULK_ODCOA_TRANSLATION_OLD_TO_NEW_LOOKUP 				= "direct:BulkODCOATranslationOldToNewLookup";
	
	@Value("${ctu.odCOAOldToNew.translation.lookup.url}")
	private String odCOAOldToNewLookupUrl;
	
	@Value("${ctu.odCOANewToOld.translation.lookup.url}")
	private String odCOANewToOldLookupUrl;
	
	@Value("${ctu.bulkOdCOAOldToNew.translation.lookup.url}")
	private String bulkOdCOAOldToNewLookupUrl;
	
	@Value("${ctu.bulkOdCOANewToOld.translation.lookup.url}")
	private String bulkOdCOANewToOldLookupUrl;
	
	@Override
	public void configureRoutes() throws Exception {
    
		//ODCOATranslationOldToNewLookup
		from(DIRECT_ODCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
			.routeId(Configurator.getStepId("ODCOATranslationOldToNewLookupRoute"))
			.routeDescription("This Receives ODCOATranslationOldToNewLookup.")
			.log(LoggingLevel.INFO, "Processing Started for ODCOATranslationOldToNewLookup CXF Endpoint...")
			.convertBodyTo(ODCOATranslationOldToNewLookupRequestType.class)
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
		
		//Bulk ODCOATranslationOldToNewLookup
		from(DIRECT_BULK_ODCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
			.routeId(Configurator.getStepId("BulkODCOATranslationOldToNewLookupRoute"))
			.routeDescription("This Receives BulkODCOATranslationOldToNewLookup.")
			.log(LoggingLevel.INFO, "Processing Started for BulkODCOATranslationOldToNewLookup CXF Endpoint...")
			.convertBodyTo(BulkODCOATranslationOldToNewLookupRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(BulkODCOATranslationOldToNewLookupRequestType.class))
			.log(LoggingLevel.INFO, "BulkODCOATranslationOldToNewLookupRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(bulkOdCOAOldToNewLookupUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.convertBodyTo(String.class)
			.choice()
				.when(body().contains("\"ERROR\""))
					.bean(ExceptionMessageHandler.class, "handleFallback")
				.otherwise()
					.unmarshal().json(JsonLibrary.Jackson, BulkODCOATranslationOldToNewLookupResponseType.class)
				.end()
			.log(LoggingLevel.INFO, "Processing For BulkODCOATranslationOldToNewLookup Finished !!!");
				
		//ODCOATranslationNewToOldLookup
		from(DIRECT_ODCOA_TRANSLATION_NEW_TO_OLD_LOOKUP)
			.routeId(Configurator.getStepId("ODCOATranslationNewToOldLookupRoute"))
			.routeDescription("This Receives ODCOATranslationNewToOldLookup.")
			.log(LoggingLevel.INFO, "Processing Started for ODCOATranslationNewToOldLookup CXF Endpoint...")
			.convertBodyTo(ODCOATranslationNewToOldLookupRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(ODCOATranslationNewToOldLookupRequestType.class))
			.log(LoggingLevel.INFO, "ODCOATranslationNewToOldLookup Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(odCOANewToOldLookupUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.convertBodyTo(String.class)
			.choice()
				.when(body().contains("\"ERROR\""))
					.bean(ExceptionMessageHandler.class, "handleFallback")
				.otherwise()
					.unmarshal().json(JsonLibrary.Jackson, ODCOATranslationNewToOldLookupResponseType.class)
				.end()
			.log(LoggingLevel.INFO, "Processing For ODCOATranslationNewToOldLookupRoute Finished !!!");
		
		//Bulk ODCOATranslationNewToOldLookup
		from(DIRECT_BULK_ODCOA_TRANSLATION_NEW_TO_OLD_LOOKUP)
			.routeId(Configurator.getStepId("BulkODCOATranslationNewToOldLookupRoute"))
			.routeDescription("This Receives BulkODCOATranslationNewToOldLookup.")
			.log(LoggingLevel.INFO, "Processing Started for BulkODCOATranslationNewToOldLookup CXF Endpoint...")
			.convertBodyTo(BulkODCOATranslationNewToOldLookupRequestType.class)
			.marshal(DataFormatUtil.dataFormatInstance(BulkODCOATranslationNewToOldLookupRequestType.class))
			.log(LoggingLevel.INFO, "BulkODCOATranslationNewToOldLookupRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(bulkOdCOANewToOldLookupUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.convertBodyTo(String.class)
			.choice()
				.when(body().contains("\"ERROR\""))
					.bean(ExceptionMessageHandler.class, "handleFallback")
				.otherwise()
					.unmarshal().json(JsonLibrary.Jackson, BulkODCOATranslationNewToOldLookupResponseType.class)
				.end()
			.log(LoggingLevel.INFO, "Processing For BulkODCOATranslationNewToOldLookup Finished !!!");
	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("odCoaExceptionProcessingRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.bean(ExceptionMessageHandler.class, "handleFallback");
	}

}
