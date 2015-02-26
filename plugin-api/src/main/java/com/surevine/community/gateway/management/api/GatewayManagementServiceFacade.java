package com.surevine.community.gateway.management.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
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
	 * Retrieve list of configured destinations
	 *
	 * @return Set of configured destinations
	 */
	public Set<Destination> getDestinations() {

		final String jsonResponseBody = getJSONResponse(serviceBaseUrl + "/api/destinations");
		final Set<Destination> destinations = parseDestinationsFromResponse(jsonResponseBody);

		return destinations;
	}

	/**
	 * Retrieve repository that is configured for inbound-federation
	 * from partner/destination
	 *
	 * @param sourceKey key of partner organisation sending repository
	 * @param repoIdentifier unique repository identifier
	 * @return
	 */
	public Repository getInboundFederatedRepository(String sourceKey,
			String repoIdentifier, String repoType) {

		final Client client = ClientBuilder.newClient();
		final Response response = client.target(serviceBaseUrl + "/api/federation/inbound-single")
									.queryParam("sourceKey", sourceKey)
									.queryParam("repoIdentifier", repoIdentifier)
									.queryParam("repoType", repoType)
									.request("application/json").get();

		if(Response.Status.fromStatusCode(response.getStatus()) != Response.Status.OK) {
			return null;
		}

		final String jsonResponseBody = response.readEntity(String.class);
		final Repository federatedRepository = parseRepositoryFromResponse(jsonResponseBody);

		return federatedRepository;
	}

	/**
	 * Retrieve repository that is configured for outbound-federation
	 * with partner/destination.
	 *
	 * @param destination destination the repository is shared with
	 * @param repoType Type of repository
	 * @param repoIdentifier Identifier of shared repository
	 * @return shared Repository or null if doesn't exist (or isn't shared)
	 */
	public Repository getOutboundFederatedRepository(Destination destination,
			String repoType, String repoIdentifier) {

		final Client client = ClientBuilder.newClient();
		final Response response = client.target(serviceBaseUrl + "/api/federation/outbound-single")
									.queryParam("destinationId", destination.getId())
									.queryParam("repoIdentifier", repoIdentifier)
									.queryParam("repoType", repoType)
									.request("application/json").get();

		if(Response.Status.fromStatusCode(response.getStatus()) != Response.Status.OK) {
			return null;
		}

		final String jsonResponseBody = response.readEntity(String.class);
		final Repository federatedRepository = parseRepositoryFromResponse(jsonResponseBody);

		return federatedRepository;

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
	 * Parses JSON response to get[In/Out]boundFederatedRepository requests
	 *
	 * @param jsonResponseBody String body of response
	 * @return Federated repository (if it exists and is shared)
	 */
	private Repository parseRepositoryFromResponse(String jsonResponseBody) {

		try {
			final JSONObject jsonFedConfig = new JSONObject(jsonResponseBody);
			final JSONObject jsonRepo = jsonFedConfig.getJSONObject("repository");
			final String repoType = jsonRepo.getString("repoType");
			final String identifier = jsonRepo.getString("identifier");
			return new Repository(repoType, identifier);
		} catch (JSONException e) {
			LOG.log(Level.WARNING, "Unable to parse response JSON.", e);
			return null;
		}

	}

}
