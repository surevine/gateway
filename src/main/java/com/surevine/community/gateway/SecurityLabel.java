package com.surevine.community.gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Joiner;

@SuppressWarnings("unchecked")
public class SecurityLabel {
	
	private static Map<String, String> LABELS;
	
	static {
		final Yaml yaml = new Yaml();
		
		final Map<String, Object> data = (Map<String, Object>)
				yaml.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("security_labels.yml"));
		
		LABELS = ((Map<String, Map<String, String>>) data.get("production")).get("labels");
	}

	public static String asFriendlyString(final String securityLabel) {
		final List<String> labels = new ArrayList<String>();
		
		for (int i = 0; i<securityLabel.length(); i++) {
			if (securityLabel.charAt(i) == '1') {
				final String key = "p" +i;
				if (LABELS.containsKey(key)) {
					labels.add(LABELS.get(key));
				}
			}
		}
		
		return Joiner.on(' ').join(labels);
	}
}
