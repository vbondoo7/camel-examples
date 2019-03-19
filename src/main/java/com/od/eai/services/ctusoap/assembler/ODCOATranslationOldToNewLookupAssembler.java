package com.od.eai.services.ctusoap.assembler;

import org.apache.camel.Body;
import org.springframework.beans.factory.annotation.Autowired;

import com.od.eai.framework.security.authproviders.SaltPasswordGenerator;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupRequestType;
import com.officedepot.eai.service.translationutility.ODCOATranslationOldToNewLookupResponseType;

public class ODCOATranslationOldToNewLookupAssembler {
	
	public ODCOATranslationOldToNewLookupResponseType assembler(@Body ODCOATranslationOldToNewLookupRequestType request) {
		ODCOATranslationOldToNewLookupResponseType response = new ODCOATranslationOldToNewLookupResponseType();
		response.setAccount(request.getAccount());
		response.setCostCenter(request.getCostCenter());
		//response.setEntity(request.getEntity());
		response.setInterCompany(request.getInterCompany());
		response.setLob(request.getLob());
		response.setLocation(request.getLocation());
		return response;
	}
	
}
