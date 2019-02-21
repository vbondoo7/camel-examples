package com.od.eai.services.ctusoap.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseProcessingRouteBuilder;
import com.od.eai.services.ctusoap.assembler.ResponseAssembler;
import com.officedepot.eai.service.translationutility.TranslationLookupRequestType;
import com.officedepot.eai.service.translationutility.TranslationLookupResponseType;

@Component("ctusoapProcessingRoutes")
public class ProcessingRoutes extends BaseProcessingRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(ProcessingRoutes.class);
	public static final String DIRECT_TRANSLATION_UTILITY_PROCESSING_ROUTE = "direct:translationUtilityProcessingRoute";
	
	@Override
	public void configureRoutes() throws Exception {
		
		from(DIRECT_TRANSLATION_UTILITY_PROCESSING_ROUTE).routeId("translationUtilityProcess")
		  //.log(LoggingLevel.INFO, "Request Body ${body.translationLookupRequestType.header.transactionId}")
		 // .to("direct:convertXMLToJson")
		  .convertBodyTo(TranslationLookupRequestType.class)
		  .bean(ResponseAssembler.class, "assemble")
		  //.marshal().json(JsonLibrary.Jackson, TranslationLookupResponseType.class)
		 .log(LoggingLevel.INFO,"Processing For Translation Utility Finished !!!");
		
		from("direct:convertXMLToJson").routeId("convertXMLToJson")
		  .marshal().json(JsonLibrary.Jackson, TranslationLookupResponseType.class)
		  //.to(call translationUtility lookup)
		  .log(LoggingLevel.INFO, "After conversion to Json: ${body}");
		
	}

	@Override
	protected void configureExceptions() {
		// TODO Auto-generated method stub
		
	}

}
