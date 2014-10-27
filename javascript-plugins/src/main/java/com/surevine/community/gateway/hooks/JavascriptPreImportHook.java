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
import com.surevine.community.gateway.rules.ConsoleRuleFileServiceImpl;
import com.surevine.community.gateway.rules.RuleFileService;

public class JavascriptPreImportHook implements GatewayPreImportHook {

	private static final Logger LOG = Logger.getLogger(JavascriptPreImportHook.class.getName());

	private Properties config = new Properties();
	private RuleFileService ruleFileService;

	public JavascriptPreImportHook() {
		this.ruleFileService = new ConsoleRuleFileServiceImpl(getConfig());
	}

	@Override
	public void call(final Path source, final Map<String, String> properties) {
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");

	    try {
	    	getConfig().load(getClass().getResourceAsStream("/javascript-hook.properties"));
		} catch (final IOException e1) {
			e1.printStackTrace(); // FIXME: Handle
		}

	    Set<Path> importFilters = new HashSet<Path>();
	    try {
	    	importFilters = ruleFileService.getImportRuleFiles();
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

	public void setRuleFileService(RuleFileService ruleFileService) {
		this.ruleFileService = ruleFileService;
	}

	public Properties getConfig() {
		return config;
	}

}
