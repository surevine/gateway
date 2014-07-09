#!/bin/sh

export PATH=$PATH:/opt/gateway/maven/bin
export JAVA_HOME=/opt/gateway/java/jre
export PATH=/opt/gateway/java/jre/bin:$PATH

CMD=""
LOG="/tmp/nexus-deploy.log"

echo "-" >> $LOG
echo "$(date) $@" >> $LOG

source /etc/bashrc

cd ${1}

for ((i=2; i<=$#; i++)); do
  CMD="${CMD} ${!i}"
done

$CMD 2>&1 >> $LOG
