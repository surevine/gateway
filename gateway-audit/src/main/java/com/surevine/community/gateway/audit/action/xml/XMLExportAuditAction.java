package com.surevine.community.gateway.audit.action.xml;

import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.model.Destination;

public class XMLExportAuditAction extends ExportAuditAction {

	public XMLExportAuditAction(String filename, Destination destination) {
		super(filename, destination);
	}

	@Override
	public String serialize() {
		StringBuilder xml = new StringBuilder();
		xml.append(String.format("<Description>Item '%s' was exported to '%s'</Description>",
				filename,
				destination.getName()) + System.getProperty("line.separator"));
		xml.append("<Export>");
		xml.append(String.format("<Data name=\"destinationName\" value=\"%s\" />", destination.getName()));
		xml.append(String.format("<Data name=\"destinationURI\" value=\"%s\" />", destination.getUri()));
		xml.append(String.format("<Data name=\"fileName\" value=\"%s\" />", filename));
		xml.append("</Export>");

		return xml.toString();
	}

}
