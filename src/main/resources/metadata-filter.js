/**
 * Logic for stripping out metadata we don't wish to send elsewhere.
 */
importClass(java.util.Arrays);

// Strip security label properties if not sending on to ORG01 or ORG02.
var organisations = Arrays.asList(metadata.get("organisation").split(","));
if (!organisations.contains("ORG01") && !organisations.contains("ORG02")) {
	metadata.remove("organisation");
	metadata.remove("classification");
	metadata.remove("nationality");
	metadata.remove("groups");
}