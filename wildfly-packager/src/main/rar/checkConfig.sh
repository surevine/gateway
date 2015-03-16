#!/bin/bash

if [ $# -ne 2 ]; then
  echo "Usage: `basename $0` template_file config_file"
  exit 1
fi

TEMPLATE_FILE="${1}"
CONFIG_FILE="${2}"

if [[ ! -f "${TEMPLATE_FILE}" ]]; then
  echo "${TEMPLATE_FILE} does not exist. Unable to check configuration against non-existant template file"
  echo
  exit 1
fi

if [[ ! -f "${CONFIG_FILE}" ]]; then
  echo "${CONFIG_FILE} does not exist. Unable to check configuration for non-existant file"
  echo
  exit 1
fi

MISSING_PROPERTIES="n"
echo

# read in template file
while IFS=, read PROPERTY DEFAULT OPTIONS DESCRIPTION; do

  # ensure we only check lines that aren't blank or commented
  if [[ ! -z "${PROPERTY}" ]] && [[ ! "${PROPERTY}" =~ ^#.* ]]; then

    # search for the read-in property in the configuration file
    PROPERTY_PRESENT=`grep "${PROPERTY}" "${CONFIG_FILE}"`

    # if an entry cannot be found, echo a warning
    if [[ -z "${PROPERTY_PRESENT}" ]]; then
      MISSING_PROPERTIES="y"
      echo "WARNING: Property '${PROPERTY}' was not found in the given configuration file."
      echo "Description: ${DESCRIPTION}"
      echo
    fi
  fi  
done 3<&0 < $1

echo
echo
if [[ "${MISSING_PROPERTIES}" == "y" ]]; then
  echo "Missing configuration properties were identified."
  echo
  echo "Run 'generateConfig.sh' to create a file with all required properties."
else
  echo "All configuration properties are present."
fi

echo

