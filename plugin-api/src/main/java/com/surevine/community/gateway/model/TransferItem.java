package com.surevine.community.gateway.model;

import java.nio.file.Path;
import java.util.Map;

import lombok.Getter;

/**
 * Maintains transfer allow status of a destination and file pairing with
 * associated metadata.
 *
 * @author rich.midwinter@gmail.com
 */
public class TransferItem {

	@Getter
	private Destination destination;

	@Getter
	private Path source;

	@Getter
	private Map<String, String> metadata;

	@Getter
	private boolean exportable;

	public TransferItem(final Destination destination, final Path source,
			final Map<String, String> metadata) {
		this.destination = destination;
		this.source = source;
		this.metadata = metadata;

		this.exportable = true;
	}

	public void setNotExportable() {
		exportable = false;
	}

	// Eclipse generated hashCode and equals:

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((source == null) ? 0 : source.hashCode());
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransferItem other = (TransferItem) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		return true;
	}
}
