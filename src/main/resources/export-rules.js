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

// Only send locally-sourced produce.
Rules.mandate(metadata.get("organisation") == "local");

// Rule: Do not send anything over FTP.
Rules.mandate(destination.indexOf("ftp://") !== 0);