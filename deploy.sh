#!/bin/bash

if [ $# -eq 0 ]; then
  CONFIG_KEYS="${USER}"
else
  CONFIG_KEYS="$@"
fi

WILDFLY_DIR="/Applications/wildfly/instance_1"

BUILT_DIRECTORY=`find wildfly-packager/target/ -type d -d -name "gateway-wildfly-package-*"`

ENV_CONFIG="env-config.properties"
SENSITIVE_CONFIG="sensitive-config.properties"

if [ -e ${SENSITIVE_CONFIG} ]; then
  cp ${SENSITIVE_CONFIG} ${BUILT_DIRECTORY}
else
  SENSITIVE_CONFIG=
fi

cd ${BUILT_DIRECTORY}

chmod +x *.sh

. ./extractEnvConfig.sh "${CONFIG_KEYS}"

. ./updateTemplateConfig.sh ${ENV_CONFIG} ${SENSITIVE_CONFIG}

. ./deploy.sh "${WILDFLY_DIR}"

