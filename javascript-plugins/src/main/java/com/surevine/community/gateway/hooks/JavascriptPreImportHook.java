package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
	    
	    final String[] hooks = config.getProperty("preimport.configurations").split(",");
	    for (final String hook : hooks) {
	    	LOG.info(String.format("STARTING javascript hook [%s].", hook));
		    	
		    final Rule rule = new Rule();
		    
		    jsEngine.put("Rules", rule);
		    jsEngine.put("source", source);
		    jsEngine.put("metadata", properties);
		    
		    try {
				jsEngine.eval(new InputStreamReader(getClass().getResourceAsStream(hook)));
			} catch (final ScriptException e) {
				e.printStackTrace(); // FIXME: Handle
			}

	    	LOG.info(String.format("COMPLETE javascript hook [%s].", hook));
	    }
	}
}
