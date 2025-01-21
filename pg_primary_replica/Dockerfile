FROM bitnami/pgpool:4

# Chạy dưới quyền root để cài đặt PostgreSQL client và server tools
USER root

# Cài đặt PostgreSQL client và các công cụ PostgreSQL server (bao gồm pg_ctl)
RUN apt-get update && \
    apt-get install -y postgresql postgresql-client postgresql-common && \
    rm -rf /var/lib/apt/lists/*

# Đặt lại người dùng để chạy container theo cấu hình Bitnami
USER 1001
