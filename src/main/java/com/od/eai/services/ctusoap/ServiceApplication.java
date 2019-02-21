package com.od.eai.services.ctusoap;

import org.apache.camel.component.hystrix.metrics.servlet.HystrixEventStreamServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import com.od.eai.framework.base.boot.BaseEAIServiceApplication;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@ImportResource({"classpath:META-INF/spring/application-context.xml"})
@EnableAutoConfiguration(exclude = { JmxAutoConfiguration.class, JmsAutoConfiguration.class/*, ErrorMvcAutoConfiguration.class*/ })
public class ServiceApplication extends BaseEAIServiceApplication {
	
	public static final Logger log = LoggerFactory.getLogger(ServiceApplication.class);
	
	@Value("${properties.file.name}")
	protected String propFile;
	
	@Value("${camel.component.servlet.mapping.servlet-name}")
	protected String camelServletName;
	
	@Value("${camel.component.servlet.mapping.context-path}")
	protected String camelServletContext;
	
	public static void main(String args[]) {
		runApp(ServiceApplication.class, args);
	}
	
//	@Bean
//	ServletRegistrationBean servletRegistrationBean() {
//		log.info("Loaded properties from [{}]", propFile);
//		log.info("Registering camel servlet with (name, context)=(["+camelServletName+"],[" + camelServletContext+"])");
//		return getCamelServletRegistrationBean(camelServletName, camelServletContext);
//	}

	 @Bean
     ServletRegistrationBean hystrixServlet() {
        return new ServletRegistrationBean(new HystrixEventStreamServlet(), "/eai-odhystrix.stream");
    }
	 
	 @Bean
	    public ServletRegistrationBean cxfServlet() {
	        org.apache.cxf.transport.servlet.CXFServlet cxfServlet = new org.apache.cxf.transport.servlet.CXFServlet();
	        ServletRegistrationBean servletDef = new ServletRegistrationBean(cxfServlet, "/eaiapi/*");
	        servletDef.setLoadOnStartup(1);
	        return servletDef;
	    }

}
