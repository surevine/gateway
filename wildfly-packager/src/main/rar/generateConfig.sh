#!/bin/bash

if [ $# -ne 2 ]; then
  echo "Usage: `basename $0` template_file target_file"
  exit 1
fi

TEMPLATE_FILE="${1}"
TARGET_FILE="${2}"

if [[ ! -f "${TEMPLATE_FILE}" ]]; then
  echo "${TEMPLATE_FILE} does not exist. Unable to generate configuration from non-existant template file"
  echo
  exit 1
fi

CONTINUE="y"
if [[ -f "${TARGET_FILE}" ]]; then
  echo "${TARGET_FIlE} exists; if you continue, the file will be overwritten. Do you wish to continue?"
  while true; do
    read -p "[Y/n]: " CONTINUE
    case ${CONTINUE} in
      [Yy] ) CONTINUE="y"; break;;
      [Nn] ) CONTINUE="n"; break;;
      * ) ;;
    esac
  done
fi

if [[ "${CONTINUE}" == "n" ]]; then
  echo "File will not be overwritten. Exiting..."
  echo
  exit 0
fi

cat /dev/null > ${TARGET_FILE}

while IFS=, read PROPERTY DEFAULT OPTIONS DESCRIPTION; do
  if [[ ! -z "${PROPERTY}" ]] && [[ ! "${PROPERTY}" =~ ^#.* ]]; then
    echo "Please enter a value for property: ${PROPERTY}"
    echo "Description: ${DESCRIPTION}"
    if [[ ! -z "${OPTIONS}" ]]; then
      echo "Options: ${OPTIONS}:"
    fi
    echo
    read -p "[${DEFAULT}]: " -u 3 VALUE
    if [[ -z "${VALUE}" ]]; then
      VALUE="${DEFAULT}"
    fi
    echo
    echo "Setting: ${PROPERTY}=${VALUE}"
    echo "${PROPERTY}=${VALUE}" >> ${TARGET_FILE}
    echo
    echo
  fi  
done 3<&0 < $1

