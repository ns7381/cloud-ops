#backup.sh  
#!/bin/sh  
# by liyongxian

INNOBACKUPEXFULL=/usr/bin/innobackupex  #INNOBACKUPEX的命令路径   
#HOSTNAME=`hostname -s`
#mysql目标服务器以及用户名和密码 
MYSQL_CMD="--host=localhost --user=root --password=${db_password} --port=3306"
MY_CNF=/etc/my.cnf #mysql的配置文件  
MYSQL=/usr/bin/mysql
BACKUP_DIR=/opt/xtrabackup/data # 备份的主目录  
if [ ! -d $BACKUP_DIR ]; then 
mkdir -p $BACKUP_DIR
fi
backup_dir=/var/log/mysql
if [ ! -d $backup_dir ]; then
mkdir -p $backup_dir
fi

if [ ! -f $backup_dir/backup.log ]; then
	touch $backup_dir/backup.log
	chmod 666 $backup_dir/backup.log
fi
backup_log=$backup_dir/backup.log

#BACKUP_FILE=${HOSTNAME}"-"`date +%F_%H-%M-%S`.tar.gz
BACKUP_FILE=`date +%F_%H-%M-%S`.tar.gz
mysql_status=`netstat -nl | awk 'NR>2{if ($4 ~ /.*:3306/) {print "Yes";exit 0}}'`   
if [ "$mysql_status" != "Yes" ];then  
    echo  "MySQL 没有启动运行."  >> $backup_log
    exit 0
fi  
  
#echo "开始于: `date +%F' '%T' '%w`"  
START_TIME=`date +%F' '%T` 
echo "开始备份时间：" $START_TIME >> $backup_log 
result= `$INNOBACKUPEXFULL --defaults-file=$MY_CNF  --use-memory=4G  $MYSQL_CMD  --stream=tar  $BACKUP_DIR 2>/dev/null|gzip > ${BACKUP_DIR}/${BACKUP_FILE}` 

END_TIME=`date +%F' '%T`
echo "备份结束时间："$END_TIME >> $backup_log

exit 0
