package com.surevine.community.gateway;

import java.io.InputStreamReader;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Metadata {

	public static Map<String, String> sanitise(final Map<String, String> properties) {
		final ScriptEngineManager manager = new ScriptEngineManager();
	    final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
	    
	    jsEngine.put("metadata", properties);
	    
	    try {
			jsEngine.eval(new InputStreamReader(JavaScriptExportFilter.class.getResourceAsStream("/metadata-filter.js")));
		} catch (final ScriptException e) {
			e.printStackTrace();
		}
	    
	    return properties;
	}
}
