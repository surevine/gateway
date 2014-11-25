package com.surevine.community.gateway.audit.action;

import com.surevine.community.gateway.model.Destination;

/**
 * Represents an item being exported through gateway.
 *
 * @author jonnyheavey
 *
 */
public class ExportAuditAction implements AuditAction {

	private String filename;
	private Destination destination;

	public ExportAuditAction(String filename, Destination destination) {
		this.filename = filename;
		this.destination = destination;
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
