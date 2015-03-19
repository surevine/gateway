package com.surevine.community.gateway.audit.action.xml;

import java.nio.file.Path;

import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.model.Partner;

public class XMLRuleFailAuditAction extends RuleFailAuditAction {

	public XMLRuleFailAuditAction(Path source, Partner partner) {
		super(source, partner);
	}

	@Override
	public String serialize() {
		StringBuilder xml = new StringBuilder();
		xml.append(String.format("<Description>Item '%s' failed export rules for partner '%s'</Description>",
				source.getFileName().toString(),
				partner.getName()) + System.getProperty("line.separator"));
		xml.append("<Export>");
		xml.append("<Outcome>");
		xml.append("<Data Name=\"result\" Value=\"failure\" />");
		xml.append("<Data Name=\"reason\" Value=\"export rule file failure\" />");
		xml.append("</Outcome>");
		xml.append(String.format("<Data Name=\"partnerName\" Value=\"%s\" />", partner.getName()));
		xml.append(String.format("<Data Name=\"fileName\" Value=\"%s\" />", source.getFileName().toString()));
		xml.append("</Export>");

		return xml.toString();
	}

}
