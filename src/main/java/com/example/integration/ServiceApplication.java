package com.example.integration;

import org.apache.camel.component.servlet.springboot.ServletMappingAutoConfiguration;
import org.apache.camel.http.common.CamelServlet;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

/**
 * @author saxena.vishal1986
 *
 */
@Slf4j
@SpringBootApplication
@EnableAutoConfiguration(exclude = { JmxAutoConfiguration.class, JmsAutoConfiguration.class, ServletMappingAutoConfiguration.class })
public class ServiceApplication {

	@Value("${camel.component.servlet.mapping.servlet-name}")
	protected String camelServletName;

	@Value("${camel.component.servlet.mapping.context-path}")
	protected String camelServletContext;

	@Value("${calc.utility.cxf.url-mappings}")
	protected String cxfServletMapping;

	public static void main(String args[]) {
		SpringApplication.run(ServiceApplication.class, args);
	}

	/*@Bean
	ServletRegistrationBean<CamelServlet> servletRegistrationBean() {
		log.info("Registering camel servlet with (name, context)=([" + camelServletName + "],[" + camelServletContext+ "])");
		ServletRegistrationBean<CamelServlet> servletDef = new ServletRegistrationBean<CamelServlet>(new CamelServlet(), camelServletContext);
		servletDef.setLoadOnStartup(1);
		return servletDef;
	}*/

	@Bean
	public ServletRegistrationBean<CXFServlet> cxfServlet() {
		org.apache.cxf.transport.servlet.CXFServlet cxfServlet = new org.apache.cxf.transport.servlet.CXFServlet();
		ServletRegistrationBean<CXFServlet> servletDef = new ServletRegistrationBean<CXFServlet>(cxfServlet, cxfServletMapping);
		servletDef.setLoadOnStartup(1);
		return servletDef;
	}
}
