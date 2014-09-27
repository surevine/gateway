package com.surevine.community.gateway.hooks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.surevine.community.gateway.model.TransferItem;

public abstract class MetadataPersistingTransportHook implements GatewayExportTransferHook {

	private static final Logger LOG = Logger.getLogger(MetadataPersistingTransportHook.class.getName());

	
	public void call(final Set<TransferItem> transferQueue) {
		for (final TransferItem item : transferQueue) {
			final Path source = item.getSource();
			final Map<String, String> metadata = item.getMetadata();
			final URI destination = item.getDestination();
			replaceMetadataFiles(item);
			transferSingleItem(item);
		}
	}
	
	public abstract void transferSingleItem(TransferItem item);
	
	protected void replaceMetadataFiles(final TransferItem item) throws IOException, InterruptedException {
		final Path source = item.getSource();
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
				Iterator<String> props = metadata.keySet().iterator();
				while (props.hasNext()) {
					String s = props.next();
					metadataStr.append("\"").append(s).append("\" : \"").append(metadata.get(s)).append("\"");
					if (props.hasNext()) {
						metadataStr.append(",");
					}
				}
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
