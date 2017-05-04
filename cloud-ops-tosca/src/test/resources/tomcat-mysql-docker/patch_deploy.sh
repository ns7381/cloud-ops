#!/bin/bash


#set JAVA_HOME
source /etc/profile

#set CATALINA_HOME
CATALINA_HOME=$tomcat_home

#stop tomcat
sh $CATALINA_HOME/bin/shutdown.sh
sleep 5
PID=$(ps -ef |grep $tomcat_home |grep -v 'grep'|awk '{print $2}')
if [ -n "$PID" ];then
    kill -9 $PID
fi

#unzip patch_package, delete if exists
if [ -d "${patch_package}unzip" ]; then
	rm -rf ${patch_package}unzip
fi
unzip -d${patch_package}unzip -o ${patch_package}

#backup war package end
BACKUP_DIR=/opt/iop-ops/backup/app-package/`date +%F_%H-%M-%S`
if [ -d "${BACKUP_DIR}" ]; then
	rm -rf ${BACKUP_DIR}
fi
mkdir -p $BACKUP_DIR
cp -rf ${CATALINA_HOME}/webapps/* $BACKUP_DIR

#resolve file: 1.delete unused file 2.check unchanged file md5 3.overwrite changed file
cd ${CATALINA_HOME}/webapps/${app_context}
cat "${patch_package}unzip/files-to-del.txt"| while read line
do
    rm -rf $line
done
md5sum --check ${patch_package}unzip/files-to-check.txt |grep FAILED
#unalias cp
cp -rf ${patch_package}unzip/* ${CATALINA_HOME}/webapps/${app_context}/

sh ${CATALINA_HOME}/bin/startup.sh
