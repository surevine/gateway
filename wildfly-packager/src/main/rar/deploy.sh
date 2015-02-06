#!/bin/bash

if [ $# -lt 1 ]; then
  echo "Usage: `basename $0` wildfly_dir"
  exit 1
fi

WILDFLY_DIR="$1"

rm -rf ${WILDFLY_DIR}/standalone/deployments/gateway.war
rm -rf ${WILDFLY_DIR}/modules/com/surevine/community/config/gateway

cp -rf modules ${WILDFLY_DIR}
cp -rf standalone ${WILDFLY_DIR}

