
if [ $# -lt 1 ]; then
  echo "Usage: `basename $0` environment_key1 [ environment_key2 ... ]" 1>&2
  exit 1
fi

ENV_FILE="env-config-template.properties"

TARGET_FILE="env-config.properties"

if [ -e ${TARGET_FILE} ]; then
  rm ${TARGET_FILE}
fi

for ENVIRONMENT_KEY in "$@"; do
  grep "${ENVIRONMENT_KEY}\." ${ENV_FILE} | sed -e "s/${ENVIRONMENT_KEY}\.//g" >> ${TARGET_FILE}
done

