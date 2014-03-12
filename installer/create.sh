#!/bin/sh

#
# Shell script to create an installer package for release manangement
#

# Ensure vanilla installers are in place
#mkdir /mnt/fbd
#mountpoint -q /mnt/fbd || mount 10.66.2.16:/fbd /mnt/fbd

STATIC_ROOT="/var/lib/jenkins/tps/installer"

# Create installer structure
mkdir -p installer/packages
mkdir -p installer/config

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

# Suck down source control files
curl -sk "https://raw.github.com/surevine/gateway/master/gateway-web/src/main/resources/gateway.properties" -o "installer/config/gateway.properties"
curl -sk "https://raw.github.com/surevine/gateway/master/javascript-plugins/src/main/resources/export-rules.js" -o "installer/config/export-rules.js"
curl -sk "https://raw.github.com/surevine/gateway/master/javascript-plugins/src/main/resources/import-filter.js" -o "installer/config/import-filter.js"
curl -sk "https://raw.github.com/surevine/gateway/master/javascript-plugins/src/main/resources/javascript-hook.properties" -o "installer/config/javascript-hook.properties"
curl -sk "https://raw.github.com/surevine/gateway/master/javascript-plugins/src/main/resources/metadata-filter.js" -o "installer/config/metadata-filter.js"
curl -sk "https://raw.github.com/surevine/gateway/master/javascript-plugins/src/main/resources/transport1.js" -o "installer/config/transport1.js"
curl -sk "https://raw.github.com/surevine/gateway/master/transfer-plugins/nexus-deploy-plugin/src/main/resources/nexus-deploy.sh" -o "installer/packages/nexus-deploy.sh"
curl -sk "https://raw.github.com/surevine/gateway/master/installer/README" -o "installer/README"
curl -sk "https://raw.github.com/surevine/gateway/master/installer/install.sh" -o "installer/install.sh"
chmod +x installer/packages/nexus-deploy.sh
chmod +x installer/install.sh

# Suck down release files
curl -sk "https://nexus.surevine.net/service/local/artifact/maven/redirect?r=TPS&g=com.surevine.community&a=gateway-web&p=war&v=LATEST" -o "installer/packages/gateway.war"
curl -sk "https://nexus.surevine.net/service/local/artifact/maven/redirect?r=TPS&g=com.surevine.community&a=gateway-web&p=war&v=LATEST" -o "installer/packages/gateway.war"


# Create archive
tar czvf tps-releases-installer-$(date +%Y%m%d).tar.gz installer

