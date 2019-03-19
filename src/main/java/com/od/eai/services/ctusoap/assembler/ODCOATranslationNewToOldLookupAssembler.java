package com.od.eai.services.ctusoap.assembler;

import org.apache.camel.Body;

import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationNewToOldLookupResponseType;

public class ODCOATranslationNewToOldLookupAssembler {
	
	public ODCOATranslationNewToOldLookupResponseType assembler(@Body ODCOATranslationNewToOldLookupRequestType request) {
		ODCOATranslationNewToOldLookupResponseType response = new ODCOATranslationNewToOldLookupResponseType();
		response.setAccount(request.getAccount());
		response.setCostCenter(request.getCostCenter());
		//response.setEntity(request.getEntity());
		response.setInterCompany(request.getInterCompany());
		response.setLob(request.getLob());
		response.setLocation(request.getLocation());
		//response.setFuture(request.get);
		return response;
	}

}
