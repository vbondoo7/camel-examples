package com.example.integration.assembler;

import javax.xml.ws.Holder;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;
import org.tempuri.Add;
import org.tempuri.Subtract;

@Component("requestAssembler")
public class RequestAssembler {

	@Handler
	public Add assembleForAdd(Exchange exchange) {
		
		Object[] args =  exchange.getIn().getBody(Object[].class);

        /*Holder<Integer> intA = (Holder<Integer>) args[0];
        Holder<Integer> intB = (Holder<Integer>) args[1];*/
		Add obj = new Add();
		obj.setIntA((int)args[0]);
		obj.setIntB((int)args[1]);
		return obj;
	}
	
	public Subtract assembleForSubtract(Exchange exchange) {
		Object[] args =  exchange.getIn().getBody(Object[].class);
		
		Subtract obj = new Subtract();
		obj.setIntA((int)args[0]);
		obj.setIntB((int)args[1]);
		return obj;
	}
}
