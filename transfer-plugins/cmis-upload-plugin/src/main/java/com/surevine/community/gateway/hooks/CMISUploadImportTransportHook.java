package com.surevine.community.gateway.hooks;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.surevine.community.gateway.properties.CMISProperties;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
 
public class CMISUploadImportTransportHook implements GatewayImportTransferHook {
	
	private static final Logger LOG = Logger.getLogger(CMISUploadImportTransportHook.class.getName());
	
	protected void deployMainArtifact(final File[] received, final Map<String, String> propertiesIn) {
		
		if (received.length>1) {
			LOG.warning(received.length+" files were found, but only one ("+received[0].getName()+") will be processed");
		}
		
		Preconditions.checkArgument(received.length > 0, "No files recieved.  Must be one file to deploy to Alfresco");
		
		for (final String destination : CMISProperties.listDestinations()) {
			try {
		           File file = received[0];
		            
		            for (int i=0; i < received.length; i++) {
		            	if (received[i].isFile() && !received[i].getName().startsWith(".")) {
		            		file=received[i];
		            	}
		            }
		            
				String target= "http://"+CMISProperties.get(destination, CMISProperties.HOST)+":"+CMISProperties.get(destination, CMISProperties.PORT)+"/alfresco/service/api/cmis";
				String userName = CMISProperties.get(destination, CMISProperties.USERNAME);
				String password = CMISProperties.get(destination, CMISProperties.PASSWORD);
	            String siteId = CMISProperties.get(destination, CMISProperties.SITE);
	            String uploadDir=CMISProperties.get(destination, CMISProperties.FOLDER);
	            if (uploadDir==null) {
	            	uploadDir="";
	            }
	            String targetFolder="/Sites/"+siteId+"/documentLibrary/"+uploadDir;
	            
	            LOG.info("Sending "+file+" to "+target+":"+targetFolder);
	            
	            // default factory implementation
	            SessionFactory factory = SessionFactoryImpl.newInstance();
	            Map<String, String> parameter = new HashMap<String, String>();
	
	            // user credentials
	            parameter.put(SessionParameter.USER, userName);
	            parameter.put(SessionParameter.PASSWORD, password);
	
	            // connection settings
	            parameter.put(SessionParameter.ATOMPUB_URL, target);
	            parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
	            parameter.put(SessionParameter.REPOSITORY_ID, "myRepository");
	
	            // create session
	            Session session = factory.createSession(parameter);
	            
	            // properties 
	            // (minimal set: name and object type id)
	            String name = file.getName();
	            Map<String, Object> CMISProperties = new HashMap<String, Object>();
	            CMISProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
	            CMISProperties.put(PropertyIds.NAME, name);
	
	 
	            
	            try {
	            	byte[] content = Files.toByteArray(file); 
	            	InputStream stream = new ByteArrayInputStream(content);
	            	ContentStream contentStream = new ContentStreamImpl(name, BigInteger.valueOf(content.length), "text/plain", stream);
	
	            	Folder parent = (Folder)(session.getObjectByPath(targetFolder));
	
	            	//TODO:  Find a better way than exception handling to determine if this exists
	            	try {
	            		Document existing = (Document)(session.getObjectByPath(targetFolder+"/"+file.getName()));
	            		existing.setContentStream(contentStream, true);
	            	}
	            	catch (CmisObjectNotFoundException e) { //Means object does not exist, so create it
	                	parent.createDocument(CMISProperties, contentStream, VersioningState.MAJOR);
	            	}
	            }
	            catch (IOException e) {
	            		LOG.warning("Could not read from content file due to: "+e);
	            		e.printStackTrace();
	            }
			}
			catch (CmisBaseException e) {
				LOG.warning("CMIS exception when attempting to upload to destination "+destination+".  See stderr for details");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void call(final File[] received, final Map<String, String> properties) {
		deployMainArtifact(received, properties);
	}

	@Override
	public boolean supports(Map<String, String> properties) {
		LOG.info("Source type is: "+properties.get("SOURCE_TYPE"));
		boolean rV= properties.get("SOURCE_TYPE").toString().equalsIgnoreCase("ALFRESCO");
		LOG.info("Does this class support this artifact? "+rV);
		return rV;
	}
}
