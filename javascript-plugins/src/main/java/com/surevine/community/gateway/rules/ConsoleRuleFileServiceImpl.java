package com.surevine.community.gateway.rules;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.surevine.community.gateway.model.Partner;

/**
 * Implementation of RuleFileService for gateway management console maintained rule files.
 *
 * @author jonnyheavey
 *
 */
public class ConsoleRuleFileServiceImpl implements RuleFileService {

	public Properties config;

	/**
	 * Create new RuleFileService
	 * @param config Javascript plugin configuration
	 */
	public ConsoleRuleFileServiceImpl(Properties config) {
		this.config = config;
	}

	/**
	 * Loads all export rule files / filters to be executed for destination (including global rules)
	 *
	 * @param partner Partner to load rules for
	 * @return Set of string paths of rule files to be executed
	 * @throws FileNotFoundException
	 */
	public Set<Path> getExportRuleFiles(Partner partner) throws FileNotFoundException {
		Set<Path> ruleFiles = new HashSet<Path>();

	    // Include global rule file in rule set (first)
	    ruleFiles.add(Paths.get(config.getProperty("management.console.global.rules.dir") + "/global-export.js"));

	    // Include destination-specific rule file in rule set
	    ruleFiles.add(Paths.get(config.getProperty("management.console.destination.rules.dir") + "/" + partner.getId() + "/export.js"));

	    // Ensure all rule files exists
	    for(Path ruleFile : ruleFiles) {
		    if(!Files.exists(ruleFile)) {
		    	throw new FileNotFoundException("Could not load rule file: " + ruleFile.toString());
		    }
	    }

	    return ruleFiles;
	}

	/**
	 * Loads all import rule files / filters to be executed
	 *
	 * @param config
	 * @return
	 * @throws FileNotFoundException
	 */
	public Set<Path> getImportRuleFiles() throws FileNotFoundException {
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
