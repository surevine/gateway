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
	 * @return the GatewayManagementServiceFacade
	 */
	public static GatewayManagementServiceFacade getInstance() {
		if(_instance == null) {
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

		String jsonResponseBody = getJSONResponse(serviceBaseUrl + "/api/destinations");
		Set<Destination> destinations = parseDestinationsFromResponse(jsonResponseBody);

		return destinations;
	}

	/**
	 * Parses JSON response to getDestinations request into list of Destinations
	 *
	 * @param responseBody String body of response
	 * @return List of destinations
	 * @throws URISyntaxException
	 */
	private Set<Destination> parseDestinationsFromResponse(String responseBody) {

		Set<Destination> destinations = new HashSet<Destination>();

		JSONArray jsonDestinations = new JSONArray(responseBody);

		for (int i = 0; i < jsonDestinations.length(); i++) {

			try {

				JSONObject jsonDestination = jsonDestinations.getJSONObject(i);
				Long id = jsonDestination.getLong("id");
				String name = jsonDestination.getString("name");
				URI url = new URI(jsonDestination.getString("url"));
				Set<String> projects = new HashSet<String>();

				JSONArray jsonProjects = jsonDestination.getJSONArray("projects");
				for (int p = 0; p < jsonProjects.length(); p++) {
					JSONObject jsonProject = jsonProjects.getJSONObject(p);
					String projectSlug = String.format("%s/%s", jsonProject.getString("projectKey"), jsonProject.getString("repositorySlug"));
					projects.add(projectSlug);
				}

				Destination destination = new Destination(id, name, url, projects);
				destinations.add(destination);

			} catch (JSONException e) {
				LOG.warning("Unable to parse destination from JSON: " + e);
				continue;
			} catch (URISyntaxException e) {
				LOG.warning("Incorrect URI syntax for destination URL: " + e);
				continue;
			}

		}

		return destinations;
	}

	/**
	 * Retrieve list of whitelisted projects from management console API
	 *
	 * @return Set of whitelisted projects
	 */
	public Set<WhitelistedProject> getWhitelistedProjects() {

		String jsonResponseBody = getJSONResponse(serviceBaseUrl + "/api/inbound-projects");
		Set<WhitelistedProject> projects = parseProjectsFromReponse(jsonResponseBody);

		return projects;
	}

	/**
	 * Parses JSON response to getWhitelistedProjects request into list of whitelisted projects
	 * @param responseBody String response to API request
	 * @return Set of whitelisted projects
	 */
	private Set<WhitelistedProject> parseProjectsFromReponse(String responseBody) {

		Set<WhitelistedProject> projects = new HashSet<WhitelistedProject>();
		JSONArray jsonProjects = new JSONArray(responseBody);

		for (int i = 0; i < jsonProjects.length(); i++) {

			try {

				JSONObject jsonProject = jsonProjects.getJSONObject(i);
				String sourceOrganisation = jsonProject.getString("sourceOrganisation");
				String projectKey = jsonProject.getString("projectKey");
				String repositorySlug = jsonProject.getString("repositorySlug");

				WhitelistedProject project = new WhitelistedProject(sourceOrganisation,
																	projectKey,
																	repositorySlug);
				projects.add(project);

			} catch (JSONException e) {
				LOG.warning("Unable to parse whitelisted project from JSON: " + e);
				continue;
			}

		}

		return projects;

	}

	/**
	 * Helper method to request JSON from management console API endpoint
	 * @param url API URL to make request to
	 * @return
	 */
	private String getJSONResponse(String url) {

		LOG.info("Request to management console API: " + url);

		Client client = ClientBuilder.newClient();
		Response response = client.target(url)
								.request("application/json").get();

		if(response.getStatusInfo() != Response.Status.OK) {
			throw new ServiceUnavailableException("Error making request to management console API. Response code: " + response.getStatus());
		}

		return response.readEntity(String.class);

	}

}
