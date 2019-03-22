package com.od.eai.services.ctusoap.util;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class FaultUtil {
	
	public static SoapFault createFault(String code, String message, QName faultCode) {
		SoapFault soapfault = new SoapFault(code, faultCode);
		Element detail = soapfault.getOrCreateDetail();
		Document doc = detail.getOwnerDocument();
		Text tn = doc.createTextNode(message);
		detail.appendChild(tn);
		return soapfault;
	}
	
	public static SoapFault createClientFault(String code, String message) {
		return createFault(code, message, SoapFault.FAULT_CODE_CLIENT);
	}
	
	public static SoapFault createServerFault(String code, String message) {
		return createFault(code, message, SoapFault.FAULT_CODE_SERVER);
	}
	
}
