package com.od.eai.services.ctusoap.assembler;

import org.apache.camel.Body;

import com.officedepot.eai.service.translationutility.CCCOATranslationNewToOldLookupRequestType;
import com.officedepot.eai.service.translationutility.CCCOATranslationNewToOldLookupResponseType;

public class CCCOATranslationNewToOldLookupAssembler {
	
	public CCCOATranslationNewToOldLookupResponseType assembler(@Body CCCOATranslationNewToOldLookupRequestType request) {
		CCCOATranslationNewToOldLookupResponseType response = new CCCOATranslationNewToOldLookupResponseType();
		response.setAccount(request.getAccount());
		response.setCostCenter(request.getCostCenter());
		response.setEntity(request.getLegalEntity());
		//response.setInterCompany();
		response.setLob(request.getLob());
		response.setLocation(request.getGeo());
		return response;
	}

}
