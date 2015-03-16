#!/bin/bash

if [ $# -lt 1 ]; then
  echo "Usage: `basename $0` wildfly_dir"
  exit 1
fi

if [ -z ${COMMUNITY_SCRIPT_FUNCTIONS} ]; then
  . ./setFunctions.sh
fi

WILDFLY_DIR="$1"

# remove the existing deployment
printStart "Removing existing deployment"
rm -rf ${WILDFLY_DIR}/standalone/deployments/gateway.war
rm -rf ${WILDFLY_DIR}/modules/com/surevine/community/config/gateway
printFinish

