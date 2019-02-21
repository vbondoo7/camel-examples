package com.od.eai.services.ctusoap.assembler;

import com.officedepot.eai.service.translationutility.ResultDocType;
import com.officedepot.eai.service.translationutility.ResultType;
import com.officedepot.eai.service.translationutility.StatusType;
import com.officedepot.eai.service.translationutility.TranslationLookupRequestType;
import com.officedepot.eai.service.translationutility.TranslationLookupResponseType;

public class ResponseAssembler {
	
	public TranslationLookupResponseType assemble(TranslationLookupRequestType request) {
		TranslationLookupResponseType response = new TranslationLookupResponseType();
		ResultType resultType = new ResultType();
		ResultDocType resultDoc = new ResultDocType();
		resultDoc.setTargetField1(request.getCriteria().getSourceField1().toUpperCase());
		resultDoc.setTargetField2(request.getCriteria().getSourceField2().toUpperCase());
		resultDoc.setTargetField3(request.getCriteria().getSourceField3().toUpperCase());
		resultDoc.setTargetField5(request.getCriteria().getSourceField4().toUpperCase());
		resultDoc.setTargetField6(request.getCriteria().getSourceField5().toUpperCase());
		resultDoc.setTargetField7(request.getCriteria().getSourceField6().toUpperCase());
		resultDoc.setTargetField8(request.getCriteria().getSourceField7().toUpperCase());
		resultDoc.setTargetField9(request.getCriteria().getSourceField8().toUpperCase());
		resultDoc.setTargetField10(request.getCriteria().getSourceField9().toUpperCase());
		resultDoc.setTargetField11(request.getCriteria().getSourceField10().toUpperCase());
		resultDoc.setTargetField12(request.getHeader().getConsumer());
		resultDoc.setTargetField13(request.getCriteria().getTranslationName());
		resultDoc.setTargetField14(request.getCriteria().getTranslationId());
		resultDoc.setTargetField15("SEGMENT1");
		resultDoc.setTargetField16("SEGMENT2");
		resultDoc.setTargetField17("SEGMENT3");
		resultDoc.setTargetField18("SEGMENT4");
		resultDoc.setTargetField19("SEGMENT5");
		resultDoc.setTargetField20("SEGMENT6");
		
		StatusType status = new StatusType();
		status.setStatus("Success");
		status.setStatusCode(00);
		
		resultType.setStatus(status);
		resultType.setResult(resultDoc);
		response.setResult(resultType);
		return response;
	}

}
