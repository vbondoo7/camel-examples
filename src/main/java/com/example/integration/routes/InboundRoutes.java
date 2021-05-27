package com.example.integration.routes;

import java.util.concurrent.TimeUnit;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.cxf.CxfComponent;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * @author saxena.vishal1986
 *
 */
@Component("inboundRoutes")
public class InboundRoutes extends EndpointRouteBuilder {
	
	@Value("${calc.utility.url}")
	private String calcUtilityURL;
	
	@Value("${calc.utility.service.name}")
	private String calcServiceName;
	
	@Value("${calc.utility.service.wsdl}")
	private String calcWsdlUrl;
	
	@Value("${calc.utility.service.loggingFeature}")
	private Boolean calcLoggingFeature;
	
	@Value("${rest.health-api.url}")
	private String healthAPI;
	
	private void preConfigure() {
		restConfiguration().enableCORS(true).corsHeaderProperty("Access-Control-Allow-Origin", "*")
			.corsHeaderProperty("Access-Control-Allow-Headers","Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization")
			.component("servlet")
			.bindingMode(RestBindingMode.off)
			.dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES")
			.dataFormatProperty("json.in.enableFeatures", "FAIL_ON_NUMBERS_FOR_ENUMS,USE_BIG_DECIMAL_FOR_FLOATS");
	}
	
	@Override
	public void configure() throws Exception {

		CxfComponent calcCxf = ComponentsBuilderFactory.cxf()
								.bridgeErrorHandler(true)
								.build(getContext());
		
		getContext().addComponent("calcCxf", calcCxf);
		
		getContext().getShutdownStrategy().setTimeout(10L);
		getContext().getShutdownStrategy().setTimeUnit(TimeUnit.SECONDS);
		
		preConfigure();
		
		configureExceptions();
		
		// Health Check
		rest(healthAPI)
			.get()
				.route().transform().constant("Service UP !!").end();
		
		from(cxf("calcCxf", calcUtilityURL)
				.publishedEndpointUrl(calcUtilityURL)
				//.serviceName(calcServiceName)
				.wsdlURL(calcWsdlUrl)
				.serviceClass((Class<Object>) Class.forName("com.mydemo.calcutilityservice.CalcUtilityService"))
				.loggingFeatureEnabled(calcLoggingFeature)).routeId("calc-utility-route")
			.log(LoggingLevel.DEBUG,"******* Received CalcUtilityService request for operation :: ${header.operationName}")
			.choice()
	  		.when(PredicateBuilder.or(header(CxfConstants.OPERATION_NAME).isEqualTo("Add"), header(CxfConstants.OPERATION_NAME).isEqualTo("Subtract")))
	  			.to(ProcessingRoutes.DIRECT_ADD_REQUEST_PROCESSING_ROUTE)
  			.otherwise() 
	  			.throwException(new UnsupportedOperationException())
		.end();
		
	}

	@Bean
	public Bus cxf() {
		return new SpringBus();
	}
	
	private void configureExceptions() {
		onException(Exception.class)
			.id("exceptionInboundRoute")
			.log(LoggingLevel.ERROR, "Exception occurred : ${exception.stacktrace}");
			//.setFaultBody(simple("${exception.message}")).throwSoapFault();
			//.bean(ExceptionMessageHandler.class, "handleFallback");
	}

}
