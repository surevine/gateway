/**
 * This is a JavaScript file which defines a list of export control rules in
 * the format:
 *
 * Rules.mandate(condition);
 *
 * Where condition is a clause that returns a boolean, the value of which must
 * always be true if the file is to be sent. If one mandated rule returns false
 * then the file will not be sent. Global variables are available by default
 * for:
 *   source      - Path to the file queued for transfer.
 *   metadata    - A map of key-value properties for the file.
 *   destination - The URI intended for the file to be sent to.
 */
importClass(java.util.Arrays);
importClass(java.lang.System);

// Debug: Print out initial variables for debugging.
var it = metadata.keySet().iterator();
while (it.hasNext()) {
	var key = it.next();
	System.out.println(key +" : " +metadata.get(key));
}

// Rule: Don't send auxiliary files as these currently break Nexus import.
Rules.mandate(!metadata.get("name").endsWith("-bundle.zip"), "Refusing to export bundle.");
Rules.mandate(!metadata.get("name").endsWith("-sources.jar"), "Refusing to export sources.");

// Rule: Only send items with a valid security label.
Rules.mandate(metadata.get("classification") != null, "Classification is required.");
Rules.mandate(metadata.get("decorator") != null, "Decorator is required.");
Rules.mandate(metadata.get("groups") != null && metadata.get("groups").split(",").length > 0,
		"Group metadata is invalid: " +metadata.get("groups"));

// Rule: Only send locally-sourced produce.
//Rules.mandate(metadata.get("organisation") == "local", "Organisation must be local");

// Rule: Do not send anything over FTP.
Rules.mandate(destination.indexOf("ftp://") !== 0, "Destination scheme cannot be FTP.");

// Rule: Do not send STAFF labelled data.
Rules.mandate(function() {
	if (destination.equals("file:///tmp/foreign")) {
		return !Arrays.asList(metadata.get("groups").split(",")).contains("STAFF");
	} else {
		return true;
	}
}(), "STAFF group cannot be applied to foreign destination.");

// Rule: File copy only.
//Rules.mandate(destination.indexOf("file://") === 0)

/**
 * Logic for stripping out metadata we don't wish to send elsewhere.
 */
//Strip security label properties if not sending on to ORG01 or ORG02.
if (metadata.containsKey("organisation")) {
	var organisations = Arrays.asList(metadata.get("organisation").split(","));

	if (!organisations.contains("ORG01") && !organisations.contains("ORG02")) {
		metadata.remove("organisation");
		metadata.remove("classification");
		metadata.remove("nationality");
		metadata.remove("groups");
	}
}