package com.od.eai.services.ctusoap.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseProcessingRouteBuilder;
import com.od.eai.framework.core.dispatch.Configurator;
import com.od.eai.services.ctusoap.assembler.CCCOATranslationNewToOldLookupAssembler;
import com.od.eai.services.ctusoap.exception.handler.ExceptionMessageHandler;
import com.od.eai.services.ctusoap.util.DataFormatUtil;
import com.officedepot.eai.service.translationutility.BulkCCCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkCCCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.CCCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupResponseType;

@Component("ctusoapCcCoaProcessingRoutes")
public class CCCOAProcessingRoutes extends BaseProcessingRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(CCCOAProcessingRoutes.class);
	private static final String CTU_INTERNAL_URL 											= "ctuInternalURL";
	public static final String DIRECT_CCCOA_TRANSLATION_NEW_TO_OLD_LOOKUP 					= "direct:CCCOATranslationNewToOldLookup";
	public static final String DIRECT_CCCOA_TRANSLATION_OLD_TO_NEW_LOOKUP 					= "direct:CCCOATranslationOldToNewLookup";
	public static final String DIRECT_BULK_CCCOA_TRANSLATION_OLD_TO_NEW_LOOKUP 				= "direct:bulkCCCOATranslationOldToNewLookup";
	
	@Value("${ctu.ccCOAOldToNew.translation.lookup.url}")
	private String ccCOAOldToNewLookupUrl;
	
	@Value("${ctu.bulkCcCOAOldToNew.translation.lookup.url}")
	private String bulkCcCOAOldToNewLookupUrl;
	
	@Override
	public void configureRoutes() throws Exception {
    
		//CCCOATranslationOldToNewLookup
		from(DIRECT_CCCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
			.routeId(Configurator.getStepId("CCCOATranslationOldToNewLookupRoute"))
			.routeDescription("This Receives CCCOATranslationOldToNewLookup.")
			.log(LoggingLevel.INFO, "Processing Started for CCCOATranslationOldToNewLookup CXF Endpoint...")
			.convertBodyTo(CCCOATranslationOldToNewLookupRequestType.class)
			.setProperty("originalPayload", body())
			.marshal(DataFormatUtil.dataFormatInstance(CCCOATranslationOldToNewLookupRequestType.class))
			.log(LoggingLevel.INFO, "CCCOATranslationOldToNewLookupRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(ccCOAOldToNewLookupUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.unmarshal().json(JsonLibrary.Jackson, CCCOATranslationOldToNewLookupResponseType.class)
			.log(LoggingLevel.INFO, "Processing For CCCOATranslationOldToNewLookupRoute Finished !!!");
		
		//Bulk CCCOATranslationOldToNewLookup
		from(DIRECT_BULK_CCCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
			.routeId(Configurator.getStepId("BulkCCCOATranslationOldToNewLookupRoute"))
			.routeDescription("This Receives BulkCCCOATranslationOldToNewLookup.")
			.log(LoggingLevel.INFO, "Processing Started for BulkCCCOATranslationOldToNewLookup CXF Endpoint...")
			.convertBodyTo(BulkCCCOATranslationOldToNewLookupRequestType.class)
			.setProperty("originalPayload", body())
			.marshal(DataFormatUtil.dataFormatInstance(BulkCCCOATranslationOldToNewLookupRequestType.class))
			.log(LoggingLevel.INFO, "BulkCCCOATranslationOldToNewLookupRequestType Body after conversion to Json: ${body}")
			.setProperty(CTU_INTERNAL_URL, constant(bulkCcCOAOldToNewLookupUrl))
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_INTERNAL_ROUTE)
			.unmarshal().json(JsonLibrary.Jackson, BulkCCCOATranslationOldToNewLookupResponseType.class)
			.log(LoggingLevel.INFO, "Processing For BulkCCCOATranslationOldToNewLookup Finished !!!");
		
		//CCCOATranslationNewToOldLookup
		from(DIRECT_CCCOA_TRANSLATION_NEW_TO_OLD_LOOKUP)
			.routeId(Configurator.getStepId("CCCOATranslationNewToOldLookupRoute"))
			.routeDescription("This Receives CCCOATranslationNewToOldLookup.")
			.log(LoggingLevel.INFO, "Processing Started for CCCOATranslationNewToOldLookup CXF Endpoint...")
			.convertBodyTo(CCCOATranslationNewToOldLookupRequestType.class)
			.setProperty("originalPayload", body())
			.bean(CCCOATranslationNewToOldLookupAssembler.class, "assembler")
			.log(LoggingLevel.INFO, "Processing For CCCOATranslationNewToOldLookupRoute Finished !!!")
			.removeProperties("*");
	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("ccCoaExceptionProcessingRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.bean(ExceptionMessageHandler.class, "handleFallback");
	}

}
