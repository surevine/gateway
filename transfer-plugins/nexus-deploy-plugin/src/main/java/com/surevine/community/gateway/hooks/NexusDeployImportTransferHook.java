package com.surevine.community.gateway.hooks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.surevine.community.gateway.properties.NexusProperties;

public class NexusDeployImportTransferHook implements GatewayImportTransferHook {
	
	private static final Logger LOG = Logger.getLogger(NexusDeployImportTransferHook.class.getName());
	
	protected void deployMainArtifact(final File[] received, final Map<String, String> properties) {
		LOG.info("Checking properties for Nexus deployment.");
		
		// Generic
		Preconditions.checkArgument(received.length == 1, String.format("%d files received. I only know how to deploy 1 to Nexus.", received.length));
		
		// Maven-specific
		Preconditions.checkNotNull(properties.get(MavenKey.GROUP_ID.toString()), "Group ID must be specified to deploy to Nexus.");
		Preconditions.checkNotNull(properties.get(MavenKey.ARTIFACT_ID.toString()), "Artifact ID must be specified to deploy to Nexus.");
		Preconditions.checkNotNull(properties.get(MavenKey.SOURCE_TYPE.toString()), "Source type must be specified to deploy to Nexus.");
		
		if (properties.get(MavenKey.SOURCE_TYPE.toString()).equalsIgnoreCase("NEXUS")) {
			final File target = received[0];
			
			for (final String destination : NexusProperties.listDestinations()) {
				LOG.info("File is for Nexus deploy. Executing.");
				
				final String url = getRepositoryUrl(destination)
						+properties.get(MavenKey.REPOSITORY.toString());
						
				properties.put(MavenKey.URL.toString(), url);
				
				// Push into Nexus
				final List<String> flags = new ArrayList<String>();
				
				//If no repository ID is specified, default to the "local-releases" repository
				if (!properties.containsKey(MavenKey.REPOSITORY_ID.toString())) {
					properties.put(MavenKey.REPOSITORY_ID.toString(), "local-releases");
				}
				
				for (final MavenKey key : MavenKey.values()) {
					if (properties.containsKey(key.toString())) {
						flags.add(String.format("-D%s=%s", key.toString(), properties.get(key.toString())));
					}
				}
				flags.add(String.format("-Dfile=%s", target.getAbsolutePath()));
				
				final String filename = target.getAbsolutePath();
				
				if (filename.endsWith("-bundle.zip")) {
					flags.add(String.format("-Dclassifier=%s", "bundle"));
				} else if (filename.endsWith("-sources.jar")) {
					flags.add(String.format("-Dclassifier=%s", "sources"));
				} else if (filename.endsWith("-securitylabel.xml")) {
					flags.add(String.format("-Dclassifier=%s", "securitylabel"));
				}
				
				final String[] mvnArgs = new String[(4 +flags.size())];
				mvnArgs[0] = NexusProperties.get(NexusProperties.DEPLOY_SCRIPT);
				mvnArgs[1] = target.getParentFile().toString();
				mvnArgs[2] = "mvn";
				mvnArgs[3] = "deploy:deploy-file";
				for (int i = 0; i<flags.size(); i++) {
					mvnArgs[i+4] = flags.get(i);
				}

				LOG.info("Running command:");
				LOG.info(Joiner.on(" ").join(mvnArgs));
				
				try {
					final Process p = Runtime.getRuntime().exec(
							mvnArgs,
							new String[] {},
							target.getParentFile());
					
					String line;
					final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = br.readLine()) != null) {
						LOG.info(line);
					}
					
					p.waitFor();
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected String getRepositoryUrl(final String destination) {
		return String.format("%s/nexus/content/repositories/",
				NexusProperties.get(destination, NexusProperties.URL_BASE));
	}

	@Override
	public void call(final File[] received, final Map<String, String> properties) {
		deployMainArtifact(received, properties);
	}

	@Override
	public boolean supports(Map<String, String> properties) {
		LOG.info("Does this bundle support nexus transfer?");
		try {
			LOG.info("Source type is: "+properties.get("SOURCE_TYPE"));
			boolean rV= properties.get("SOURCE_TYPE").toString().equalsIgnoreCase("NEXUS");
			LOG.info("Does this class support this artifact? "+rV);
			return rV;
		}
		catch (Exception e) {
			LOG.info("Exception during support method: "+e);
			return false;
		}
	}
}
