package com.example.integration.routes;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.stereotype.Component;


/**
 * @author saxena.vishal1986
 *
 */
@Component("jmsInboundRoutes")
public class JMSInboundRoutes extends EndpointRouteBuilder {
	
	private void preConfigure() {
		
		JmsConnectionFactory conn = new JmsConnectionFactory();
		conn.setRemoteURI("amqp://localhost:61616");
		conn.setUsername("admin");
		conn.setPassword("admin");

		PooledConnectionFactory poolConn = new PooledConnectionFactory();
		poolConn.setConnectionFactory(conn);
		poolConn.setMaxConnections(1);
		poolConn.setMaximumActiveSessionPerConnection(10);
		
		JmsComponent jms = ComponentsBuilderFactory.jms()
				//.acknowledgementModeName("CLIENT_ACKNOWLEDGE")
				.connectionFactory(poolConn)
				.disableReplyTo(true)
				.testConnectionOnStartup(true)
				.build(getContext());

		getContext().addComponent("jms", jms);
	}
	
	@Override
	public void configure() throws Exception {

		preConfigure();
		
		configureExceptions();
		
		from(jms("jms", "test.queue").transacted(true))
			.routeId("jms-consumer-route")
			.log(LoggingLevel.INFO,"AMQ Payload received : ${body}")
			.to(JMSProcessingRoutes.JMS_PROCESSING_ROUTE);
			
	}
	
	private void configureExceptions() {
		onException(Exception.class)
			.id("exceptionJMSInboundRoute")
			.useOriginalMessage()
			//.maximumRedeliveries(2).redeliveryDelay(1000L)
			.log(LoggingLevel.ERROR, "Exception occurred in jmsInboundRoute : ${exception}");
	}

}
