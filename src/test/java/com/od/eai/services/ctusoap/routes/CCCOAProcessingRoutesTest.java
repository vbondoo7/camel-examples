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

import com.officedepot.eai.service.translationutility.BulkCCCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.ObjectFactory;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DisableJmx(true)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith
public class CCCOAProcessingRoutesTest extends CamelTestSupport {
	
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
		
		context.getRouteDefinition("eaiaudit_CCCOATranslationOldToNewLookupRoute").adviceWith(context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						weaveAddLast().to("mock:end");
						String elasticResponse = getTestJsonResponse("data/CcCoaOldToNewLookupResponse.json");
						interceptSendToEndpoint("direct:hystrixEnabledCTUInternalRoute")
							.skipSendToOriginalEndpoint().setBody(constant(elasticResponse));
					}
				});
		
		context.getRouteDefinition("eaiaudit_BulkCCCOATranslationOldToNewLookupRoute").adviceWith(context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						weaveAddLast().to("mock:end");
						String elasticResponse = getTestJsonResponse("data/bulkCcCoaOldToNewLookupResponse.json");
						interceptSendToEndpoint("direct:hystrixEnabledCTUInternalRoute")
							.skipSendToOriginalEndpoint().setBody(constant(elasticResponse));
					}
				});
		
		context.start();
	}

	@Test
	public void testCCCOATranslationOldToNewLookupRoute() throws Exception {

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		InputStream in = getClass().getClassLoader().getResourceAsStream("data/CcCoaOldToNewLookupRequest.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<CCCOATranslationOldToNewLookupRequestType> ccCOATranslationOldToNewLookupRequestJaxbElement = (JAXBElement<CCCOATranslationOldToNewLookupRequestType>) unmarshaller.unmarshal(in);
		CCCOATranslationOldToNewLookupRequestType ccCOATranslationOldToNewLookupRequest = ccCOATranslationOldToNewLookupRequestJaxbElement.getValue();
		
		MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		mockEndpoint.expectedMessageCount(1);

		Map<String, Object> mapHeaders = new HashMap<String, Object>();
		template.requestBodyAndHeaders("direct:CCCOATranslationOldToNewLookup", ccCOATranslationOldToNewLookupRequest, mapHeaders);
		
		mockEndpoint.assertIsSatisfied();
		
		CCCOATranslationOldToNewLookupResponseType expectedResponse = new CCCOATranslationOldToNewLookupResponseType();
		expectedResponse.setEntity("P510");
		expectedResponse.setCostCenter("36002");
		expectedResponse.setAccount("412050");
		
		CCCOATranslationOldToNewLookupResponseType actualResponse = mockEndpoint.getExchanges().get(0).getIn().getBody(CCCOATranslationOldToNewLookupResponseType.class);
		assertEquals("Matched with expected response.", expectedResponse.getEntity(), actualResponse.getEntity());
		assertEquals("Matched with expected response.", expectedResponse.getCostCenter(), actualResponse.getCostCenter());
		assertEquals("Matched with expected response.", expectedResponse.getAccount(), actualResponse.getAccount());
	}
	
	@Test
	public void testBulkCCCOATranslationOldToNewLookupRoute() throws Exception {

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		InputStream in = getClass().getClassLoader().getResourceAsStream("data/bulkCcCoaOldToNewLookupRequest.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<BulkCCCOATranslationOldToNewLookupRequestType> bulkCCCOATranslationOldToNewLookupRequestJaxbElement = (JAXBElement<BulkCCCOATranslationOldToNewLookupRequestType>) unmarshaller.unmarshal(in);
		BulkCCCOATranslationOldToNewLookupRequestType bulkCCCOATranslationOldToNewLookupRequest = bulkCCCOATranslationOldToNewLookupRequestJaxbElement.getValue();
		
		MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		mockEndpoint.expectedMessageCount(1);

		Map<String, Object> mapHeaders = new HashMap<String, Object>();
		template.requestBodyAndHeaders("direct:bulkCCCOATranslationOldToNewLookup", bulkCCCOATranslationOldToNewLookupRequest, mapHeaders);
		
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
