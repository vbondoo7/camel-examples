package com.od.eai.services.ctusoap.assembler;

import org.apache.camel.Body;

import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationOldToNewLookupResponseType;

public class CCCOATranslationOldToNewLookupAssembler {
	
	public CCCOATranslationOldToNewLookupResponseType assembler(@Body CCCOATranslationOldToNewLookupRequestType request) {
		CCCOATranslationOldToNewLookupResponseType response = new CCCOATranslationOldToNewLookupResponseType();
		//response.setAccount(request.getAccount());
		response.setCostCenter(request.getCostCenter());
		response.setEntity(request.getLegalEntity());
		//response.setInterCompany(request.get);
		response.setLob(request.getLob());
		response.setLocation(request.getGeo());
		return response;
	}

}
