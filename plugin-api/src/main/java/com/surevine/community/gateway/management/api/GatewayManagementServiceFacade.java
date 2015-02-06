package com.surevine.community.gateway.management.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.surevine.community.gateway.GatewayProperties;
import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.model.WhitelistedProject;

/**
 * Service facade around gateway management console API
 *
 * @author jonnyheavey
 *
 */
public class GatewayManagementServiceFacade {

	private static final Logger LOG = Logger.getLogger(GatewayManagementServiceFacade.class.getName());

	private final String serviceBaseUrl = GatewayProperties.get(GatewayProperties.MANAGEMENT_CONSOLE_API_BASE_URL);

	private static GatewayManagementServiceFacade _instance = null;

	private GatewayManagementServiceFacade() {

	}

	/**
	 * Get an instance of GatewayManagementServiceFacade
	 *
	 * @return the GatewayManagementServiceFacade
	 */
	public static GatewayManagementServiceFacade getInstance() {
		if (_instance == null) {
			_instance = new GatewayManagementServiceFacade();
		}
		return _instance;
	}

	/**
	 * Retrieve list of destinations from management console API
	 *
	 * @return
	 */
	public Set<Destination> getDestinations() {

		final String jsonResponseBody = getJSONResponse(serviceBaseUrl + "/api/destinations");
		final Set<Destination> destinations = parseDestinationsFromResponse(jsonResponseBody);

		return destinations;
	}

	/**
	 * Parses JSON response to getDestinations request into list of Destinations
	 *
	 * @param responseBody
	 *            String body of response
	 * @return List of destinations
	 * @throws URISyntaxException
	 */
	private Set<Destination> parseDestinationsFromResponse(final String responseBody) {

		final Set<Destination> destinations = new HashSet<Destination>();

		final JSONArray jsonDestinations = new JSONArray(responseBody);

		for (int i = 0; i < jsonDestinations.length(); i++) {

			try {

				final JSONObject jsonDestination = jsonDestinations.getJSONObject(i);
				final Long id = jsonDestination.getLong("id");
				final String name = jsonDestination.getString("name");
				final URI url = new URI(jsonDestination.getString("url"));

				final Set<String> projects = new HashSet<String>();
				final JSONArray jsonProjects = jsonDestination.getJSONArray("projects");
				for (int p = 0; p < jsonProjects.length(); p++) {
					final JSONObject jsonProject = jsonProjects.getJSONObject(p);
					final String projectSlug = String.format("%s/%s", jsonProject.getString("projectKey"),
							jsonProject.getString("repositorySlug"));
					projects.add(projectSlug);
				}

				final Set<String> issueProjects = new HashSet<String>();
				final JSONArray jsonIssueProjects = jsonDestination.getJSONArray("issueProjects");
				for (int p = 0; p < jsonIssueProjects.length(); p++) {
					final JSONObject jsonProject = jsonIssueProjects.getJSONObject(p);
					final String projectidentifier = jsonProject.getString("projectKey");
					issueProjects.add(projectidentifier);
				}

				final Destination destination = new Destination(id, name, url, projects, issueProjects);
				destinations.add(destination);

			} catch (final JSONException e) {
				LOG.warning("Unable to parse destination from JSON: " + e);
				continue;
			} catch (final URISyntaxException e) {
				LOG.warning("Incorrect URI syntax for destination URL: " + e);
				continue;
			}

		}

		return destinations;
	}

	/**
	 * Retrieve list of whitelisted scm projects from management console API
	 *
	 * @return Set of whitelisted projects
	 */
	public Set<WhitelistedProject> getWhitelistedScmProjects() {

		final String jsonResponseBody = getJSONResponse(serviceBaseUrl + "/api/inbound-projects");
		final Set<WhitelistedProject> projects = parseProjectsFromReponse(jsonResponseBody);

		return projects;
	}

	/**
	 * Retrieve list of whitelisted issue projects from management console API
	 *
	 * @return Set of whitelisted projects
	 */
	public Set<WhitelistedProject> getWhitelistedIssueProjects() {

		final String jsonResponseBody = getJSONResponse(serviceBaseUrl + "/api/inbound-issue-projects");
		final Set<WhitelistedProject> projects = parseProjectsFromReponse(jsonResponseBody);

		return projects;
	}

	/**
	 * Parses JSON response to getWhitelistedProjects request into list of whitelisted projects
	 *
	 * @param responseBody
	 *            String response to API request
	 * @return Set of whitelisted projects
	 */
	private Set<WhitelistedProject> parseProjectsFromReponse(final String responseBody) {

		final Set<WhitelistedProject> projects = new HashSet<WhitelistedProject>();
		final JSONArray jsonProjects = new JSONArray(responseBody);

		for (int i = 0; i < jsonProjects.length(); i++) {

			try {

				final JSONObject jsonProject = jsonProjects.getJSONObject(i);
				final String sourceOrganisation = jsonProject.getString("sourceOrganisation");
				final String projectKey = jsonProject.getString("projectKey");
				final String repositorySlug = !jsonProject.has("repositorySlug") ? null : jsonProject
						.getString("repositorySlug");

				final WhitelistedProject project = new WhitelistedProject(sourceOrganisation, projectKey,
						repositorySlug);
				projects.add(project);

			} catch (final JSONException e) {
				LOG.warning("Unable to parse whitelisted project from JSON: " + e);
				continue;
			}

		}

		return projects;

	}

	/**
	 * Helper method to request JSON from management console API endpoint
	 *
	 * @param url
	 *            API URL to make request to
	 * @return
	 */
	private String getJSONResponse(final String url) {

		LOG.info("Request to management console API: " + url);

		final Client client = ClientBuilder.newClient();
		final Response response = client.target(url).request("application/json").get();

		if (response.getStatusInfo() != Response.Status.OK) {
			throw new ServiceUnavailableException("Error making request to management console API. Response code: "
					+ response.getStatus());
		}

		return response.readEntity(String.class);

	}

}
