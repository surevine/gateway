package com.surevine.community.gateway.audit.action.xml;

import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.model.Partner;

public class XMLExportAuditAction extends ExportAuditAction {

	public XMLExportAuditAction(String filename, Partner partner) {
		super(filename, partner);
	}

	@Override
	public String serialize() {
		StringBuilder xml = new StringBuilder();
		xml.append(String.format("<Description>Item '%s' was exported to '%s'</Description>",
				filename,
				partner.getName()) + System.getProperty("line.separator"));
		xml.append("<Export>");
		xml.append(String.format("<Data name=\"partnerName\" value=\"%s\" />", partner.getName()));
		xml.append(String.format("<Data name=\"partnerURI\" value=\"%s\" />", partner.getUri()));
		xml.append(String.format("<Data name=\"fileName\" value=\"%s\" />", filename));
		xml.append("</Export>");

		return xml.toString();
	}

}
