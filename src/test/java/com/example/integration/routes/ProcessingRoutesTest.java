package com.example.integration.routes;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DisableJmx(true)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith
@ContextConfiguration
public class ProcessingRoutesTest {
	
	@Autowired
	private CamelContext camelContext;

	@EndpointInject("mock:testEndpoint")
	private MockEndpoint mockEndpoint;
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	/*
	 * @Override public void setUp() throws Exception { super.setUp();
	 * RouteDefinition definition = context().getRouteDefinitions().get(0);
	 * definition.adviceWith(context(), new RouteBuilder() {
	 * 
	 * @Override public void configure() throws Exception {
	 * onException(Exception.class).maximumRedeliveries(0); } }); }
	 */

	/*@Override
	protected void doPostSetup() throws Exception {
		
		AdviceWithRouteBuilder.adviceWith(camelContext, "odp-opsys-subscriber-route-1", route -> {

			route.replaceFromWith("direct:odp-opsys-subscriber-route-1");
			route.weaveById("xml-processor-1").replace()
					.process(exchange -> System.out.println("Processing completed by XML Processor..."));
			route.weaveAddLast().to(mockEndpoint);
		});
		
		context.start();
	}*/

	@DirtiesContext
	@Test
	public void testTranslationLookupRequestProcessingRoute() throws Exception {

		/*
		 * JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class); InputStream in
		 * = getClass().getClassLoader().getResourceAsStream(
		 * "data/translationLookupRequest.xml"); Unmarshaller unmarshaller =
		 * jc.createUnmarshaller();
		 * 
		 * @SuppressWarnings("unchecked") JAXBElement<TranslationLookupRequestType>
		 * translationLookupRequestJaxbElement =
		 * (JAXBElement<TranslationLookupRequestType>) unmarshaller.unmarshal(in);
		 * TranslationLookupRequestType translationLookupRequest =
		 * translationLookupRequestJaxbElement.getValue();
		 * 
		 * MockEndpoint mockEndpoint = getMockEndpoint("mock:end");
		 * mockEndpoint.expectedMessageCount(1);
		 * 
		 * Map<String, Object> mapHeaders = new HashMap<String, Object>();
		 * template.requestBodyAndHeaders(
		 * "direct:translationLookupRequestProcessingRoute", translationLookupRequest,
		 * mapHeaders);
		 * 
		 * mockEndpoint.assertIsSatisfied();
		 */
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
