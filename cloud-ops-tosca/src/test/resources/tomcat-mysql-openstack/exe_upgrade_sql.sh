#!/bin/bash

#unzip patch_package, delete if exists
if [ -d "${sql_file}unzip" ]; then
	rm -rf ${sql_file}unzip
fi
unzip -d${sql_file}unzip -o ${sql_file}

if [ -e "${sql_file}unzip/upgrade.sql" ]; then
	mysql -u${db_user} -p${db_password} ${db_name} --force < ${sql_file}unzip/upgrade.sql
fi
