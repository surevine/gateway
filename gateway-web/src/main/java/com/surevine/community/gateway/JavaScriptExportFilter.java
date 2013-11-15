package com.surevine.community.gateway;

import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.surevine.community.gateway.model.Rule;

public class JavaScriptExportFilter {

	public static URI[] filter(final Path source, final Map<String, String> properties, final URI... destinations) {
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
	    
	    final List<URI> filtered = new ArrayList<URI>();
	    
	    for (final URI destination : destinations) {
		    final Rule rule = new Rule();
		    
		    jsEngine.put("Rules", rule);
		    jsEngine.put("source", source);
		    jsEngine.put("metadata", properties);
		    jsEngine.put("destination", destination.toString());
		    
		    try {
				jsEngine.eval(new InputStreamReader(JavaScriptExportFilter.class.getResourceAsStream("/export-rules.js")));
			} catch (final ScriptException e) {
				e.printStackTrace();
			}
		    
		    if (rule.isAllowed()) {
		    	filtered.add(destination);
		    }
	    }
	    
		return filtered.toArray(new URI[0]);
	}
}
