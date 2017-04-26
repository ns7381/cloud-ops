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