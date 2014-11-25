package com.surevine.community.gateway.audit.action;

import java.nio.file.Path;

import com.surevine.community.gateway.model.Destination;

/**
 * Represents an export rule failure occurence.
 *
 * @author jonnyheavey
 *
 */
public class RuleFailAction implements AuditAction  {

	Path source;
	Destination destination;

	public RuleFailAction(Path source, Destination destination) {
		this.source = source;
		this.destination = destination;
	}

	@Override
	public String serialize() {
		StringBuilder xml = new StringBuilder();
		xml.append(String.format("<Description>Item '%s' failed export rules for destination '%s'</Description>",
				source.getFileName().toString(),
				destination.getName()) + System.getProperty("line.separator"));
		xml.append("<Export>");
		xml.append("<Outcome>");
		xml.append("<Data Name=\"result\" Value=\"failure\">");
		xml.append("<Data Name=\"reason\" Value=\"export rule file failure\" />");
		xml.append("</Data>");
		xml.append("</Outcome>");
		xml.append(String.format("<Data Name=\"destinationName\" Value=\"%s\" />", destination.getName()));
		xml.append(String.format("<Data Name=\"fileName\" Value=\"%s\" />", source.getFileName().toString()));
		xml.append("</Export>");

		return xml.toString();
	}

}
