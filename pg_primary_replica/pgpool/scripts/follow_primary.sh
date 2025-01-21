##!/bin/bash
#
## Thông số nhận từ Pgpool-II
#NEW_PRIMARY_NODE_ID=$1
#NEW_PRIMARY_HOST=$2
#STANDBY_HOST=$3
#STANDBY_PORT=$4
#
#REPLICATION_USER="replicator3"
#REPLICATION_PASSWORD="1234"
#DATA_DIR="/var/lib/postgresql/data"
#
## Dừng dịch vụ PostgreSQL trên standby node
#ssh $STANDBY_HOST "pg_ctl -D $DATA_DIR -m fast stop"
#
## Đồng bộ dữ liệu từ primary mới
#ssh $STANDBY_HOST "pg_basebackup -h $NEW_PRIMARY_HOST -U $REPLICATION_USER -D $DATA_DIR -Fp -Xs -P -R"
#
## Tạo file standby.signal
##ssh $STANDBY_HOST "touch $DATA_DIR/standby.signal"
#
## Cập nhật thông tin primary node
##ssh $STANDBY_HOST "echo \"primary_conninfo = 'host=$NEW_PRIMARY_HOST port=$STANDBY_PORT user=$REPLICATION_USER password=$REPLICATION_PASSWORD'\" >> $DATA_DIR/postgresql.auto.conf"
#
## Khởi động lại PostgreSQL
#ssh $STANDBY_HOST "pg_ctl -D $DATA_DIR start"


#!/bin/bash

# Thông số nhận từ Pgpool-II
NEW_PRIMARY_NODE_ID=$1
NEW_PRIMARY_HOST=$2
STANDBY_HOST=$3
STANDBY_PORT=$4

#REPLICATION_USER="replicator3"
REPLICATION_USER="replicator3"
REPLICATION_PASSWORD="1234"
DATA_DIR="/var/lib/postgresql/data"

export PGPASSWORD=$REPLICATION_PASSWORD

# 1. Dừng PostgreSQL trên node standby
#pg_ctl -D "$DATA_DIR" -m fast stop

# 2. Đồng bộ dữ liệu từ primary mới
pg_basebackup -h "$NEW_PRIMARY_HOST" -p 5433 -U "$REPLICATION_USER" -D "$DATA_DIR" -Fp -Xs -P -R

# 3. Khởi động lại PostgreSQL trên standby
#pg_ctl -D "$DATA_DIR" start
