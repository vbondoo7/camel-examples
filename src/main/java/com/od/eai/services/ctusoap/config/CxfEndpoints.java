package com.od.eai.services.ctusoap.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.officedepot.eai.translationutilityservice.TranslationUtilityService;

@Configuration
public class CxfEndpoints {
	
	@Autowired
	private CamelContext camelContext;
	
	@Value("${translation.utility.lookup.url}")
	private String translationUtilityLookupcxfURL;
	
	@Value("${eai.service.name.translationUtility}")
	private String serviceName;
	
	@Value("${ctu.service.loggingFeature}")
	private Boolean ctuLoggingFeature;
	
	@Bean
	public Bus cxf() {
		return new SpringBus();
	}
	
	@Bean
	public CxfEndpoint translationUtilityCxfEndpoint() {
		CxfEndpoint cxfEndpoint = new CxfEndpoint();
		cxfEndpoint.setCamelContext(camelContext);
		cxfEndpoint.setAddress(translationUtilityLookupcxfURL);
		cxfEndpoint.setPublishedEndpointUrl(translationUtilityLookupcxfURL);
		cxfEndpoint.setServiceNameString(serviceName);
		cxfEndpoint.setServiceClass(TranslationUtilityService.class);
		cxfEndpoint.setDataFormat(DataFormat.POJO);
		Map<String, Object> properties = new HashMap<String, Object>();
		cxfEndpoint.setProperties(properties);
		
		List<Feature> features = new ArrayList<>();
		cxfEndpoint.setFeatures(features);
		cxfEndpoint.setLoggingFeatureEnabled(ctuLoggingFeature);
		return cxfEndpoint;
	}

}
