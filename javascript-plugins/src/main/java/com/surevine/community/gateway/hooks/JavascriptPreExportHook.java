package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.surevine.community.gateway.model.Rule;
import com.surevine.community.gateway.util.Redis;

public class JavascriptPreExportHook implements GatewayPreExportHook {
	
	private static final Logger LOG = Logger.getLogger(JavascriptPreExportHook.class.getName());

	@Override
	public void call(final Path source, final Map<String, String> properties,
			final List<URI> destinations) {
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
	    final Properties config = new Properties();
	    try {
			config.load(getClass().getResourceAsStream("/javascript-hook.properties"));
		} catch (final IOException e1) {
			e1.printStackTrace(); // FIXME: Handle
		}

    	final Set<URI> toRemove = new HashSet<URI>(destinations.size());
	    final String[] hooks = config.getProperty("preexport.configurations").split(",");
	    
	    // FIXME: Is this the wrong way round, should loop through destinations then hooks?
	    // That way we could skip other hooks if one fails.
	    for (final URI destination : destinations) {
	    	LOG.info("Processing destination " +destination);
	    	
	    	// Metadata is from scratch per destination.
	    	// This means we can sanitize it (e.g. remove security groups) for one destination
	    	// but still use them for the next.
	    	final Map<String, String> metadata = new HashMap<>();
	    	metadata.putAll(properties);
	    	
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
			    
			    if (!rule.isAllowed() & destinations.contains(destination)) {
			    	System.out.println(String.format(
			    			"Destination %s did not pass export rules for %s.",
			    			destination, source));
			    	toRemove.add(destination);
			    	
			    	break; // Do not continue evaluating hook scripts for this destination, we're not sending the artifact.
			    }

		    	LOG.info(String.format("COMPLETE javascript hook [%s].", hook));
		    }
	    }
    	destinations.removeAll(toRemove);
	}
}
