package com.surevine.community.gateway.sanitisation;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interacts with community portal sanitisation service
 * to sanitise SCM files.
 *
 * @author jonnyheavey
 */
public class SanitisationServiceFacade {

	private static final Logger LOG = Logger.getLogger(SanitisationServiceFacade.class.getName());

	private static final int SUCCESS = 200;

	private static SanitisationServiceFacade _instance = null;

	private Properties config = new Properties();

	private SanitisationServiceFacade() {
		try {
			getConfig().load(getClass().getResourceAsStream("/federator.properties"));
		} catch (IOException e) {
			LOG.warning("Failed to load sanitisation hook configuration.");
			e.printStackTrace();
		}
	}

	public static SanitisationServiceFacade getInstance() {
		if(_instance == null) {
			_instance = new SanitisationServiceFacade();
		}
		return _instance;
	}

	/**
	 * Confirms whether archive contents are valid/clean
	 * according to sanitisation service.
	 * @param archiveToSanitise path to archive to sanitise
	 * @param projectKey project the repository belongs to
	 * @param repoSlug repository the archive of changes derive from
	 * @param identifier String identifier to describe archive source
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public SanitisationResult isSane(Path archiveToSanitise, String projectKey, String repoSlug, String identifier) {

		LOG.info("Sending archive to sanitisation service: " + archiveToSanitise.toString());

		File archive = new File(archiveToSanitise.toString());

		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("archive", new FileBody(archive));
		try {
			entity.addPart("projectKey", new StringBody(projectKey));
			entity.addPart("repoSlug", new StringBody(repoSlug));
			entity.addPart("identifier", new StringBody(identifier));
		} catch (UnsupportedEncodingException e1) {
			LOG.severe("Failed to build entities for sanitisation request.");
			e1.printStackTrace();
		}

		String url = getConfig().getProperty("sanitisation.service.base.url") + "/sanitise";

		SanitisationResult result = new SanitisationResult(archive, false);

		try {
			HttpResponse response = Request.Post(url)
										.body(entity)
										.execute().returnResponse();

			if(response.getStatusLine().getStatusCode() != SUCCESS) {
				result.setSane(false);
				result.addError("Sanitisation service failed.");
				return result;
			}

			String responseString = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
			try {
				parseJSONResponse(result, responseString);
			} catch (JSONException e2) {
				result.setSane(false);
				result.addError("Failed to parse sanitisation service response.");
			}

		} catch (IOException e3) {
			LOG.severe("Failed to send archive to sanitisation service.");
			e3.printStackTrace();
		}

		return result;
	}

	private Properties getConfig() {
		return config;
	}

	/**
	 * Parse JSON response from sanitisation service
	 * @param result SanitisationResult to store parsed values in
	 * @param responseString raw JSON string response
	 * @throws JSONException
	 */
	private void parseJSONResponse(SanitisationResult result, String responseString) throws JSONException {
		JSONObject responseBody;

			responseBody = new JSONObject(responseString);
			result.setSane((boolean) responseBody.get("safe"));

			JSONArray errorsArray = (JSONArray) responseBody.get("errors");
			for(int i=0; i<errorsArray.length(); i++) {
				String errorMessage = (String) errorsArray.get(i);
				result.addError(errorMessage);
			}
	}

}