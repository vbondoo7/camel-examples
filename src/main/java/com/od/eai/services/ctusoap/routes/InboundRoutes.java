package com.od.eai.services.ctusoap.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseInboundRouteBuilder;
import com.od.eai.framework.core.dispatch.Configurator;

@Component("ctusoapInboundRoutes")
public class InboundRoutes extends BaseInboundRouteBuilder {
	
	public static final Logger log = LoggerFactory.getLogger(InboundRoutes.class);

	@Autowired
	private CxfEndpoint translationUtilityLookupcxfURL;
	
	@Value("${translation.utility.request.route}")
	private String traslationUtilityRequest;
	
	@Override
	protected void preConfigure() {
		restConfiguration().enableCORS(true).corsHeaderProperty("Access-Control-Allow-Origin", "*").corsHeaderProperty(
			"Access-Control-Allow-Headers",
			"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization")
			.component("servlet").bindingMode(RestBindingMode.off)
			.dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES")
			.dataFormatProperty("json.in.enableFeatures", "FAIL_ON_NUMBERS_FOR_ENUMS,USE_BIG_DECIMAL_FOR_FLOATS");
	}
	
	@Override
	public void configureRoutes() throws Exception {
		
		from(translationUtilityLookupcxfURL).id(Configurator.getStepId(traslationUtilityRequest))
		  .log(LoggingLevel.INFO,"******* Received Translation Utility Request ******** ${header.operationName}")
		  .choice()
		  		.when(header(CxfConstants.OPERATION_NAME).isEqualTo("translationLookUpRequest"))
		  			.to(ProcessingRoutes.DIRECT_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE)
		  		.when(header(CxfConstants.OPERATION_NAME).isEqualTo("bulkTranslationLookUpRequest"))
		  			.to(ProcessingRoutes.DIRECT_BULK_TRANSLATION_LOOKUP_REQUEST_PROCESSING_ROUTE)
	  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("translationUpsertReq"))
		  			.to(ProcessingRoutes.DIRECT_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE)
	  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("bulkTranslationUpsertReq"))
		  			.to(ProcessingRoutes.DIRECT_BULK_TRANSLATION_UPSERT_REQUEST_PROCESSING_ROUTE)
	  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("translationDeleteReq"))
		  			.to(ProcessingRoutes.DIRECT_TRANSLATION_DELETE_REQUEST_PROCESSING_ROUTE)
  				.when(header(CxfConstants.OPERATION_NAME).isEqualTo("oDCOATranslationOldToNewLookupReq"))
		  			.to(ODCOAProcessingRoutes.DIRECT_ODCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
	  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("bulkODCOATranslationOldToNewLookupReq"))
		  			.to(ODCOAProcessingRoutes.DIRECT_BULK_ODCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
	  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("oDCOATranslationNewToOldLookupReq"))
		  			.to(ODCOAProcessingRoutes.DIRECT_ODCOA_TRANSLATION_NEW_TO_OLD_LOOKUP)
		  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("bulkODCOATranslationNewToOldLookupReq"))
		  			.to(ODCOAProcessingRoutes.DIRECT_BULK_ODCOA_TRANSLATION_NEW_TO_OLD_LOOKUP)
	  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("cCCOATranslationOldToNewLookupReq"))
		  			.to(CCCOAProcessingRoutes.DIRECT_CCCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
	  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("bulkCCCOATranslationOldToNewLookupReq"))
		  			.to(CCCOAProcessingRoutes.DIRECT_BULK_CCCOA_TRANSLATION_OLD_TO_NEW_LOOKUP)
	  			.when(header(CxfConstants.OPERATION_NAME).isEqualTo("cCCOATranslationNewToOldLookupReq"))
		  			.to(CCCOAProcessingRoutes.DIRECT_CCCOA_TRANSLATION_NEW_TO_OLD_LOOKUP)
		  		.otherwise() 
		  			.throwException(new UnsupportedOperationException());
	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("exceptionInboundRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.setFaultBody(simple("${exception.message}"));
	}

}
