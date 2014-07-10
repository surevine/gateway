#!/bin/sh

set -e

LOG_FILE="install.log"
INSTALL_DIR="/opt/gateway"
NEXUS_USER="nexus"
WILDFLY_USER="gateway"

# Adjust the below rpm paths depending on your version of CENTOS

CREATEREPO_RPM="packages/createrepo-0.4.11-3.el5.noarch.rpm" # For Centos 5.10
# CREATEREPO_RPM="packages/createrepo-0.9.9-18.el6.noarch.rpm" # For Centos 6

LIBXML_RPM="packages/libxml2-python-2.6.26-2.1.21.el5_9.3.x86_64.rpm" # For Centos 5.10
#LIBXML_RPM="packages/libxml2-python-2.7.6-14.el6_5.2.x86_64.rpm"

PYTHON_DELTA_RPM="packages/python-deltarpm-3.5-0.5.20090913git.el6.x86_64.rpm"
LIBXML="packages/libxml2-2.7.6-14.el6_5.2.x86_64.rpm"

# Run script with umask 0022 but reset umask to original value on
# exit, even if an error occurs during installation
INITIAL_UMASK=`umask`
function reset_umask {
	umask $INITIAL_UMASK
}
trap reset_umask EXIT

LOG_FILE="install.log"
INSTALL_DIR="/opt/gateway"
NEXUS_USER="nexus"
WILDFLY_USER="gateway"

clear

function print_progress() {
  printf "%0.s#" $(seq 0 $1)
  printf "%0.s " $(seq 0 $((40-$1)))
  printf "  ($(($1*100/40))%%)\r"
}

echo
echo "                  Nexus and community gateway installation."
echo
echo "Please select an installation directory:"
read -p "[${INSTALL_DIR}]: " TMP_INSTALL_DIR
if [[ ! -z "$TMP_INSTALL_DIR" ]]; then INSTALL_DIR="$TMP_INSTALL_DIR"; fi
echo
echo "Please wait..."
echo

# Move log files aside
cat /dev/null > $LOG_FILE

print_progress 1
touch $LOG_FILE

# Header logs
date >> $LOG_FILE
echo "Installing to $INSTALL_DIR" >> $LOG_FILE

# Create directory
if [ -d "$INSTALL_DIR" ]; then
  echo "Installation directory already exists at $INSTALL_DIR. Aborting."
  exit 1
else
  mkdir -p "$INSTALL_DIR"
fi

# Exract JDK
print_progress 8
tar xzvf "packages/jdk-7u51-linux-x64.tar.gz" -C "$INSTALL_DIR" >> $LOG_FILE
ln -sf "$INSTALL_DIR/jdk1.7.0_51" "$INSTALL_DIR/java" >> $LOG_FILE

# Extract Nexus
print_progress 12
tar xzvf "packages/nexus-2.7.2-03-bundle.tar.gz" -C "$INSTALL_DIR" >> $LOG_FILE
ln -sf "$INSTALL_DIR/nexus-2.7.2-03" "$INSTALL_DIR/nexus" >> $LOG_FILE

# Extract JBoss
print_progress 16
tar xzvf "packages/wildfly-8.0.0.Final.tar.gz" -C "$INSTALL_DIR" >> $LOG_FILE
ln -sf "$INSTALL_DIR/wildfly-8.0.0.Final" "$INSTALL_DIR/wildfly" >> $LOG_FILE

# Extract Maven
print_progress 20
tar xzvf "packages/apache-maven-3.1.1-bin.tar.gz" -C "$INSTALL_DIR" >> $LOG_FILE
ln -sf "$INSTALL_DIR/apache-maven-3.1.1" "$INSTALL_DIR/maven" >> $LOG_FILE

# Create keystore and configure Nexus' Jetty for SSL
/opt/gateway/java/jre/bin/keytool -genkey -noprompt -alias `hostname` -dname "cn=`hostname`" -storepass changeit -keyalg RSA -keystore /opt/gateway/nexus/conf/keystore.jks -keysize 2048 -keypass changeit >> $LOG_FILE

cp -f "nexus_jetty.xml" "$INSTALL_DIR/nexus/conf/jetty.xml" >> $LOG_FILE
echo "application-port-ssl=8443" > "$INSTALL_DIR/nexus/conf/nexus.properties" 

# NAT Nexus to port 80/443 using iptables
print_progress 25
iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8081 >> $LOG_FILE
iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 443 -j REDIRECT --to-port 8443 >> $LOG_FILE
service iptables save >> $LOG_FILE

# Install createrepo dependency
print_progress 27
IS_AMZN=`uname -a | grep amzn | wc -l`
if [ $IS_AMZN -ne 0 ]
then
	yum -y install createrepo >> $LOG_FILE
else
        rpm -ivh $LIBXML_RPM >> $LOG_FILE
	rpm -ivh $CREATEREPO_RPM >> $LOG_FILE
	
fi

# Customise Nexus
print_progress 30
cp "packages/fluent-hc-4.2.5.jar" "$INSTALL_DIR/nexus/nexus/WEB-INF/lib/" >> $LOG_FILE
cp "packages/httpmime-4.2.5.jar" "$INSTALL_DIR/nexus/nexus/WEB-INF/lib/" >> $LOG_FILE
cp "packages/nexus-gateway-plugin.jar" "$INSTALL_DIR/nexus/nexus/WEB-INF/lib/" >> $LOG_FILE

# Add gateway war
print_progress 31
cp "packages/gateway.war" "$INSTALL_DIR/wildfly/standalone/deployments/" >> $LOG_FILE

# System users
print_progress 32
id -u $NEXUS_USER 1>> $LOG_FILE 2>> $LOG_FILE || useradd $NEXUS_USER >> $LOG_FILE
id -u $WILDFLY_USER 1>> $LOG_FILE 2>> $LOG_FILE || useradd $WILDFLY_USER >> $LOG_FILE

# Nexus service script
print_progress 33
ln -sf "$INSTALL_DIR/nexus/bin/nexus" "/etc/init.d/nexus" >> $LOG_FILE
sed -i "s/#RUN_AS_USER=/RUN_AS_USER=$NEXUS_USER/g" "/etc/init.d/nexus" >> $LOG_FILE
sed -i "12i\\\nJAVA_HOME=$INSTALL_DIR/java/jre\nPATH=\$JAVA_HOME/bin:\$PATH" "/etc/init.d/nexus" >> $LOG_FILE
chkconfig --add nexus >> $LOG_FILE
chkconfig --levels 345 nexus on >> $LOG_FILE
chown -R $NEXUS_USER:$NEXUS_USER "$INSTALL_DIR/nexus-2.7.2-03" >> $LOG_FILE
chown -R $NEXUS_USER:$NEXUS_USER "$INSTALL_DIR/sonatype-work" >> $LOG_FILE

# JBoss service script
print_progress 34
ln -sf "$INSTALL_DIR/wildfly/bin/init.d/wildfly-init-redhat.sh" "/etc/init.d/wildfly" >> $LOG_FILE
chkconfig --add wildfly >> $LOG_FILE
chkconfig --levels 345 wildfly on >> $LOG_FILE
sed -i "11i\\\nexport JBOSS_USER=$WILDFLY_USER\nJBOSS_HOME=$INSTALL_DIR/wildfly\nJAVA_HOME=$INSTALL_DIR/java/jre" "/etc/init.d/wildfly" >> $LOG_FILE
chown -R "$WILDFLY_USER:$WILDFLY_USER" "$INSTALL_DIR/wildfly-8.0.0.Final" >> $LOG_FILE

# Install nexus-deploy
cp "packages/nexus-deploy.sh" $INSTALL_DIR/ >> $LOG_FILE

# System configuration

# Application configuration
print_progress 35
sed -i "s/jboss.bind.address:127.0.0.1}\"/jboss.bind.address:0.0.0.0}\"/g" "$INSTALL_DIR/wildfly/standalone/configuration/standalone.xml" >> $LOG_FILE
sed -i "s/jboss.bind.address.management:127.0.0.1}\"/jboss.bind.address.management:0.0.0.0}\"/g" "$INSTALL_DIR/wildfly/standalone/configuration/standalone.xml" >> $LOG_FILE
sed -i "s/socket-binding=.http./socket-binding=\"http\" max-post-size=\"2147483648\"/g" "$INSTALL_DIR/wildfly/standalone/configuration/sta ndalone.xml" >> $LOG_FILE 
sed -i "s/-Xmx512m/-Xmx2048m/g" "$INSTALL_DIR/wildfly/standalone.conf" >> $LOG_FILE
mkdir -p "$INSTALL_DIR/wildfly/modules/com/surevine/community/gateway/main" >> $LOG_FILE
ln -sf "$INSTALL_DIR/wildfly/modules/com/surevine/community/gateway/main" "$INSTALL_DIR/config" >> $LOG_FILE
cp -r config/* "$INSTALL_DIR/config" >> $LOG_FILE

# Application startup
print_progress 36
service nexus start >> $LOG_FILE
print_progress 38
service wildfly start >> $LOG_FILE

print_progress 40
printf "\n"

echo
echo "Installation complete. Services will be started shortly."
echo
date
