package com.od.eai.services.ctusoap.config;

import org.apache.camel.CamelContext;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.cxf.Bus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.officedepot.eai.translationutilityservice.TranslationUtilityService;

@Configuration
public class CxfEndpoints {
	
	@Autowired
	private CamelContext camelContext;
	
	@Autowired
	private Bus cxf;

	@Value("${translation.utility.lookup.url}")
	private String translationUtilityLookupcxfURL;
	
	@Bean
	public CxfEndpoint translationUtilityCxfEndpoint() {
		CxfEndpoint cxfEndpoint = new CxfEndpoint();
		cxfEndpoint.setCamelContext(camelContext);
		cxfEndpoint.setAddress(translationUtilityLookupcxfURL);
		cxfEndpoint.setPublishedEndpointUrl(translationUtilityLookupcxfURL);
		//cxfEndpoint.setEndpointUriIfNotSpecified(translationUtilityLookupcxfURL);
		cxfEndpoint.setServiceNameString(translationUtilityLookupcxfURL);
		cxfEndpoint.setServiceClass(TranslationUtilityService.class);
		cxfEndpoint.setDataFormat(DataFormat.POJO);
		cxfEndpoint.setBus(cxf);
		return cxfEndpoint;
	}

}
