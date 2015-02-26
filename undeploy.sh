#!/bin/bash

if [ $# -eq 0 ]; then
  WILDFLY_DIR="/Applications/wildfly/instance_1"
else
  WILDFLY_DIR="${1}"
  shift
fi

BUILT_DIRECTORY=`find wildfly-packager/target/ -type d -d -name "gateway-wildfly-package-*"`

cd ${BUILT_DIRECTORY}

chmod +x *.sh

. ./undeploy.sh "${WILDFLY_DIR}"

