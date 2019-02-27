package com.od.eai.services.ctusoap.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.od.eai.framework.base.routes.BaseProcessingRouteBuilder;
import com.od.eai.framework.core.dispatch.Configurator;
import com.od.eai.services.ctusoap.util.DataFormatUtil;
import com.officedepot.eai.service.translationutility.TranslationLookupRequestType;
import com.officedepot.eai.service.translationutility.TranslationLookupResponseType;

@Component("ctusoapProcessingRoutes")
public class ProcessingRoutes extends BaseProcessingRouteBuilder {

	public static final Logger log = LoggerFactory.getLogger(ProcessingRoutes.class);
	public static final String DIRECT_CONVERT_XML_TO_JSON = "direct:convertXMLToJson";
	public static final String DIRECT_TRANSLATION_UTILITY_PROCESSING_ROUTE = "direct:translationUtilityProcessingRoute";
	
	@Value("${ctu.soap.processing.route}")
	private String ctuSoapProcessingRoute;
	
	@Value("${ctu.soap.to.json.processing.route}")
	private String ctuSoapToJsonProcessingRoute;
	
	@Override
	public void configureRoutes() throws Exception {

		from(DIRECT_TRANSLATION_UTILITY_PROCESSING_ROUTE)
			.id(Configurator.getStepId(ctuSoapProcessingRoute))
			.routeDescription("This Receive Request For CTU LookUp Service.")
			.log(LoggingLevel.INFO, "Processing Started for CXF Endpoint...")
			.convertBodyTo(TranslationLookupRequestType.class)
			.to(DIRECT_CONVERT_XML_TO_JSON)
			.log(LoggingLevel.INFO, "Processing For Translation Utility Finished !!!");

		from(DIRECT_CONVERT_XML_TO_JSON)
			.id(Configurator.getStepId(ctuSoapToJsonProcessingRoute))
			.routeDescription("This route converts xml to Json..")
			.marshal(DataFormatUtil.dataFormatInstance(TranslationLookupRequestType.class))
			.log(LoggingLevel.INFO, "Body after conversion to Json: ${body}")
			.to(OutboundRoutes.HYSTRIX_ENABLED_CTU_LOOKUP_ENDPOINT)
			.unmarshal().json(JsonLibrary.Jackson, TranslationLookupResponseType.class);

	}

	@Override
	protected void configureExceptions() {
		onException(Exception.class)
			.id(Configurator.getStepId("exceptionProcessingRoute"))
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}")
			.handled(true);
		
	}

}
