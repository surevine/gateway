#!/bin/bash

if [ $# -lt 1 ]; then
  echo "Warning: No source configuration file specified for replacement processing. Template scripts will not be changed."
  echo "Usage: `basename $0` replacement_source1 [ replacement_source2 ... ]"
fi

if [ -z ${COMMUNITY_SCRIPT_FUNCTIONS} ]; then
  . ./setFunctions.sh
fi

# Expects there to be a template directory, which will be copied before substitutions are processed
TEMPLATE_DIRECTORY="cli-template"

# The name of the copied directory containing all of the scipts to be updated before deployment
TARGET_DIRECTORY="cli"

# If the directory containing the template scripts doesn't exist, exit with an error
if [ ! -d "${TEMPLATE_DIRECTORY}" ]; then
  echo "Error: please ensure that a '${TEMPLATE_DIRECTORY}' directory exists to update." 1>&2
  exit 1
fi

# remove any existing directory with updated scripts from a previous run
if [ -d "${TARGET_DIRECTORY}" ]; then
  printStart "Removing existing generated scripts"
  rm -rf ${TARGET_DIRECTORY}
  printFinish
fi

printStart "Generating configured executable CLI scripts"

# Take a copy of the template directory
cp -r "${TEMPLATE_DIRECTORY}" "${TARGET_DIRECTORY}"

# Loop through the files in the copied template directory, processing replacements on each in turn
for TARGET_FILE in `find ${TARGET_DIRECTORY} -name "*.cli"`; do

  # For each file we process, loop through the source configuration files given to the script
  if [ ! -z "$@" ]; then
    for REPLACEMENT_FILE in "$@"; do

      # Split each line of the source files into the key and value components as separated by the =
      while IFS="=" read -r KEY VALUE; do

        # Replace '.' in the key with '\.' to ensure they are escaped in the sed regex replacements
        KEY=`echo "${KEY}" | sed "s/\./\\\./g"`

        # Replace '.', ' ' and '/' in the value to ensure they are appropriately escaped in the sed regex replacements
        VALUE=`echo "${VALUE}" | sed "s/\./\\\./g" | sed 's/[ ]/WHITESPACE/g' | sed "s/\//FORWARDSLASH/g"`

        # Only process if we have a key (skips blank lines)
        if [ ! -z "${KEY}" ]; then

          # Perform the replacement to put the new value in
          # Will only match when the key is at the start of the line - will skip commented configuration
          sed -i -e "s/\[\[${KEY}\]\]/${VALUE}/g" ${TARGET_FILE}

          # Reverse the previous escape replacements
          sed -i -e "s/WHITESPACE/ /g" ${TARGET_FILE}
          sed -i -e "s/FORWARDSLASH/\//g" ${TARGET_FILE}
        fi
      done < "${REPLACEMENT_FILE}"
    done
  fi
done

printFinish
