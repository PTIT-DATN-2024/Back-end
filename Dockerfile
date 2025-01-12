# Sử dụng OpenJDK 17 slim làm base image
FROM openjdk:17-jdk-slim

# Đặt thư mục làm việc bên trong container
WORKDIR /app

# Sao chép tệp JAR ứng dụng từ quá trình build vào container
COPY target/backend-application.jar app.jar

# Expose cổng mà ứng dụng sẽ chạy
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
