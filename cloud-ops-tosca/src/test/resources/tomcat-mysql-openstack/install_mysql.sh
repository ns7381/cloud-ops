#!/bin/bash

unzip -d${install_package}_unzip -o -qq ${install_package}
hostIp=`curl http://169.254.169.254/latest/meta-data/public-ipv4`
install_package=${install_package}_unzip
cd ${install_package}/packages/monit
chmod a+x install 
./install 
cd ../.. 
cd ${install_package}/packages/mysqlCommon 
chmod a+x install 
./install 
cd ../.. 
cd ${install_package}/packages/mysqlClient 
chmod a+x install 
./install 
cd ../.. 
cd ${install_package}/packages/mysqlServer 
chmod a+x install 
./install

echo "installed mysql rpm"
#cd ${install_package}/template
cp -f ${install_package}/template/wsrep.cnf /etc/my.cnf.d/
cp -f ${install_package}/template/mysql /etc/init.d/
cp -f ${install_package}/template/my.cnf /etc/my.cnf
cp -f ${install_package}/template/ntp.conf /etc/ntp.conf
cp -f ${install_package}/template/pt-duplicate-key-checker /usr/bin/
cp -f ${install_package}/template/monit /etc/init.d
#systemctl stop firewalld.service
#systemctl disable firewalld.service
firewall-cmd --zone=public --add-port=3307/tcp --permanent  >/dev/null
firewall-cmd --zone=public --add-port=4567/tcp --permanent  >/dev/null
firewall-cmd --zone=public --add-port=4667/tcp --permanent  >/dev/null
firewall-cmd --zone=public --add-port=4444/tcp --permanent  >/dev/null
firewall-cmd --reload  >/dev/null
sed -i 's/UsePAM yes/UsePAM no/g' /etc/ssh/sshd_config
chkconfig  mysql off

egrep "^mysql" /etc/group >& /dev/null
if [ $? -ne 0 ];  then  
    groupadd mysql
fi
egrep "^mysql" /etc/passwd >& /dev/null
if [ $? -ne 0 ];  then  
    useradd -g mysql mysql -s /bin/false
fi
if [ ! -d '/var/log/mysql' ]; then
	mkdir  -p /var/log/mysql
fi

if [ -d '/var/lib/mysql' ]; then
	rm -rf /var/lib/mysql/lost+found 2>/dev/null
fi

if [ ! -f '/var/log/mysql/slow.log' ]; then
	touch /var/log/mysql/slow.log
	chmod 666 /var/log/mysql/slow.log
	chown -R mysql:mysql /var/log/mysql
fi
if [ ! -f '/var/log/mysql/mysql.log' ]; then
	touch /var/log/mysql/mysql.log
	chmod 666 /var/log/mysql/mysql.log
	chown -R mysql:mysql /var/log/mysql
fi

if [ ! -d '/var/lib/galera' ]; then
    mkdir -p /var/lib/galera
	chown -R mysql:mysql /var/lib/galera
else
	chown -R mysql:mysql /var/lib/galera
fi


if [ ! -f '/usr/local/bin/mysql_ctl' ]; then
cp -f ${install_package}/bin/mysql_ctl /usr/local/bin/
fi
chmod u+x /usr/local/bin/mysql_ctl
#PORTNUM=`netstat -lnt|grep 3306|wc -l`
#PORTNUM=`ps -ef | grep mysql | grep -v grep | wc -l`
chown -R mysql:mysql /var/lib/galera
 
/usr/local/bin/mysql_ctl start
#garbd -a gcomm://127.0.0.1:4567 -g 5k8q7hed -o base_port=4667  -d
systemctl start monit
#update user set host='10.60.10.95' where User='sst' and host='%';
#update user set host='10.60.10.95' where User='sst';
#GRANT ALL PRIVILEGES ON *.* TO 'sst'@'localhost' IDENTIFIED BY '123456a?' WITH GRANT OPTION;
PORTNUM=`netstat -lnt|grep 3307|wc -l`
if [ $PORTNUM = 1  ]; then 
mysqladmin -uroot password '123456a?' 2>/dev/null
mysql -uroot -p123456a? 2>/dev/null <<EOF
GRANT ALL PRIVILEGES ON *.* TO "root"@"${hostIp}" IDENTIFIED BY '123456a?' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'sst'@'localhost' IDENTIFIED BY '123456a?' WITH GRANT OPTION;
FLUSH PRIVILEGES;
use mysql
update user set Password=password('123456a?') where User='sst';

delete from user where host='localhost.localdomain';
delete from proxies_priv where host='localhost.localdomain';
commit;
flush privileges;
EOF
#update user set host='10.60.10.95' where User='sst' and host='%';
mysql -uroot -p123456a? --force < ${install_package}/packages/sql/iop_cc.sql &> /var/log/exe_sql.log
mysql -uroot -p123456a? --force < ${install_package}/packages/sql/iop_monitor.sql &> /var/log/exe_sql.log
#./grantPrivilege
else
echo "Starting MySQL ERROR! "
fi
#GRANT ALL PRIVILEGES ON *.* TO 'sst'@'localhost' IDENTIFIED BY '123456a?' WITH GRANT OPTION;
#update user set host='10.60.10.95' where User='sst' and host='%';