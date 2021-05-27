package com.example.integration.aggregators;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component("jmsRollbackAggregationStrategy")
public class JMSRollbackAggregationStrategy implements AggregationStrategy{

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		if (oldExchange == null) {
			if(null != newExchange.getException()) {
				newExchange.setProperty("exception", newExchange.getException());
				newExchange.setException(null);
			}
            return newExchange;
        }
		
		if(null != newExchange.getException()) {
			oldExchange.setProperty("exception", newExchange.getException());
			oldExchange.setException(null);
		}
		
        return oldExchange;
        
	}

}
