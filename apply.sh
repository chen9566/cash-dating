#!/usr/bin/env bash

# 将数据恢复至本地mysql tao_ke 帐号需为root 密码需为空
# usage: apply.sh [sql]
mysql -h localhost -u root <<EOF
DROP DATABASE  IF EXISTS  cash;
create database cash;
reset master;
EOF

mysql -h localhost -u root cash < $1

