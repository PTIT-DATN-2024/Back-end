#!/bin/bash
FAILED_NODE_ID=$1
NEW_PRIMARY_NODE=$2

echo "Failover triggered. Node $FAILED_NODE_ID failed. Promoting $NEW_PRIMARY_NODE to primary."

# Đặt mật khẩu cho user 'admin' thông qua biến môi trường
export PGPASSWORD="1234"

if [ "$FAILED_NODE_ID" == "0" ]; then
    psql -h postgres_slave -p 5433 -U admin -c "SELECT pg_promote();"
fi



#!/bin/bash
#!/bin/bash
#
#FAILED_NODE_ID=$1
#NEW_PRIMARY_NODE=$2
#
#echo "Failover triggered New Version. Node $FAILED_NODE_ID failed. Promoting $NEW_PRIMARY_NODE to primary."
#
## Lấy danh sách các node từ pgpool
#POOL_NODES=$(psql -U admin -h localhost -c "SHOW pool_nodes;" -t -A -F '|')
#
#if [ -z "$POOL_NODES" ]; then
#    echo "Không thể lấy danh sách pool nodes từ Pgpool. Kiểm tra kết nối hoặc quyền truy cập."
#    exit 1
#fi
#
## Debug output
#echo "LIST_NODES:
#$POOL_NODES"
#
## Duyệt qua danh sách để tìm thông tin Slave
#PGPOOL_HOST=""
#PGPOOL_PORT=""
## Dùng input redirect IFS=' |', để loop vẫn trong phạm vi shell (nếu dùng pipeline -> loop nằm trong subshell -> ko thay đổi biến ở ngoài)
#while IFS='|' read -r NODE_ID NODE_HOST NODE_PORT _ _ _ NODE_ROLE _; do
#    echo "Node ID: $NODE_ID, Role: $NODE_ROLE, Host: $NODE_HOST, Port: $NODE_PORT"
#
#    if [[ "$NODE_ROLE" == "standby" && "$NODE_ID" != 0 ]]; then
#        PGPOOL_HOST="$NODE_HOST"
#        PGPOOL_PORT="$NODE_PORT"
#        break
#    fi
#done <<< "$POOL_NODES"
#
#if [ -z "$PGPOOL_HOST" ] || [ -z "$PGPOOL_PORT" ]; then
#    echo "Not found Slave node to perform failover."
#    exit 1
#fi
#
#echo "Performing failover with Slave node: $PGPOOL_HOST:$PGPOOL_PORT"
#
## Đặt mật khẩu cho user 'admin' thông qua biến môi trường
#export PGPASSWORD="${PGPOOL_POSTGRES_PASSWORD:-1234}"
#echo "Pass: $PGPASSWORD"
#
## Thực hiện lệnh pg_promote trên Slave node
#if ! psql -h "$PGPOOL_HOST" -p "$PGPOOL_PORT" -U admin -c "SELECT pg_promote();"; then
#    echo "Failed to execute pg_promote on $PGPOOL_HOST:$PGPOOL_PORT."
#    exit 1
#fi
#
#echo "Failover Successfully."
