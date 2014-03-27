package com.surevine.community.gateway.hooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

public class LabellingNexusDeployImportTransferHook extends NexusDeployImportTransferHook {
	
	private static final Logger LOG = Logger.getLogger(LabellingNexusDeployImportTransferHook.class.getName());
	
	@Override
	public void call(final File[] received, final Map<String, String> properties) {
		deployMainArtifact(received, properties);
		
		deployLabelArtifact(received, properties);
	}
	
	private void deployLabelArtifact(final File[] received, final Map<String, String> properties) {
		final File target = received[0];
		
		// FIXME: Optionally push label.
		final StringBuilder label = new StringBuilder();
		label.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><securityLabel>");
		label.append("<classification>");
		label.append(properties.get(LabelKey.CLASSIFICATION));
		label.append("</classification>");
		label.append("<decorator>");
		label.append(properties.get(LabelKey.DECORATOR));
		label.append("</decorator>");
		label.append("<groups>");
		
		final String[] groups = properties.get(LabelKey.GROUPS).split(",");
		for (final String group : groups) {
			label.append("<group>");
			label.append(group);
			label.append("</groups>");
		}
		
		label.append("</groups");
		label.append("<countries>");

		final String[] countries = properties.get(LabelKey.COUNTRIES).split(",");
		for (final String country : countries) {
			label.append("<country>");
			label.append(country);
			label.append("</country>");
		}
		
		label.append("</countries>");
		label.append("</securityLabel>");
		
		// Write out a security label file.
		try {
			Files.write(Paths.get(target.getParent(), "securitylabel.xml"), label.toString().getBytes());
		} catch (final IOException e1) {
			// FIXME: Handle better
			e1.printStackTrace();
		}
	}
}
