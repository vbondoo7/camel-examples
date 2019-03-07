package com.od.eai.services.ctusoap.routes;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.officedepot.eai.service.translationutility.BulkTranslationLookupRequestType;
import com.officedepot.eai.service.translationutility.ObjectFactory;
import com.officedepot.eai.service.translationutility.TranslationLookupRequestType;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DisableJmx(true)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith
public class ProcessingRoutesTest extends CamelTestSupport {
	
	@Autowired
	private CamelContext camelContext;

	@Override
	protected CamelContext createCamelContext() throws Exception {
		return camelContext;
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		RouteDefinition definition = context().getRouteDefinitions().get(0);
		definition.adviceWith(context(), new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				onException(Exception.class).maximumRedeliveries(0);
			}
		});
	}

	@Override
	protected void doPostSetup() throws Exception {
		
		context.getRouteDefinition("eaiaudit_CTU_SOAP_TRANSLATION_REQUEST_PROCESSING_ROUTE").adviceWith(context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						weaveAddLast().to("mock:end");
						String elasticResponse = getTestJsonResponse("data/translationLookupResponse.json");
						interceptSendToEndpoint("direct:hystrixEnabledCTUInternalRoute")
							.skipSendToOriginalEndpoint().setBody(constant(elasticResponse));
					}
				});
		
		context.getRouteDefinition("eaiaudit_CTU_SOAP_BULK_TRANSLATION_REQUEST_PROCESSING_ROUTE").adviceWith(context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						weaveAddLast().to("mock:end");
						String elasticResponse = getTestJsonResponse("data/bulkTranslationLookupResponse.json");
						interceptSendToEndpoint("direct:hystrixEnabledCTUInternalRoute")
							.skipSendToOriginalEndpoint().setBody(constant(elasticResponse));
					}
				});
		
		context.start();
	}

	@Test
	public void testTranslationLookupRequestProcessingRoute() throws Exception {

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		InputStream in = getClass().getClassLoader().getResourceAsStream("data/translationLookupRequest.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<TranslationLookupRequestType> translationLookupRequestJaxbElement = (JAXBElement<TranslationLookupRequestType>) unmarshaller.unmarshal(in);
		TranslationLookupRequestType translationLookupRequest = translationLookupRequestJaxbElement.getValue();
		
		MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		mockEndpoint.expectedMessageCount(1);

		Map<String, Object> mapHeaders = new HashMap<String, Object>();
		template.requestBodyAndHeaders("direct:translationLookupRequestProcessingRoute", translationLookupRequest, mapHeaders);
		
		mockEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void testBulkTranslationLookupRequestProcessingRoute() throws Exception {

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		InputStream in = getClass().getClassLoader().getResourceAsStream("data/bulkTranslationLookupRequest.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<BulkTranslationLookupRequestType> bulkTranslationLookupRequestJaxbElement = (JAXBElement<BulkTranslationLookupRequestType>) unmarshaller.unmarshal(in);
		BulkTranslationLookupRequestType bulkTranslationLookupRequest = bulkTranslationLookupRequestJaxbElement.getValue();
		
		MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		mockEndpoint.expectedMessageCount(1);

		Map<String, Object> mapHeaders = new HashMap<String, Object>();
		template.requestBodyAndHeaders("direct:bulkTranslationLookupRequestProcessingRoute", bulkTranslationLookupRequest, mapHeaders);
		
		mockEndpoint.assertIsSatisfied();
	}
	
	private String getTestJsonResponse(String filename) throws Exception {
		Path path = Paths.get(getClass().getClassLoader().getResource(filename).toURI());
		assertNotNull(path);

		StringBuilder data = new StringBuilder();
		Stream<String> lines = Files.lines(path);
		lines.forEach(line -> data.append(line).append("\n"));
		lines.close();
		return data.toString();
	}
}
