package com.surevine.community.gateway.rules;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Set;

import com.surevine.community.gateway.model.Destination;

/**
 * Rule file service. Retrieves gateway rule files.
 *
 * @author jonnyheavey
 *
 */
public interface RuleFileService {

	/**
	 * Loads all export rule files / filters to be executed for destination (including global rules)
	 *
	 * @param destination
	 * @return
	 * @throws FileNotFoundException
	 */
	Set<Path> getExportRuleFiles(Destination destination) throws FileNotFoundException;

	/**
	 * Loads all import rule files / filters to be executed
	 *
	 * @return
	 * @throws FileNotFoundException
	 */
	Set<Path> getImportRuleFiles() throws FileNotFoundException;

}
