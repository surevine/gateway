package com.surevine.community.gateway.management.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.surevine.community.gateway.GatewayProperties;
import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.model.Repository;
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
	 * Retrieve list of repositories configured for outbound federation with destination
	 * from management console API
	 *
	 * @param destination destination to retrieve outbound-federated repositories for
	 * @return
	 */
	public Set<Repository> getFederatedOutboundRepositoriesForDestination(Destination destination) {

		// TODO pass required repository type to API (to narrow down result set)

		final String jsonResponseBody = getJSONResponse(serviceBaseUrl + "/api/federation/" + destination.getId() + "/outbound");
		final Set<Repository> federatedRepositories = parseFederatedRepositories(jsonResponseBody);

		return federatedRepositories;
	}

	/**
	 * Parses JSON response to get[In/Out]boundRepositoriesForDestination requests into list of Repositories
	 *
	 * @param jsonResponseBody String body of response
	 * @return set of repositories
	 */
	private Set<Repository> parseFederatedRepositories(String jsonResponseBody) {

		final Set<Repository> repositories = new HashSet<Repository>();

		final JSONArray jsonFederationConfigurations = new JSONArray(jsonResponseBody);

		for (int i = 0; i < jsonFederationConfigurations.length(); i++) {

			try {

				final JSONObject jsonFedConfig = jsonFederationConfigurations.getJSONObject(i);
				final JSONObject jsonRepo = jsonFedConfig.getJSONObject("repository");
				final String repoType = jsonRepo.getString("repoType");
				final String identifier = jsonRepo.getString("identifier");
				final Repository repository = new Repository(repoType, identifier);

				repositories.add(repository);

			} catch (final JSONException e) {
				LOG.warning("Unable to parse destination from JSON: " + e);
				continue;
			}

		}

		return repositories;

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
				final String sourceKey = jsonDestination.getString("sourceKey");

				final Destination destination = new Destination(id, name, url, sourceKey);
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
	private String getJSONResponse(final String url, final Map<String, String> queryParams) {

		final Client client = ClientBuilder.newClient();

		WebTarget requestURL= client.target(url);

		if(!queryParams.isEmpty()) {
			Iterator<Entry<String, String>> it = queryParams.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String,String> param = it.next();
				requestURL.queryParam(param.getKey(), param.getValue());
			}
		}

		LOG.info("Request to management console API: " + requestURL.toString());

		final Response response = requestURL.request("application/json").get();

		if (response.getStatusInfo() != Response.Status.OK) {
			throw new ServiceUnavailableException("Error making request to management console API. Response code: "
					+ response.getStatus());
		}

		return response.readEntity(String.class);

	}

	private String getJSONResponse(final String url) {
		return getJSONResponse(url, Collections.<String,String>emptyMap());
	}

	/**
	 * Retrieve repository configured for inbound federation from partner organisation
	 * from management console API
	 *
	 * @param sourceKey key of partner organisation sending repository
	 * @param repoIdentifier unique repository identifier
	 * @return
	 */
	public Repository getFederatedInboundRepository(String sourceKey,
			String repoIdentifier, String repoType) {

		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("sourceKey", sourceKey);
		queryParams.put("repoIdentifier", repoIdentifier);
		queryParams.put("repoType", repoType);

		final String jsonResponseBody = getJSONResponse(serviceBaseUrl + "/api/federation/inbound", queryParams);
		final Repository federatedRepository = parseFederatedRepository(jsonResponseBody);

		return federatedRepository;
	}

	/**
	 * Parses JSON response to getFederatedInboundRepository request
	 *
	 * @param jsonResponseBody String body of response
	 * @return Federated repository (if it exists and is shared)
	 */
	private Repository parseFederatedRepository(String jsonResponseBody) {

		final JSONArray jsonFederationConfigurations = new JSONArray(jsonResponseBody);

		for (int i = 0; i < jsonFederationConfigurations.length(); i++) {

			try {
				final JSONObject jsonFedConfig = jsonFederationConfigurations.getJSONObject(i);
				final JSONObject jsonRepo = jsonFedConfig.getJSONObject("repository");
				final String repoType = jsonRepo.getString("repoType");
				final String identifier = jsonRepo.getString("identifier");

				return new Repository(repoType, identifier);
			} catch (final JSONException e) {
				LOG.warning("Unable to parse destination from JSON: " + e);
				continue;
			}

		}

		return null;

	}

}
