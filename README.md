Gateway
=======

Exposes an API to decide if a file should pass through.

Takes key-value pairs to include security label, owner and destination.

Passes the inputs through any configured plugins / rules listings.

Returns either a 200 OK or a 403 forbidden depending on server-defined rules.

Auditing.
