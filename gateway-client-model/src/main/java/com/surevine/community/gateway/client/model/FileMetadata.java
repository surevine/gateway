package com.surevine.community.gateway.client.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileMetadata {

	@JsonProperty(value = "source_type")
	private String sourceType;

	@JsonProperty(value = "limit_distribution_to")
	private String limitDistributionTo;

	private String project;

	private String repo;

	@JsonProperty(value = "distribution_type")
	private String distributionType;

	@JsonProperty(value = "source_organisation")
	private String sourceOrganisation;

	@JsonProperty(value = "source_type")
	private String fileName;

	private String classification;

	private String decorator;

	private List<String> groups;

	public FileMetadata() {
	}

	public FileMetadata(final Map<String, String> propertyMap) {
		if (propertyMap != null) {
			setSourceType(propertyMap.get("source_type"));
			setLimitDistributionTo(propertyMap.get("limit_distribution_to"));
			setProject(propertyMap.get("project"));
			setRepo(propertyMap.get("repo"));
			setDistributionType(propertyMap.get("distribution_type"));
			setSourceOrganisation(propertyMap.get("source_organisation"));
			setFileName(propertyMap.get("file_name"));
			setClassification(propertyMap.get("classification"));
			setDecorator(propertyMap.get("decorator"));
			final String groupString = propertyMap.get("groups");
			if (groupString != null) {
				final String[] groups = groupString.split(",");
				setGroups(Arrays.asList(groups));
			}
		}
	}

	/**
	 * @return the sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType
	 *            the sourceType to set
	 */
	public void setSourceType(final String sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return the limitDistributionTo
	 */
	public String getLimitDistributionTo() {
		return limitDistributionTo;
	}

	/**
	 * @param limitDistributionTo
	 *            the limitDistributionTo to set
	 */
	public void setLimitDistributionTo(final String limitDistributionTo) {
		this.limitDistributionTo = limitDistributionTo;
	}

	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(final String project) {
		this.project = project;
	}

	/**
	 * @return the repo
	 */
	public String getRepo() {
		return repo;
	}

	/**
	 * @param repo
	 *            the repo to set
	 */
	public void setRepo(final String repo) {
		this.repo = repo;
	}

	/**
	 * @return the distributionType
	 */
	public String getDistributionType() {
		return distributionType;
	}

	/**
	 * @param distributionType
	 *            the distributionType to set
	 */
	public void setDistributionType(final String distributionType) {
		this.distributionType = distributionType;
	}

	/**
	 * @return the sourceOrganisation
	 */
	public String getSourceOrganisation() {
		return sourceOrganisation;
	}

	/**
	 * @param sourceOrganisation
	 *            the sourceOrganisation to set
	 */
	public void setSourceOrganisation(final String sourceOrganisation) {
		this.sourceOrganisation = sourceOrganisation;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the classification
	 */
	public String getClassification() {
		return classification;
	}

	/**
	 * @param classification
	 *            the classification to set
	 */
	public void setClassification(final String classification) {
		this.classification = classification;
	}

	/**
	 * @return the decorator
	 */
	public String getDecorator() {
		return decorator;
	}

	/**
	 * @param decorator
	 *            the decorator to set
	 */
	public void setDecorator(final String decorator) {
		this.decorator = decorator;
	}

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(final List<String> groups) {
		this.groups = groups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("FileMetadata [");
		if (sourceType != null) {
			builder.append("sourceType=").append(sourceType).append(", ");
		}
		if (limitDistributionTo != null) {
			builder.append("limitDistributionTo=").append(limitDistributionTo).append(", ");
		}
		if (project != null) {
			builder.append("project=").append(project).append(", ");
		}
		if (repo != null) {
			builder.append("repo=").append(repo).append(", ");
		}
		if (distributionType != null) {
			builder.append("distributionType=").append(distributionType).append(", ");
		}
		if (sourceOrganisation != null) {
			builder.append("sourceOrganisation=").append(sourceOrganisation).append(", ");
		}
		if (fileName != null) {
			builder.append("fileName=").append(fileName).append(", ");
		}
		if (classification != null) {
			builder.append("classification=").append(classification).append(", ");
		}
		if (decorator != null) {
			builder.append("decorator=").append(decorator).append(", ");
		}
		if (groups != null) {
			builder.append("groups=").append(groups);
		}
		builder.append("]");
		return builder.toString();
	}

	public Map<String, String> toMap() {
		final Map<String, String> propertyMap = new HashMap<String, String>();

		if (sourceType != null) {
			propertyMap.put("source_type", sourceType);
		}
		if (limitDistributionTo != null) {
			propertyMap.put("limit_distribution_to", limitDistributionTo);
		}
		if (project != null) {
			propertyMap.put("project", project);
		}
		if (repo != null) {
			propertyMap.put("repo", repo);
		}
		if (distributionType != null) {
			propertyMap.put("distribution_type", distributionType);
		}
		if (sourceOrganisation != null) {
			propertyMap.put("source_organisation", sourceOrganisation);
		}
		if (fileName != null) {
			propertyMap.put("file_name", fileName);
		}
		if (classification != null) {
			propertyMap.put("classification", classification);
		}
		if (decorator != null) {
			propertyMap.put("decorator", decorator);
		}
		if (groups != null && groups.size() > 0) {
			if (groups.size() == 1) {
				propertyMap.put("groups", groups.get(0));
			} else {
				final StringBuilder groupStringBuilder = new StringBuilder();
				for (int i = 0; i < groups.size(); i++) {
					groupStringBuilder.append(groups.get(i));
					if (i < groups.size() - 1) {
						groupStringBuilder.append(", ");
					}
				}
				propertyMap.put("groups", groupStringBuilder.toString());
			}
		}

		return propertyMap;
	}

}
