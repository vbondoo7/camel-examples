package com.od.eai.services.ctusoap.util;

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataFormatUtil {
	
	public static JaxbDataFormat createJaxbDataFormat(Class<?> clazz) {
		return new JaxbDataFormat(clazz.getPackage().getName());
	}
	
	public static JacksonDataFormat dataFormatInstance(Class<?> clazz) {
		JacksonDataFormat dataFormat = new JacksonDataFormat(clazz);
		dataFormat.setDisableFeatures("FAIL_ON_UNKNOWN_PROPERTIES");
		dataFormat.setEnableFeatures("FAIL_ON_NUMBERS_FOR_ENUMS,USE_BIG_DECIMAL_FOR_FLOATS");
		return dataFormat;
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
		return objectMapper;
	}
}
