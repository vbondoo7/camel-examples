package com.od.eai.services.ctusoap.config;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {
	
	@Bean
	public Bus cxf() {
		return new SpringBus();
	}
	
}
