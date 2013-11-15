package com.surevine.community.gateway.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

@Path("/gitlab")
public class GitlabAPI {
	
	private static final Logger LOG = Logger.getLogger(GitlabAPI.class.getName());

	@POST
	@Path("/push")
	@Consumes(MediaType.APPLICATION_JSON)
	public void push(final String content) {
		LOG.info("Received gitlab push.");
		
		try {
			final JsonNode json = new ObjectMapper().readTree(content);
			final JsonNode repository = json.get("repository");

			final String projectName = repository.get("name").getTextValue();
			final String url = repository.get("url").getTextValue();

			// TODO: Assert known source system / project?
			
			// Create quarantine if it doesn't exist.
			final String uuid = UUID.randomUUID().toString();
			
			// TODO: Don't hard code path.
			final java.nio.file.Path quarantinePath = Paths.get("/tmp/quarantine/" +uuid);
			if (!Files.exists(quarantinePath)) {
				Files.createDirectory(quarantinePath);
			}
			
			// Git clone.
			Runtime.getRuntime().exec(
					new String[] {"git", "clone", "--mirror", url, projectName},
					new String[] {},
					quarantinePath.toFile()).waitFor();
			
			// Tarball.
			Runtime.getRuntime().exec(
					new String[] {"tar", "cvzf", projectName +".tar.gz", projectName},
					new String[] {},
					quarantinePath.toFile()).waitFor();
			
			LOG.info(String.format("Sending project %s into generic gateway API.", projectName));
			
			// Push into generic API avec metadata.
			pushToGeneric(quarantinePath, projectName, url);
			
			// Clean up delete clone / tarball.
			Files.delete(Paths.get(quarantinePath.toString(), projectName +".tar.gz"));
			Files.delete(quarantinePath);
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "IO exception importing gitlab project.", e);
		} catch (InterruptedException e) {
			LOG.log(Level.SEVERE, "IO exception importing gitlab project.", e);
		}
	}
	
	private void pushToGeneric(final java.nio.file.Path quarantinePath,
			final String projectName, final String projectUrl) {
		final java.nio.file.Path tarball = Paths.get(quarantinePath.toString(), projectName +".tar.gz");
		
		try {
			final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("file", new FileBody(tarball.toFile()));
			entity.addPart("name", new StringBody(projectName));
			entity.addPart("sourceUrl", new StringBody(projectUrl));
			
			// FIXME: Hardcodetastic.
			Request.Post("http://10.66.2.231/gateway/api/projects")
					.body(entity)
					.execute().returnContent().asString();
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Unable to retrieve projects from gitlab.", e);
		}
	}
}
