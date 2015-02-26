#!/bin/bash

function printHeaderStart() {
  echo "################################################"
  echo
}

function printHeaderText() {
  TEXT="${@}"
  echo "`date`:  ${TEXT}"
  echo
}

function printStart() {
  TEXT="${1}"
  printHeaderStart
  printHeaderText ${TEXT}
}

function printFinish() {
  echo
  echo "`date`:  Done."
  echo
  echo "################################################"
  echo
  echo
  echo
}

export COMMUNITY_SCRIPT_FUNCTIONS=true
