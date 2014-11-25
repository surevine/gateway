package com.surevine.community.gateway.audit.action;

public class ImportAuditAction implements AuditAction {

	private String filename;
	private String source;

	public ImportAuditAction(String filename, String source) {
		this.filename = filename;
		this.source = source;
	}

	@Override
	public String serialize() {
		StringBuilder xml = new StringBuilder();
		xml.append(String.format("<Description>Item '%s' was imported from '%s'</Description>",
				filename, source) + System.getProperty("line.separator"));
		xml.append("<Import>");
		xml.append(String.format("<Data name=\"filename\" value=\"%s\" />", filename));
		xml.append(String.format("<Data name=\"source\" value=\"%s\" />", source));
		xml.append("</Import>");

		return xml.toString();
	}

}
