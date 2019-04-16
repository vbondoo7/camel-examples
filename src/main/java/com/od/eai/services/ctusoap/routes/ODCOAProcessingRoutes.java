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
		
	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("odCoaExceptionProcessingRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.bean(ExceptionMessageHandler.class, "handleFallback");
	}

}
