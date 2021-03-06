package com.surevine.community.gateway.hooks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.surevine.community.gateway.model.Rule;
import com.surevine.community.gateway.rules.ConsoleRuleFileServiceImpl;
import com.surevine.community.gateway.rules.RuleFileService;

public class JavascriptPreImportHook implements GatewayPreImportHook {

	private static final Logger LOG = Logger.getLogger(JavascriptPreImportHook.class.getName());

	private final Properties config = new Properties();
	private RuleFileService ruleFileService;

	public JavascriptPreImportHook() {
		this.ruleFileService = new ConsoleRuleFileServiceImpl(getConfig());
	}

	@Override
	public void call(final Path source, final Map<String, String> properties) {
		final ScriptEngineManager manager = new ScriptEngineManager();
		final ScriptEngine jsEngine = manager.getEngineByName("JavaScript");

		InputStream stream = null;
		try {
			stream = getClass().getResourceAsStream("/javascript-hook.properties");
			getConfig().load(stream);
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Failed to load Javascript hook module configuration.", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (final IOException e) {
					LOG.log(Level.SEVERE, "Failed to load Javascript hook module configuration.", e);
				}
			}
		}

		Set<Path> importFilters = new HashSet<Path>();
		try {
			importFilters = ruleFileService.getImportRuleFiles();
		} catch (final FileNotFoundException e) {
			LOG.warning("Could not load all import filter files.");
			// TODO should we prevent import at this stage?
		}

		for (final Path importFilter : importFilters) {
			LOG.info(String.format("STARTING javascript hook [%s].", importFilter));

			final Rule rule = new Rule();

			jsEngine.put("Rules", rule);
			jsEngine.put("source", source);
			jsEngine.put("metadata", properties);

			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(Files.newInputStream(importFilter));
				jsEngine.eval(reader);
			} catch (final Exception e) {
				LOG.log(Level.SEVERE, "Failed to evaluate Javascript rule file.", e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						LOG.log(Level.SEVERE, "Failed to evaluate Javascript rule file.", e);
					}
				}
			}

			LOG.info(String.format("COMPLETE javascript hook [%s].", importFilter));
		}
	}

	public void setRuleFileService(final RuleFileService ruleFileService) {
		this.ruleFileService = ruleFileService;
	}

	public Properties getConfig() {
		return config;
	}

}
