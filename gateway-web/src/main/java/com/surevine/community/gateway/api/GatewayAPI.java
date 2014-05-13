package com.surevine.community.gateway.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.surevine.community.gateway.GatewayProperties;
import com.surevine.community.gateway.Quarantine;
import com.surevine.community.gateway.history.History;
import com.surevine.community.gateway.hooks.GatewayTransferException;
import com.surevine.community.gateway.hooks.Hooks;
import com.surevine.community.gateway.model.Project;
import com.surevine.community.gateway.model.Projects;
import com.surevine.community.gateway.model.TransferItem;

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
		LOG.info("Parsing multipart form.");
		
		byte[] file = null;		
		final Map<String, String> properties = new HashMap<String, String>(form.getFormDataMap().size());
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
		History.getInstance().add(String.format("Received file %s for export.", properties.get("filename")));
		
		// Save file to quarantine.
		final java.nio.file.Path source = Quarantine.save(file, properties);
		
		// Get all possible export destinations (convert from CSV property
		// String to a List of URIs).
		final List<String> urls = new ArrayList<String>(Arrays.asList(GatewayProperties.get(
				GatewayProperties.EXPORT_DESTINATIONS).split(",")));
		final List<URI> destinations = Lists.transform(urls, new Function<String, URI>() {
			@Override
			public URI apply(final String uri) {
				try {
					return new URI(uri);
				} catch (final URISyntaxException e) {
					throw new RuntimeException(String.format(
							"The supplied URI [%s] is invalid.", uri), e);
				}
			}
		});
		
		// Setup transfer queue
		final Set<TransferItem> transferQueue = new HashSet<TransferItem>();
		for (final URI destination : destinations) {
			transferQueue.add(new TransferItem(destination, source,
					new HashMap<String, String>(properties)));
		}
		
		// Call preExport hooks.
		Hooks.callPreExport(transferQueue);
		
		try {
			replaceMetadataFiles(transferQueue);
		}
		catch (IOException e) {
			LOG.warning("Encountered IOException when attempting to replace metadata files.  Aborting transfer");
			return;
		}
		catch (InterruptedException e) {
			LOG.warning("Encountered InterruptedException when attempting to replace metadata files.  Aborting transfer");
			return;
		}
		
		// Configurable delay?
		
		// Call export transfer hooks
		Hooks.callExportTransfer(transferQueue);
		
		// Clean up quarantine.
		//Quarantine.remove(source);
		History.getInstance().add(String.format("Finished exporting %s.", properties.get("filename")));
		
		//FIXME: Send notifications, add UI hooks. 
	}
	
	protected void replaceMetadataFiles(Set<TransferItem> transferQueue) throws IOException, InterruptedException {
	    for (final TransferItem item : transferQueue) {
			final java.nio.file.Path source = item.getSource();
			final Map<String, String> metadata = item.getMetadata();
		    
	        // Extract.
	        LOG.info("Extracting received file.");
	        
	        Runtime.getRuntime().exec(
	                new String[] {"tar", "xzvf", source.toString(), "-C", source.getParent().toString()},
	                new String[] {},
	                source.toFile().getAbsoluteFile().getParentFile()).waitFor();
	
	        // Look for existing metdata.json file
	        LOG.info("Finding existing metadata file.");
	        File metadataFile = new File(source.toFile().getParentFile(), ".metadata.json");
	        
	        
	        if (metadataFile.exists()) {
		        LOG.info("Existing metadata file exists.");

	        	//Create new metadata file
	        	metadataFile.delete();
	        	metadataFile.createNewFile();
	        	PrintStream ps = new PrintStream(new FileOutputStream(metadataFile));
	        	try {
    				final StringBuilder metadataStr = new StringBuilder();
    				metadataStr.append("{");
    				metadataStr.append(String.format("\"repository\": \"%s\",", metadata.get("repository")));
    				metadataStr.append(String.format("\"groupId\": \"%s\",", metadata.get("groupId")));
    				metadataStr.append(String.format("\"artifactId\": \"%s\",", metadata.get("artifactId")));
    				metadataStr.append(String.format("\"version\": \"%s\",", metadata.get("version")));
    				metadataStr.append(String.format("\"packaging\": \"%s\",", metadata.get("packaging")));
    				metadataStr.append(String.format("\"classification\": \"%s\",", metadata.get("classification")));
    				metadataStr.append(String.format("\"decorator\": \"%s\",", metadata.get("decorator")));
    				metadataStr.append(String.format("\"groups\": \"%s\",", metadata.get("groups")));
    				metadataStr.append(String.format("\"countries\": \"%s\",", metadata.get("countries")));
    				metadataStr.append(String.format("\"name\": \"%s\",", metadata.get("name")));
    				metadataStr.append(String.format("\"source_type\": \"%s\",", metadata.get("source_type")));
    				metadataStr.append("}");
    				ps.println(metadataStr);
	        	}
	        	finally {
	        		ps.close();
	       		}
	        	
	        	//Replace file in gzip bundle
		        source.toFile().delete();
		        
		        String[] baseParams=new String[] {"tar", "czvf", source.toString(), "-C", source.getParent().toString()};
		        List<String> gzipParams = new ArrayList<String>(Arrays.asList(baseParams));
		        File[] children = source.getParent().toFile().listFiles();
		        for (File f : children) {
		        	gzipParams.add(f.getName());
		        }
		        
		        LOG.info("Packing command: "+gzipParams.toString());
		        Runtime.getRuntime().exec(
		        		gzipParams.toArray(new String[1]),
		                new String[] {},
		                source.toFile().getAbsoluteFile().getParentFile()).waitFor();
	        }
	        else {
	        	LOG.fine("Metadata file does not exist");
	        }
	    }
	}
}
