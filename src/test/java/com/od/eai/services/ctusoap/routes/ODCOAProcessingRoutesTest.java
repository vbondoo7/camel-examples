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

import com.officedepot.eai.service.translationutility.BulkODCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.BulkODCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupResponseType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupResponseType;
import com.officedepot.eai.service.translationutility.ObjectFactory;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DisableJmx(true)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith
public class ODCOAProcessingRoutesTest extends CamelTestSupport {
	
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
		
		context.getRouteDefinition("eaiaudit_ODCOATranslationOldToNewLookupRoute").adviceWith(context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						weaveAddLast().to("mock:end");
						String elasticResponse = getTestJsonResponse("data/OdCoaOldToNewLookupResponse.json");
						interceptSendToEndpoint("direct:hystrixEnabledCTUInternalRoute")
							.skipSendToOriginalEndpoint().setBody(constant(elasticResponse));
					}
				});
		
		context.getRouteDefinition("eaiaudit_BulkODCOATranslationOldToNewLookupRoute").adviceWith(context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						weaveAddLast().to("mock:end");
						String elasticResponse = getTestJsonResponse("data/bulkOdCoaOldToNewLookupResponse.json");
						interceptSendToEndpoint("direct:hystrixEnabledCTUInternalRoute")
							.skipSendToOriginalEndpoint().setBody(constant(elasticResponse));
					}
				});
		
		context.getRouteDefinition("eaiaudit_ODCOATranslationNewToOldLookupRoute").adviceWith(context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						weaveAddLast().to("mock:end");
						String elasticResponse = getTestJsonResponse("data/OdCoaNewToOldLookupResponse.json");
						interceptSendToEndpoint("direct:hystrixEnabledCTUInternalRoute")
							.skipSendToOriginalEndpoint().setBody(constant(elasticResponse));
					}
				});
		
		context.getRouteDefinition("eaiaudit_BulkODCOATranslationNewToOldLookupRoute").adviceWith(context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						weaveAddLast().to("mock:end");
						String elasticResponse = getTestJsonResponse("data/bulkOdCoaNewToOldLookupResponse.json");
						interceptSendToEndpoint("direct:hystrixEnabledCTUInternalRoute")
							.skipSendToOriginalEndpoint().setBody(constant(elasticResponse));
					}
				});
		
		context.start();
	}

	@Test
	public void testODCOATranslationOldToNewLookupRoute() throws Exception {

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		InputStream in = getClass().getClassLoader().getResourceAsStream("data/OdCoaOldToNewLookupRequest.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<ODCOATranslationOldToNewLookupRequestType> odCOATranslationOldToNewLookupRequestJaxbElement = (JAXBElement<ODCOATranslationOldToNewLookupRequestType>) unmarshaller.unmarshal(in);
		ODCOATranslationOldToNewLookupRequestType odCOATranslationOldToNewLookupRequest = odCOATranslationOldToNewLookupRequestJaxbElement.getValue();
		
		MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		mockEndpoint.expectedMessageCount(1);

		Map<String, Object> mapHeaders = new HashMap<String, Object>();
		template.requestBodyAndHeaders("direct:ODCOATranslationOldToNewLookup", odCOATranslationOldToNewLookupRequest, mapHeaders);
		
		mockEndpoint.assertIsSatisfied();
		
		ODCOATranslationOldToNewLookupResponseType expectedResponse = new ODCOATranslationOldToNewLookupResponseType();
		expectedResponse.setLob("21");
		expectedResponse.setLocation("06704,06705,06706,06707,06708,06709,06710,06711,06712,06713");
		
		ODCOATranslationOldToNewLookupResponseType actualResponse = mockEndpoint.getExchanges().get(0).getIn().getBody(ODCOATranslationOldToNewLookupResponseType.class);
		assertEquals("Matched with expected response.", expectedResponse.getLob(), actualResponse.getLob());
		assertEquals("Matched with expected response.", expectedResponse.getLocation(), actualResponse.getLocation());
	}
	
	@Test
	public void testBulkODCOATranslationOldToNewLookupRoute() throws Exception {

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		InputStream in = getClass().getClassLoader().getResourceAsStream("data/bulkOdCoaOldToNewLookupRequest.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<BulkODCOATranslationOldToNewLookupRequestType> bulkODCOATranslationOldToNewLookupRequestJaxbElement = (JAXBElement<BulkODCOATranslationOldToNewLookupRequestType>) unmarshaller.unmarshal(in);
		BulkODCOATranslationOldToNewLookupRequestType bulkODCOATranslationOldToNewLookupRequest = bulkODCOATranslationOldToNewLookupRequestJaxbElement.getValue();
		
		MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		mockEndpoint.expectedMessageCount(1);

		Map<String, Object> mapHeaders = new HashMap<String, Object>();
		template.requestBodyAndHeaders("direct:BulkODCOATranslationOldToNewLookup", bulkODCOATranslationOldToNewLookupRequest, mapHeaders);
		
		mockEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void testODCOATranslationNewToOldLookupRoute() throws Exception {

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		InputStream in = getClass().getClassLoader().getResourceAsStream("data/OdCoaNewToOldLookupRequest.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<ODCOATranslationNewToOldLookupRequestType> odCOATranslationNewToOldLookupRequestJaxbElement = (JAXBElement<ODCOATranslationNewToOldLookupRequestType>) unmarshaller.unmarshal(in);
		ODCOATranslationNewToOldLookupRequestType odCOATranslationNewToOldLookupRequestType = odCOATranslationNewToOldLookupRequestJaxbElement.getValue();
		
		MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		mockEndpoint.expectedMessageCount(1);

		Map<String, Object> mapHeaders = new HashMap<String, Object>();
		template.requestBodyAndHeaders("direct:ODCOATranslationNewToOldLookup", odCOATranslationNewToOldLookupRequestType, mapHeaders);
		
		mockEndpoint.assertIsSatisfied();
		
		ODCOATranslationNewToOldLookupResponseType expectedResponse = new ODCOATranslationNewToOldLookupResponseType();
		expectedResponse.setLob("21");
		
		ODCOATranslationNewToOldLookupResponseType actualResponse = mockEndpoint.getExchanges().get(0).getIn().getBody(ODCOATranslationNewToOldLookupResponseType.class);
		assertEquals("Matched with expected response.", expectedResponse.getLob(), actualResponse.getLob());
	}
	
	@Test
	public void testBulkODCOATranslationNewToOldLookupRoute() throws Exception {

		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
		InputStream in = getClass().getClassLoader().getResourceAsStream("data/bulkOdCoaNewToOldLookupRequest.xml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<BulkODCOATranslationNewToOldLookupRequestType> bulkODCOATranslationNewToOldLookupRequestTypeJaxbElement = (JAXBElement<BulkODCOATranslationNewToOldLookupRequestType>) unmarshaller.unmarshal(in);
		BulkODCOATranslationNewToOldLookupRequestType bulkODCOATranslationNewToOldLookupRequest = bulkODCOATranslationNewToOldLookupRequestTypeJaxbElement.getValue();
		
		MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		mockEndpoint.expectedMessageCount(1);

		Map<String, Object> mapHeaders = new HashMap<String, Object>();
		template.requestBodyAndHeaders("direct:BulkODCOATranslationNewToOldLookup", bulkODCOATranslationNewToOldLookupRequest, mapHeaders);
		
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
