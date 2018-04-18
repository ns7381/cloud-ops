#!/bin/bash
unzip -d${install_package}_unzip -o -qq ${install_package}
hostIp=`curl http://169.254.169.254/latest/meta-data/public-ipv4`
install_package=${install_package}_unzip
CATALINA_HOME=$tomcat_home

mkdir /usr/lib/java
tar -zxf ${install_package}/jdk-8u101-linux-x64.gz -C /usr/lib/java

echo '
JAVA_HOME="/usr/lib/java/jdk1.8.0_101"
PATH=$JAVA_HOME/bin:$PATH
CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export JAVA_HOME
export PATH
export CLASSPATH
' >>/etc/profile
source /etc/profile
echo "127.0.0.1 $HOSTNAME" |  tee -a /etc/hosts
systemctl stop firewalld
mkdir -p ${CATALINA_HOME}/webapps
cp -rf ${install_package}/tomcat-cloud-web/* ${CATALINA_HOME}/
sed -i "s/10.110.17.144/${hostIp}/g" `grep -lr "10.110.17.144" /opt/tomcat-cloud-web/webapps/cloud-web/WEB-INF/classes/*`
chmod u+x ${CATALINA_HOME}/bin/catalina.sh
chmod u+x ${CATALINA_HOME}/bin/startup.sh
sh ${CATALINA_HOME}/bin/startup.sh

