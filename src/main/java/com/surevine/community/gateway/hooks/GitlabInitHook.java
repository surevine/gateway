package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.surevine.community.gateway.GatewayProperties;
import com.surevine.community.gateway.model.Project;
import com.surevine.community.gateway.model.Projects;

public class GitlabInitHook implements GatewayInitHook {
	
	private static final Logger LOG = Logger.getLogger(GitlabInitHook.class.getName());

	@Override
	public void call() {
		// Call gitlab sources for list of projects.
		final String url = GatewayProperties.get(GatewayProperties.GITLAB_PROTOCOL) +
				"://" +
				GatewayProperties.get(GatewayProperties.GITLAB_HOST) +
				":" +
				GatewayProperties.get(GatewayProperties.GITLAB_PORT) +
				GatewayProperties.get(GatewayProperties.GITLAB_CONTEXT) +
				"/api/" +
				GatewayProperties.get(GatewayProperties.GITLAB_VERSION) +
				"/projects?private_token=" +
				GatewayProperties.get(GatewayProperties.GITLAB_TOKEN);
		
		LOG.finest("Connecting to URL " +url);
		
		String content = null;
		
		try {
			content = Request.Get(url).execute().returnContent().asString();
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Unable to retrieve projects from gitlab.", e);
		}
		
		// Parse response
		if (content == null) throw new RuntimeException("barf");

		LOG.finest("Response content: " +content);
		
		// Update cache of projects. This wants re-running periodically.
		try {
			final JsonNode json = new ObjectMapper().readTree(content);
			
			for (final JsonNode project : json) {
				final Project p = new Project();
				p.setName(project.get("name").getTextValue());
				p.setDescription(project.get("description").getTextValue());
				p.setSourceUrl(project.get("ssh_url_to_repo").getTextValue());
				p.setWebUrl(project.get("web_url").getTextValue());
				p.setSecurityLabel(project.get("security_label").getTextValue());
				
				Projects.put(p);
			}
			LOG.info("Total projects: " +Projects.get().size());
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Unable to parse projects from gitlab.", e);
		}
	}
}
