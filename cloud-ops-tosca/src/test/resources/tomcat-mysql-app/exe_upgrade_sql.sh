#!/bin/bash

#unzip patch_package, delete if exists
if [ -d "${sql_file}unzip" ]; then
	rm -rf ${sql_file}unzip
fi
unzip -d${sql_file}unzip -o -qq ${sql_file}
EXE_LOG_DIR=/opt/xtrabackup/data # 执行日志的主目录
if [ ! -d $EXE_LOG_DIR ]; then
    mkdir -p /opt/iop-ops/log
fi
if [ -e "${sql_file}unzip/upgrade.sql" ]; then
	mysql -u${db_user} -p${db_password} ${db_name} --force < ${sql_file}unzip/upgrade.sql &> /opt/iop-ops/log/exe_sql.log
fi
