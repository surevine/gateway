package com.surevine.community.gateway.util;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.rules.ConsoleRuleFileServiceImpl;

public class MockRuleFileService extends ConsoleRuleFileServiceImpl {

	public MockRuleFileService(Properties config) {
		super(config);
	}

	@Override
	public Set<Path> getExportRuleFiles(Destination destination) throws FileNotFoundException {
		Set<Path> testRuleFiles = new HashSet<Path>();

		try {
			testRuleFiles.add(Paths.get(getClass().getResource("/global-export.js").toURI()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return testRuleFiles;
	}

}
