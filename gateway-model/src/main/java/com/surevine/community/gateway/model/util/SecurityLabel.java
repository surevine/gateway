package com.surevine.community.gateway.model.util;

import java.io.IOException;
import java.io.InputStream;
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

		final InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("security_labels.yml");
		Map<String, Object> data;
		try {
			data = (Map<String, Object>) yaml.load(stream);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		LABELS = ((Map<String, Map<String, String>>) data.get("production")).get("labels");
	}

	public static String asFriendlyString(final String securityLabel) {
		final List<String> labels = new ArrayList<String>();

		for (int i = 0; i < securityLabel.length(); i++) {
			if (securityLabel.charAt(i) == '1') {
				final String key = "p" + i;
				if (LABELS.containsKey(key)) {
					labels.add(LABELS.get(key));
				}
			}
		}

		return Joiner.on(' ').join(labels);
	}
}
