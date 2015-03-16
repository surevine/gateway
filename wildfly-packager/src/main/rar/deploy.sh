#!/bin/bash

if [ $# -lt 1 ]; then
  echo "Usage: `basename $0` wildfly_dir"
  exit 1
fi

if [ -z ${COMMUNITY_SCRIPT_FUNCTIONS} ]; then
  . ./setFunctions.sh
fi

WILDFLY_DIR="$1"

# run CLI scripts
. ./runScripts.sh ${WILDFLY_DIR}

# copy the new deployment
printStart "Deploying modules"
cp -rf modules ${WILDFLY_DIR}
printFinish

printStart "Deploying archives"
cp -rf standalone ${WILDFLY_DIR}
printFinish

