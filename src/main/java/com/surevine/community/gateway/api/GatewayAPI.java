package com.surevine.community.gateway.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.google.common.io.ByteStreams;
import com.surevine.community.gateway.JavaScriptExportFilter;
import com.surevine.community.gateway.Metadata;
import com.surevine.community.gateway.Quarantine;
import com.surevine.community.gateway.hooks.GatewayTransferException;
import com.surevine.community.gateway.hooks.Hooks;
import com.surevine.community.gateway.model.Project;
import com.surevine.community.gateway.model.Projects;

@Path("/projects")
public class GatewayAPI {
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Project> getProjects() {
		return new ArrayList<Project>(Projects.get());
	}
	
	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Project getProject(final @PathParam("name") String name) {
		return Projects.get(name);
	}

	@POST
	@Path("/")
	public void upload(final MultipartFormDataInput form) throws IOException, GatewayTransferException {		
		final Map<String, String> properties = new HashMap<String, String>(form.getFormDataMap().size());
		
		byte[] file = null;
		
		final Map<String, List<InputPart>> data = form.getFormDataMap();
		for (final String key : data.keySet()) {
			if (key.equals("file")) {
				final InputPart part = data.get(key).get(0);
				
	            final Pattern p = Pattern.compile("filename=\"(.*)\""); 
	            final Matcher m = p.matcher(part.getHeaders().getFirst("Content-Disposition"));            
	            if (m.find()) {
	                properties.put("filename", m.group(1));
	            }
				
				file = ByteStreams.toByteArray(part.getBody(InputStream.class, null));
			} else {
				final List<InputPart> inputs = data.get(key);
				
				for (final InputPart part : inputs) { // If there are two values for the same key we're borked.
					if (part.getMediaType().isCompatible(MediaType.TEXT_PLAIN_TYPE)) {
						properties.put(key, part.getBodyAsString());
					}
				}
			}
		}

		// Save file to quarantine.
		final java.nio.file.Path source = Quarantine.save(file, properties);
		
		// Call pre-receive hooks.
		Hooks.callPreReceive(source, properties);
		
		// If pre-receive hooks haven't thrown exceptions, queue files.
		
		// Get all possible export destinations.
		final URI[] destinations = null;
		
		// Run rules engine to see if and where we're going to send it.
		final URI[] filteredDestinations = JavaScriptExportFilter.filter(source, properties);
		
		// Call transfer hooks with sanitised properties after delay.
		Hooks.callTransfer(source, Metadata.sanitise(properties), filteredDestinations);
		
		// Send notifications.
	}
}
