#!/bin/bash

# Xóa tệp PID cũ trước khi Pgpool-II khởi động
if [ -f "/opt/bitnami/pgpool/tmp/pgpool.pid" ]; then
  echo "Xóa tệp PID cũ..."
  rm -f /opt/bitnami/pgpool/tmp/pgpool.pid
fi

cp /opt/bitnami/scripts/pgpool/conf/pgpool.conf /opt/bitnami/pgpool/conf/pgpool.conf
cp /opt/bitnami/scripts/pgpool/conf/pool_hba.conf /opt/bitnami/pgpool/conf/pool_hba.conf
cp /opt/bitnami/scripts/pgpool/conf/pool_passwd /opt/bitnami/pgpool/conf/pool_passwd
exec /opt/bitnami/scripts/pgpool/run.sh
