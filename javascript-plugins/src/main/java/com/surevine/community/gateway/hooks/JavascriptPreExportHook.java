package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
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
	    for (final String hook : hooks) {
	    	LOG.info(String.format("STARTING javascript hook [%s].", hook));
	    	
		    for (final URI destination : destinations) {
		    	LOG.info("Processing destination " +destination);
		    	
			    final Rule rule = new Rule();
			    
			    jsEngine.put("Rules", rule);
			    jsEngine.put("source", source);
			    jsEngine.put("metadata", properties);
			    jsEngine.put("destination", destination.toString());
			    
			    try {
					jsEngine.eval(new InputStreamReader(getClass().getResourceAsStream(hook)));
				} catch (final ScriptException e) {
					rule.mandate(false, "Marking rule as failed due to " +e.getMessage());
					
					e.printStackTrace(); // FIXME: Handle
				}
			    
			    if (!rule.isAllowed() & destinations.contains(destination)) {
			    	System.out.println(String.format(
			    			"Destination %s did not pass export rules for %s.",
			    			destination, source));
			    	toRemove.add(destination);
			    }
		    }
    	  LOG.info(String.format("COMPLETE javascript hook [%s].", hook));
	    }
    	destinations.removeAll(toRemove);
	}
}
