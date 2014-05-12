package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.Iterator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.surevine.community.gateway.model.Rule;
import com.surevine.community.gateway.model.TransferItem;
import com.surevine.community.gateway.util.Redis;

public class JavascriptPreExportHook implements GatewayPreExportHook {
	
	private static final Logger LOG = Logger.getLogger(JavascriptPreExportHook.class.getName());

	@Override
	public void call(final Set<TransferItem> transferQueue) {
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
	    final Properties config = new Properties();
	    try {
			config.load(getClass().getResourceAsStream("/javascript-hook.properties"));
		} catch (final IOException e1) {
			e1.printStackTrace(); // FIXME: Handle
		}

	    final String[] hooks = config.getProperty("preexport.configurations").split(",");
	    
	    for (final TransferItem item : transferQueue) {
			final Path source = item.getSource();
			final Map<String, String> metadata = item.getMetadataForModification();
			final URI destination = item.getDestination();
	    	LOG.info("Processing destination " +destination);
	    	
	    	for (final String hook : hooks) {
	    		LOG.info(String.format("STARTING javascript hook [%s].", hook));
		    	
			    final Rule rule = new Rule();
			    
			    jsEngine.put("Rules", rule);
			    jsEngine.put("Redis", new Redis("localhost"));
			    jsEngine.put("source", source);
			    jsEngine.put("metadata", metadata);
			    jsEngine.put("destination", destination.toString());
			    
			    try {
					jsEngine.eval(new InputStreamReader(getClass().getResourceAsStream(hook)));
				} catch (final Exception e) {
					rule.mandate(false, "Marking rule as failed due to " +e.getMessage());
					
					e.printStackTrace(); // FIXME: Handle
				}
			    
				final Map<String, String> metadata = item.getMetadata();
				Iteratotor<Sting> keySet = metadata.keySet().iterator();
				while (keySet.hasNext()) {
					String key = keySet.next();
					LOG.info("Metadata field: "+key+" : "+metadata.get(key));
				}
			    
			    
			    if (!rule.isAllowed()) {
			    	LOG.info(String.format(
			    			"Destination %s did not pass export rules for %s.",
			    			destination, source));
			    	item.setNotExportable();
			    	
			    	break; // Do not continue evaluating hook scripts for this destination, we're not sending the artifact.
			    }

		    	LOG.info(String.format("COMPLETE javascript hook [%s].", hook));
		    }
	    }
	}
}
