package com.surevine.community.gateway.hooks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.model.Rule;

public class JavascriptPreImportHook implements GatewayPreImportHook {

	private static final Logger LOG = Logger.getLogger(JavascriptPreImportHook.class.getName());

	@Override
	public void call(final Path source, final Map<String, String> properties) {
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
	    final Properties config = new Properties();
	    try {
			config.load(getClass().getResourceAsStream("/javascript-hook.properties"));
		} catch (final IOException e1) {
			e1.printStackTrace(); // FIXME: Handle
		}

	    Set<Path> importFilters = new HashSet<Path>();
	    try {
	    	importFilters = loadImportFilters(config);
	    }
	    catch(FileNotFoundException e) {
	    	LOG.warning("Could not load all import filter files.");
	    	// TODO should we prevent import at this stage?
	    }

	    for (final Path importFilter : importFilters) {
	    	LOG.info(String.format("STARTING javascript hook [%s].", importFilter));

		    final Rule rule = new Rule();

		    jsEngine.put("Rules", rule);
		    jsEngine.put("source", source);
		    jsEngine.put("metadata", properties);

		    try {
		    	jsEngine.eval(new InputStreamReader(Files.newInputStream(importFilter)));
			} catch (final Exception e) {
				e.printStackTrace(); // FIXME: Handle
			}

	    	LOG.info(String.format("COMPLETE javascript hook [%s].", importFilter));
	    }
	}

	/**
	 * Load import filter files
	 *
	 * @param config
	 * @return
	 * @throws FileNotFoundException
	 */
	private Set<Path> loadImportFilters(Properties config) throws FileNotFoundException {
		Set<Path> ruleFiles = new HashSet<Path>();

	    // Include global rule file in rule set (first)
	    ruleFiles.add(Paths.get(config.getProperty("management.console.global.rules.dir") + "/global-import.js"));

	    // Ensure all rule files exists
	    for(Path ruleFile : ruleFiles) {
		    if(!Files.exists(ruleFile)) {
		    	throw new FileNotFoundException("Could not load rule file: " + ruleFile.toString());
		    }
	    }

	    return ruleFiles;
	}
}
