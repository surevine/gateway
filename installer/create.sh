#!/bin/sh

#
# Shell script to create an installer package for release manangement
#

STATIC_ROOT="/var/lib/jenkins/tps/installer"

# Create installer structure
mkdir -p installer/packages
mkdir -p installer/sources
mkdir -p installer/config
mkdir -p installer/config-scm

# Copy static files into place
cp ${STATIC_ROOT}/README installer/
cp ${STATIC_ROOT}/packages/* installer/packages/

cat > installer/config/module.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.1" name="com.surevine.community.gateway">
    <resources>
        <resource-root path="."/>
    </resources>
</module>
EOF

cat > installer/config-scm/module.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.1" name="com.surevine.gateway.scm">
    <resources>
        <resource-root path="."/>
    </resources>
</module>
EOF

# Suck down source control files
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/gateway-web/src/main/resources/gateway.properties" -o "installer/config/gateway.properties"
curl -sk "https://raw.githubusercontent.com/surevine/federated-scm/master/src/main/resources/federated-scm.properties" -o "installer/config-scm/federated-scm.properties"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/scm-federator-plugin/src/main/resources/scm-federator-plugin.properties" -o "installer/config/scm-federator-plugin.properties"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/javascript-plugins/src/main/resources/export-rules.js" -o "installer/config/export-rules.js"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/javascript-plugins/src/main/resources/import-filter.js" -o "installer/config/import-filter.js"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/javascript-plugins/src/main/resources/javascript-hook.properties" -o "installer/config/javascript-hook.properties"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/javascript-plugins/src/main/resources/metadata-filter.js" -o "installer/config/metadata-filter.js"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/javascript-plugins/src/main/resources/transport1.js" -o "installer/config/transport1.js"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/transfer-plugins/nexus-deploy-plugin/src/main/resources/nexus.properties" -o "installer/config/nexus.properties"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/transfer-plugins/nexus-deploy-plugin/src/main/resources/nexus-deploy.sh" -o "installer/packages/nexus-deploy.sh"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/gateway-audit/src/main/resources/audit.properties" -o "installer/config/audit.properties"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/gateway-audit/src/main/resources/audit-event-template.xml" -o "installer/config/audit-event-template.xml"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/gateway-audit/src/main/resources/audit-file-template.xml" -o "installer/config/audit-file-template.xml"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/installer/README" -o "installer/README"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/installer/install.sh" -o "installer/install.sh"
curl -sk "https://raw.githubusercontent.com/surevine/gateway/master/installer/maven_home.tar.gz" -o "installer/packages/maven_home.tar.gz"

chmod +x installer/packages/nexus-deploy.sh
chmod +x installer/install.sh

# Source tarballs
curl -skL "https://github.com/surevine/gateway/archive/master.zip" -o "installer/sources/gateway.src.zip"
curl -skL "https://github.com/surevine/nexus-gateway-plugin/archive/master.zip" -o "installer/sources/nexus-gateway-plugin.src.zip"
curl -skL "https://github.com/surevine/federated-scm/archive/master.zip" -o "installer/sources/federated-scm.src.zip"

# Nexus jetty config
curl -skL "https://raw.githubusercontent.com/surevine/gateway/master/installer/nexus_jetty.xml" -o installer/config/nexus_jetty.xml

# Suck down release files
curl -skL -u "$NEXUS_USERNAME:$NEXUS_PASSWORD" "https://nexus.surevine.net/service/local/artifact/maven/redirect?r=TPS&g=com.surevine.community&a=gateway-web&p=war&v=LATEST" -o "installer/packages/gateway.war"
curl -skL -u "$NEXUS_USERNAME:$NEXUS_PASSWORD" "https://nexus.surevine.net/service/local/artifact/maven/redirect?r=TPS&g=com.surevine.community&a=gateway-management&p=zip&v=LATEST" -o "installer/packages/gateway-management.zip"
# nexus-gateway-plugin needed here:
#curl -sk "https://nexus.surevine.net/service/local/artifact/maven/redirect?r=TPS&g=com.surevine.community&a=gateway-web&p=war&v=LATEST" -o "installer/packages/gateway.war"
curl -skL -u "$NEXUS_USERNAME:$NEXUS_PASSWORD" "https://nexus.surevine.net/service/local/artifact/maven/redirect?r=TPS&g=com.surevine.gateway.scm&a=federated-scm&p=war&v=LATEST" -o "installer/packages/federated-scm.war"


# Create archive
tar czvf tps-releases-installer-$(date +%Y%m%d).tar.gz installer

# Clean up after ourselves
rm -r installer
