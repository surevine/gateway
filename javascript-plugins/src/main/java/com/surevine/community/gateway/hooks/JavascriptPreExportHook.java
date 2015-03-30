package com.surevine.community.gateway.hooks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.surevine.community.gateway.audit.Audit;
import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.model.Partner;
import com.surevine.community.gateway.model.Rule;
import com.surevine.community.gateway.model.TransferItem;
import com.surevine.community.gateway.rules.ConsoleRuleFileServiceImpl;
import com.surevine.community.gateway.rules.RuleFileService;
import com.surevine.community.gateway.util.Redis;

public class JavascriptPreExportHook implements GatewayPreExportHook {

	private static final Logger LOG = Logger.getLogger(JavascriptPreExportHook.class.getName());

	private final Properties config = new Properties();
	private RuleFileService ruleFileService;

	public JavascriptPreExportHook() {
		this.ruleFileService = new ConsoleRuleFileServiceImpl(getConfig());
	}

	@Override
	public void call(final Set<TransferItem> transferQueue) {
		final ScriptEngineManager manager = new ScriptEngineManager();
		final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");

		InputStream stream = null;
		try {
			stream = getClass().getResourceAsStream("/javascript-hook.properties");
			getConfig().load(stream);
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Failed to load javascript hook module configuration.", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (final IOException e) {
					LOG.log(Level.WARNING, "Failed to close input stream.");
				}
			}
		}

		for (final TransferItem item : transferQueue) {

			final Path source = item.getSource();
			final Map<String, String> metadata = item.getMetadata();
			final Partner partner = item.getPartner();

			LOG.info(String.format("Processing destination %s [%s]", partner.getName(), partner.getUri().toString()));

			Set<Path> exportRuleFiles = new HashSet<Path>();
			try {
				exportRuleFiles = ruleFileService.getExportRuleFiles(partner);
			} catch (final FileNotFoundException e) {
				LOG.warning(String
						.format("Could not load all rule files for destination %s [%s]. Skipping item export to destination. %s",
								partner.getName(), partner.getId(), e.getMessage()));
				item.setNotExportable();
				continue;
			}

			final Map<String, String> metadataAfter = item.getMetadata();
			final Iterator<String> keySet = metadataAfter.keySet().iterator();
			while (keySet.hasNext()) {
				final String key = keySet.next();
				LOG.info("Metadata field: " + key + " : " + metadataAfter.get(key));
			}

			for (final Path ruleFile : exportRuleFiles) {
				LOG.info(String.format("STARTING javascript hook [%s].", ruleFile));

				final Rule rule = new Rule();

				jsEngine.put("Rules", rule);
				jsEngine.put("Redis", new Redis("localhost"));
				jsEngine.put("source", source);
				jsEngine.put("metadata", metadata);
				jsEngine.put("destination", partner.getUri().toString());

				InputStreamReader reader = null;
				try {
					reader = new InputStreamReader(Files.newInputStream(ruleFile));
					jsEngine.eval(reader);
				} catch (final Exception e) {
					rule.mandate(false, "Marking rule as failed due to " + e.getMessage());
					LOG.log(Level.INFO, "Javascript rule failed.", e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (final IOException e) {
							rule.mandate(false, "Marking rule as failed due to " + e.getMessage());
							LOG.log(Level.INFO, "Javascript rule failed.", e);
						}
					}
				}

				if (!rule.isAllowed()) {
					LOG.info(String.format("Destination %s did not pass export rules for %s.", partner, source));
					item.setNotExportable();

					final RuleFailAuditAction ruleFailAction = Audit.getRuleFailAuditAction(source, partner);
					Audit.audit(ruleFailAction);

					break; // Do not continue evaluating hook scripts for this destination, we're not sending the
					// artifact.
				}

				LOG.info(String.format("COMPLETE javascript hook [%s].", ruleFile));
			}
		}
	}

	public void setRuleFileService(final RuleFileService ruleFileService) {
		this.ruleFileService = ruleFileService;
	}

	public Properties getConfig() {
		return config;
	}

}
