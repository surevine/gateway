package com.surevine.community.gateway.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.google.common.io.ByteStreams;
import com.surevine.community.gateway.Quarantine;
import com.surevine.community.gateway.hooks.GatewayTransferException;
import com.surevine.community.gateway.hooks.Hooks;
import com.surevine.community.gateway.model.Project;
import com.surevine.community.gateway.model.Projects;

@Path("/export")
public class GatewayAPI {
	
	private static final Logger LOG = Logger.getLogger(GatewayAPI.class.getName());
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Project> getProjects() {
		return new ArrayList<Project>(Projects.get());
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Project getProject(final @PathParam("id") String id) {
		return Projects.get(id);
	}
	
	@PUT
	@Path("/enabled/{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void setEnabled(@PathParam("name") final String name,
			@FormParam("enabled") final Boolean enabled) {
		final Project p = Projects.get(name);
		p.setEnabled(enabled);
		
		Projects.put(p);
	}

	/**
	 * Generic method of submitting content for export.
	 * 
	 * Uses a multipart form upload. Invokeable:
	 * 
	 * curl -X POST -F "filename=myproject.tar.gz" -F "file=@myproject.tar.gz" http://gateway/gateway/api/export/
	 * 
	 * The content must be a gzipped tarball and must contain a file named
	 * .metadata.json with key-value metadata properties in it.
	 */
	@POST
	@Path("/")
	public void upload(final MultipartFormDataInput form) throws IOException, GatewayTransferException, URISyntaxException {		
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
		
		LOG.info(String.format("Importing %s", properties.get("filename")));
		
		// Save file to quarantine.
		final java.nio.file.Path source = Quarantine.save(file, properties);
		
		// Get all possible export destinations.
		// We're hardcoding our own file import destination to export to.
		// N.B. Arrays.asList returns a fixed length list that you cannot remove
		// items from - which we need to allow preExport plugins to do, hence
		// the seemingly superfluous new ArrayList();
		final List<URI> destinations = new ArrayList<URI>(Arrays.asList(new URI[] {
				new URI("file:///tmp/import-quarantine") //FIXME: Don't hard code
		}));
		
		// Call preExport hooks.
		Hooks.callPreExport(source, properties, destinations);
		
		// Configurable delay?
		
		// Call export transfer hooks
		Hooks.callExportTransfer(source, properties, destinations.toArray(new URI[]{}));
		
		// Clean up quarantine.
		Quarantine.remove(source);
		
		//FIXME: Send notifications, add UI hooks. 
	}
}
