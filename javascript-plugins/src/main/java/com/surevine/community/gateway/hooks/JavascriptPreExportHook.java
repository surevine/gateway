package com.surevine.community.gateway.hooks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.Iterator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.surevine.community.gateway.model.Destination;
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

	    for (final TransferItem item : transferQueue) {

			final Path source = item.getSource();
			final Map<String, String> metadata = item.getMetadata();
			final Destination destination = item.getDestination();

	    	LOG.info(String.format("Processing destination %s [%s]", destination.getName(), destination.getUri().toString()));

		    Set<String> ruleFiles = new HashSet<String>();
		    try {
		    	ruleFiles = loadRuleFiles(destination, config);
		    }
		    catch(FileNotFoundException e) {
		    	LOG.warning(String.format("Could not load all rule files for destination %s [%s]. Skipping item export to destination.",
		    				destination.getName(),
		    				destination.getId()));
		    	continue;
		    }

	    	for (final String ruleFile : ruleFiles) {
	    		LOG.info(String.format("STARTING javascript hook [%s].", ruleFile));

			    final Rule rule = new Rule();

			    jsEngine.put("Rules", rule);
			    jsEngine.put("Redis", new Redis("localhost"));
			    jsEngine.put("source", source);
			    jsEngine.put("metadata", metadata);
			    jsEngine.put("destination", destination.getUri().toString());

			    try {
					jsEngine.eval(new InputStreamReader(getClass().getResourceAsStream(ruleFile)));
				} catch (final Exception e) {
					rule.mandate(false, "Marking rule as failed due to " +e.getMessage());

					e.printStackTrace(); // FIXME: Handle
				}

				final Map<String, String> metadataAfter = item.getMetadata();
				Iterator<String> keySet = metadataAfter.keySet().iterator();
				while (keySet.hasNext()) {
					String key = keySet.next();
					LOG.info("Metadata field: "+key+" : "+metadataAfter.get(key));
				}


			    if (!rule.isAllowed()) {
			    	LOG.info(String.format(
			    			"Destination %s did not pass export rules for %s.",
			    			destination, source));
			    	item.setNotExportable();

			    	break; // Do not continue evaluating hook scripts for this destination, we're not sending the artifact.
			    }

		    	LOG.info(String.format("COMPLETE javascript hook [%s].", ruleFile));
		    }
	    }
	}

	/**
	 * Loads all rule files to be executed for destination (including global rules)
	 *
	 * @param destination Destination to load rules for
	 * @param config Configuration properties
	 * @return Set of string paths of rule files to be executed
	 * @throws FileNotFoundException
	 */
	private Set<String> loadRuleFiles(Destination destination, Properties config) throws FileNotFoundException {
		Set<String> ruleFiles = new HashSet<String>();

	    // Include global rule file in rule set (first)
	    ruleFiles.add(config.getProperty("management.console.global.rules.dir") + "/global.js");

	    // Include destination-specific rule file in rule set
	    ruleFiles.add(config.getProperty("management.console.destination.rules.dir") + "/" + destination.getId() + "/custom.js");

	    // Ensure all rule files exists
	    for(String ruleFile : ruleFiles) {
	    	Path ruleFilePath = Paths.get(ruleFile);
		    if(!Files.exists(ruleFilePath)) {
		    	throw new FileNotFoundException("Could not load rule file: " + ruleFilePath.toString());
		    }
	    }

	    return ruleFiles;
	}
}
