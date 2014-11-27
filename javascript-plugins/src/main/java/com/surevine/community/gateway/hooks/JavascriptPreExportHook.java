package com.surevine.community.gateway.hooks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import com.surevine.community.gateway.audit.Audit;
import com.surevine.community.gateway.audit.AuditService;
import com.surevine.community.gateway.audit.XMLAuditServiceImpl;
import com.surevine.community.gateway.audit.action.AuditAction;
import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.model.Rule;
import com.surevine.community.gateway.model.TransferItem;
import com.surevine.community.gateway.util.Redis;
import com.surevine.community.gateway.rules.ConsoleRuleFileServiceImpl;
import com.surevine.community.gateway.rules.RuleFileService;

public class JavascriptPreExportHook implements GatewayPreExportHook {

	private static final Logger LOG = Logger.getLogger(JavascriptPreExportHook.class.getName());

	private Properties config = new Properties();
	private RuleFileService ruleFileService;

	public JavascriptPreExportHook() {
		this.ruleFileService = new ConsoleRuleFileServiceImpl(getConfig());
	}

	@Override
	public void call(final Set<TransferItem> transferQueue) {
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");

	    try {
			getConfig().load(getClass().getResourceAsStream("/javascript-hook.properties"));
		} catch (final IOException e1) {
			e1.printStackTrace(); // FIXME: Handle
		}

	    for (final TransferItem item : transferQueue) {

			final Path source = item.getSource();
			final Map<String, String> metadata = item.getMetadata();
			final Destination destination = item.getDestination();

	    	LOG.info(String.format("Processing destination %s [%s]", destination.getName(), destination.getUri().toString()));

		    Set<Path> exportRuleFiles = new HashSet<Path>();
		    try {
		    	exportRuleFiles = ruleFileService.getExportRuleFiles(destination);
		    }
		    catch(FileNotFoundException e) {
		    	LOG.warning(String.format("Could not load all rule files for destination %s [%s]. Skipping item export to destination. %s",
		    				destination.getName(),
		    				destination.getId(),
		    				e.getMessage()));
		    	item.setNotExportable();
		    	continue;
		    }

	    	for (final Path ruleFile : exportRuleFiles) {
	    		LOG.info(String.format("STARTING javascript hook [%s].", ruleFile));

			    final Rule rule = new Rule();

			    jsEngine.put("Rules", rule);
			    jsEngine.put("Redis", new Redis("localhost"));
			    jsEngine.put("source", source);
			    jsEngine.put("metadata", metadata);
			    jsEngine.put("destination", destination.getUri().toString());

			    try {
					jsEngine.eval(new InputStreamReader(Files.newInputStream(ruleFile)));
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

			    	RuleFailAuditAction ruleFailAction = Audit.getRuleFailAuditAction(source, destination);
			    	Audit.audit(ruleFailAction);

			    	break; // Do not continue evaluating hook scripts for this destination, we're not sending the artifact.
			    }

		    	LOG.info(String.format("COMPLETE javascript hook [%s].", ruleFile));
		    }
	    }
	}

	public void setRuleFileService(RuleFileService ruleFileService) {
		this.ruleFileService = ruleFileService;
	}

	public Properties getConfig() {
		return config;
	}

}
