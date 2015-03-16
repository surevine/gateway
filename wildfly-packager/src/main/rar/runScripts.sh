#!/bin/bash

if [ $# -lt 1 ]; then
  echo "Usage: `basename $0` wildfly_dir"
  exit 1
fi

if [ -z ${COMMUNITY_SCRIPT_FUNCTIONS} ]; then
  . ./setFunctions.sh
fi

WILDFLY_DIR="$1"

# run cli scripts
for FILE in `find cli -name "*.cli" | sort`; do

  printStart "Running CLI scipt: ${FILE}"
  ${WILDFLY_DIR}/bin/jboss-cli.sh --file=${FILE}
  printFinish

done
